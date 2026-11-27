package com.matrix.client.mixins;

import com.matrix.client.categories.misc.Optimizer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Distance culling for entity rendering. This is the cheapest place to skip an entity: the
 * renderer, its model and its nametag are never touched once {@code shouldRender} answers false.
 */
@Mixin({EntityRenderManager.class})
public class MixinEntityRenderManagerOptimizer {
   @Inject(
      method = {"method_3950(Lnet/minecraft/class_1297;Lnet/minecraft/class_4604;DDD)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private <E extends Entity> void matrix$optimizerCullEntity(
      E entity, Frustum frustum, double offsetX, double offsetY, double offsetZ, CallbackInfoReturnable<Boolean> callbackInfo
   ) {
      if (Optimizer.shouldCullEntity(entity, offsetX, offsetY, offsetZ)) {
         callbackInfo.setReturnValue(false);
      }
   }
}
