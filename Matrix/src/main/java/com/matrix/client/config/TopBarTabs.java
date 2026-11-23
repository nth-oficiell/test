package com.matrix.client.config;

import com.matrix.client.MatrixClient;
import com.matrix.client.gui.MatrixPeekUtilsRender;
import com.matrix.client.gui.TwoDidUtils;
import com.matrix.client.render.CardMatrixUtils;
import com.matrix.client.render.MatrixClearUtils;
import com.matrix.client.render.MatrixEspUtils;
import com.matrix.client.categories.settings.ClientSounds;
import com.matrix.client.render.MatrixIdentifierUtils;
import com.matrix.client.render.MatrixIdentifierUtils.Spring;
import com.matrix.client.render.MatrixPeekUtilsIdentifier;
import com.matrix.client.render.MatrixRoundedUtils;
import com.matrix.client.render.MatrixStackUtilsAmount;
import com.matrix.client.render.MatrixStackUtilsAmount.Anims;
import com.matrix.client.render.MatrixStackUtilsAmount.Hits;
import com.matrix.client.render.RenderMatrixUtilsRelease;
import com.matrix.client.render.VertexTextureFont;
import com.matrix.client.utils.IconsPngType;
import com.matrix.client.utils.PngIcons;
import ai.auth.Module;
import java.awt.Color;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class TopBarTabs {
   private static final Identifier LOGO_TEXTURE = Identifier.of("matrixclient", "textures/matrix_mark.png");
   private static final String VERSION_TEXT = "v2.0.1";
   private static final int CHIP_WIDTH = 42;
   private static final int CHIP_GAP = 2;
   private static long introStartNanos;
   private static long lastFrameNanos;
   private static int lastScreenHash;
   private static boolean justCreated;
   private final List<IconsPngType> categories;
   private final int tabCount;
   private final int panelX;
   private final int panelY;
   private final int barWidth;
   private final int barHeight;

   private final Hits hitRegions = new Hits();
   private final Anims rowAnims = new Anims();
   private final Spring underlineSpring = new Spring(0.0);
   private final float[] chipXs;
   private final float[] chipWidths;
   private final float[] hoverAmounts;
   private final float[] activeAmounts;
   private final String[] pressAnimIds;
   private final Runnable[] pageActions;
   private float labelScale;
   private float underlineX;
   private float entranceAlpha;
   private float entranceScale;
   private float introElapsedMs;
   private boolean underlineInitialized;
   private int hoveredIndex;
   private int lastHoveredIndex;
   private float tooltipAlpha;
   private float tooltipX;
   private int tooltipIndex;

   private void updateTimeClock(int slotCount) {
      long nanoTime = System.nanoTime();
      if (justCreated || lastFrameNanos == 0L || nanoTime - lastFrameNanos > 300000000L) {
         introStartNanos = nanoTime;
         justCreated = false;
      }

      lastFrameNanos = nanoTime;
      this.introElapsedMs = introStartNanos == 0L ? 0.0F : (float)(nanoTime - introStartNanos) / 1000000.0F;
      if (introStartNanos != 0L && this.introElapsedMs > (slotCount + 2) * 20.0F + 200.0F) {
         introStartNanos = 0L;
      }
   }

   private void applyStaggerForSlot(int slot) {
      this.entranceAlpha = 1.0F;
      this.entranceScale = 1.0F;
      if (introStartNanos != 0L) {
         float staggerValue = this.introElapsedMs - slot * 20.0F;
         if (!(staggerValue >= 200.0F)) {
            float staggerFactor = Math.max(0.0F, staggerValue / 200.0F);
            this.entranceAlpha = staggerValue <= 0.0F ? 0.0F : MatrixStackUtilsAmount.getAmountForFloat(staggerValue / 130.0F);
            this.entranceScale = 0.94F + 0.060000002F * MatrixStackUtilsAmount.getAmount(staggerFactor, 1.6F);
         }
      }
   }

   private void fitTabs(VertexTextureFont fontRenderer) {
      float availableWidth = this.barWidth - 16;
      float brandWidth = 104.0F;
      float fixedWidth = this.tabCount * 14 + (this.tabCount - 1) * CHIP_GAP;
      float rawWidthsSum = 0.0F;

      for (int i = 0; i < this.tabCount; i++) {
         rawWidthsSum += (float)Math.ceil(MatrixStackUtilsAmount.getAmount3(this.categories.get(i).getText()));
      }

      this.labelScale = 0.75F;
      float chipsSum = fixedWidth + rawWidthsSum * this.labelScale;
      if (chipsSum > availableWidth - brandWidth) {
         float scaleMax = (availableWidth - brandWidth - fixedWidth) / rawWidthsSum;
         this.labelScale = Math.max(0.56F, Math.min(this.labelScale, scaleMax));
      }

      float x = this.panelX + 8 + brandWidth;

      for (int i = 0; i < this.tabCount; i++) {
         float textWidth = (float)Math.ceil(MatrixStackUtilsAmount.getAmount3(this.categories.get(i).getText())) * this.labelScale;
         this.chipWidths[i] = 14 + Math.max(15.0F, textWidth);
         this.chipXs[i] = x;
         x += this.chipWidths[i] + CHIP_GAP;
      }
   }

   public void updateAnimation() {
      this.updateTimeClock(this.tabCount);
   }

   public void renderBar(DrawContext context, int mouseX, int mouseY, float tickDelta) {
      this.hitRegions.update();
      this.updateTimeClock(this.tabCount);
      VertexTextureFont fontRenderer = MatrixClient.fontRenderer();
      if (this.labelScale <= 0.0F) {
         this.fitTabs(fontRenderer);
      }

      MatrixStack matrices = MatrixPeekUtilsRender.getMatrixStack();
      this.applyStaggerForSlot(0);
      float panelOverlay = MatrixRoundedUtils.getPanelOverlay(this.entranceAlpha);

      try {
         MatrixPeekUtilsRender.renderMatrixPeek(context, this.panelX, this.panelY, this.barWidth, this.barHeight, 7.0F, CardMatrixUtils.getValue8());
         MatrixPeekUtilsRender.renderMatrixPeek(context, this.panelX, this.panelY, this.barWidth, this.barHeight, 7.0F, MatrixPeekUtilsRender.getAmount(), 352321535);
         TwoDidUtils.renderMinPeek(context, 1, this.panelX, this.panelY, this.barWidth, this.barHeight, 7.0F, 1.0F);
      } finally {
         MatrixRoundedUtils.handlePanelOverlay(panelOverlay);
      }

      this.applyStaggerForSlot(0);
      float logoProfile = this.getLogoIconX();
      float roundOffset = this.panelY + (this.barHeight - 16) / 2.0F;
      float titleBaseY = MatrixStackUtilsAmount.getAmount(this.panelY + this.barHeight / 2.0F - 8.28F);
      boolean logoPush = MatrixStackUtilsAmount.canTranslatePush(context, logoProfile + 24.0F, this.panelY + this.barHeight / 2.0F, this.entranceScale);
      panelOverlay = MatrixRoundedUtils.getPanelOverlay(this.entranceAlpha);

      try {
         MatrixPeekUtilsIdentifier.handle(logoProfile, roundOffset, 16.0F, 16.0F, LOGO_TEXTURE, new Color(-920587), matrices);
         MatrixStackUtilsAmount.handlePushScale("Vortex", titleX(), titleBaseY - 4.0F, 1.1F, -920587);
         MatrixStackUtilsAmount.handlePushScale(
            VERSION_TEXT, titleX() + MatrixStackUtilsAmount.getAmount3("Vortex") * 1.1F - 2.0F, titleBaseY + 6.5F, 0.72F, -11117467
         );
      } finally {
         MatrixRoundedUtils.handlePanelOverlay(panelOverlay);
         MatrixStackUtilsAmount.renderPopMatrices(context, logoPush);
      }

      int indexOf = this.categories.indexOf(PngIcons.getInstance().getIconsPngType());
      this.hoveredIndex = -1;

      for (int i = 0; i < this.tabCount; i++) {
         this.renderTab(context, matrices, fontRenderer, i, indexOf, mouseX, mouseY, tickDelta);
      }

      this.updateUnderline(indexOf);
      this.renderUnderline(context, tickDelta);
      this.updateTooltip(tickDelta);
      this.renderHoverTooltip(context);
      if (this.hoveredIndex >= 0 && this.hoveredIndex != this.lastHoveredIndex) {
         ClientSounds.updateScrollHover();
      }

      this.lastHoveredIndex = this.hoveredIndex;
   }

   private float getLogoIconX() {
      return this.panelX + 10.0F;
   }

   private float titleX() {
      return this.panelX + 32.0F;
   }

   private void renderTab(DrawContext context, MatrixStack matrices, VertexTextureFont vertexTextureFont, int i, int activeIndex, int mouseX, int mouseY, float tickDelta) {
      IconsPngType expanditem = this.categories.get(i);
      this.applyStaggerForSlot(1 + i);
      double matrixIdentifierFactor = MatrixClearUtils.getAmount();
      float chipX = this.chipXs[i];
      float chipWidth = this.chipWidths[i];
      int chipY = this.panelY + 3;
      int chipHeight = this.barHeight - 6;
      boolean topBarFlag = i == activeIndex;
      boolean hoverEnabled = MatrixStackUtilsAmount.canFlag(mouseX, mouseY, chipX, chipY, chipWidth, chipHeight);
      if (hoverEnabled) {
         this.hoveredIndex = i;
      }

      this.hoverAmounts[i] = animateToward(this.hoverAmounts[i], hoverEnabled && !topBarFlag ? 1.0F : 0.0F, hoverEnabled ? 16.0 : 11.0, matrixIdentifierFactor, 0.002F);
      this.activeAmounts[i] = animateToward(this.activeAmounts[i], topBarFlag ? 1.0F : 0.0F, 14.0, matrixIdentifierFactor, 0.002F);
      if (!(this.entranceAlpha <= 0.002F)) {
         float logoProfile = this.hoverAmounts[i];
         float cardMatrixFactor = this.activeAmounts[i];
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(
            context, chipX + chipWidth / 2.0F, chipY + chipHeight / 2.0F, this.entranceScale * this.rowAnims.getTime(this.pressAnimIds[i], 0.035F)
         );
         float panelOverlay = MatrixRoundedUtils.getPanelOverlay(this.entranceAlpha);

         try {
            RenderMatrixUtilsRelease.renderForDrawContextFloatDouble(context, chipX, chipY, chipWidth, chipHeight, 8.0F, logoProfile * 0.25F + cardMatrixFactor * 0.25F);
            float iconX = chipX + (chipWidth - 15.0F) / 2.0F;
            float iconY = chipY + (chipHeight - 15.0F) / 2.0F - 6.0F;
            int iconColor = CardMatrixUtils.getValue(
               CardMatrixUtils.getValue(-10722709, -4604216, logoProfile * 0.8), MatrixEspUtils.releaseRightValue, cardMatrixFactor
            );
            MatrixPeekUtilsIdentifier.handle(iconX, iconY, 15.0F, 15.0F, expanditem.getIdentifier(), new Color(iconColor, true), matrices);
            float textWidth = (float)Math.ceil(MatrixStackUtilsAmount.getAmount3(expanditem.getText())) * this.labelScale;
            int labelColor = CardMatrixUtils.getValue(CardMatrixUtils.getValue(-8090988, -920587, logoProfile * 0.6), CardMatrixUtils.getValue(-920587, MatrixEspUtils.releaseRightValue, 0.35 * cardMatrixFactor), cardMatrixFactor);
            MatrixStackUtilsAmount.handlePushScale(
               vertexTextureFont.formatText(expanditem.getText(), textWidth / this.labelScale), chipX + (chipWidth - textWidth) / 2.0F - 1.1F, MatrixStackUtilsAmount.getAmount(chipY + 21.5F), this.labelScale, labelColor
            );
         } finally {
            MatrixRoundedUtils.handlePanelOverlay(panelOverlay);
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
         }

         this.hitRegions.handleFlag(chipX, chipY, chipWidth, chipHeight, this.pageActions[i], this.rowAnims, this.pressAnimIds[i]);
      }
   }

   private void updateUnderline(int activeIndex) {
      double matrixIdentifierFactor = MatrixClearUtils.getAmount();
      if (activeIndex >= 0) {
         float springValue = this.chipXs[activeIndex] + this.chipWidths[activeIndex] / 2.0F - 8.0F;
         if (!this.underlineInitialized) {
            this.underlineSpring.handleAmount(springValue);
            this.underlineInitialized = true;
         } else if (this.underlineSpring.springPosition != springValue || this.underlineSpring.anywayEnableCount != 0.0) {
            int underlineDivisor = Math.max(1, (int)Math.ceil(matrixIdentifierFactor / 0.004166666666666667));
            double underlineFactor = matrixIdentifierFactor / underlineDivisor;

            for (int i = 0; i < underlineDivisor; i++) {
               this.underlineSpring.handleAmount(springValue, 784.0, 42.0, underlineFactor);
            }

            if (Math.abs(this.underlineSpring.springPosition - springValue) < 0.04 && Math.abs(this.underlineSpring.anywayEnableCount) < 1.5) {
               this.underlineSpring.handleAmount(springValue);
            }
         }

         this.underlineX = MatrixStackUtilsAmount.getAmount((float)this.underlineSpring.springPosition);
      }
   }

   private void renderUnderline(DrawContext context, float tickDelta) {
      int activeIndex = this.categories.indexOf(PngIcons.getInstance().getIconsPngType());
      if (activeIndex >= 0) {
         this.applyStaggerForSlot(1 + activeIndex);
         float underlineAlpha = this.entranceAlpha;
         if (!(underlineAlpha <= 0.002F)) {
            float panelOverlay = MatrixRoundedUtils.getPanelOverlay(underlineAlpha);

            try {
               MatrixPeekUtilsRender.renderMatrixPeek(
                  context, this.underlineX, this.panelY + this.barHeight - 3.0F, 16.0F, 3.0F, 1.5F, MatrixEspUtils.releaseRightValue
               );
            } finally {
               MatrixRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }
      }
   }

   private void updateTooltip(float tickDelta) {
      double matrixIdentifierFactor = MatrixClearUtils.getAmount();
      this.tooltipAlpha = animateToward(this.tooltipAlpha, this.hoveredIndex >= 0 ? 1.0F : 0.0F, this.hoveredIndex >= 0 ? 22.0 : 18.0, matrixIdentifierFactor, 0.004F);
   }

   private void renderHoverTooltip(DrawContext context) {
      if (this.hoveredIndex >= 0 && !(this.tooltipAlpha <= 0.004F)) {
         String tooltipText = this.categories.get(this.hoveredIndex).getText();
         float textWidth = MatrixStackUtilsAmount.getAmount3(tooltipText);
         float targetX = this.chipXs[this.hoveredIndex] + this.chipWidths[this.hoveredIndex] / 2.0F;
         this.tooltipX = (float)MatrixIdentifierUtils.getAmount(this.tooltipX, targetX, 30.0, MatrixClearUtils.getAmount());
         float tooltipX = this.tooltipX - (textWidth + 14.0F) / 2.0F;
         float tooltipY = this.panelY + this.barHeight + 3.0F;
         float panelOverlay = MatrixRoundedUtils.getPanelOverlay(this.tooltipAlpha);

         try {
            MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(context, tooltipX - 2.0F, tooltipY, textWidth + 18.0F, 19.0F, 6.0F, -1946157056, 6.0F, 0.5F);
            MatrixPeekUtilsRender.renderForDrawContextFloat(context, tooltipX, tooltipY, textWidth + 14.0F, 16.0F, 5.0F, -65990120, 385875967);
            MatrixStackUtilsAmount.handle(
               tooltipText, tooltipX + 7.0F, MatrixStackUtilsAmount.getAmount(tooltipY, 16.0F, 1.0F), -920587
            );
         } finally {
            MatrixRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   public boolean canConsumeClick(double logoProfileValue, double heightAmount, int button) {
      return button == 0 && this.hitRegions.canAnimsPress(logoProfileValue, heightAmount);
   }

   private static float animateToward(float logoProfileValue, float thumbAmount, double ctlValue, double matrixIdentifierFactor, float logoProfileLimit) {
      float logoProfileValue5 = (float)MatrixIdentifierUtils.getAmount(logoProfileValue, thumbAmount, ctlValue, matrixIdentifierFactor);
      return Math.abs(logoProfileValue5 - thumbAmount) < logoProfileLimit ? thumbAmount : logoProfileValue5;
   }

   public TopBarTabs(int logoValue, int value, int value2, int value3, List<IconsPngType> expandItems, Consumer<IconsPngType> consumer) {
      this.panelX = logoValue;
      this.panelY = value;
      this.barWidth = value2;
      this.barHeight = value3;
      this.categories = expandItems;
      this.tabCount = expandItems.size();
      this.chipXs = new float[this.tabCount];
      this.chipWidths = new float[this.tabCount];
      this.hoverAmounts = new float[this.tabCount];
      this.activeAmounts = new float[this.tabCount];
      this.pressAnimIds = new String[this.tabCount];
      this.pageActions = new Runnable[this.tabCount];
      this.hoveredIndex = -1;
      this.lastHoveredIndex = -1;
      this.tooltipIndex = -1;
      this.entranceAlpha = 1.0F;
      this.entranceScale = 1.0F;
      PngIcons instance = PngIcons.getInstance();

      for (int i = 0; i < this.tabCount; i++) {
         IconsPngType iconsPngType = this.categories.get(i);
         this.pressAnimIds[i] = "tab:" + iconsPngType.name();
         this.activeAmounts[i] = iconsPngType == instance.getIconsPngType() ? 1.0F : 0.0F;
         this.pageActions[i] = () -> {
            if (iconsPngType != PngIcons.getInstance().getIconsPngType()) {
               ClientSounds.updateCategoryClick();
            }

            consumer.accept(iconsPngType);
         };
      }

      int identityHashCode = System.identityHashCode(Module.mc.currentScreen);
      if (identityHashCode != lastScreenHash) {
         lastScreenHash = identityHashCode;
         justCreated = true;
      }
   }
}
