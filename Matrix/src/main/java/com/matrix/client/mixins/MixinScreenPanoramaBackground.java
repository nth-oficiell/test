package com.matrix.client.mixins;

import com.matrix.client.config.MenuBackgrounds;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Screen.class})
public abstract class MixinScreenPanoramaBackground {
   @Inject(
      method = {"renderPanoramaBackground"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void matrix$matrixMenuBackground(DrawContext context, float delta, CallbackInfo callbackInfo) {
      if (MenuBackgrounds.canReplace()) {
         MenuBackgrounds.draw(context, 1.0F);
         callbackInfo.cancel();
      }
   }
}
