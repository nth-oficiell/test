package com.matrix.client.utils;

import com.matrix.client.gui.MatrixMatricesScreen;
import com.matrix.client.gui.MatrixPeekUtilsRender;
import com.matrix.client.mixins.AccessorInventoryScreen;
import com.matrix.client.render.MatrixRoundedUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import com.matrix.client.categories.misc.Capes;
import com.matrix.client.categories.misc.CustomSkin;
import com.matrix.client.utils.MatrixSubmitUtils;

public final class QuaternionfRotateIdentifier {
   private boolean quaternionfRotateActive;
   private double previousIdentifierAmount;
   private long lastNanoTime;
   private final RotateQuaternionfUtils.Orbit identifierScaleOrbit = new RotateQuaternionfUtils.Orbit(152.0F, 6.0F, 1.0F, 1.0F, 1.0F);
   private double quaternionfRotateAmount;

   public void handleAmountMin(double amount, double amount2) {
      if (this.quaternionfRotateActive) {
         this.identifierScaleOrbit.handleAmount(amount - this.previousIdentifierAmount, amount2 - this.quaternionfRotateAmount);
         this.previousIdentifierAmount = amount;
         this.quaternionfRotateAmount = amount2;
      }
   }

   public void renderIdentifierScale(DrawContext context, int identifierScaleValue, int matrixStackValue, int identifierScaleValue3, int materialFactor, Identifier identifier) {
      MatrixPeekUtilsRender.renderForDrawContextFloat(context, identifierScaleValue, matrixStackValue, identifierScaleValue3, materialFactor, 6.0F, MatrixRoundedUtils.getPanelOverlay(-334754800), MatrixRoundedUtils.getPanelOverlay(318767103));
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.player != null) {
         if (!MatrixMatricesScreen.isMatrixMatricesActive()) {
            long nanoTime = System.nanoTime();
            float orbitFactor = this.lastNanoTime == 0L ? 0.0F : Math.min(0.1F, (float)(nanoTime - this.lastNanoTime) / 1.0E9F);
            this.lastNanoTime = nanoTime;
            this.identifierScaleOrbit.handleAmountMax(orbitFactor);
            float smoothedYaw = this.identifierScaleOrbit.getSmoothedYaw();
            float identifierScaleFactor = this.identifierScaleOrbit.getSmoothedPitch();
            float panelOverlay = MatrixRoundedUtils.getPanelOverlay();
            if (!(panelOverlay < 0.8F)) {
               float min2 = Math.min(1.0F, (panelOverlay - 0.8F) / 0.19999999F);
               float identifierScaleValue7 = min2 * min2 * (3.0F - 2.0F * min2);
               int max8 = Math.max(20, (int)(materialFactor * 0.46F * (0.9F + 0.1F * identifierScaleValue7)));
               Identifier identifier2 = Capes.identifier;
               Capes.identifier = identifier;
               MatrixSubmitUtils.handleAmountFlag(smoothedYaw);
               int value = CustomSkin.scaleValue;
               CustomSkin.scaleValue = -2;

               try {
                  EntityRenderState matrixBuildEntityState = AccessorInventoryScreen.matrixBuildEntityState(instance.player);
                  Quaternionf rotateZ = new Quaternionf().rotateZ((float) Math.PI);
                  Quaternionf rotateX = new Quaternionf().rotateX(identifierScaleFactor * (float) (Math.PI / 180.0));
                  rotateZ.mul(rotateX);
                  if (matrixBuildEntityState instanceof LivingEntityRenderState livingEntityRenderState) {
                     livingEntityRenderState.bodyYaw = 180.0F + smoothedYaw;
                     livingEntityRenderState.relativeHeadYaw = 0.0F;
                     livingEntityRenderState.pitch = 0.0F;
                     if (livingEntityRenderState.baseScale != 0.0F) {
                        livingEntityRenderState.width = livingEntityRenderState.width / livingEntityRenderState.baseScale;
                        livingEntityRenderState.height = livingEntityRenderState.height / livingEntityRenderState.baseScale;
                        livingEntityRenderState.baseScale = 1.0F;
                     }
                  }

                  Vector3f vector3f = new Vector3f(0.0F, matrixBuildEntityState.height / 2.0F, 0.0F);
                  context.addEntity(matrixBuildEntityState, max8, vector3f, rotateZ, rotateX, identifierScaleValue + 2, matrixStackValue + 2, identifierScaleValue + identifierScaleValue3 - 2, matrixStackValue + materialFactor - 2);
               } catch (Exception e) {
               } finally {
                  MatrixSubmitUtils.updateFlag();
                  Capes.identifier = identifier2;
                  CustomSkin.scaleValue = value;
               }
            }
         }
      }
   }

   public void updateFlagMin() {
      if (this.quaternionfRotateActive) {
         this.identifierScaleOrbit.updateMaxFlag();
      }

      this.quaternionfRotateActive = false;
   }

   public boolean canAmountMin(double amount, double amount2, int quaternionfRotateValue, int quaternionfRotateValue2, int quaternionfRotateValue3, int quaternionfRotateValue4, int quaternionfRotateValue5) {
      if (quaternionfRotateValue5 == 0 && amount >= quaternionfRotateValue && amount <= quaternionfRotateValue + quaternionfRotateValue3 && amount2 >= quaternionfRotateValue2 && amount2 <= quaternionfRotateValue2 + quaternionfRotateValue4) {
         this.quaternionfRotateActive = true;
         this.previousIdentifierAmount = amount;
         this.quaternionfRotateAmount = amount2;
         this.identifierScaleOrbit.updateMaxAmount();
         return true;
      } else {
         return false;
      }
   }
}
