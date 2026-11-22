package com.vortex.client.mixins;

import net.minecraft.client.render.SkyRendering;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.vortex.client.categories.visuals.CustomWorld;

@Mixin({SkyRendering.class})
public class MixinSkyRenderingCustomWorld {
   @Inject(
      method = {"method_62310(FLnet/minecraft/class_4587;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void vortex$hideStars(CallbackInfo callbackInfo) {
      CustomWorld cw = CustomWorld.getCustomWorld();
      if (cw != null && cw.isSkyCustom()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"method_62307(Lnet/minecraft/class_4587;FFFLnet/minecraft/class_12131;FF)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void vortex$hideCelestial(CallbackInfo callbackInfo) {
      CustomWorld cw = CustomWorld.getCustomWorld();
      if (cw != null && cw.isSkyCustom()) {
         callbackInfo.cancel();
      }
   }
}
