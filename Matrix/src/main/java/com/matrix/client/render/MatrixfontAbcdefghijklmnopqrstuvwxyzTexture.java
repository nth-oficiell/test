package com.matrix.client.render;

import com.matrix.client.MatrixClient;
import com.matrix.client.config.MsdfFontData;
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
import com.matrix.client.utils.CardStatusUtilsFlag;

public class MatrixfontAbcdefghijklmnopqrstuvwxyzTexture extends VertexTextureFont {
   private  float abcdefghijklmnopqrstuvwxyzBrownAmount;
   private static float matrixfontParsedAmount;
   private static final Identifier identifier = Identifier.of("matrixclient", "font/msdf_matrixfont");
   private static MatrixfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph matrixfontParsedGlyph;
   private static NativeImage readMatrixfontParsed;
   private static final Map<Integer, MatrixfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph> CORZFONT_CORZCLIENT_MAP = new HashMap<>();
   private boolean fontBufferActive;
   private final boolean brownActive;
   private  float brownAmount;
   private static final Map<Long, Float> CORZFONT_PNG_MAP = new HashMap<>();
   private static float parsedMatrixfontAmount;
   private static final String QUICK_BROWN_FOX = "The quick brown fox ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 0123456789";
   private static final MatrixfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph[] matrixfontMatrixclientGlyphs = new MatrixfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph[256];
   private static boolean matrixfontPngFlag;
   private static boolean matrixfontJsonFlag = false;
   private BufferBuilder bufferBuilder;
   private static boolean matrixfontParsedActive = false;
   private  float metricsFont;
   private static boolean fontMsdfFlag = false;

