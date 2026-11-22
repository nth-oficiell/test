package com.matrix.client.mixins;

import com.matrix.client.gui.Button;
import com.matrix.client.gui.ZoomHud;
import com.matrix.client.render.ClampMatrixUtils;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ai.auth.ModuleRegistry;
import com.matrix.client.utils.PlayerScaffold;

@Mixin({Mouse.class})
public class MixinMouse {
   @Shadow
   private double x;
   @Shadow
   private double y;
   @Shadow
   private double cursorDeltaX;
   @Shadow
   private double cursorDeltaY;
   @Unique
   private double matrix$cursorDeltaXBefore;
   @Unique
   private double matrix$cursorDeltaYBefore;

   @Inject(
      method = {"method_1600(JDD)V"},
      at = {@At("HEAD")}
   )
   private void onCursorPos(long cursorPosValue, double cursorPosValue2, double cursorPosValue3, CallbackInfo callbackInfo) {
      this.matrix$cursorDeltaXBefore = this.cursorDeltaX;
      this.matrix$cursorDeltaYBefore = this.cursorDeltaY;
   }

   @Inject(
      method = {"method_1600(JDD)V"},
      at = {@At("RETURN")}
   )
   private void matrixAfterCursorPos(long matrixAfterValue, double matrixAfterValue2, double matrixAfterValue3, CallbackInfo callbackInfo) {
      double deltaX = this.cursorDeltaX - this.matrix$cursorDeltaXBefore;
      double deltaY = this.cursorDeltaY - this.matrix$cursorDeltaYBefore;
      if (deltaX != 0.0 || deltaY != 0.0) {
         PlayerScaffold event = new PlayerScaffold(deltaX, deltaY);
         ModuleRegistry.dispatchPlayerScaffold(event);
      }
   }

   @Inject(
      method = {"method_1598(JDD)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onMouseScroll(long mouseScrollValue, double mouseScrollValue2, double mouseScrollValue3, CallbackInfo callbackInfo) {
      ZoomHud event = new ZoomHud(mouseScrollValue3);
      ModuleRegistry.dispatchZoomHud(event);
      if (event.isFlag()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"method_1601(JLnet/minecraft/class_11910;I)V"},
      at = {@At("HEAD")}
   )
   private void onMouseButton(long mouseButtonValue, MouseInput mouseInput, int mouseButtonValue2, CallbackInfo callbackInfo) {
      Button event = new Button(mouseInput.button(), mouseButtonValue2);
      ModuleRegistry.dispatchButtonClick(event);
   }

   @ModifyReturnValue(
      method = {"method_68879(Lnet/minecraft/class_1041;)D"},
      at = {@At("RETURN")}
   )
   private double matrix$gui3dScaledX(double matrixgui3dScaledValue, Window window) {
      return ClampMatrixUtils.getAmountFlag(matrixgui3dScaledValue, this.y, window);
   }

   @ModifyReturnValue(
      method = {"method_68883(Lnet/minecraft/class_1041;)D"},
      at = {@At("RETURN")}
   )
   private double matrix$gui3dScaledY(double matrixgui3dScaledValue, Window window) {
      return ClampMatrixUtils.getAmountFlagForDoubleWindow(this.x, matrixgui3dScaledValue, window);
   }
}
