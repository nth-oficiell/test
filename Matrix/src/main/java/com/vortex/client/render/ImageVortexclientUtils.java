package com.vortex.client.render;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public final class ImageVortexclientUtils {
   private static final String CORZCLIENT = "vortexclient";
   private static final Map<Identifier, NativeImage> IMAGE_CACHE = new HashMap<>();
   private static final Set<Identifier> REGISTERED_TEXTURE_IDS = new HashSet<>();
   private static final LinkedHashMap<String, Identifier> CORZCLIENT_MAP = new LinkedHashMap<>(64, 0.75F, true);

   private static NativeImage getRead(Identifier id) {
      if (id.getPath().startsWith("gen/")) {
         return MinecraftClient.getInstance().getTextureManager().getTexture(id) instanceof NativeImageBackedTexture nativeImageBackedTexture ? nativeImageBackedTexture.getImage() : null;
      } else {
         NativeImage read = IMAGE_CACHE.get(id);
         if (read != null) {
            return read;
         } else {
            try {
               NativeImage botCrystalValue2;
               try (InputStream open = MinecraftClient.getInstance().getResourceManager().open(id)) {
                  NativeImage botCrystalValue = NativeImage.read(open);
                  IMAGE_CACHE.put(id, botCrystalValue);
                  botCrystalValue2 = botCrystalValue;
               }

               return botCrystalValue2;
            } catch (Throwable e) {
               REGISTERED_TEXTURE_IDS.add(id);
               return null;
            }
         }
      }
   }

   private static void updateVortexclientTexture() {
      if (CORZCLIENT_MAP.size() > 160) {
         Iterator iterator = CORZCLIENT_MAP.entrySet().iterator();

         while (CORZCLIENT_MAP.size() > 160 && iterator.hasNext()) {
            Identifier identifier = (Identifier)((Entry)iterator.next()).getValue();
            iterator.remove();
            MinecraftClient.getInstance().getTextureManager().destroyTexture(identifier);
         }
      }
   }

   private static int getMin(double imageVortexclientValue) {
      int imageVortexclientBound = (int)Math.round(imageVortexclientValue);
      return imageVortexclientBound < 0 ? 0 : Math.min(255, imageVortexclientBound);
   }

   private static NativeImage getArgbColor(NativeImage nativeImage, int argbColorDivisor, int round) {
      int width = nativeImage.getWidth();
      int height = nativeImage.getHeight();
      double argbColorFactor = (double)width / argbColorDivisor;
      double argbColorFactor2 = (double)height / round;
      NativeImage nativeImage2 = new NativeImage(argbColorDivisor, round, false);

      for (int i = 0; i < round; i++) {
         double argbColorBound = i * argbColorFactor2;
         double argbColorBound2 = (i + 1) * argbColorFactor2;
         int argbColorValue = (int)Math.floor(argbColorBound);
         int argbColorLimit = Math.min(height, (int)Math.ceil(argbColorBound2));

         for (int j = 0; j < argbColorDivisor; j++) {
            double hashRegistryValue = j * argbColorFactor;
            double coverageValue = (j + 1) * argbColorFactor;
            int floor2 = (int)Math.floor(hashRegistryValue);
            int min2 = Math.min(width, (int)Math.ceil(coverageValue));
            double argbColorTotal = 0.0;
            double argbColorDivisor3 = 0.0;
            double argbColorTotal2 = 0.0;
            double argbColorTotal3 = 0.0;
            double argbColorTotal4 = 0.0;

            for (int k = argbColorValue; k < argbColorLimit; k++) {
               double argbColorFactor3 = Math.min(argbColorBound2, (double)(k + 1)) - Math.max(argbColorBound, (double)k);
               if (!(argbColorFactor3 <= 0.0)) {
                  for (int m = floor2; m < min2; m++) {
                     double argbColorFactor4 = Math.min(coverageValue, (double)(m + 1)) - Math.max(hashRegistryValue, (double)m);
                     if (!(argbColorFactor4 <= 0.0)) {
                        double argbColorFactor5 = argbColorFactor4 * argbColorFactor3;
                        int colorArgb = nativeImage.getColorArgb(m, k);
                        double argbColorFactor6 = (colorArgb >>> 24 & 0xFF) / 255.0;
                        argbColorTotal += argbColorFactor5;
                        argbColorDivisor3 += argbColorFactor5 * argbColorFactor6;
                        argbColorTotal2 += argbColorFactor5 * argbColorFactor6 * (colorArgb >> 16 & 0xFF);
                        argbColorTotal3 += argbColorFactor5 * argbColorFactor6 * (colorArgb >> 8 & 0xFF);
                        argbColorTotal4 += argbColorFactor5 * argbColorFactor6 * (colorArgb & 0xFF);
                     }
                  }
               }
            }

            int imageVortexclientValue = argbColorTotal <= 0.0 ? 0 : (int)Math.round(argbColorDivisor3 / argbColorTotal * 255.0);
            int min4 = 255;
            int min5 = 255;
            int min6 = 255;
            if (argbColorDivisor3 > 1.0E-9) {
               min4 = getMin(argbColorTotal2 / argbColorDivisor3);
               min5 = getMin(argbColorTotal3 / argbColorDivisor3);
               min6 = getMin(argbColorTotal4 / argbColorDivisor3);
            }

            nativeImage2.setColorArgb(j, i, getMin(imageVortexclientValue) << 24 | min4 << 16 | min5 << 8 | min6);
         }
      }

      return nativeImage2;
   }

   private static String formatPathRoot(Identifier id2) {
      String lowerCase = id2.getPath().toLowerCase(Locale.ROOT);
      StringBuilder stringBuilder = new StringBuilder(lowerCase.length());

      for (int i = 0; i < lowerCase.length(); i++) {
         char charAt = lowerCase.charAt(i);
         stringBuilder.append((charAt < 'a' || charAt > 'z') && (charAt < '0' || charAt > '9') && charAt != '_' && charAt != '-' ? '_' : charAt);
      }

      return stringBuilder.toString();
   }

   private static boolean canGenTextures(Identifier id2) {
      if (!"vortexclient".equals(id2.getNamespace())) {
         return false;
      } else {
         String path = id2.getPath();
         return path.startsWith("textures/") || path.startsWith("gen/");
      }
   }

   private ImageVortexclientUtils() {
   }

   public static Identifier getVortexclientTexture(Identifier vortexclientTexture, int vortexclientTextureLimit, int round) {
      if (vortexclientTexture != null && vortexclientTextureLimit > 0 && round > 0 && canGenTextures(vortexclientTexture) && !REGISTERED_TEXTURE_IDS.contains(vortexclientTexture)) {
         NativeImage read = getRead(vortexclientTexture);
         if (read == null) {
            return vortexclientTexture;
         } else {
            int width = read.getWidth();
            int height = read.getHeight();
            if (vortexclientTextureLimit >= width && round >= height) {
               return vortexclientTexture;
            } else if (vortexclientTextureLimit <= width && round <= height) {
               String vortexclientTextureText = vortexclientTexture + "@" + vortexclientTextureLimit + "x" + round;
               Identifier vortexclient_map = CORZCLIENT_MAP.get(vortexclientTextureText);
               if (vortexclient_map != null) {
                  return vortexclient_map;
               } else {
                  try {
                     NativeImage argbColor = getArgbColor(read, vortexclientTextureLimit, round);
                     Identifier finish = Identifier.of("vortexclient", "fit/" + formatPathRoot(vortexclientTexture) + "_" + vortexclientTextureLimit + "x" + round);
                     MinecraftClient.getInstance().getTextureManager().registerTexture(finish, new NativeImageBackedTexture(() -> "vortex_fit_" + vortexclientTextureText, argbColor));
                     CORZCLIENT_MAP.put(vortexclientTextureText, finish);
                     updateVortexclientTexture();
                     return finish;
                  } catch (Throwable e) {
                     REGISTERED_TEXTURE_IDS.add(vortexclientTexture);
                     return vortexclientTexture;
                  }
               }
            } else {
               return vortexclientTexture;
            }
         }
      } else {
         return vortexclientTexture;
      }
   }
}
