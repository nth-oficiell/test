package com.matrix.client.mixins;

import com.matrix.client.render.MatrixRoundedUtils;
import net.minecraft.client.texture.GlTextureView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GlTextureView.class})
public class MixinGlTextureView {
   @Inject(
      at = {@At("HEAD")},
      method = {"close()V"}
   )
   private void matrix$flushBeforeClose(CallbackInfo callbackInfo) {
      MatrixRoundedUtils.updateRenderThread();
   }
}
