package com.vortex.client.mixins;

import com.vortex.client.render.VortexRoundedUtils;
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
   private void vortex$flushBeforeClose(CallbackInfo callbackInfo) {
      VortexRoundedUtils.updateRenderThread();
   }
}
