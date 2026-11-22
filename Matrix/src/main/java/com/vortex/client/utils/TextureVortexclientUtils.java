package com.vortex.client.utils;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import com.vortex.client.utils.WarmBrainrotBase;
import com.vortex.client.utils.VortexclientWarm;

public final class TextureVortexclientUtils {
   private static final Map<String, Identifier> CORZCLIENT_MAP = new HashMap<>();

   public static Identifier getVortexclientTexture(WarmBrainrotBase warmBrainrotBase, VortexclientWarm vortexclientWarm) {
      Identifier vortexclient_map = CORZCLIENT_MAP.get(warmBrainrotBase.formatTungDin());
      if (vortexclient_map != null) {
         return vortexclient_map;
      } else {
         Identifier vortexclientTexture = Identifier.of("vortexclient", "brainrot/" + warmBrainrotBase.formatTungDin());
         NativeImage nativeImage = new NativeImage(vortexclientWarm.vortexclientValue, vortexclientWarm.vortexclientTextureValue, false);
         int[] vortexclientValues = vortexclientWarm.vortexclientValues;

         for (int i = 0; i < vortexclientWarm.vortexclientTextureValue; i++) {
            for (int j = 0; j < vortexclientWarm.vortexclientValue; j++) {
               nativeImage.setColorArgb(j, i, vortexclientValues[i * vortexclientWarm.vortexclientValue + j]);
            }
         }

         String vortexclientTextureText = "vortex_brainrot_" + warmBrainrotBase.formatTungDin();
         MinecraftClient.getInstance().getTextureManager().registerTexture(vortexclientTexture, new NativeImageBackedTexture(() -> vortexclientTextureText, nativeImage));
         CORZCLIENT_MAP.put(warmBrainrotBase.formatTungDin(), vortexclientTexture);
         return vortexclientTexture;
      }
   }

   private TextureVortexclientUtils() {
   }
}
