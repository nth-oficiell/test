package com.matrix.client.utils;

import com.matrix.client.MatrixClient;
import com.matrix.client.render.VertexTextureFont;

public final class HudFontUtils {
   private HudFontUtils() {
   }

   public static VertexTextureFont getLargeFont() {
      return MatrixClient.fontManager.getLargeFont();
   }

   public static VertexTextureFont getLargeAltFont() {
      return MatrixClient.fontManager.getLargeAltFont();
   }

   public static VertexTextureFont getSmallFont() {
      return MatrixClient.fontManager.getSmallFont();
   }
}