   private void handleFontBuffer(String[] strings, float[] fontBufferValues, float[] fontBufferValues2, float fontBufferValue, int fontBufferLimit, Color color) {
      MatrixStack matrixStack = MatrixClient.fontManager.getMatrixStack();
      Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
      float redScaled = color.getRed() / 255.0F;
      float greenScaled = color.getGreen() / 255.0F;
      float blueScaled = color.getBlue() / 255.0F;
      float fontBufferValue2 = color.getAlpha() / 255.0F * MatrixRoundedUtils.panelOverlayAmount;
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
            MatrixRoundedUtils.handleTextureView(bufferBuilder.end(), identifier);
         }
      }
   }

   public MatrixfontAbcdefghijklmnopqrstuvwxyzTexture(float matrixfontTextureValue) {
      super(getAsStream(), matrixfontTextureValue);

      boolean flag;
      try {
         updateMatrixfontParsed();
         float matrixfontTextureValue2 = super.getAbcdefghijklmnopqrstuvwxyzMatrixfont("The quick brown fox ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 0123456789");
         float matrixfontMatrixclient = getMatrixfontMatrixclient("The quick brown fox ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 0123456789");
         this.brownAmount = (
               matrixfontMatrixclient > 0.0F ? matrixfontTextureValue2 / matrixfontMatrixclient : super.getAbcdefghijklmnopqrstuvwxyzBrown() / matrixfontParsedAmount
            )
            * 1.0F;
         this.abcdefghijklmnopqrstuvwxyzBrownAmount = super.getAbcdefghijklmnopqrstuvwxyzBrown();
         this.metricsFont = this.getMetricsFont();
         flag = !matrixfontParsedActive;
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
   public void handleFlag(String[] texts, float[] matrixfontTextureValues, int matrixfontTextureValue, float vertexTextureValue, Color color) {
      if (!this.brownActive) {
         super.handleFlag(texts, matrixfontTextureValues, matrixfontTextureValue, vertexTextureValue, color);
      } else {
         this.handleFontBuffer(texts, matrixfontTextureValues, null, vertexTextureValue, matrixfontTextureValue, color);
      }
   }

   @Override
   public void handleForStringFloat(String matrixfontTextureText, float matrixfontTextureValue, float f13, Color color) {
      this.handleFontBuffer(matrixfontTextureText, matrixfontTextureValue, f13, color);
   }

   public void updateBufferBuilder() {
      BufferBuilder bufferBuilder = this.bufferBuilder;
      this.fontBufferActive = false;
      this.bufferBuilder = null;
      if (bufferBuilder != null) {
         MatrixRoundedUtils.handleTextureView(bufferBuilder.end(), identifier);
      }
   }

   @Override
   public void handleFlag(String[] strings, float[] matrixfontTextureValues, float[] matrixfontTextureValues2, int matrixfontTextureValue, Color color) {
      if (!this.brownActive) {
         super.handleFlag(strings, matrixfontTextureValues, matrixfontTextureValues2, matrixfontTextureValue, color);
      } else {
         this.handleFontBuffer(strings, matrixfontTextureValues, matrixfontTextureValues2, 0.0F, matrixfontTextureValue, color);
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

   private static long getTime(int matrixfontTextureValue, int charAt) {
      return (long)matrixfontTextureValue << 32 | charAt & 4294967295L;
   }

   @Override
   public float getAbcdefghijklmnopqrstuvwxyzMatrixfont(String matrixfontText) {
      matrixfontText = CardStatusUtilsFlag.getOrDefault(matrixfontText);
      if (!this.brownActive) {
         return super.getAbcdefghijklmnopqrstuvwxyzMatrixfont(matrixfontText);
      } else if (matrixfontText != null && !matrixfontText.isEmpty()) {
         float matrixfontTotal = 0.0F;
         int matrixfontValue = -1;

         for (int i = 0; i < matrixfontText.length(); i++) {
            char charAt = matrixfontText.charAt(i);
            MatrixfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph matrixfontMatrixclient = getMatrixfontMatrixclient(charAt);
            if (matrixfontMatrixclient == null) {
               matrixfontValue = charAt;
            } else {
               if (matrixfontPngFlag && matrixfontValue != -1) {
                  Float matrixfont_png_map = CORZFONT_PNG_MAP.get(getTime(matrixfontValue, charAt));
                  if (matrixfont_png_map != null) {
                     matrixfontTotal += matrixfont_png_map * this.brownAmount;
                  }
               }

               matrixfontTotal += matrixfontMatrixclient.matrixfontAmount * this.brownAmount;
               matrixfontValue = charAt;
            }
         }

         return matrixfontTotal;
      } else {
         return 0.0F;
      }
   }

   @Override
   public float getAbcdefghijklmnopqrstuvwxyzBrown() {
      return this.brownActive ? this.abcdefghijklmnopqrstuvwxyzBrownAmount : super.getAbcdefghijklmnopqrstuvwxyzBrown();
   }

   private static MatrixfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph getMatrixfontMatrixclient(int index) {
      MatrixfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph matrixfontMatrixclient = index < 256 ? matrixfontMatrixclientGlyphs[index] : CORZFONT_CORZCLIENT_MAP.get(index);
      if (matrixfontMatrixclient == null && index != 32) {
         matrixfontMatrixclient = matrixfontParsedGlyph;
      }

      return matrixfontMatrixclient;
   }

   private static synchronized void updateMatrixfontParsed()  throws java.io.IOException {
      if (!matrixfontJsonFlag && !matrixfontParsedActive) {
         try {
            MsdfFontData fromJson4;
            try (InputStream resourceAsStream = MatrixClient.class.getClassLoader().getResourceAsStream("assets/matrixclient/fonts/matrixfont.json")) {
               if (resourceAsStream == null) {
                  throw new IllegalStateException("matrixfont.json missing from resources");
               }

               fromJson4 = (MsdfFontData)new Gson().fromJson(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8), MsdfFontData.class);
            }

            if (fromJson4 != null && fromJson4.class172 != null && fromJson4.class245 != null && fromJson4.parsedMatrixfontItems != null) {
               matrixfontParsedAmount = fromJson4.class245.parsedMatrixfontAmount;
               parsedMatrixfontAmount = fromJson4.class245.parsedMatrixfontAmount2;
               float parsedMatrixfontAmount2 = fromJson4.class172.parsedMatrixfontAmount;
               float parsedMatrixfontAmount22 = fromJson4.class172.parsedMatrixfontAmount2;

               for (MsdfFontData.Glyph glyph : fromJson4.parsedMatrixfontItems) {
                  CORZFONT_CORZCLIENT_MAP.put(glyph.value, new MatrixfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph(glyph, parsedMatrixfontAmount2, parsedMatrixfontAmount22));
               }

               if (fromJson4.items != null) {
                  for (MsdfFontData.Kerning kerning : fromJson4.items) {
                     CORZFONT_PNG_MAP.put(getTime(kerning.value, kerning.value2), kerning.amount);
                  }
               }

               for (int i = 0; i < matrixfontMatrixclientGlyphs.length; i++) {
                  matrixfontMatrixclientGlyphs[i] = CORZFONT_CORZCLIENT_MAP.get(i);
               }

               matrixfontParsedGlyph = CORZFONT_CORZCLIENT_MAP.get(63);
               matrixfontPngFlag = !CORZFONT_PNG_MAP.isEmpty();

               try (InputStream resourceAsStream2 = MatrixClient.class.getClassLoader().getResourceAsStream("assets/matrixclient/fonts/matrixfont.png")) {
                  if (resourceAsStream2 == null) {
                     throw new IllegalStateException("matrixfont.png missing from resources");
                  }

                  readMatrixfontParsed = NativeImage.read(resourceAsStream2);
               }

               matrixfontJsonFlag = true;
            } else {
               throw new IllegalStateException("matrixfont.json parsed empty");
            }
         } catch (Throwable e) {
            matrixfontParsedActive = true;
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
            MatrixStack matrixStack = MatrixClient.fontManager.getMatrixStack();
            Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
            float redScaled = color.getRed() / 255.0F;
            float greenScaled = color.getGreen() / 255.0F;
            float blueScaled = color.getBlue() / 255.0F;
            float fontBufferValue3 = color.getAlpha() / 255.0F * MatrixRoundedUtils.panelOverlayAmount;
            BufferBuilder bufferBuilder = this.getAmountAbcdefghijklmnopqrstuvwxyz(this.fontBufferActive ? this.bufferBuilder : null, positionMatrix, fontBufferText, fontBufferValue, f13, redScaled, greenScaled, blueScaled, fontBufferValue3);
            if (this.fontBufferActive) {
               this.bufferBuilder = bufferBuilder;
            } else {
               if (bufferBuilder != null) {
                  MatrixRoundedUtils.handleTextureView(bufferBuilder.end(), identifier);
               }
            }
         }
      }
   }

   private BufferBuilder getAmountAbcdefghijklmnopqrstuvwxyz(
      BufferBuilder begin2, Matrix4f matrix4f, String matrixfontTextureText, float matrixfontTextureValue, float f13, float redScaled, float greenScaled, float blueScaled, float matrixfontTextureValue6
   ) {
      float matrixfontTextureValue7 = f13 + this.metricsFont;
      float matrixfontTextureTotal = matrixfontTextureValue;
      int charAt8 = -1;

      for (int i = 0; i < matrixfontTextureText.length(); i++) {
         char charAt = matrixfontTextureText.charAt(i);
         MatrixfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph matrixfontMatrixclient = getMatrixfontMatrixclient(charAt);
         if (matrixfontMatrixclient == null) {
            charAt8 = charAt;
         } else {
            if (matrixfontPngFlag && charAt8 != -1) {
               Float matrixfont_png_map = CORZFONT_PNG_MAP.get(getTime(charAt8, charAt));
               if (matrixfont_png_map != null) {
                  matrixfontTextureTotal += matrixfont_png_map * this.brownAmount;
               }
            }

            if (matrixfontMatrixclient.isAmount()) {
               if (begin2 == null) {
                  begin2 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
               }

               float matrixfontTextureValue9 = matrixfontTextureValue7 - matrixfontMatrixclient.planeTop * this.brownAmount;
               float matrixfontTextureValue10 = matrixfontMatrixclient.planeWidth * this.brownAmount;
               float matrixfontTextureValue11 = matrixfontMatrixclient.planeHeight * this.brownAmount;
               begin2.vertex(matrix4f, matrixfontTextureTotal, matrixfontTextureValue9 + matrixfontTextureValue11, 0.0F).texture(matrixfontMatrixclient.textureULeft, matrixfontMatrixclient.glyphAmount).color(redScaled, greenScaled, blueScaled, matrixfontTextureValue6);
               begin2.vertex(matrix4f, matrixfontTextureTotal + matrixfontTextureValue10, matrixfontTextureValue9 + matrixfontTextureValue11, 0.0F).texture(matrixfontMatrixclient.textureURight, matrixfontMatrixclient.glyphAmount).color(redScaled, greenScaled, blueScaled, matrixfontTextureValue6);
               begin2.vertex(matrix4f, matrixfontTextureTotal + matrixfontTextureValue10, matrixfontTextureValue9, 0.0F).texture(matrixfontMatrixclient.textureURight, matrixfontMatrixclient.textureVTop).color(redScaled, greenScaled, blueScaled, matrixfontTextureValue6);
               begin2.vertex(matrix4f, matrixfontTextureTotal, matrixfontTextureValue9, 0.0F).texture(matrixfontMatrixclient.textureULeft, matrixfontMatrixclient.textureVTop).color(redScaled, greenScaled, blueScaled, matrixfontTextureValue6);
            }

            matrixfontTextureTotal += matrixfontMatrixclient.matrixfontAmount * this.brownAmount;
            charAt8 = charAt;
         }
      }

      return begin2;
   }

   private static InputStream getAsStream() {
      return MatrixClient.class.getClassLoader().getResourceAsStream("assets/matrixclient/font/font.ttf");
   }

   @Override
   public void handle(String matrixfontTextureText, float matrixfontTextureValue, float f13, Color color) {
      this.handleFontBuffer(matrixfontTextureText, matrixfontTextureValue, f13, color);
   }

   private static float getMatrixfontMatrixclient(String matrixfontMatrixclientText) {
      float matrixfontMatrixclient = 0.0F;
      int matrixfontMatrixclientValue = -1;

      for (int i = 0; i < matrixfontMatrixclientText.length(); i++) {
         char charAt = matrixfontMatrixclientText.charAt(i);
         MatrixfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph matrixfont_matrixclient_map = CORZFONT_CORZCLIENT_MAP.get(Integer.valueOf(charAt));
         if (matrixfont_matrixclient_map == null && charAt != ' ') {
            matrixfont_matrixclient_map = CORZFONT_CORZCLIENT_MAP.get(63);
         }

         if (matrixfont_matrixclient_map == null) {
            matrixfontMatrixclientValue = charAt;
         } else {
            if (matrixfontMatrixclientValue != -1) {
               Float matrixfont_png_map = CORZFONT_PNG_MAP.get(getTime(matrixfontMatrixclientValue, charAt));
               if (matrixfont_png_map != null) {
                  matrixfontMatrixclient += matrixfont_png_map;
               }
            }

            matrixfontMatrixclient += matrixfont_matrixclient_map.matrixfontAmount;
            matrixfontMatrixclientValue = charAt;
         }
      }

      return matrixfontMatrixclient;
   }

   private static void updateMsdfTexture() {
      if (!fontMsdfFlag && readMatrixfontParsed != null) {
         NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(() -> "matrix_msdf_matrixfont", readMatrixfontParsed);
         MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, nativeImageBackedTexture);
         fontMsdfFlag = true;
      }
   }

   private static final class Glyph {
      final float textureVTop;
      final float matrixfontAmount;
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
         this.matrixfontAmount = glyph.amount;
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
