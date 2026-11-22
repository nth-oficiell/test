package com.vortex.client.mixins;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.FramePass;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.vortex.client.categories.visuals.CustomSky;

@Mixin({WorldRenderer.class})
public abstract class MixinWorldRendererCustomSky {
   @Shadow
   @Final
   private DefaultFramebufferSet framebufferSet;

   @Inject(
      method = {"method_62200(Lnet/minecraft/class_9909;Lnet/minecraft/class_4184;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void vortex$customSky(FrameGraphBuilder frameGraphBuilder, Camera camera, GpuBufferSlice gpuBufferSlice, CallbackInfo callbackInfo) {
      CustomSky sf = CustomSky.getCustomSky();
      if (sf != null) {
         float vis = sf.getAmount();
         if (!(vis < 1.0F)) {
            this.vortex$addSkyPass(frameGraphBuilder, gpuBufferSlice, sf, 1.0F);
            callbackInfo.cancel();
         }
      }
   }

   @Inject(
      method = {"method_62200(Lnet/minecraft/class_9909;Lnet/minecraft/class_4184;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"},
      at = {@At("RETURN")}
   )
   private void vortex$customSkyFade(FrameGraphBuilder frameGraphBuilder, Camera camera, GpuBufferSlice gpuBufferSlice, CallbackInfo callbackInfo) {
      CustomSky sf = CustomSky.getCustomSky();
      if (sf != null) {
         float vis = sf.getEasedFade();
         if (!(vis <= 0.0F) && !(vis >= 1.0F)) {
            this.vortex$addSkyPass(frameGraphBuilder, gpuBufferSlice, sf, vis);
         }
      }
   }

   @Unique
   private void vortex$addSkyPass(FrameGraphBuilder frameGraphBuilder, GpuBufferSlice shaderFog, CustomSky customSky, float vortexaddSkyValue) {
      FramePass framePass = frameGraphBuilder.createPass("vortex_custom_sky");
      this.framebufferSet.mainFramebuffer = framePass.transfer(this.framebufferSet.mainFramebuffer);
      framePass.setRenderer(() -> {
         RenderSystem.setShaderFog(shaderFog);
         customSky.handleFramebufferWindow(vortexaddSkyValue);
      });
   }

   @Inject(
      method = {"method_62204(Lnet/minecraft/class_9909;Lnet/minecraft/class_4063;Lnet/minecraft/class_243;JFIF)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void vortex$customSkyClouds(
      FrameGraphBuilder frameGraphBuilder, CloudRenderMode cloudRenderMode, Vec3d vec, long vortexcustomSkyValue, float vortexcustomSkyValue2, int vortexcustomSkyValue3, float vortexcustomSkyValue4, CallbackInfo callbackInfo
   ) {
      CustomSky sf = CustomSky.getCustomSky();
      if (sf != null && sf.isMax()) {
         callbackInfo.cancel();
      }
   }
}
