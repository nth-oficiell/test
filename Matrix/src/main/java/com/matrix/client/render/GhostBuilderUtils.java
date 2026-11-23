package com.matrix.client.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public final class GhostBuilderUtils {
   public static final RenderPipeline GHOST_CORZ = getGhostMatrix("ghost", DepthTestFunction.LEQUAL_DEPTH_TEST, true, true);
   public static final RenderPipeline GHOST_OCCLUDED_PIPELINE = getGhostMatrix("ghost_occluded", DepthTestFunction.GREATER_DEPTH_TEST, true, false);
   public static final RenderPipeline CORZ_SCHEM = getMatrixSchem("overlay_quads", DepthTestFunction.LEQUAL_DEPTH_TEST, true);
   public static final RenderPipeline OVERLAY_QUADS_OCCLUDED_PIPELINE = getMatrixSchem("overlay_quads_occluded", DepthTestFunction.GREATER_DEPTH_TEST, false);
   public static final RenderPipeline RENDER_PIPELINE = getDepthBias("outline", DepthTestFunction.LEQUAL_DEPTH_TEST, true);
   public static final RenderPipeline DEPTH_BIAS = getDepthBias("outline_occluded", DepthTestFunction.GREATER_DEPTH_TEST, false);

   public static void update() {
      BuilderSchemUtils.update();
   }

   private static Identifier getIdentifier(String identifierText) {
      return Identifier.of("matrixclient", "pipeline/schem_" + identifierText);
   }

   private static RenderPipeline getDepthBias(String depthBiasText, DepthTestFunction depthTestFunction, boolean depthBiasFlag) {
      Builder withDepthWrite = RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
         .withLocation(getIdentifier(depthBiasText))
         .withBlend(BlendFunction.TRANSLUCENT)
         .withCull(false)
         .withDepthTestFunction(depthTestFunction)
         .withDepthWrite(false);
      if (depthBiasFlag) {
         withDepthWrite = withDepthWrite.withDepthBias(-1.2F, -0.2F);
      }

      return getRegister(withDepthWrite.build());
   }

   private static RenderPipeline getMatrixSchem(String matrixSchemText, DepthTestFunction depthTestFunction, boolean matrixSchemFlag) {
      Builder withDepthWrite = RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation(getIdentifier(matrixSchemText))
         .withVertexShader(getIdentifierForString("matrix_schem_overlay"))
         .withFragmentShader(getIdentifierForString("matrix_schem_overlay"))
         .withUniform("MatrixGhostData", UniformType.UNIFORM_BUFFER)
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withBlend(BlendFunction.TRANSLUCENT)
         .withCull(false)
         .withDepthTestFunction(depthTestFunction)
         .withDepthWrite(false);
      if (matrixSchemFlag) {
         withDepthWrite = withDepthWrite.withDepthBias(-0.4F, -0.8F);
      }

      return getRegister(withDepthWrite.build());
   }

   private static RenderPipeline getRegister(RenderPipeline renderPipeline) {
      return RenderPipelines.register(renderPipeline);
   }

   private static RenderPipeline getGhostMatrix(String ghostMatrixText, DepthTestFunction depthTestFunction, boolean ghostMatrixFlag, boolean ghostMatrixFlag2) {
      Builder withDepthWrite = RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation(getIdentifier(ghostMatrixText))
         .withVertexShader(getIdentifierForString("matrix_schem_ghost"))
         .withFragmentShader(getIdentifierForString("matrix_schem_ghost"))
         .withSampler("Sampler0")
         .withUniform("MatrixGhostData", UniformType.UNIFORM_BUFFER)
         .withVertexFormat(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, DrawMode.QUADS)
         .withBlend(BlendFunction.TRANSLUCENT)
         .withCull(ghostMatrixFlag)
         .withDepthTestFunction(depthTestFunction)
         .withDepthWrite(false);
      if (ghostMatrixFlag2) {
         withDepthWrite = withDepthWrite.withDepthBias(-0.3F, -0.6F);
      }

      return getRegister(withDepthWrite.build());
   }

   private GhostBuilderUtils() {
   }

   private static Identifier getIdentifierForString(String identifierText) {
      return Identifier.of("matrixclient", "core/" + identifierText);
   }
}
