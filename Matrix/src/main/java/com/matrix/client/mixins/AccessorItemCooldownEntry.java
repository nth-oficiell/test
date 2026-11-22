package com.matrix.client.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(
   targets = {"net/minecraft/class_1796$class_1797"}
)
public interface AccessorItemCooldownEntry {
   @Accessor("comp_3083")
   int matrixStartTick();

   @Accessor("comp_3084")
   int matrixEndTick();
}
