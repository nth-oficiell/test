package com.matrix.client.categories.visuals;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.matrix.client.config.BooleanSetting;
import com.matrix.client.config.ConfigSetting;
import com.matrix.client.config.EnumSetting;
import com.matrix.client.config.ModuleConfigAutosave;
import com.matrix.client.config.StringBase;
import com.matrix.client.gui.ButtonBase;
import com.matrix.client.gui.DisabledEnabled;
import com.matrix.client.config.PlayerSkinResolver;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.matrix.client.categories.misc.FakeCoordinates;
import com.matrix.client.utils.IconsPngType;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.text.Text;
import net.minecraft.util.AssetInfo.TextureAsset;
import net.minecraft.util.Identifier;

public class SkinChanger extends Module {
   private static final String CONFIG_FOLDER = "corzclient";
   private static final String SKINS_FOLDER = "skins";
   private static volatile SkinTextures importedSkin = null;
   private static final AtomicBoolean LOADING = new AtomicBoolean(false);
   private final StringBase skinFileSetting = new StringBase("Skin File", "PNG file inside .minecraft/config/corzclient/skins (set automatically on import)", "skin.png");
   private final EnumSetting modelSetting = new EnumSetting("Model", "Player model used with the imported skin", "Auto", "Auto", "Wide", "Slim");
   private final BooleanSetting selfSetting = new BooleanSetting("Apply To Self", "Wear the imported skin on your own player", true);
   private final BooleanSetting othersSetting = new BooleanSetting("Apply To Others", "Render the imported skin on other players too", false);
   private final ButtonBase importButton = new ButtonBase("Import Skin", "Pick a skin PNG file from your computer", "Import", this::importFromFile);
   private final ButtonBase reloadButton = new ButtonBase("Reload Skin", "Re-read the selected skin file from the skins folder", "Reload", this::reloadFromFile);

   public SkinChanger() {
      super("SkinChanger", IconsPngType.VISUAL);
      this.addSettings(new ConfigSetting[]{this.skinFileSetting, this.modelSetting, this.selfSetting, this.othersSetting, this.importButton, this.reloadButton});
   }

