package com.matrix.client.mixins;

import com.matrix.client.gui.ScreenGuiApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({Window.class})
public class MixinWindow {
   @ModifyVariable(
      method = {"method_4476(IZ)I"},
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private int matrix$forceMatrixGuiScale(int matrixforceMatrixGuiScale) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc != null && mc.currentScreen instanceof ScreenGuiApi) {
         Window self = (Window)((net.minecraft.client.util.Window) (Object) (this));
         return ScreenGuiApi.getValue(self.getFramebufferWidth(), self.getFramebufferHeight());
      } else {
         return matrixforceMatrixGuiScale;
      }
   }
}
