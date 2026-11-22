package com.vortex.client.mixins;

import net.minecraft.client.gl.GlCommandEncoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({GlCommandEncoder.class})
public interface AccessorGlCommandEncoder {
   @Accessor("renderPassOpen")
   boolean vortexRenderPassOpen();
}
