package com.vortex.client.render;

import com.vortex.client.VortexClient;
import com.vortex.client.config.MsdfFontData;
import com.google.gson.Gson;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.awt.Color;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import com.vortex.client.utils.CardStatusUtilsFlag;

public class VortexfontAbcdefghijklmnopqrstuvwxyzTexture extends VertexTextureFont {
   private  float abcdefghijklmnopqrstuvwxyzBrownAmount;
   private static float vortexfontParsedAmount;
   private static final Identifier identifier = Identifier.of("vortexclient", "font/msdf_vortexfont");
   private static VortexfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph vortexfontParsedGlyph;
   private static NativeImage readVortexfontParsed;
   private static final Map<Integer, VortexfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph> CORZFONT_CORZCLIENT_MAP = new HashMap<>();
   private boolean fontBufferActive;
   private final boolean brownActive;
   private  float brownAmount;
   private static final Map<Long, Float> CORZFONT_PNG_MAP = new HashMap<>();
   private static float parsedVortexfontAmount;
   private static final String QUICK_BROWN_FOX = "The quick brown fox ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 0123456789";
   private static final VortexfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph[] vortexfontVortexclientGlyphs = new VortexfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph[256];
   private static boolean vortexfontPngFlag;
   private static boolean vortexfontJsonFlag = false;
   private BufferBuilder bufferBuilder;
   private static boolean vortexfontParsedActive = false;
   private  float metricsFont;
   private static boolean fontMsdfFlag = false;

   private void handleFontBuffer(String[] strings, float[] fontBufferValues, float[] fontBufferValues2, float fontBufferValue, int fontBufferLimit, Color color) {
      MatrixStack matrixStack = VortexClient.fontManager.getMatrixStack();
      Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
      float redScaled = color.getRed() / 255.0F;
      float greenScaled = color.getGreen() / 255.0F;
      float blueScaled = color.getBlue() / 255.0F;
      float fontBufferValue2 = color.getAlpha() / 255.0F * VortexRoundedUtils.panelOverlayAmount;
      BufferBuilder bufferBuilder = this.fontBufferActive ? this.bufferBuilder : null;

      for (int i = 0; i < fontBufferLimit; i++) {
         String orDefault = CardStatusUtilsFlag.getOrDefault(strings[i]);
         if (orDefault != null && !orDefault.isEmpty()) {
            updateMsdfTexture();
            if (!fontMsdfFlag) {
               break;
            }

            bufferBuilder = this.getAmountAbcdefghijklmnopqrstuvwxyz(bufferBuilder, positionMatrix, orDefault, fontBufferValues[i], fontBufferValues2 == null ? fontBufferValue : fontBufferValues2[i], redScaled, greenScaled, blueScaled, fontBufferValue2);
         }
      }

      if (this.fontBufferActive) {
         this.bufferBuilder = bufferBuilder;
      } else {
         if (bufferBuilder != null) {
            VortexRoundedUtils.handleTextureView(bufferBuilder.end(), identifier);
         }
      }
   }

   public VortexfontAbcdefghijklmnopqrstuvwxyzTexture(float vortexfontTextureValue) {
      super(getAsStream(), vortexfontTextureValue);

      boolean flag;
      try {
         updateVortexfontParsed();
         float vortexfontTextureValue2 = super.getAbcdefghijklmnopqrstuvwxyzVortexfont("The quick brown fox ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 0123456789");
         float vortexfontVortexclient = getVortexfontVortexclient("The quick brown fox ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 0123456789");
         this.brownAmount = (
               vortexfontVortexclient > 0.0F ? vortexfontTextureValue2 / vortexfontVortexclient : super.getAbcdefghijklmnopqrstuvwxyzBrown() / vortexfontParsedAmount
            )
            * 1.0F;
         this.abcdefghijklmnopqrstuvwxyzBrownAmount = super.getAbcdefghijklmnopqrstuvwxyzBrown();
         this.metricsFont = this.getMetricsFont();
         flag = !vortexfontParsedActive;
      } catch (Throwable e) {
         e.printStackTrace();
         flag = false;
      }

      this.brownActive = flag;
   }

   @Override
   public float getAbcdefghijklmnopqrstuvwxyzBrownBrownActive() {
      return this.brownActive ? 0.716F * this.brownAmount : super.getAbcdefghijklmnopqrstuvwxyzBrownBrownActive();
   }

   @Override
   public void handleFlag(String[] texts, float[] vortexfontTextureValues, int vortexfontTextureValue, float vertexTextureValue, Color color) {
      if (!this.brownActive) {
         super.handleFlag(texts, vortexfontTextureValues, vortexfontTextureValue, vertexTextureValue, color);
      } else {
         this.handleFontBuffer(texts, vortexfontTextureValues, null, vertexTextureValue, vortexfontTextureValue, color);
      }
   }

