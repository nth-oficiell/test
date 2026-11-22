package com.vortex.client.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.ModelCommandRenderer.CrumblingOverlayCommand;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.vortex.client.utils.RenderMatrixUtilsWarm;

@Mixin({LivingEntityRenderer.class})
public class MixinLivingEntityRendererBrainrot {
   @WrapOperation(
      method = {"method_4054(Lnet/minecraft/class_10042;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_11659;method_73490(Lnet/minecraft/class_3879;Ljava/lang/Object;Lnet/minecraft/class_4587;Lnet/minecraft/class_1921;IIILnet/minecraft/class_1058;ILnet/minecraft/class_11683$class_11792;)V"
      )}
   )
   private void vortex$brainrotBody(
      OrderedRenderCommandQueue orderedRenderCommandQueue,
      Model<?> model,
      Object obj,
      MatrixStack matrixStack,
      RenderLayer renderLayer,
      int vortexbrainrotBodyValue,
      int vortexbrainrotBodyValue2,
      int vortexbrainrotBodyValue3,
      Sprite sprite,
      int vectorOutlineValue,
      CrumblingOverlayCommand crumblingOverlayCommand,
      Operation<Void> operation
   ) {
      if (!(obj instanceof PlayerEntityRenderState s && RenderMatrixUtilsWarm.canCustomSubmit(orderedRenderCommandQueue, s, matrixStack, renderLayer, vortexbrainrotBodyValue, vortexbrainrotBodyValue2, vortexbrainrotBodyValue3, vectorOutlineValue))) {
         operation.call(new Object[]{orderedRenderCommandQueue, model, obj, matrixStack, renderLayer, vortexbrainrotBodyValue, vortexbrainrotBodyValue2, vortexbrainrotBodyValue3, sprite, vectorOutlineValue, crumblingOverlayCommand});
      }
   }
}
