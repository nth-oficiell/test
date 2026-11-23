package com.matrix.client.utils;

import com.matrix.client.gui.VertexGuiUtils;
import com.matrix.client.render.MatrixRoundedUtils;
import com.matrix.client.render.FlameMatrixUtils;
import com.matrix.client.render.GhostBuilderUtils;
import com.matrix.client.render.SamplerBuilderUtils;
import com.matrix.client.render.TexBuilderUtils;
import com.matrix.client.render.VertexBuilderUtils;

public class Utils170 {
   public static void update() {
      MatrixRoundedUtils.updateRegisterBuild();
      VertexGuiUtils.updateTriangleFan();
      VertexBuilderUtils.updateRegisterBuild();
      FlameMatrixUtils.updateRegisterBuild();
      TexBuilderUtils.update();
      SamplerBuilderUtils.update();
      GhostBuilderUtils.update();
   }
}
