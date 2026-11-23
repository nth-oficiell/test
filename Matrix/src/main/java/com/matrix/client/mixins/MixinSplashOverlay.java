package com.matrix.client.mixins;

import com.matrix.client.config.MenuBackgrounds;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({SplashOverlay.class})
public abstract class MixinSplashOverlay {
   @Redirect(
      method = {"render"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"
      )
   )
   private void matrix$matrixLoadingBackground(DrawContext instance, int x, int y, int width, int height, int color) {
      MenuBackgrounds.draw(instance, (color >>> 24) / 255.0F);
   }
}
