package com.vortex.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.RenderSystem.ShapeIndexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.BuiltBuffer.DrawParameters;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.vortex.client.utils.MinimalSharpUtils;

public final class FlameVortexUtils {
   private static GpuBuffer gpuBuffer;
   public static final RenderPipeline build = RenderPipeline.builder()
      .withLocation(Identifier.of("vortexclient", "pipeline/flame"))
      .withVertexShader(Identifier.of("vortexclient", "core/vortex_flame"))
      .withFragmentShader(Identifier.of("vortexclient", "core/vortex_flame"))
      .withVertexFormat(VertexFormats.POSITION, DrawMode.QUADS)
      .withUniform("VortexFlame", UniformType.UNIFORM_BUFFER)
      .withBlend(BlendFunction.TRANSLUCENT)
      .withCull(false)
      .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
      .withDepthWrite(false)
      .build();
   private static GpuBuffer deviceCreate;
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private static boolean flameVortexActive;
   private static final ByteBuffer order = ByteBuffer.allocateDirect(96).order(ByteOrder.nativeOrder());

   public static void updateRegisterBuild() {
      if (MinimalSharpUtils.canFlagForFlag(flameVortexActive)) {
         flameVortexActive = true;
         RenderPipelines.register(build);
      }
   }

   private static GpuBuffer getDeviceCreate(GpuBuffer deviceCreate, String deviceCreateText, int deviceCreateValue, int remaining) {
      if (deviceCreate != null && deviceCreate.size() >= remaining) {
         return deviceCreate;
      } else {
         if (deviceCreate != null) {
            deviceCreate.close();
         }

         return RenderSystem.getDevice().createBuffer(() -> deviceCreateText, deviceCreateValue, remaining);
      }
   }

   private FlameVortexUtils() {
   }

   public static boolean canFlag(
      float flameVortexValue,
      float flameVortexFactor,
      float panelSamplerValue,
      float flameVortexBound,
      float flameVortexValue3,
      float flameVortexValue4,
      float flameVortexValue5,
      float flameVortexValue6,
      float flameVortexValue7,
      float flameVortexValue8,
      float flameVortexValue9,
      float flameVortexValue10,
      float flameVortexValue11,
      float flameVortexValue12,
      float flameVortexValue13,
      float flameVortexValue14,
      float flameVortexValue15,
      float flameVortexValue16,
      float flameVortexValue17,
      float flameVortexValue18,
      float flameVortexValue19,
      float flameVortexValue20
   ) {
      return MinimalSharpUtils.isBoolean(
         () -> {
            updateRegisterBuild();
            float flameVortexValue21 = flameVortexFactor * (1.0F - flameVortexValue20);
            float flameVortexValue22 = panelSamplerValue / Math.max(1.0F, flameVortexBound);
            ByteBuffer order2 = order;
            order2.putFloat(0, flameVortexValue);
            order2.putFloat(4, flameVortexFactor);
            order2.putFloat(8, panelSamplerValue);
            order2.putFloat(12, flameVortexBound);
            order2.putFloat(16, flameVortexValue3);
            order2.putFloat(20, flameVortexValue4);
            order2.putFloat(24, flameVortexValue5);
            order2.putFloat(28, flameVortexValue6);
            order2.putFloat(32, flameVortexValue7);
            order2.putFloat(36, flameVortexValue8);
            order2.putFloat(40, flameVortexValue9);
            order2.putFloat(44, 1.0F);
            order2.putFloat(48, flameVortexValue10);
            order2.putFloat(52, flameVortexValue11);
            order2.putFloat(56, flameVortexValue12);
            order2.putFloat(60, flameVortexValue13);
            order2.putFloat(64, flameVortexValue14);
            order2.putFloat(68, flameVortexValue15);
            order2.putFloat(72, flameVortexValue16);
            order2.putFloat(76, flameVortexValue17);
            order2.putFloat(80, flameVortexValue18);
            order2.putFloat(84, flameVortexValue19);
            order2.putFloat(88, flameVortexValue22);
            order2.putFloat(92, flameVortexValue20);
            BufferBuilder begin = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION);
            begin.vertex(MATRIX4F, 0.0F, flameVortexValue21, 0.0F);
            begin.vertex(MATRIX4F, 0.0F, flameVortexFactor, 0.0F);
            begin.vertex(MATRIX4F, flameVortexValue, flameVortexFactor, 0.0F);
            begin.vertex(MATRIX4F, flameVortexValue, flameVortexValue21, 0.0F);
            GpuDevice device = RenderSystem.getDevice();

            try (BuiltBuffer end = begin.end()) {
               DrawParameters drawParameters = end.getDrawParameters();
               ByteBuffer buffer = end.getBuffer();
               int remaining = buffer.remaining();
               deviceCreate = getDeviceCreate(deviceCreate, "vortex_flame_vtx", 40, remaining);
               gpuBuffer = getDeviceCreate(gpuBuffer, "vortex_flame_ubo", 136, 96);
               ShapeIndexBuffer sequentialBuffer = RenderSystem.getSequentialBuffer(DrawMode.QUADS);
               GpuBuffer indexBuffer = sequentialBuffer.getIndexBuffer(drawParameters.indexCount());
               Framebuffer framebuffer = Module.mc.getFramebuffer();
               CommandEncoder commandEncoder = device.createCommandEncoder();
               commandEncoder.writeToBuffer(deviceCreate.slice(0L, remaining), buffer);
               commandEncoder.writeToBuffer(gpuBuffer.slice(0L, 96L), order2);

               try (RenderPass renderPass = commandEncoder.createRenderPass(
                     () -> "vortex_flame", framebuffer.getColorAttachmentView(), OptionalInt.empty(), framebuffer.getDepthAttachmentView(), OptionalDouble.empty()
                  )) {
                  renderPass.setPipeline(build);
                  MatrixWindowUtils.handleDequeScissor(renderPass);
                  renderPass.setUniform("VortexFlame", gpuBuffer);
                  renderPass.setVertexBuffer(0, deviceCreate);
                  renderPass.setIndexBuffer(indexBuffer, sequentialBuffer.getIndexType());
                  renderPass.drawIndexed(0, 0, drawParameters.indexCount(), 1);
               }
            }

            return true;
         }
      );
   }
}
