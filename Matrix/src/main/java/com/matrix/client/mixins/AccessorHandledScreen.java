package com.matrix.client.mixins;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({HandledScreen.class})
public interface AccessorHandledScreen {
   @Accessor("x")
   int matrixX();

   @Accessor("y")
   int matrixY();

   @Accessor("backgroundWidth")
   int matrixBackgroundWidth();
}