   @Override
   public void handleForStringFloat(String vortexfontTextureText, float vortexfontTextureValue, float f13, Color color) {
      this.handleFontBuffer(vortexfontTextureText, vortexfontTextureValue, f13, color);
   }

   public void updateBufferBuilder() {
      BufferBuilder bufferBuilder = this.bufferBuilder;
      this.fontBufferActive = false;
      this.bufferBuilder = null;
      if (bufferBuilder != null) {
         VortexRoundedUtils.handleTextureView(bufferBuilder.end(), identifier);
      }
   }

   @Override
   public void handleFlag(String[] strings, float[] vortexfontTextureValues, float[] vortexfontTextureValues2, int vortexfontTextureValue, Color color) {
      if (!this.brownActive) {
         super.handleFlag(strings, vortexfontTextureValues, vortexfontTextureValues2, vortexfontTextureValue, color);
      } else {
         this.handleFontBuffer(strings, vortexfontTextureValues, vortexfontTextureValues2, 0.0F, vortexfontTextureValue, color);
      }
   }

   public boolean isFlagBuffer() {
      if (this.brownActive && !this.fontBufferActive) {
         this.fontBufferActive = true;
         this.bufferBuilder = null;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public int getAbcdefghijklmnopqrstuvwxyzBrown(int brownValue, int boxHeight) {
      return !this.brownActive
         ? super.getAbcdefghijklmnopqrstuvwxyzBrown(brownValue, boxHeight)
         : Math.round(brownValue + boxHeight / 2.0F - (this.metricsFont - 0.716F * this.brownAmount / 2.0F));
   }

   private static long getTime(int vortexfontTextureValue, int charAt) {
      return (long)vortexfontTextureValue << 32 | charAt & 4294967295L;
   }

   @Override
   public float getAbcdefghijklmnopqrstuvwxyzVortexfont(String vortexfontText) {
      vortexfontText = CardStatusUtilsFlag.getOrDefault(vortexfontText);
      if (!this.brownActive) {
         return super.getAbcdefghijklmnopqrstuvwxyzVortexfont(vortexfontText);
      } else if (vortexfontText != null && !vortexfontText.isEmpty()) {
         float vortexfontTotal = 0.0F;
         int vortexfontValue = -1;

         for (int i = 0; i < vortexfontText.length(); i++) {
            char charAt = vortexfontText.charAt(i);
            VortexfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph vortexfontVortexclient = getVortexfontVortexclient(charAt);
            if (vortexfontVortexclient == null) {
               vortexfontValue = charAt;
            } else {
               if (vortexfontPngFlag && vortexfontValue != -1) {
                  Float vortexfont_png_map = CORZFONT_PNG_MAP.get(getTime(vortexfontValue, charAt));
                  if (vortexfont_png_map != null) {
                     vortexfontTotal += vortexfont_png_map * this.brownAmount;
                  }
               }

               vortexfontTotal += vortexfontVortexclient.vortexfontAmount * this.brownAmount;
               vortexfontValue = charAt;
            }
         }

         return vortexfontTotal;
      } else {
         return 0.0F;
      }
   }

   @Override
   public float getAbcdefghijklmnopqrstuvwxyzBrown() {
      return this.brownActive ? this.abcdefghijklmnopqrstuvwxyzBrownAmount : super.getAbcdefghijklmnopqrstuvwxyzBrown();
   }

   private static VortexfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph getVortexfontVortexclient(int index) {
      VortexfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph vortexfontVortexclient = index < 256 ? vortexfontVortexclientGlyphs[index] : CORZFONT_CORZCLIENT_MAP.get(index);
      if (vortexfontVortexclient == null && index != 32) {
         vortexfontVortexclient = vortexfontParsedGlyph;
      }

      return vortexfontVortexclient;
   }

   private static synchronized void updateVortexfontParsed()  throws java.io.IOException {
      if (!vortexfontJsonFlag && !vortexfontParsedActive) {
         try {
            MsdfFontData fromJson4;
            try (InputStream resourceAsStream = VortexClient.class.getClassLoader().getResourceAsStream("assets/vortexclient/fonts/vortexfont.json")) {
               if (resourceAsStream == null) {
                  throw new IllegalStateException("vortexfont.json missing from resources");
               }

               fromJson4 = (MsdfFontData)new Gson().fromJson(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8), MsdfFontData.class);
            }

            if (fromJson4 != null && fromJson4.class172 != null && fromJson4.class245 != null && fromJson4.parsedVortexfontItems != null) {
               vortexfontParsedAmount = fromJson4.class245.parsedVortexfontAmount;
               parsedVortexfontAmount = fromJson4.class245.parsedVortexfontAmount2;
               float parsedVortexfontAmount2 = fromJson4.class172.parsedVortexfontAmount;
               float parsedVortexfontAmount22 = fromJson4.class172.parsedVortexfontAmount2;

               for (MsdfFontData.Glyph glyph : fromJson4.parsedVortexfontItems) {
                  CORZFONT_CORZCLIENT_MAP.put(glyph.value, new VortexfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph(glyph, parsedVortexfontAmount2, parsedVortexfontAmount22));
               }

               if (fromJson4.items != null) {
                  for (MsdfFontData.Kerning kerning : fromJson4.items) {
                     CORZFONT_PNG_MAP.put(getTime(kerning.value, kerning.value2), kerning.amount);
                  }
               }

               for (int i = 0; i < vortexfontVortexclientGlyphs.length; i++) {
                  vortexfontVortexclientGlyphs[i] = CORZFONT_CORZCLIENT_MAP.get(i);
               }

               vortexfontParsedGlyph = CORZFONT_CORZCLIENT_MAP.get(63);
               vortexfontPngFlag = !CORZFONT_PNG_MAP.isEmpty();

               try (InputStream resourceAsStream2 = VortexClient.class.getClassLoader().getResourceAsStream("assets/vortexclient/fonts/vortexfont.png")) {
                  if (resourceAsStream2 == null) {
                     throw new IllegalStateException("vortexfont.png missing from resources");
                  }

                  readVortexfontParsed = NativeImage.read(resourceAsStream2);
               }

               vortexfontJsonFlag = true;
            } else {
               throw new IllegalStateException("vortexfont.json parsed empty");
            }
         } catch (Throwable e) {
            vortexfontParsedActive = true;
            throw e;
         }
      }
   }

   private void handleFontBuffer(String fontBufferText, float fontBufferValue, float f13, Color color) {
      fontBufferText = CardStatusUtilsFlag.getOrDefault(fontBufferText);
      if (!this.brownActive) {
         super.handleForStringFloat(fontBufferText, fontBufferValue, f13, color);
      } else if (fontBufferText != null && !fontBufferText.isEmpty()) {
         updateMsdfTexture();
         if (fontMsdfFlag) {
            MatrixStack matrixStack = VortexClient.fontManager.getMatrixStack();
            Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
            float redScaled = color.getRed() / 255.0F;
            float greenScaled = color.getGreen() / 255.0F;
            float blueScaled = color.getBlue() / 255.0F;
            float fontBufferValue3 = color.getAlpha() / 255.0F * VortexRoundedUtils.panelOverlayAmount;
            BufferBuilder bufferBuilder = this.getAmountAbcdefghijklmnopqrstuvwxyz(this.fontBufferActive ? this.bufferBuilder : null, positionMatrix, fontBufferText, fontBufferValue, f13, redScaled, greenScaled, blueScaled, fontBufferValue3);
            if (this.fontBufferActive) {
               this.bufferBuilder = bufferBuilder;
            } else {
               if (bufferBuilder != null) {
                  VortexRoundedUtils.handleTextureView(bufferBuilder.end(), identifier);
               }
            }
         }
      }
   }

   private BufferBuilder getAmountAbcdefghijklmnopqrstuvwxyz(
      BufferBuilder begin2, Matrix4f matrix4f, String vortexfontTextureText, float vortexfontTextureValue, float f13, float redScaled, float greenScaled, float blueScaled, float vortexfontTextureValue6
   ) {
      float vortexfontTextureValue7 = f13 + this.metricsFont;
      float vortexfontTextureTotal = vortexfontTextureValue;
      int charAt8 = -1;

      for (int i = 0; i < vortexfontTextureText.length(); i++) {
         char charAt = vortexfontTextureText.charAt(i);
         VortexfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph vortexfontVortexclient = getVortexfontVortexclient(charAt);
         if (vortexfontVortexclient == null) {
            charAt8 = charAt;
         } else {
            if (vortexfontPngFlag && charAt8 != -1) {
               Float vortexfont_png_map = CORZFONT_PNG_MAP.get(getTime(charAt8, charAt));
               if (vortexfont_png_map != null) {
                  vortexfontTextureTotal += vortexfont_png_map * this.brownAmount;
               }
            }

            if (vortexfontVortexclient.isAmount()) {
               if (begin2 == null) {
                  begin2 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
               }

               float vortexfontTextureValue9 = vortexfontTextureValue7 - vortexfontVortexclient.planeTop * this.brownAmount;
               float vortexfontTextureValue10 = vortexfontVortexclient.planeWidth * this.brownAmount;
               float vortexfontTextureValue11 = vortexfontVortexclient.planeHeight * this.brownAmount;
               begin2.vertex(matrix4f, vortexfontTextureTotal, vortexfontTextureValue9 + vortexfontTextureValue11, 0.0F).texture(vortexfontVortexclient.textureULeft, vortexfontVortexclient.glyphAmount).color(redScaled, greenScaled, blueScaled, vortexfontTextureValue6);
               begin2.vertex(matrix4f, vortexfontTextureTotal + vortexfontTextureValue10, vortexfontTextureValue9 + vortexfontTextureValue11, 0.0F).texture(vortexfontVortexclient.textureURight, vortexfontVortexclient.glyphAmount).color(redScaled, greenScaled, blueScaled, vortexfontTextureValue6);
               begin2.vertex(matrix4f, vortexfontTextureTotal + vortexfontTextureValue10, vortexfontTextureValue9, 0.0F).texture(vortexfontVortexclient.textureURight, vortexfontVortexclient.textureVTop).color(redScaled, greenScaled, blueScaled, vortexfontTextureValue6);
               begin2.vertex(matrix4f, vortexfontTextureTotal, vortexfontTextureValue9, 0.0F).texture(vortexfontVortexclient.textureULeft, vortexfontVortexclient.textureVTop).color(redScaled, greenScaled, blueScaled, vortexfontTextureValue6);
            }

            vortexfontTextureTotal += vortexfontVortexclient.vortexfontAmount * this.brownAmount;
            charAt8 = charAt;
         }
      }

      return begin2;
   }

   private static InputStream getAsStream() {
      return VortexClient.class.getClassLoader().getResourceAsStream("assets/vortexclient/font/font.ttf");
   }

   @Override
   public void handle(String vortexfontTextureText, float vortexfontTextureValue, float f13, Color color) {
      this.handleFontBuffer(vortexfontTextureText, vortexfontTextureValue, f13, color);
   }

   private static float getVortexfontVortexclient(String vortexfontVortexclientText) {
      float vortexfontVortexclient = 0.0F;
      int vortexfontVortexclientValue = -1;

      for (int i = 0; i < vortexfontVortexclientText.length(); i++) {
         char charAt = vortexfontVortexclientText.charAt(i);
         VortexfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph vortexfont_vortexclient_map = CORZFONT_CORZCLIENT_MAP.get(Integer.valueOf(charAt));
         if (vortexfont_vortexclient_map == null && charAt != ' ') {
            vortexfont_vortexclient_map = CORZFONT_CORZCLIENT_MAP.get(63);
         }

         if (vortexfont_vortexclient_map == null) {
            vortexfontVortexclientValue = charAt;
         } else {
            if (vortexfontVortexclientValue != -1) {
               Float vortexfont_png_map = CORZFONT_PNG_MAP.get(getTime(vortexfontVortexclientValue, charAt));
               if (vortexfont_png_map != null) {
                  vortexfontVortexclient += vortexfont_png_map;
               }
            }

            vortexfontVortexclient += vortexfont_vortexclient_map.vortexfontAmount;
            vortexfontVortexclientValue = charAt;
         }
      }

      return vortexfontVortexclient;
   }

   private static void updateMsdfTexture() {
      if (!fontMsdfFlag && readVortexfontParsed != null) {
         NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(() -> "vortex_msdf_vortexfont", readVortexfontParsed);
         MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, nativeImageBackedTexture);
         fontMsdfFlag = true;
      }
   }

