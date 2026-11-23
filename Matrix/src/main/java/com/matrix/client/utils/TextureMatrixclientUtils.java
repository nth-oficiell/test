package com.matrix.client.utils;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import com.matrix.client.utils.WarmBrainrotBase;
import com.matrix.client.utils.MatrixclientWarm;

public final class TextureMatrixclientUtils {
   private static final Map<String, Identifier> CORZCLIENT_MAP = new HashMap<>();

   public static Identifier getMatrixclientTexture(WarmBrainrotBase warmBrainrotBase, MatrixclientWarm matrixclientWarm) {
      Identifier matrixclient_map = CORZCLIENT_MAP.get(warmBrainrotBase.formatTungDin());
      if (matrixclient_map != null) {
         return matrixclient_map;
      } else {
         Identifier matrixclientTexture = Identifier.of("matrixclient", "brainrot/" + warmBrainrotBase.formatTungDin());
         NativeImage nativeImage = new NativeImage(matrixclientWarm.matrixclientValue, matrixclientWarm.matrixclientTextureValue, false);
         int[] matrixclientValues = matrixclientWarm.matrixclientValues;

         for (int i = 0; i < matrixclientWarm.matrixclientTextureValue; i++) {
            for (int j = 0; j < matrixclientWarm.matrixclientValue; j++) {
               nativeImage.setColorArgb(j, i, matrixclientValues[i * matrixclientWarm.matrixclientValue + j]);
            }
         }

         String matrixclientTextureText = "matrix_brainrot_" + warmBrainrotBase.formatTungDin();
         MinecraftClient.getInstance().getTextureManager().registerTexture(matrixclientTexture, new NativeImageBackedTexture(() -> matrixclientTextureText, nativeImage));
         CORZCLIENT_MAP.put(warmBrainrotBase.formatTungDin(), matrixclientTexture);
         return matrixclientTexture;
      }
   }

   private TextureMatrixclientUtils() {
   }
}
