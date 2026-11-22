package com.vortex.client.utils;

import com.vortex.client.VortexClient;
import com.vortex.client.render.VertexTextureFont;

public final class HudFontUtils {
   private HudFontUtils() {
   }

   public static VertexTextureFont getLargeFont() {
      return VortexClient.fontManager.getLargeFont();
   }

   public static VertexTextureFont getLargeAltFont() {
      return VortexClient.fontManager.getLargeAltFont();
   }

   public static VertexTextureFont getSmallFont() {
      return VortexClient.fontManager.getSmallFont();
   }
}
