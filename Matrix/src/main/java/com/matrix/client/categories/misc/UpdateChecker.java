package com.matrix.client.categories.misc;

import ai.auth.Module;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matrix.client.config.BooleanSetting;
import com.matrix.client.config.ConfigSetting;
import com.matrix.client.gui.ButtonBase;
import com.matrix.client.gui.DisabledEnabled;
import com.matrix.client.utils.IconsPngType;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import net.fabricmc.loader.api.FabricLoader;

public class UpdateChecker extends Module {
   private static final String UPDATE_URL = "https://raw.githubusercontent.com/nth-oficiell/test/refs/heads/vibe/vortex-394c92/Matrix/update.json";
   private static final long MIN_JAR_BYTES = 1000000L;
   private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8L)).build();
   private final BooleanSetting toastSetting;
   private final BooleanSetting autoInstall;
   private final ButtonBase checkNowButton;
   private volatile boolean latestVersionLoaded = false;
   private volatile String latestVersion = null;
   private volatile String latestJarUrl = null;
   private volatile boolean latestVersionWasNew = false;
   private volatile boolean latestVersionWasNewNotified = false;
   private volatile boolean latestVersionWasInstalled = false;

   public UpdateChecker() {
      super("UpdateChecker", IconsPngType.MISC);
      this.toastSetting = new BooleanSetting("Toast", "Show a notification when a new version is found", true);
      this.autoInstall = new BooleanSetting("Auto Install", "Download the new version and remove the old jar", true);
      this.checkNowButton = new ButtonBase("Check Now", "Look for a newer Matrix release now", "Check", this::checkForUpdate);
      this.addSettings(new ConfigSetting[]{this.toastSetting, this.autoInstall, this.checkNowButton});
      cleanupStaleUpdateFiles();
   }

   @Override
   protected void onEnable() {
      this.checkForUpdate();
   }

   @Override
   public void onConfigLoaded() {
      this.checkForUpdate();
   }

   public void checkForUpdate() {
      Thread checkThread = new Thread(this::runCheck, "Matrix-UpdateChecker");
      checkThread.setDaemon(true);
      checkThread.start();
   }

   private void runCheck() {
      try {
         HttpRequest request = HttpRequest.newBuilder(URI.create(UPDATE_URL))
            .timeout(Duration.ofSeconds(8L))
            .header("Cache-Control", "no-cache")
            .GET()
            .build();
         HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
         if (response.statusCode() == 200) {
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            String version = json.has("version") ? json.get("version").getAsString() : "";
            String jarUrl = json.has("jar") ? json.get("jar").getAsString() : "";
            if (!version.isBlank()) {
               this.latestVersion = version;
               this.latestJarUrl = jarUrl;
               this.latestVersionWasNew = !version.equals(getCurrentVersion());
               this.latestVersionLoaded = true;
               if (this.latestVersionWasNew && this.latestVersionWasNewNotified) {
                  this.latestVersionWasNewNotified = false;
               }

               if (this.latestVersionWasNew && !this.latestVersionWasInstalled && this.autoInstall.isEnabled() && !jarUrl.isBlank()) {
                  this.installUpdate(jarUrl, version);
               }
            }
         }
      } catch (Exception e) {
         System.err.println("[UpdateChecker] " + e.getMessage());
      }
   }

   private void installUpdate(String jarUrl, String version) {
      Path tempFile = null;

      try {
         Path modsDir = FabricLoader.getInstance().getGameDir().resolve("mods");
         Files.createDirectories(modsDir);
         Path target = modsDir.resolve("Matrix-" + sanitizeVersion(version) + ".jar");
         tempFile = modsDir.resolve("Matrix-" + sanitizeVersion(version) + ".jar.update");
         HttpRequest request = HttpRequest.newBuilder(URI.create(jarUrl))
            .timeout(Duration.ofSeconds(120L))
            .header("Cache-Control", "no-cache")
            .GET()
            .build();
         HttpResponse<Path> response = HTTP_CLIENT
            .send(
               request,
               HttpResponse.BodyHandlers
                  .ofFile(tempFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)
            );
         if (response.statusCode() != 200 || !Files.isRegularFile(tempFile) || Files.size(tempFile) < MIN_JAR_BYTES || !hasZipMagic(tempFile)) {
            Files.deleteIfExists(tempFile);
            this.handleFailure("Update download failed (HTTP " + response.statusCode() + ")");
            return;
         }

         if (target.equals(currentModOrigin())) {
            Files.deleteIfExists(tempFile);
            this.latestVersionWasInstalled = true;
            this.latestVersionWasNewNotified = true;
            if (this.toastSetting.isEnabled()) {
               DisabledEnabled.getInstance()
                  .handleDisabledEnabled("Update Installed", "Matrix " + version + " is ready \u2014 restart the game", true);
            }
         } else if (this.removeOldModJar()) {
            Files.move(tempFile, target, StandardCopyOption.REPLACE_EXISTING);
            this.latestVersionWasInstalled = true;
            this.latestVersionWasNewNotified = true;
            if (this.toastSetting.isEnabled()) {
               DisabledEnabled.getInstance()
                  .handleDisabledEnabled("Update Installed", "Matrix " + version + " is ready \u2014 restart the game", true);
            }
         } else {
            Files.deleteIfExists(tempFile);
            this.handleFailure("Close the game, then it will update on the next launch");
         }
      } catch (Exception e) {
         System.err.println("[UpdateChecker] " + e.getMessage());
         if (tempFile != null) {
            try {
               Files.deleteIfExists(tempFile);
            } catch (Exception ignore) {
            }
         }

         this.handleFailure("Update could not be installed");
      }
   }

   private boolean removeOldModJar() {
      Path oldJar = currentModOrigin();
      if (oldJar == null || !Files.isRegularFile(oldJar) || oldJar.getFileName() == null || !oldJar.getFileName().toString().endsWith(".jar")) {
         return true;
      }

      try {
         Files.deleteIfExists(oldJar);
         return true;
      } catch (Exception deleteFailed) {
         try {
            Files.move(oldJar, oldJar.resolveSibling(oldJar.getFileName() + ".matrixold"), StandardCopyOption.REPLACE_EXISTING);
            return true;
         } catch (Exception renameFailed) {
            System.err.println("[UpdateChecker] old jar " + oldJar.getFileName() + " is still in use, update will retry after restart");
            return false;
         }
      }
   }

   private static Path currentModOrigin() {
      try {
         return FabricLoader.getInstance().getModContainer("matrixclient").map(mod -> (Path)mod.getOrigin()).orElse(null);
      } catch (Throwable e) {
         return null;
      }
   }

   private static boolean hasZipMagic(Path path) {
      try {
         byte[] head = new byte[2];
         try (InputStream open = Files.newInputStream(path)) {
            if (open.read(head) < 2) {
               return false;
            }
         }

         return head[0] == 80 && head[1] == 75;
      } catch (Exception e) {
         return false;
      }
   }

   private static void cleanupStaleUpdateFiles() {
      try {
         Path modsDir = FabricLoader.getInstance().getGameDir().resolve("mods");
         if (!Files.isDirectory(modsDir)) {
            return;
         }

         try (DirectoryStream<Path> stream = Files.newDirectoryStream(modsDir, "*.{matrixold,update}")) {
            for (Path staleFile : stream) {
               try {
                  Files.deleteIfExists(staleFile);
               } catch (Exception ignore) {
               }
            }
         }
      } catch (Throwable e) {
      }
   }

   private void handleFailure(String toastText) {
      if (this.toastSetting.isEnabled()) {
         DisabledEnabled.getInstance().handleDisabledEnabled("Updater", toastText, false);
      }
   }

   private static String sanitizeVersion(String version) {
      return version == null ? "new" : version.replaceAll("[^A-Za-z0-9._-]", "_");
   }

   public static String getCurrentVersion() {
      try {
         return FabricLoader.getInstance()
            .getModContainer("matrixclient")
            .map(mod -> mod.getMetadata().getVersion().getFriendlyString())
            .orElse("?");
      } catch (Throwable e) {
         return "?";
      }
   }

   public boolean isUpdateAvailable() {
      return this.latestVersionLoaded && this.latestVersionWasNew;
   }

   public String getLatestVersion() {
      return this.latestVersion;
   }

   public void notifyIfUpdateAvailable() {
      if (
         this.isEnabled()
            && this.toastSetting.isEnabled()
            && this.isUpdateAvailable()
            && !this.latestVersionWasNewNotified
            && !this.latestVersionWasInstalled
      ) {
         this.latestVersionWasNewNotified = true;
         DisabledEnabled.getInstance()
            .handleDisabledEnabled("Update Available", "Matrix " + this.latestVersion + " is out \u2014 you are on " + getCurrentVersion(), true);
      }
   }
}