   public static SkinTextures getOverride(AbstractClientPlayerEntity abstractClientPlayerEntity) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance == null) {
         return null;
      }

      if (!(ModuleRegistry.getModule(SkinChanger.class) instanceof SkinChanger skinChanger) || !skinChanger.isEnabled() || importedSkin == null) {
         return null;
      }

      boolean isSelf = instance.player == abstractClientPlayerEntity;
      if (isSelf && skinChanger.selfSetting.isEnabled()) {
         return importedSkin;
      }

      if (!isSelf && skinChanger.othersSetting.isEnabled()) {
         return importedSkin;
      }

      return null;
   }

   @Override
   protected void onEnable() {
      this.reloadFromFile();
   }

   private void importFromFile() {
      if (!LOADING.compareAndSet(false, true)) {
         return;
      }

      Thread thread = new Thread(() -> {
         try {
            FileDialog fileDialog = new FileDialog((Frame)null, "Import skin PNG", 0);
            fileDialog.setFile("*.png");
            fileDialog.setVisible(true);
            String directory = fileDialog.getDirectory();
            String file = fileDialog.getFile();
            if (directory == null || file == null) {
               return;
            }

            Path sourcePath = Path.of(directory, file);
            byte[] skinBytes = Files.readAllBytes(sourcePath);
            String skinName = sanitizeSkinName(file);
            Path skinFolder = getSkinFolder();
            Files.createDirectories(skinFolder);
            Files.write(skinFolder.resolve(skinName), skinBytes);
            this.skinFileSetting.handleText(skinName);
            ModuleConfigAutosave.updateFlag2();
            this.loadSkinBytes(skinBytes, skinName);
         } catch (Throwable error) {
            announce("Import Failed", "Not a valid skin PNG", false);
         } finally {
            LOADING.set(false);
         }
      }, "matrix-skin-import");
      thread.setDaemon(true);
      thread.start();
   }

   private void reloadFromFile() {
      String skinName = this.skinFileSetting.getCurrentText();
      if (skinName == null || skinName.isBlank()) {
         return;
      }

      if (!LOADING.compareAndSet(false, true)) {
         return;
      }

      Thread thread = new Thread(() -> {
         try {
            Path skinPath = getSkinFolder().resolve(skinName).normalize();
            if (!skinPath.startsWith(getSkinFolder()) || !Files.isRegularFile(skinPath)) {
               return;
            }

            this.loadSkinBytes(Files.readAllBytes(skinPath), skinName);
         } catch (Throwable error) {
            announce("Skin Changer", "Could not read " + skinName, false);
         } finally {
            LOADING.set(false);
         }
      }, "matrix-skin-reload");
      thread.setDaemon(true);
      thread.start();
   }

   private void loadSkinBytes(byte[] skinBytes, String skinName) {
      try {
         NativeImage skinImage = PlayerSkinResolver.upgradeLegacySkin(NativeImage.read(new ByteArrayInputStream(skinBytes)));
         PlayerSkinType skinModel = this.detectModel(skinImage);
         MinecraftClient instance = MinecraftClient.getInstance();
         if (instance == null) {
            skinImage.close();
            return;
         }

         final NativeImage nativeImage = skinImage;
         final PlayerSkinType nativeModel = skinModel;
         instance.execute(() -> {
            try {
               Identifier textureId = Identifier.of("matrixclient", "skins/imported_" + System.currentTimeMillis());
               instance.getTextureManager().registerTexture(textureId, new NativeImageBackedTexture(() -> "matrix_imported_skin", nativeImage));
               importedSkin = SkinTextures.create(new SkinChanger.ImportedAsset(textureId), null, null, nativeModel);
               announce("Skin Applied", skinName, true);
            } catch (Throwable error) {
               nativeImage.close();
               announce("Skin Failed", "Texture upload error", false);
            }
         });
      } catch (Throwable error) {
         announce("Import Failed", "Not a valid skin PNG", false);
      }
   }

   private PlayerSkinType detectModel(NativeImage skinImage) {
      String modelText = this.modelSetting.getCurrentOption();
      if ("Slim".equalsIgnoreCase(modelText)) {
         return PlayerSkinType.SLIM;
      }

      if ("Wide".equalsIgnoreCase(modelText)) {
         return PlayerSkinType.WIDE;
      }

      for (int y = 16; y <= 20; y++) {
         for (int x = 50; x <= 53; x++) {
            if (x < skinImage.getWidth() && y < skinImage.getHeight() && skinImage.getColorArgb(x, y) >>> 24 >= 128) {
               return PlayerSkinType.WIDE;
            }
         }
      }

      return PlayerSkinType.SLIM;
   }

   private static Path getSkinFolder() {
      MinecraftClient instance = MinecraftClient.getInstance();
      Path runDirectory = instance.runDirectory.toPath();
      return runDirectory.resolve("config").resolve(CONFIG_FOLDER).resolve(SKINS_FOLDER);
   }

   private static String sanitizeSkinName(String fileText) {
      String baseName = Path.of(fileText).getFileName().toString();
      if (!baseName.toLowerCase().endsWith(".png")) {
         baseName = baseName + ".png";
      }

      return baseName.replaceAll("[^a-zA-Z0-9_.\\- ]", "_").trim();
   }

   private static void announce(String titleText, String subtitleText, boolean goodFlag) {
      try {
         DisabledEnabled.getInstance().handleDisabledEnabled(titleText, subtitleText, goodFlag);
         MinecraftClient instance = MinecraftClient.getInstance();
         if (instance != null && instance.player != null) {
            instance.inGameHud.getChatHud().addMessage(FakeCoordinates.getLiteral(Text.literal("\u00a78[\u00a7dSkin Changer\u00a78] \u00a77" + subtitleText)));
         }
      } catch (Throwable ignored) {
      }
   }

   private record ImportedAsset(Identifier textureId) implements TextureAsset {
      @Override
      public Identifier texturePath() {
         return this.textureId;
      }

      @Override
      public Identifier id() {
         return this.textureId;
      }
   }
}
