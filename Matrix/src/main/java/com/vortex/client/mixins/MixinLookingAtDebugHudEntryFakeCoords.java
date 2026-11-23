package com.vortex.client.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.hud.debug.LookingAtBlockDebugHudEntry;
import net.minecraft.client.gui.hud.debug.LookingAtFluidDebugHudEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.vortex.client.categories.misc.FakeCoordinates;

@Mixin({LookingAtBlockDebugHudEntry.class, LookingAtFluidDebugHudEntry.class})
public abstract class MixinLookingAtDebugHudEntryFakeCoords {
   @ModifyExpressionValue(
      method = {"method_72751(Lnet/minecraft/class_11630;Lnet/minecraft/class_1937;Lnet/minecraft/class_2818;Lnet/minecraft/class_2818;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_2338;method_10263()I"
      )}
   )
   private int vortex$fakeX(int vortexfakeXValue) {
      return FakeCoordinates.getInstance(vortexfakeXValue);
   }

   @ModifyExpressionValue(
      method = {"method_72751(Lnet/minecraft/class_11630;Lnet/minecraft/class_1937;Lnet/minecraft/class_2818;Lnet/minecraft/class_2818;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_2338;method_10260()I"
      )}
   )
   private int vortex$fakeZ(int vortexfakeZValue) {
      return FakeCoordinates.getInstanceForInt(vortexfakeZValue);
   }
}
