package com.matrix.client.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.hud.debug.LookingAtBlockDebugHudEntry;
import net.minecraft.client.gui.hud.debug.LookingAtFluidDebugHudEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.matrix.client.categories.misc.FakeCoordinates;

@Mixin({LookingAtBlockDebugHudEntry.class, LookingAtFluidDebugHudEntry.class})
public abstract class MixinLookingAtDebugHudEntryFakeCoords {
   @ModifyExpressionValue(
      method = {"method_72751(Lnet/minecraft/class_11630;Lnet/minecraft/class_1937;Lnet/minecraft/class_2818;Lnet/minecraft/class_2818;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_2338;method_10263()I"
      )}
   )
   private int matrix$fakeX(int matrixfakeXValue) {
      return FakeCoordinates.getInstance(matrixfakeXValue);
   }

   @ModifyExpressionValue(
      method = {"method_72751(Lnet/minecraft/class_11630;Lnet/minecraft/class_1937;Lnet/minecraft/class_2818;Lnet/minecraft/class_2818;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_2338;method_10260()I"
      )}
   )
   private int matrix$fakeZ(int matrixfakeZValue) {
      return FakeCoordinates.getInstanceForInt(matrixfakeZValue);
   }
}
