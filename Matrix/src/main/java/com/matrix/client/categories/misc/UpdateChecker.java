package com.matrix.client.categories.misc;

import ai.auth.Module;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matrix.client.config.BooleanSetting;
import com.matrix.client.config.ConfigSetting;
import com.matrix.client.gui.ButtonBase;
import com.matrix.client.gui.DisabledEnabled;
import com.matrix.client.utils.IconsPngType;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import net.fabricmc.loader.api.FabricLoader;

public class UpdateChecker extends Module {
   private static final String UPDATE_URL = "https://raw.githubusercontent.com/nth-oficiell/test/refs/heads/vibe/vortex-394c92/Matrix/update.json";
   private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8L)).build();
   private final BooleanSetting toastSetting;
   private final ButtonBase checkNowButton;
   private volatile boolean latestVersionLoaded = false;
   private volatile String latestVersion = null;
   private volatile boolean latestVersionWasNew = false;
   private volatile boolean latestVersionWasNewNotified = false;

   public UpdateChecker() {
      super("UpdateChecker", IconsPngType.MISC);
      this.toastSetting = new BooleanSetting("Toast", "Show a notification when a new version is found", true);
      this.checkNowButton = new ButtonBase("Check Now", "Look for a newer Matrix release now", "Check", this::checkForUpdate);
      this.addSettings(new ConfigSetting[]{this.toastSetting, this.checkNowButton});
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
            if (!version.isBlank()) {
               this.latestVersion = version;
               this.latestVersionWasNew = !version.equals(getCurrentVersion());
               this.latestVersionLoaded = true;
               if (this.latestVersionWasNew && this.latestVersionWasNewNotified) {
                  this.latestVersionWasNewNotified = false;
               }
            }
         }
      } catch (Exception e) {
         System.err.println("[UpdateChecker] " + e.getMessage());
      }
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
      if (this.isEnabled() && this.toastSetting.isEnabled() && this.isUpdateAvailable() && !this.latestVersionWasNewNotified) {
         this.latestVersionWasNewNotified = true;
         DisabledEnabled.getInstance()
            .handleDisabledEnabled("Update Available", "Matrix " + this.latestVersion + " is out \u2014 you are on " + getCurrentVersion(), true);
      }
   }
}
