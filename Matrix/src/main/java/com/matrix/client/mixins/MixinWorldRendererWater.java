package com.matrix.client.mixins;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.matrix.client.utils.WaterMatrixUtils;

@Mixin({WorldRenderer.class})
public abstract class MixinWorldRendererWater {
   @Inject(
      method = {"method_3295(IIIZ)V"},
      at = {@At("HEAD")},
      require = 0
   )
   private void matrix$waterSectionDirty(int matrixwaterSectionValue, int matrixwaterSectionValue2, int matrixwaterSectionValue3, boolean matrixwaterSectionFlag, CallbackInfo callbackInfo) {
      WaterMatrixUtils.handleChunkHash(matrixwaterSectionValue, matrixwaterSectionValue2, matrixwaterSectionValue3);
   }
}
