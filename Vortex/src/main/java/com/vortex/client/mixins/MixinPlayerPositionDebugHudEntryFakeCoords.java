package com.vortex.client.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.hud.debug.PlayerPositionDebugHudEntry;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.vortex.client.categories.misc.FakeCoordinates;

@Mixin({PlayerPositionDebugHudEntry.class})
public abstract class MixinPlayerPositionDebugHudEntryFakeCoords {
   @ModifyExpressionValue(
      method = {"method_72751(Lnet/minecraft/class_11630;Lnet/minecraft/class_1937;Lnet/minecraft/class_2818;Lnet/minecraft/class_2818;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_1297;method_23317()D"
      )}
   )
   private double vortex$fakeX(double vortexfakeXValue) {
      return FakeCoordinates.getX(vortexfakeXValue);
   }

   @ModifyExpressionValue(
      method = {"method_72751(Lnet/minecraft/class_11630;Lnet/minecraft/class_1937;Lnet/minecraft/class_2818;Lnet/minecraft/class_2818;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_1297;method_23321()D"
      )}
   )
   private double vortex$fakeZ(double vortexfakeZValue) {
      return FakeCoordinates.getZ(vortexfakeZValue);
   }

   @ModifyExpressionValue(
      method = {"method_72751(Lnet/minecraft/class_11630;Lnet/minecraft/class_1937;Lnet/minecraft/class_2818;Lnet/minecraft/class_2818;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_1297;method_24515()Lnet/minecraft/class_2338;"
      )}
   )
   private BlockPos vortex$fakeBlock(BlockPos pos2) {
      return FakeCoordinates.getAdd(pos2);
   }
}
