package com.matrix.client.utils;

import com.matrix.client.gui.MatrixMatricesScreen;
import com.matrix.client.gui.MatrixPeekUtilsRender;
import com.matrix.client.mixins.AccessorInventoryScreen;
import com.matrix.client.render.MatrixRoundedUtils;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import com.matrix.client.categories.misc.SkinProtect;
import com.matrix.client.categories.misc.CustomSkin;

public final class QuaternionfRotateMatrixclient {
   private double lastAngleDegrees;
   private boolean quaternionfRotateActive;
   private float matrixclientScaleAmount;
   private double quaternionfRotateAmount;
   private float bodyYawOffsetDegrees = 0.0F;

   public boolean canAmountFlag(double amount, double amount2, int quaternionfRotateValue, int quaternionfRotateValue2, int quaternionfRotateValue3, int quaternionfRotateValue4, int quaternionfRotateValue5) {
      if (quaternionfRotateValue5 == 0 && amount >= quaternionfRotateValue && amount <= quaternionfRotateValue + quaternionfRotateValue3 && amount2 >= quaternionfRotateValue2 && amount2 <= quaternionfRotateValue2 + quaternionfRotateValue4) {
         this.quaternionfRotateActive = true;
         this.lastAngleDegrees = amount;
         this.quaternionfRotateAmount = amount2;
         return true;
      } else {
         return false;
      }
   }

   public void renderMatrixclientScale(DrawContext context, int matrixclientScaleValue, int editValue, int releaseRightLimit, int n4, int matrixclientValue) {
      MatrixPeekUtilsRender.renderMatrixStack(context, matrixclientScaleValue, editValue, releaseRightLimit, n4, 6, MatrixRoundedUtils.getPanelOverlay(new Color(255, 255, 255, 18).getRGB()));
      MatrixPeekUtilsRender.renderMatrixStack(context, matrixclientScaleValue + 1, editValue + 1, releaseRightLimit - 2, n4 - 2, 5, MatrixRoundedUtils.getPanelOverlay(new Color(12, 12, 16, 236).getRGB()));
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.player != null) {
         if (!MatrixMatricesScreen.isMatrixMatricesActive()) {
            int max5 = Math.max(20, (int)(n4 * 0.46F));
            int matrixclientValue2 = SkinProtect.matrixclientValue;
            SkinProtect.matrixclientValue = matrixclientValue;
            int value = CustomSkin.scaleValue;
            CustomSkin.scaleValue = -2;

            try {
               EntityRenderState matrixBuildEntityState = AccessorInventoryScreen.matrixBuildEntityState(instance.player);
               Quaternionf rotateZ = new Quaternionf().rotateZ((float) Math.PI);
               Quaternionf rotateX = new Quaternionf().rotateX(this.matrixclientScaleAmount * (float) (Math.PI / 180.0));
               rotateZ.mul(rotateX);
               if (matrixBuildEntityState instanceof LivingEntityRenderState livingEntityRenderState) {
                  livingEntityRenderState.bodyYaw = 180.0F + this.bodyYawOffsetDegrees;
                  livingEntityRenderState.relativeHeadYaw = 0.0F;
                  livingEntityRenderState.pitch = 0.0F;
                  if (livingEntityRenderState.baseScale != 0.0F) {
                     livingEntityRenderState.width = livingEntityRenderState.width / livingEntityRenderState.baseScale;
                     livingEntityRenderState.height = livingEntityRenderState.height / livingEntityRenderState.baseScale;
                     livingEntityRenderState.baseScale = 1.0F;
                  }
               }

               Vector3f vector3f = new Vector3f(0.0F, matrixBuildEntityState.height / 2.0F, 0.0F);
               context.addEntity(matrixBuildEntityState, max5, vector3f, rotateZ, rotateX, matrixclientScaleValue + 2, editValue + 2, matrixclientScaleValue + releaseRightLimit - 2, editValue + n4 - 2);
            } catch (Exception e) {
            } finally {
               SkinProtect.matrixclientValue = matrixclientValue2;
               CustomSkin.scaleValue = value;
            }
         }
      }
   }

   public void handleAmountFlag(double amount, double amount2) {
      if (this.quaternionfRotateActive) {
         this.bodyYawOffsetDegrees = RotateQuaternionfUtils.getAmount(this.bodyYawOffsetDegrees + RotateQuaternionfUtils.getAmount(amount - this.lastAngleDegrees));
         this.matrixclientScaleAmount = RotateQuaternionfUtils.getAmountForFloat(this.matrixclientScaleAmount + RotateQuaternionfUtils.getAmountForDouble(amount2 - this.quaternionfRotateAmount));
         this.lastAngleDegrees = amount;
         this.quaternionfRotateAmount = amount2;
      }
   }

   public QuaternionfRotateMatrixclient() {
      this.matrixclientScaleAmount = 0.0F;
      this.quaternionfRotateActive = false;
   }

   public void updateFlag() {
      this.quaternionfRotateActive = false;
   }
}
