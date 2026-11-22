package com.vortex.client.mixins;

import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.PlayerLikeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.vortex.client.utils.MatrixSubmitUtils;

@Mixin({PlayerEntityRenderer.class})
public class MixinPlayerEntityRendererCape {
   @Inject(
      method = {"method_62604(Lnet/minecraft/class_11890;Lnet/minecraft/class_10055;F)V"},
      at = {@At("TAIL")}
   )
   private void vortex$captureCape3d(PlayerLikeEntity playerLikeEntity, PlayerEntityRenderState playerEntityRenderState, float vortexcaptureCape3dValue, CallbackInfo callbackInfo) {
      MatrixSubmitUtils.handleCaptureCape(playerLikeEntity, playerEntityRenderState, vortexcaptureCape3dValue);
   }
}
