package com.matrix.client.render;

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
import com.matrix.client.utils.MinimalSharpUtils;

public final class FlameMatrixUtils {
   private static GpuBuffer gpuBuffer;
   public static final RenderPipeline build = RenderPipeline.builder()
      .withLocation(Identifier.of("matrixclient", "pipeline/flame"))
      .withVertexShader(Identifier.of("matrixclient", "core/matrix_flame"))
      .withFragmentShader(Identifier.of("matrixclient", "core/matrix_flame"))
      .withVertexFormat(VertexFormats.POSITION, DrawMode.QUADS)
      .withUniform("MatrixFlame", UniformType.UNIFORM_BUFFER)
      .withBlend(BlendFunction.TRANSLUCENT)
      .withCull(false)
      .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
      .withDepthWrite(false)
      .build();
   private static GpuBuffer deviceCreate;
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private static boolean flameMatrixActive;
   private static final ByteBuffer order = ByteBuffer.allocateDirect(96).order(ByteOrder.nativeOrder());

   public static void updateRegisterBuild() {
      if (MinimalSharpUtils.canFlagForFlag(flameMatrixActive)) {
         flameMatrixActive = true;
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

   private FlameMatrixUtils() {
   }

   public static boolean canFlag(
      float flameMatrixValue,
      float flameMatrixFactor,
      float panelSamplerValue,
      float flameMatrixBound,
      float flameMatrixValue3,
      float flameMatrixValue4,
      float flameMatrixValue5,
      float flameMatrixValue6,
      float flameMatrixValue7,
      float flameMatrixValue8,
      float flameMatrixValue9,
      float flameMatrixValue10,
      float flameMatrixValue11,
      float flameMatrixValue12,
      float flameMatrixValue13,
      float flameMatrixValue14,
      float flameMatrixValue15,
      float flameMatrixValue16,
      float flameMatrixValue17,
      float flameMatrixValue18,
      float flameMatrixValue19,
      float flameMatrixValue20
   ) {
      return MinimalSharpUtils.isBoolean(
         () -> {
            updateRegisterBuild();
            float flameMatrixValue21 = flameMatrixFactor * (1.0F - flameMatrixValue20);
            float flameMatrixValue22 = panelSamplerValue / Math.max(1.0F, flameMatrixBound);
            ByteBuffer order2 = order;
            order2.putFloat(0, flameMatrixValue);
            order2.putFloat(4, flameMatrixFactor);
            order2.putFloat(8, panelSamplerValue);
            order2.putFloat(12, flameMatrixBound);
            order2.putFloat(16, flameMatrixValue3);
            order2.putFloat(20, flameMatrixValue4);
            order2.putFloat(24, flameMatrixValue5);
            order2.putFloat(28, flameMatrixValue6);
            order2.putFloat(32, flameMatrixValue7);
            order2.putFloat(36, flameMatrixValue8);
            order2.putFloat(40, flameMatrixValue9);
            order2.putFloat(44, 1.0F);
            order2.putFloat(48, flameMatrixValue10);
            order2.putFloat(52, flameMatrixValue11);
            order2.putFloat(56, flameMatrixValue12);
            order2.putFloat(60, flameMatrixValue13);
            order2.putFloat(64, flameMatrixValue14);
            order2.putFloat(68, flameMatrixValue15);
            order2.putFloat(72, flameMatrixValue16);
            order2.putFloat(76, flameMatrixValue17);
            order2.putFloat(80, flameMatrixValue18);
            order2.putFloat(84, flameMatrixValue19);
            order2.putFloat(88, flameMatrixValue22);
            order2.putFloat(92, flameMatrixValue20);
            BufferBuilder begin = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION);
            begin.vertex(MATRIX4F, 0.0F, flameMatrixValue21, 0.0F);
            begin.vertex(MATRIX4F, 0.0F, flameMatrixFactor, 0.0F);
            begin.vertex(MATRIX4F, flameMatrixValue, flameMatrixFactor, 0.0F);
            begin.vertex(MATRIX4F, flameMatrixValue, flameMatrixValue21, 0.0F);
            GpuDevice device = RenderSystem.getDevice();

            try (BuiltBuffer end = begin.end()) {
               DrawParameters drawParameters = end.getDrawParameters();
               ByteBuffer buffer = end.getBuffer();
               int remaining = buffer.remaining();
               deviceCreate = getDeviceCreate(deviceCreate, "matrix_flame_vtx", 40, remaining);
               gpuBuffer = getDeviceCreate(gpuBuffer, "matrix_flame_ubo", 136, 96);
               ShapeIndexBuffer sequentialBuffer = RenderSystem.getSequentialBuffer(DrawMode.QUADS);
               GpuBuffer indexBuffer = sequentialBuffer.getIndexBuffer(drawParameters.indexCount());
               Framebuffer framebuffer = Module.mc.getFramebuffer();
               CommandEncoder commandEncoder = device.createCommandEncoder();
               commandEncoder.writeToBuffer(deviceCreate.slice(0L, remaining), buffer);
               commandEncoder.writeToBuffer(gpuBuffer.slice(0L, 96L), order2);

               try (RenderPass renderPass = commandEncoder.createRenderPass(
                     () -> "matrix_flame", framebuffer.getColorAttachmentView(), OptionalInt.empty(), framebuffer.getDepthAttachmentView(), OptionalDouble.empty()
                  )) {
                  renderPass.setPipeline(build);
                  MatrixWindowUtils.handleDequeScissor(renderPass);
                  renderPass.setUniform("MatrixFlame", gpuBuffer);
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
