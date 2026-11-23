package com.vortex.client.mixins;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({HandledScreen.class})
public interface AccessorHandledScreen {
   @Accessor("x")
   int vortexX();

   @Accessor("y")
   int vortexY();

   @Accessor("backgroundWidth")
   int vortexBackgroundWidth();
}
