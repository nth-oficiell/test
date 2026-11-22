package com.matrix.client.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.nio.ByteBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.matrix.client.categories.visuals.CustomWorld;

@Mixin({FogRenderer.class})
public class MixinFogRendererCustomWorld {
   @Unique
   private static Vector4f matrix$color(int matrixcolorValue) {
      return new Vector4f((matrixcolorValue >> 16 & 0xFF) / 255.0F, (matrixcolorValue >> 8 & 0xFF) / 255.0F, (matrixcolorValue & 0xFF) / 255.0F, (matrixcolorValue >>> 24 & 0xFF) / 255.0F);
   }

   @Inject(
      method = {"method_62185(Lnet/minecraft/class_4184;FLnet/minecraft/class_638;IF)Lorg/joml/Vector4f;"},
      at = {@At("TAIL")},
      cancellable = true
   )
   private void matrix$fogColor(Camera camera, float matrixfogColorValue, ClientWorld clientWorld, int matrixfogColorValue2, float matrixfogColorValue3, CallbackInfoReturnable<Vector4f> callbackInfoReturnable) {
      CustomWorld cw = CustomWorld.getCustomWorld();
      if (cw != null && cw.isBase()) {
         callbackInfoReturnable.setReturnValue(matrix$color(cw.getFogColor()));
      }
   }

   @WrapOperation(
      method = {"method_3211(Lnet/minecraft/class_4184;ILnet/minecraft/class_9779;FLnet/minecraft/class_638;)Lorg/joml/Vector4f;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_758;method_71110(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"
      )},
      require = 0
   )
   private void matrix$worldFog(
      FogRenderer fogRenderer,
      ByteBuffer byteBuffer,
      int matrixworldFogValue,
      Vector4f vector4f,
      float matrixworldFogValue2,
      float matrixworldFogValue3,
      float matrixworldFogValue4,
      float matrixworldFogValue5,
      float matrixworldFogValue6,
      float matrixworldFogValue7,
      Operation<Void> operation
   ) {
      CustomWorld cw = CustomWorld.getCustomWorld();
      if (cw != null && cw.isBase()) {
         float end = cw.getFogEnd();
         float start = Math.min(cw.getAmount(), end);
         operation.call(new Object[]{fogRenderer, byteBuffer, matrixworldFogValue, matrix$color(cw.getFogColor()), start, end, start, end, end, end});
      } else {
         operation.call(
            new Object[]{fogRenderer, byteBuffer, matrixworldFogValue, vector4f, matrixworldFogValue2, matrixworldFogValue3, matrixworldFogValue4, matrixworldFogValue5, matrixworldFogValue6, matrixworldFogValue7}
         );
      }
   }
}
