package com.matrix.client.config;

import com.matrix.client.MatrixClient;
import com.matrix.client.render.MatrixfontAbcdefghijklmnopqrstuvwxyzTexture;
import com.matrix.client.render.TtfFont;
import com.matrix.client.render.VertexTextureFont;
import java.io.InputStream;
import net.minecraft.client.util.math.MatrixStack;

public class ClientFonts {
   private final VertexTextureFont largeAltFallbackFont;
   private MatrixStack matrixStack = new MatrixStack();
   private VertexTextureFont smallTtfFont;
   private VertexTextureFont largeAltTtfFont;
   private final VertexTextureFont smallFont;
   private final VertexTextureFont largeFont = new VertexTextureFont(this.openFontStream(), 18.0F);
   private VertexTextureFont largeTtfFont;

   private void ensureTtfFontsLoaded() {
      if (this.largeTtfFont == null) {
         this.largeTtfFont = new MatrixfontAbcdefghijklmnopqrstuvwxyzTexture(18.0F);
         this.largeAltTtfFont = new MatrixfontAbcdefghijklmnopqrstuvwxyzTexture(18.0F);
         this.smallTtfFont = new MatrixfontAbcdefghijklmnopqrstuvwxyzTexture(13.0F);
      }
   }

   private InputStream openFontStream() {
      return MatrixClient.class.getClassLoader().getResourceAsStream("assets/matrixclient/font/font.ttf");
   }

   public VertexTextureFont getLargeFont() {
      if (TtfFont.TTF_ENABLED) {
         this.ensureTtfFontsLoaded();
         return this.largeTtfFont;
      } else {
         return this.largeFont;
      }
   }

   public MatrixStack getMatrixStack() {
      return this.matrixStack;
   }

   public VertexTextureFont getSmallFont() {
      if (TtfFont.TTF_ENABLED) {
         this.ensureTtfFontsLoaded();
         return this.smallTtfFont;
      } else {
         return this.smallFont;
      }
   }

   public VertexTextureFont getLargeAltFont() {
      if (TtfFont.TTF_ENABLED) {
         this.ensureTtfFontsLoaded();
         return this.largeAltTtfFont;
      } else {
         return this.largeAltFallbackFont;
      }
   }

   public void setMatrixStack(MatrixStack matrices) {
      this.matrixStack = matrices != null ? matrices : new MatrixStack();
   }

   public ClientFonts() {
      this.largeAltFallbackFont = new VertexTextureFont(this.openFontStream(), 18.0F, 1);
      this.smallFont = new VertexTextureFont(this.openFontStream(), 13.0F, 1);
   }
}