   private static final class Glyph {
      final float textureVTop;
      final float vortexfontAmount;
      final float glyphAmount;
      final float textureULeft;
      final float planeHeight;
      final float textureURight;
      final float planeWidth;
      final float planeTop;

      boolean isAmount() {
         return this.planeWidth > 0.0F && this.planeHeight > 0.0F && (this.textureURight != this.textureULeft || this.glyphAmount != this.textureVTop);
      }

      Glyph(MsdfFontData.Glyph glyph, float glyphDivisor, float glyphDivisor2) {
         this.vortexfontAmount = glyph.amount;
         MsdfFontData.Bounds class1912 = glyph.class1912;
         if (class1912 != null) {
            this.textureULeft = class1912.amount4 / glyphDivisor;
            this.textureURight = class1912.amount3 / glyphDivisor;
            this.textureVTop = 1.0F - class1912.amount2 / glyphDivisor2;
            this.glyphAmount = 1.0F - class1912.amount / glyphDivisor2;
         } else {
            this.textureULeft = this.textureURight = this.textureVTop = this.glyphAmount = 0.0F;
         }

         MsdfFontData.Bounds class191 = glyph.class191;
         if (class191 != null) {
            this.planeWidth = class191.amount3 - class191.amount4;
            this.planeHeight = class191.amount2 - class191.amount;
            this.planeTop = class191.amount2;
         } else {
            this.planeWidth = this.planeHeight = this.planeTop = 0.0F;
         }
      }
   }
}
