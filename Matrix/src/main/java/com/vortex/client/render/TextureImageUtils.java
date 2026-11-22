package com.vortex.client.render;

import com.vortex.client.gui.MatrixPeekUtilsRender;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import com.vortex.client.utils.AmountPreviewUtils;
import com.vortex.client.utils.GrassDarkUtils;

public final class TextureImageUtils {
   private static final NativeImageBackedTexture[] vortexclientNativeImageBackedTextures = new NativeImageBackedTexture[4];
   public static final int UNUSED_ONE = 1;
   private static final long[] vortexclientTimes = new long[4];
   private static final CompletableFuture<int[]>[] completableFutures = new CompletableFuture[4];
   private static boolean futuresCompletableActive;

   public static void updateFuturesCompletable() {
      if (!futuresCompletableActive) {
         for (int i = 0; i < 4; i++) {
            if (vortexclientNativeImageBackedTextures[i] == null && completableFutures[i] == null) {
               int futuresCompletableValue = i;

               try {
                  completableFutures[i] = CompletableFuture.supplyAsync(() -> getValuesForInt(futuresCompletableValue));
               } catch (Throwable e) {
                  handleFlag(e);
                  return;
               }
            }
         }
      }
   }

   public static void renderBackdropZoom(DrawContext context, int index, float backdropZoomValue, float configsLikeValue, float materialFactor, float backdropZoomDivisor, float vortexLicenseLimit) {
      if (!(materialFactor <= 0.0F) && !(backdropZoomDivisor <= 0.0F)) {
         index = getValue(index);
         int matrixStackValue = GrassDarkUtils.backdropZoomValues[index];
         float vortexclientBacked = getVortexclientBacked(index);
         if (vortexclientBacked < 0.999F) {
            MatrixPeekUtilsRender.renderMatrixPeek(context, backdropZoomValue, configsLikeValue, materialFactor, backdropZoomDivisor, vortexLicenseLimit, matrixStackValue);
         }

         if (!(vortexclientBacked <= 0.001F)) {
            try {
               GpuTextureView glTextureView = vortexclientNativeImageBackedTextures[index].getGlTextureView();
               float grassDarkLimit = materialFactor / backdropZoomDivisor;
               float max7 = GrassDarkUtils.getMax(grassDarkLimit);
               float maxForFloat = GrassDarkUtils.getMaxForFloat(grassDarkLimit);
               float backdropZoomValue9 = (1.0F - max7) / 2.0F;
               float backdropZoomValue10 = (1.0F - maxForFloat) / 2.0F;
               VortexRoundedUtils.handleTextureColor(
                  MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix(),
                  backdropZoomValue,
                  configsLikeValue,
                  backdropZoomValue + materialFactor,
                  configsLikeValue + backdropZoomDivisor,
                  backdropZoomValue9,
                  backdropZoomValue10,
                  backdropZoomValue9 + max7,
                  backdropZoomValue10 + maxForFloat,
                  glTextureView,
                  vortexLicenseLimit,
                  vortexclientBacked
               );
            } catch (Throwable e) {
               handleFlag(e);
            }
         }
      }
   }

   static int[] getValuesForInt(int textureImageValue) {
      short textureImageFactor = 256;
      short textureImageFactor2 = 160;
      int[] ints = new int[textureImageFactor * textureImageFactor2];

      for (int i = 0; i < textureImageFactor2; i++) {
         for (int j = 0; j < textureImageFactor; j++) {
            ints[i * textureImageFactor + j] = GrassDarkUtils.getValue(textureImageValue, j, i, textureImageFactor, textureImageFactor2);
         }
      }

      return ints;
   }

   public static int getBackdropZoom(int backdropZoomValue) {
      return GrassDarkUtils.backdropZoomValues[getValue(backdropZoomValue)];
   }

   private static float getVortexclientBacked(int index) {
      if (futuresCompletableActive) {
         return 0.0F;
      } else if (vortexclientNativeImageBackedTextures[index] == null && !canVortexclientFutures(index)) {
         return 0.0F;
      } else {
         long vortexclientBackedValue = vortexclientTimes[index];
         float millis = (float)(System.nanoTime() - vortexclientBackedValue) / 1000000.0F;
         return AmountPreviewUtils.getAmount6(millis / 220.0F);
      }
   }

   private TextureImageUtils() {
   }

   private static int getValue(int textureImageLimit) {
      return textureImageLimit < 0 ? 0 : (textureImageLimit >= 4 ? 3 : textureImageLimit);
   }

   public static String formatGrassLight(int grassLightValue) {
      return GrassDarkUtils.grassLightTexts[getValue(grassLightValue)];
   }

   private static boolean canVortexclientFutures(int index) {
      CompletableFuture completableFuture = completableFutures[index];
      if (completableFuture == null) {
         updateFuturesCompletable();
         return false;
      } else if (!completableFuture.isDone()) {
         return false;
      } else {
         completableFutures[index] = null;

         try {
            int[] join = (int[])completableFuture.join();
            NativeImage nativeImage = new NativeImage(256, 160, false);

            for (int i = 0; i < join.length; i++) {
               nativeImage.setColorArgb(i % 256, i / 256, join[i]);
            }

            String vortexclientFuturesText = "vortex_preview_backdrop_" + index;
            NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(() -> vortexclientFuturesText, nativeImage);
            Identifier of = Identifier.of("vortexclient", "gen/preview_backdrop_" + index);
            MinecraftClient.getInstance().getTextureManager().registerTexture(of, nativeImageBackedTexture);
            vortexclientNativeImageBackedTextures[index] = nativeImageBackedTexture;
            vortexclientTimes[index] = System.nanoTime();
            return true;
         } catch (Throwable e) {
            handleFlag(e);
            return false;
         }
      }
   }

   private static void handleFlag(Throwable throwable) {
      if (!futuresCompletableActive) {
         futuresCompletableActive = true;
      }
   }
}
