package com.vortex.client.mixins;

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
import com.vortex.client.categories.visuals.CustomWorld;

@Mixin({FogRenderer.class})
public class MixinFogRendererCustomWorld {
   @Unique
   private static Vector4f vortex$color(int vortexcolorValue) {
      return new Vector4f((vortexcolorValue >> 16 & 0xFF) / 255.0F, (vortexcolorValue >> 8 & 0xFF) / 255.0F, (vortexcolorValue & 0xFF) / 255.0F, (vortexcolorValue >>> 24 & 0xFF) / 255.0F);
   }

   @Inject(
      method = {"method_62185(Lnet/minecraft/class_4184;FLnet/minecraft/class_638;IF)Lorg/joml/Vector4f;"},
      at = {@At("TAIL")},
      cancellable = true
   )
   private void vortex$fogColor(Camera camera, float vortexfogColorValue, ClientWorld clientWorld, int vortexfogColorValue2, float vortexfogColorValue3, CallbackInfoReturnable<Vector4f> callbackInfoReturnable) {
      CustomWorld cw = CustomWorld.getCustomWorld();
      if (cw != null && cw.isBase()) {
         callbackInfoReturnable.setReturnValue(vortex$color(cw.getFogColor()));
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
   private void vortex$worldFog(
      FogRenderer fogRenderer,
      ByteBuffer byteBuffer,
      int vortexworldFogValue,
      Vector4f vector4f,
      float vortexworldFogValue2,
      float vortexworldFogValue3,
      float vortexworldFogValue4,
      float vortexworldFogValue5,
      float vortexworldFogValue6,
      float vortexworldFogValue7,
      Operation<Void> operation
   ) {
      CustomWorld cw = CustomWorld.getCustomWorld();
      if (cw != null && cw.isBase()) {
         float end = cw.getFogEnd();
         float start = Math.min(cw.getAmount(), end);
         operation.call(new Object[]{fogRenderer, byteBuffer, vortexworldFogValue, vortex$color(cw.getFogColor()), start, end, start, end, end, end});
      } else {
         operation.call(
            new Object[]{fogRenderer, byteBuffer, vortexworldFogValue, vector4f, vortexworldFogValue2, vortexworldFogValue3, vortexworldFogValue4, vortexworldFogValue5, vortexworldFogValue6, vortexworldFogValue7}
         );
      }
   }
}
