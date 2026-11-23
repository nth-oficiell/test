package com.matrix.client.mixins;

import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.matrix.client.categories.visuals.CustomWorld;

@Mixin({GameOptions.class})
public class MixinGameOptionsViewDistance {
   @Inject(
      method = {"method_38521()I"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void matrix$clampViewDistance(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
      CustomWorld cw = CustomWorld.getCustomWorld();
      if (cw != null && cw.isBase()) {
         int original = callbackInfoReturnable.getReturnValueI();
         int chunks = Math.max(2, (int)(cw.getFogEnd() / 16.0F));
         callbackInfoReturnable.setReturnValue(Math.min(chunks, original));
      }
   }
}
