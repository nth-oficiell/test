package com.matrix.client.mixins;

import com.matrix.client.categories.misc.Optimizer;
import net.minecraft.client.render.block.entity.BlockEntityRenderManager;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Distance culling for block entities (chests, signs, banners, skulls, beds, ...). */
@Mixin({BlockEntityRenderManager.class})
public class MixinBlockEntityRenderManagerOptimizer {
   @Inject(
      method = {"method_3555(Lnet/minecraft/class_11954;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void matrix$optimizerCullBlockEntity(
      BlockEntityRenderState state,
      MatrixStack matrices,
      OrderedRenderCommandQueue queue,
      CameraRenderState cameraState,
      CallbackInfo callbackInfo
   ) {
      if (Optimizer.shouldCullBlockEntity(state.pos, cameraState.pos)) {
         callbackInfo.cancel();
      }
   }
}
