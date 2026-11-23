package com.vortex.client.render;

import com.vortex.client.VortexClient;
import com.vortex.client.config.MsdfFontData;
import com.google.gson.Gson;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public final class VertexTexturePeek {
   private boolean textureReadActive;
   private float fontScale;
   private final String descriptor;
   private boolean parsedVortexfontActive;
   private final Identifier vortexclient;
   private VertexTexturePeek.Glyph[] vortexclientClass137sParsedCor;
   private  boolean vortexclientFlag;
   private static final Map<String, VertexTexturePeek> INSTANCES = new HashMap<>();
   private final Map<Integer, VertexTexturePeek.Glyph> parsedVortexfontGlyphMap = new HashMap<>();
   private float parsedVortexfontAmount;
   private final Map<Long, Float> parsedVortexfontFloatMap = new HashMap<>();
   private int hashShift;
   private long[] vortexclientTimes;
   private NativeImage readParsedVortexfont;
   private int parsedVortexfontValue;
   private float[] vortexclientAmounts;

   public static VertexTexturePeek getIfAbsent(String ifAbsentText) {
      return INSTANCES.computeIfAbsent(ifAbsentText, VertexTexturePeek::new);
   }

   public static VertexTexturePeek getWave() {
      return getIfAbsent("wave");
   }

   private void updateParsedVortexfont()  throws java.io.IOException {
      String parsedVortexfontText = "assets/vortexclient/fonts/hud/" + this.descriptor + "/";

      MsdfFontData fromJson4;
      try (InputStream asStream = getAsStream(parsedVortexfontText + "font.json")) {
         if (asStream == null) {
            throw new IllegalStateException("missing " + parsedVortexfontText + "font.json");
         }

         fromJson4 = (MsdfFontData)new Gson().fromJson(new InputStreamReader(asStream, StandardCharsets.UTF_8), MsdfFontData.class);
      }

      if (fromJson4 != null && fromJson4.class172 != null && fromJson4.class245 != null && fromJson4.parsedVortexfontItems != null) {
         this.fontScale = fromJson4.class245.parsedVortexfontAmount;
         this.parsedVortexfontAmount = fromJson4.class245.parsedVortexfontAmount2;
         float parsedVortexfontAmount = fromJson4.class172.parsedVortexfontAmount;
         float parsedVortexfontAmount2 = fromJson4.class172.parsedVortexfontAmount2;

         for (MsdfFontData.Glyph glyph : fromJson4.parsedVortexfontItems) {
            this.parsedVortexfontGlyphMap.put(glyph.value, new VertexTexturePeek.Glyph(glyph, parsedVortexfontAmount, parsedVortexfontAmount2));
         }

         if (fromJson4.items != null) {
            for (MsdfFontData.Kerning kerning : fromJson4.items) {
               this.parsedVortexfontFloatMap.put(getTime(kerning.value, kerning.value2), kerning.amount);
            }
         }

         try (InputStream asStream2 = getAsStream(parsedVortexfontText + "font.png")) {
            if (asStream2 == null) {
               throw new IllegalStateException("missing " + parsedVortexfontText + "font.png");
            }

            this.readParsedVortexfont = NativeImage.read(asStream2);
         }

         int parsedVortexfontValue = -1;

         for (int parsedVortexfontLimit : this.parsedVortexfontGlyphMap.keySet()) {
            if (parsedVortexfontLimit >= 0 && parsedVortexfontLimit <= 65535 && parsedVortexfontLimit > parsedVortexfontValue) {
               parsedVortexfontValue = parsedVortexfontLimit;
            }
         }

         VertexTexturePeek.Glyph[] vortexclientClass137s = new VertexTexturePeek.Glyph[parsedVortexfontValue + 1];

         for (Entry entry : this.parsedVortexfontGlyphMap.entrySet()) {
            int index = (Integer)entry.getKey();
            if (index >= 0 && index <= 65535) {
               vortexclientClass137s[index] = (VertexTexturePeek.Glyph)entry.getValue();
            }
         }

         this.vortexclientClass137sParsedCor = vortexclientClass137s;
         int value = 0;

         for (long keyItem : this.parsedVortexfontFloatMap.keySet()) {
            if (keyItem >>> 32 <= 65535L && (keyItem & 4294967295L) <= 65535L) {
               value++;
            }
         }

         int parsedVortexfontSize = 2;

         while (parsedVortexfontSize < value * 2) {
            parsedVortexfontSize <<= 1;
         }

         long[] vortexclientTimes = new long[parsedVortexfontSize];
         float[] vortexclientAmounts = new float[parsedVortexfontSize];
         Arrays.fill(vortexclientTimes, Long.MIN_VALUE);
         int value2 = 64 - Integer.numberOfTrailingZeros(parsedVortexfontSize);

         for (Entry entryItem : this.parsedVortexfontFloatMap.entrySet()) {
            long parsedVortexfontValue3 = (Long)entryItem.getKey();
            if (parsedVortexfontValue3 >>> 32 <= 65535L && (parsedVortexfontValue3 & 4294967295L) <= 65535L) {
               int index2 = (int)(parsedVortexfontValue3 * -7046029254386353131L >>> value2);

               while (vortexclientTimes[index2] != Long.MIN_VALUE) {
                  index2 = index2 + 1 & parsedVortexfontSize - 1;
               }

               vortexclientTimes[index2] = parsedVortexfontValue3;
               vortexclientAmounts[index2] = (Float)entryItem.getValue();
            }
         }

         this.vortexclientTimes = vortexclientTimes;
         this.vortexclientAmounts = vortexclientAmounts;
         this.hashShift = value2;
         this.parsedVortexfontValue = value;
         this.parsedVortexfontActive = true;
      } else {
         throw new IllegalStateException("empty descriptor: " + this.descriptor);
      }
   }

   public static VertexTexturePeek getSuisse() {
      return getIfAbsent("suisse");
   }

   public float getVortexclientAmounts(String vortexclientText, float vortexclientValue) {
      if (this.parsedVortexfontActive && vortexclientText != null && !vortexclientText.isEmpty()) {
         float vortexclientFactor = getAmount(vortexclientValue);
         float vortexclientAmounts = 0.0F;
         int vertexTextureValue = -1;

         for (int i = 0; i < vortexclientText.length(); i++) {
            char charAt = vortexclientText.charAt(i);
            VertexTexturePeek.Glyph vortexclient = this.getVortexclient(charAt);
            if (vortexclient == null) {
               vertexTextureValue = charAt;
            } else {
               if (vertexTextureValue != -1) {
                  int vortexclientTimes = this.getVortexclientTimes(getTime(vertexTextureValue, charAt));
                  if (vortexclientTimes >= 0) {
                     vortexclientAmounts += this.vortexclientAmounts[vortexclientTimes] * vortexclientFactor;
                  }
               }

               vortexclientAmounts += vortexclient.vortexclientAmount * vortexclientFactor;
               vertexTextureValue = charAt;
            }
         }

         return vortexclientAmounts;
      } else {
         return 0.0F;
      }
   }

   public void handleAmountPosition(String positionText, float positionValue, float positionValue2, int vertexTextureValue, int positionValue4, float vortexclientValue) {
      if (this.isVortexclientFlag() && positionText != null && !positionText.isEmpty()) {
         this.updateTextureRead();
         if (this.textureReadActive) {
            float vortexclientAmounts = this.getVortexclientAmounts(positionText, vortexclientValue);
            if (vortexclientAmounts <= 0.0F) {
               this.handlePeekEnd(positionText, positionValue, positionValue2, vertexTextureValue, vortexclientValue);
            } else {
               MatrixStack matrixStack = VortexClient.fontManager.getMatrixStack();
               Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
               float positionFactor = getAmount(vortexclientValue);
               float positionValue6 = positionValue2 + this.parsedVortexfontAmount * positionFactor;
               float positionTotal = positionValue;
               int charAt7 = -1;
               BufferBuilder bufferBuilder = null;

               for (int i = 0; i < positionText.length(); i++) {
                  char charAt = positionText.charAt(i);
                  VertexTexturePeek.Glyph vortexclient = this.getVortexclient(charAt);
                  if (vortexclient == null) {
                     charAt7 = charAt;
                  } else {
                     if (charAt7 != -1) {
                        int vortexclientTimes = this.getVortexclientTimes(getTime(charAt7, charAt));
                        if (vortexclientTimes >= 0) {
                           positionTotal += this.vortexclientAmounts[vortexclientTimes] * positionFactor;
                        }
                     }

                     if (vortexclient.isAmount()) {
                        if (bufferBuilder == null) {
                           bufferBuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                        }

                        float positionValue8 = positionValue6 - vortexclient.glyphOffsetY * positionFactor;
                        float positionValue9 = vortexclient.glyphAmount * positionFactor;
                        float positionValue10 = vortexclient.positionAmount * positionFactor;
                        int positionValue11 = getValue(vertexTextureValue, positionValue4, (positionTotal - positionValue) / vortexclientAmounts);
                        int positionValue12 = getValue(vertexTextureValue, positionValue4, (positionTotal + positionValue9 - positionValue) / vortexclientAmounts);
                        handleVertexTexture(bufferBuilder, positionMatrix, positionTotal, positionValue8 + positionValue10, vortexclient.uvLeft, vortexclient.textureAmount, positionValue11);
                        handleVertexTexture(bufferBuilder, positionMatrix, positionTotal + positionValue9, positionValue8 + positionValue10, vortexclient.uvRight, vortexclient.textureAmount, positionValue12);
                        handleVertexTexture(bufferBuilder, positionMatrix, positionTotal + positionValue9, positionValue8, vortexclient.uvRight, vortexclient.uvTop, positionValue12);
                        handleVertexTexture(bufferBuilder, positionMatrix, positionTotal, positionValue8, vortexclient.uvLeft, vortexclient.uvTop, positionValue11);
                     }

                     positionTotal += vortexclient.vortexclientAmount * positionFactor;
                     charAt7 = charAt;
                  }
               }

               if (bufferBuilder != null) {
                  VortexRoundedUtils.handleTextureView(bufferBuilder.end(), this.vortexclient);
               }
            }
         }
      }
   }

   private static void handleVertexTexture(BufferBuilder buffer, Matrix4f matrix, float vertexTextureValue, float vertexTextureValue2, float vertexTextureValue3, float vertexTextureValue4, int vertexTextureValue5) {
      float vertexTextureValue6 = (vertexTextureValue5 >>> 24 & 0xFF) / 255.0F * VortexRoundedUtils.panelOverlayAmount;
      buffer.vertex(matrix, vertexTextureValue, vertexTextureValue2, 0.0F).texture(vertexTextureValue3, vertexTextureValue4).color((vertexTextureValue5 >> 16 & 0xFF) / 255.0F, (vertexTextureValue5 >> 8 & 0xFF) / 255.0F, (vertexTextureValue5 & 0xFF) / 255.0F, vertexTextureValue6);
   }

   public float getAmountFlag(char ch, float vertexTextureValue) {
      if (!this.parsedVortexfontActive) {
         return 0.0F;
      } else {
         VertexTexturePeek.Glyph vortexclient = this.getVortexclient(ch);
         if (vortexclient == null) {
            return 0.0F;
         } else {
            float vertexTextureValue2 = 0.0F;
            return vertexTextureValue2 + vortexclient.vortexclientAmount * getAmount(vertexTextureValue);
         }
      }
   }

   public static VertexTexturePeek getRegular() {
      return getIfAbsent("sf_regular");
   }

   private BufferBuilder getAmountTexture(
      BufferBuilder amountTexture, Matrix4f matrix4f, String textureText, int textureValue, int textureLimit, float peekEndValue, float textureValue3, float textureFactor, float textureValue4, float textureValue5, float textureValue6, float textureValue7
   ) {
      float x2 = peekEndValue;
      int vertexTextureValue = -1;

      for (int i = textureValue; i < textureLimit; i++) {
         char charAt = textureText.charAt(i);
         VertexTexturePeek.Glyph vortexclient = this.getVortexclient(charAt);
         if (vortexclient == null) {
            vertexTextureValue = charAt;
         } else {
            if (vertexTextureValue != -1) {
               int vortexclientTimes = this.getVortexclientTimes(getTime(vertexTextureValue, charAt));
               if (vortexclientTimes >= 0) {
                  x2 += this.vortexclientAmounts[vortexclientTimes] * textureFactor;
               }
            }

            if (vortexclient.isAmount()) {
               if (amountTexture == null) {
                  amountTexture = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
               }

               float textureValue9 = textureValue3 - vortexclient.glyphOffsetY * textureFactor;
               float textureValue10 = vortexclient.glyphAmount * textureFactor;
               float textureValue11 = vortexclient.positionAmount * textureFactor;
               amountTexture.vertex(matrix4f, x2, textureValue9 + textureValue11, 0.0F).texture(vortexclient.uvLeft, vortexclient.textureAmount).color(textureValue4, textureValue5, textureValue6, textureValue7);
               amountTexture.vertex(matrix4f, x2 + textureValue10, textureValue9 + textureValue11, 0.0F).texture(vortexclient.uvRight, vortexclient.textureAmount).color(textureValue4, textureValue5, textureValue6, textureValue7);
               amountTexture.vertex(matrix4f, x2 + textureValue10, textureValue9, 0.0F).texture(vortexclient.uvRight, vortexclient.uvTop).color(textureValue4, textureValue5, textureValue6, textureValue7);
               amountTexture.vertex(matrix4f, x2, textureValue9, 0.0F).texture(vortexclient.uvLeft, vortexclient.uvTop).color(textureValue4, textureValue5, textureValue6, textureValue7);
            }

            x2 += vortexclient.vortexclientAmount * textureFactor;
            vertexTextureValue = charAt;
         }
      }

      return amountTexture;
   }

   private int getVortexclientTimes(long vortexclientValue) {
      if (this.parsedVortexfontValue == 0) {
         return -1;
      } else {
         long[] vortexclientTimes = this.vortexclientTimes;
         int vortexclientValue2 = vortexclientTimes.length - 1;
         int index = (int)(vortexclientValue * -7046029254386353131L >>> this.hashShift);

         while (true) {
            long vortexclientValue3 = vortexclientTimes[index];
            if (vortexclientValue3 == vortexclientValue) {
               return index;
            }

            if (vortexclientValue3 == Long.MIN_VALUE) {
               return -1;
            }

            index = index + 1 & vortexclientValue2;
         }
      }
   }

   private VertexTexturePeek.Glyph getVortexclient(int index) {
      return index < this.vortexclientClass137sParsedCor.length ? this.vortexclientClass137sParsedCor[index] : null;
   }

   public static VertexTexturePeek getIcon() {
      return getIfAbsent("icon");
   }

   public void handlePeekEnd(String peekEndText, float[] peekEndValues, int peekEndLimit, float peekEndValue, int peekEndValue2, float vertexTextureValue) {
      if (this.isVortexclientFlag() && peekEndText != null && peekEndLimit > 0) {
         this.updateTextureRead();
         if (this.textureReadActive) {
            MatrixStack matrixStack = VortexClient.fontManager.getMatrixStack();
            Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
            float peekEndFactor = getAmount(vertexTextureValue);
            float peekEndValue4 = (peekEndValue2 >>> 24 & 0xFF) / 255.0F * VortexRoundedUtils.panelOverlayAmount;
            float peekEndValue5 = (peekEndValue2 >> 16 & 0xFF) / 255.0F;
            float peekEndValue6 = (peekEndValue2 >> 8 & 0xFF) / 255.0F;
            float peekEndValue7 = (peekEndValue2 & 0xFF) / 255.0F;
            float peekEndValue8 = peekEndValue + this.parsedVortexfontAmount * peekEndFactor;
            BufferBuilder bufferBuilder = null;

            for (int i = 0; i < peekEndLimit; i++) {
               bufferBuilder = this.getAmountTexture(bufferBuilder, positionMatrix, peekEndText, i, i + 1, peekEndValues[i], peekEndValue8, peekEndFactor, peekEndValue5, peekEndValue6, peekEndValue7, peekEndValue4);
            }

            if (bufferBuilder != null) {
               VortexRoundedUtils.handleTextureView(bufferBuilder.end(), this.vortexclient);
            }
         }
      }
   }

   private static float getAmount(float vertexTextureValue) {
      return vertexTextureValue * 0.5F;
   }

   private static long getTime(int vertexTextureValue, int charAt) {
      return (long)vertexTextureValue << 32 | charAt & 4294967295L;
   }

   private VertexTexturePeek(String descriptor) {
      this.vortexclientClass137sParsedCor = new VertexTexturePeek.Glyph[0];
      this.vortexclientTimes = new long[0];
      this.vortexclientAmounts = new float[0];
      this.descriptor = descriptor;
      this.vortexclient = Identifier.of("vortexclient", "font/hud_" + descriptor);

      try {
         this.updateParsedVortexfont();
      } catch (Throwable e) {
         this.vortexclientFlag = true;
         e.printStackTrace();
      }
   }

   private static int getValue(int vertexTextureValue, int vertexTextureValue2, float vertexTextureFactor) {
      vertexTextureFactor = vertexTextureFactor < 0.0F ? 0.0F : (vertexTextureFactor > 1.0F ? 1.0F : vertexTextureFactor);
      int vertexTextureValue3 = vertexTextureValue >>> 24 & 0xFF;
      int vertexTextureValue4 = vertexTextureValue >> 16 & 0xFF;
      int vertexTextureValue5 = vertexTextureValue >> 8 & 0xFF;
      int vertexTextureValue6 = vertexTextureValue & 0xFF;
      int vertexTextureValue7 = vertexTextureValue2 >>> 24 & 0xFF;
      int vertexTextureValue8 = vertexTextureValue2 >> 16 & 0xFF;
      int vertexTextureValue9 = vertexTextureValue2 >> 8 & 0xFF;
      int vertexTextureValue10 = vertexTextureValue2 & 0xFF;
      return (int)(vertexTextureValue3 + (vertexTextureValue7 - vertexTextureValue3) * vertexTextureFactor) << 24
         | (int)(vertexTextureValue4 + (vertexTextureValue8 - vertexTextureValue4) * vertexTextureFactor) << 16
         | (int)(vertexTextureValue5 + (vertexTextureValue9 - vertexTextureValue5) * vertexTextureFactor) << 8
         | (int)(vertexTextureValue6 + (vertexTextureValue10 - vertexTextureValue6) * vertexTextureFactor);
   }

   private void updateTextureRead() {
      if (!this.textureReadActive && this.readParsedVortexfont != null) {
         NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(() -> "hud_font_" + this.descriptor, this.readParsedVortexfont);
         MinecraftClient.getInstance().getTextureManager().registerTexture(this.vortexclient, nativeImageBackedTexture);
         this.textureReadActive = true;
      }
   }

   public void handlePeekEnd(String peekEndText, float peekEndValue, float peekEndValue2, int peekEndValue3, float vertexTextureValue) {
      if (this.isVortexclientFlag() && peekEndText != null && !peekEndText.isEmpty()) {
         this.updateTextureRead();
         if (this.textureReadActive) {
            MatrixStack matrixStack = VortexClient.fontManager.getMatrixStack();
            Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
            float peekEndFactor = getAmount(vertexTextureValue);
            float peekEndValue5 = (peekEndValue3 >>> 24 & 0xFF) / 255.0F * VortexRoundedUtils.panelOverlayAmount;
            float peekEndValue6 = (peekEndValue3 >> 16 & 0xFF) / 255.0F;
            float peekEndValue7 = (peekEndValue3 >> 8 & 0xFF) / 255.0F;
            float peekEndValue8 = (peekEndValue3 & 0xFF) / 255.0F;
            float peekEndValue9 = peekEndValue2 + this.parsedVortexfontAmount * peekEndFactor;
            BufferBuilder amountTexture = this.getAmountTexture(null, positionMatrix, peekEndText, 0, peekEndText.length(), peekEndValue, peekEndValue9, peekEndFactor, peekEndValue6, peekEndValue7, peekEndValue8, peekEndValue5);
            if (amountTexture != null) {
               VortexRoundedUtils.handleTextureView(amountTexture.end(), this.vortexclient);
            }
         }
      }
   }

   public void handlePeekEnd(String[] strings, float[] peekEndValues, float[] peekEndValues2, int[] peekEndValues3, int peekEndLimit, float peekEndValue) {
      if (this.isVortexclientFlag()) {
         MatrixStack matrixStack = VortexClient.fontManager.getMatrixStack();
         Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
         float peekEndFactor = getAmount(peekEndValue);
         BufferBuilder bufferBuilder = null;

         for (int i = 0; i < peekEndLimit; i++) {
            String peekEndText = strings[i];
            if (peekEndText != null && !peekEndText.isEmpty()) {
               this.updateTextureRead();
               if (!this.textureReadActive) {
                  break;
               }

               int peekEndValue2 = peekEndValues3[i];
               float peekEndValue3 = (peekEndValue2 >>> 24 & 0xFF) / 255.0F * VortexRoundedUtils.panelOverlayAmount;
               float peekEndValue4 = (peekEndValue2 >> 16 & 0xFF) / 255.0F;
               float peekEndValue5 = (peekEndValue2 >> 8 & 0xFF) / 255.0F;
               float peekEndValue6 = (peekEndValue2 & 0xFF) / 255.0F;
               float peekEndValue7 = peekEndValues2[i] + this.parsedVortexfontAmount * peekEndFactor;
               bufferBuilder = this.getAmountTexture(bufferBuilder, positionMatrix, peekEndText, 0, peekEndText.length(), peekEndValues[i], peekEndValue7, peekEndFactor, peekEndValue4, peekEndValue5, peekEndValue6, peekEndValue3);
            }
         }

         if (bufferBuilder != null) {
            VortexRoundedUtils.handleTextureView(bufferBuilder.end(), this.vortexclient);
         }
      }
   }

   public boolean isVortexclientFlag() {
      return this.parsedVortexfontActive && !this.vortexclientFlag;
   }

   public float getAmountForFloat(float vertexTextureValue) {
      return this.fontScale * getAmount(vertexTextureValue);
   }

   private static InputStream getAsStream(String streamText) {
      return VortexClient.class.getClassLoader().getResourceAsStream(streamText);
   }

   public static VertexTexturePeek getIconsminced() {
      return getIfAbsent("iconsminced");
   }

   public float getAmountParsedVortexfontAmount(float vertexTextureValue) {
      return this.parsedVortexfontAmount * getAmount(vertexTextureValue);
   }

   private static final class Glyph {
      final float uvRight;
      final float positionAmount;
      final float textureAmount;
      final float vortexclientAmount;
      final float glyphAmount;
      final float uvTop;
      final float uvLeft;
      final float glyphOffsetY;

      Glyph(MsdfFontData.Glyph glyph, float glyphDivisor, float glyphDivisor2) {
         this.vortexclientAmount = glyph.amount;
         MsdfFontData.Bounds class1912 = glyph.class1912;
         if (class1912 != null) {
            this.uvLeft = class1912.amount4 / glyphDivisor;
            this.uvRight = class1912.amount3 / glyphDivisor;
            this.uvTop = 1.0F - class1912.amount2 / glyphDivisor2;
            this.textureAmount = 1.0F - class1912.amount / glyphDivisor2;
         } else {
            this.uvLeft = this.uvRight = this.uvTop = this.textureAmount = 0.0F;
         }

         MsdfFontData.Bounds class191 = glyph.class191;
         if (class191 != null) {
            this.glyphAmount = class191.amount3 - class191.amount4;
            this.positionAmount = class191.amount2 - class191.amount;
            this.glyphOffsetY = class191.amount2;
         } else {
            this.glyphAmount = this.positionAmount = this.glyphOffsetY = 0.0F;
         }
      }

      boolean isAmount() {
         return this.glyphAmount > 0.0F && this.positionAmount > 0.0F && (this.uvRight != this.uvLeft || this.textureAmount != this.uvTop);
      }
   }
}
