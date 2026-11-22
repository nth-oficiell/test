package com.matrix.client.mixins;

import com.matrix.client.render.MatrixWindowUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.matrix.client.categories.misc.NameProtect;

@Mixin({DrawContext.class})
public class MixinDrawContext {
   @Inject(
      method = {"method_44379(IIII)V"},
      at = {@At("HEAD")}
   )
   private void matrix_enableScissor(int matrixEnableValue, int matrixEnableValue2, int matrixEnableValue3, int matrixEnableValue4, CallbackInfo callbackInfo) {
      MatrixWindowUtils.handleDequeWindow(matrixEnableValue, matrixEnableValue2, matrixEnableValue3, matrixEnableValue4);
   }

   @Inject(
      method = {"method_44380()V"},
      at = {@At("HEAD")}
   )
   private void matrix_disableScissor(CallbackInfo callbackInfo) {
      MatrixWindowUtils.updateDeque();
   }

   @ModifyVariable(
      method = {"method_51439(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;IIIZ)V"},
      at = @At("HEAD"),
      argsOnly = true,
      index = 2
   )
   private Text matrix_maskDrawnName(Text displayName) {
      return NameProtect.getDisplayName(displayName);
   }
}
