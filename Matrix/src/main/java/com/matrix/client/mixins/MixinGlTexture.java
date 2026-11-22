package com.matrix.client.mixins;

import com.matrix.client.render.MatrixRoundedUtils;
import net.minecraft.client.texture.GlTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GlTexture.class})
public class MixinGlTexture {
   @Inject(
      at = {@At("HEAD")},
      method = {"close()V"}
   )
   private void matrix$flushBeforeClose(CallbackInfo callbackInfo) {
      MatrixRoundedUtils.updateRenderThread();
   }
}
