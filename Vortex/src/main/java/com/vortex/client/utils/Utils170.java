package com.vortex.client.utils;

import com.vortex.client.gui.VertexGuiUtils;
import com.vortex.client.render.VortexRoundedUtils;
import com.vortex.client.render.FlameVortexUtils;
import com.vortex.client.render.GhostBuilderUtils;
import com.vortex.client.render.SamplerBuilderUtils;
import com.vortex.client.render.TexBuilderUtils;
import com.vortex.client.render.VertexBuilderUtils;

public class Utils170 {
   public static void update() {
      VortexRoundedUtils.updateRegisterBuild();
      VertexGuiUtils.updateTriangleFan();
      VertexBuilderUtils.updateRegisterBuild();
      FlameVortexUtils.updateRegisterBuild();
      TexBuilderUtils.update();
      SamplerBuilderUtils.update();
      GhostBuilderUtils.update();
   }
}
