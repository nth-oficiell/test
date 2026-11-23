package com.vortex.client.mixins;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.vortex.client.utils.WaterVortexUtils;

@Mixin({WorldRenderer.class})
public abstract class MixinWorldRendererWater {
   @Inject(
      method = {"method_3295(IIIZ)V"},
      at = {@At("HEAD")},
      require = 0
   )
   private void vortex$waterSectionDirty(int vortexwaterSectionValue, int vortexwaterSectionValue2, int vortexwaterSectionValue3, boolean vortexwaterSectionFlag, CallbackInfo callbackInfo) {
      WaterVortexUtils.handleChunkHash(vortexwaterSectionValue, vortexwaterSectionValue2, vortexwaterSectionValue3);
   }
}
