package com.matrix.client.mixins;

import net.minecraft.client.option.InactivityFpsLimiter;
import net.minecraft.client.option.InactivityFpsLimiter.LimitReason;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.matrix.client.utils.CrystalPvpTickController;
import com.matrix.client.utils.SwordPlayer;

@Mixin({InactivityFpsLimiter.class})
public class MixinInactivityFpsLimiter {
   @Inject(
      method = {"method_66514()Lnet/minecraft/class_9919$class_10601;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void matrixBotKeepsFrames(CallbackInfoReturnable<LimitReason> callbackInfoReturnable) {
      if (CrystalPvpTickController.isGloballyEngaged()) {
         callbackInfoReturnable.setReturnValue(LimitReason.NONE);
      } else if (SwordPlayer.isFightActive()) {
         callbackInfoReturnable.setReturnValue(LimitReason.NONE);
      }
   }
}
