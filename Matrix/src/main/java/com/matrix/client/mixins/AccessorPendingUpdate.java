package com.matrix.client.mixins;

import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(
   targets = {"net/minecraft/class_7202$class_7203"}
)
public interface AccessorPendingUpdate {
   @Accessor("field_37958")
   BlockState matrixServerState();

   @Accessor("field_37957")
   int matrixSequence();
}
