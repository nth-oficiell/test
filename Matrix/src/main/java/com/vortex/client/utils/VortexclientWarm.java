package com.vortex.client.utils;

import java.util.List;
import com.vortex.client.utils.AmountLoft;
import com.vortex.client.utils.ForeShinScale;
import com.vortex.client.utils.LoftLatheFlag;
import com.vortex.client.utils.ForeEar;

public final class VortexclientWarm {
   public final float boundsMinZ;
   public final float scaleAmount;
   public final int[] vortexclientWarmTable;
   public final float vortexclientWarmAmount;
   public final int partIndex;
   public final float boundsMaxY;
   public final int[] vortexclientValues;
   public final float[] personCustomTable;
   public final String vortexclientWarmText;
   public final String[] vortexclientWarmTexts;
   public final float boundsMaxX;
   public final int vortexclientValue;
   public final int vortexclientTextureValue;
   public final int vortexclientWarmValue;
   public final int listSize;
   public final float[][] vectorEmitFloats;
   public final int personCustomValue;
   public final float boundsMaxZ;

   public boolean canFlag(int vortexclientWarmValue, int vectorEmitValue) {
      int vortexclientWarmValue2 = vortexclientWarmValue;

      while (vortexclientWarmValue2 >= 0) {
         if (vortexclientWarmValue2 == vectorEmitValue) {
            return true;
         }

         vortexclientWarmValue2 = this.vortexclientWarmTable[vortexclientWarmValue2];
      }

      return false;
   }

   private static AmountLoft getAmount(float[] vortexclientWarmValues, int index, AmountLoft amountLoft) {
      amountLoft.matrixM0F = vortexclientWarmValues[index];
      amountLoft.matrixMFloat = vortexclientWarmValues[index + 1];
      amountLoft.matrixM02 = vortexclientWarmValues[index + 2];
      amountLoft.translateX = vortexclientWarmValues[index + 3];
      amountLoft.matrixM10 = vortexclientWarmValues[index + 4];
      amountLoft.matrixM11 = vortexclientWarmValues[index + 5];
      amountLoft.matrixM12 = vortexclientWarmValues[index + 6];
      amountLoft.translateY = vortexclientWarmValues[index + 7];
      amountLoft.matrixM20 = vortexclientWarmValues[index + 8];
      amountLoft.loftAmount = vortexclientWarmValues[index + 9];
      amountLoft.matrixM22 = vortexclientWarmValues[index + 10];
      amountLoft.translateZ = vortexclientWarmValues[index + 11];
      return amountLoft;
   }

   public float getMax() {
      if (this.personCustomValue < 0) {
         return 0.0F;
      } else {
         float vortexclientWarmValue = this.personCustomTable[3 * this.personCustomValue + 1];
         float vortexclientWarmBound = 0.0F;

         for (int i = 0; i < this.listSize; i++) {
            if (this.canFlag(i, this.personCustomValue)) {
               float[] vortexclientWarmValues = this.vectorEmitFloats[i];

               for (int j = 0; j < vortexclientWarmValues.length; j += 9) {
                  vortexclientWarmBound = Math.max(vortexclientWarmBound, vortexclientWarmValue - vortexclientWarmValues[j + 1]);
               }
            }
         }

         return vortexclientWarmBound;
      }
   }

   public void handleAmounts(ForeShinScale foreShinScale, float[] vortexclientWarmValues) {
      AmountLoft amountLoft = new AmountLoft();
      AmountLoft amountLoft2 = new AmountLoft();

      for (int i = 0; i < this.listSize; i++) {
         float vortexclientWarmValue = this.personCustomTable[3 * i];
         float vortexclientWarmValue2 = this.personCustomTable[3 * i + 1];
         float vortexclientWarmValue3 = this.personCustomTable[3 * i + 2];
         amountLoft2.getAmount()
            .getAmountTranslateX(vortexclientWarmValue + foreShinScale.offsetX[i], vortexclientWarmValue2 + foreShinScale.foreShinTable[i], vortexclientWarmValue3 + foreShinScale.offsetZ[i])
            .getAmount3(foreShinScale.table[i])
            .getAmount(foreShinScale.rotationZ[i])
            .getAmountForFloat(foreShinScale.rotationX[i])
            .getAmountAmountLoft(foreShinScale.scaleX[i], foreShinScale.scaleY[i], foreShinScale.scaleZ[i])
            .getAmountTranslateX(-vortexclientWarmValue, -vortexclientWarmValue2, -vortexclientWarmValue3);
         int vortexclientWarmValue4 = this.vortexclientWarmTable[i];
         if (vortexclientWarmValue4 < 0) {
            amountLoft.getAmount(amountLoft2);
         } else {
            getAmount(vortexclientWarmValues, vortexclientWarmValue4 * 21, amountLoft).getAmountForAmountLoft(amountLoft2);
         }

         amountLoft.handleAmount(vortexclientWarmValues, i * 21);
         amountLoft.handleAmountForFloatArrayInt(vortexclientWarmValues, i * 21 + 12);
      }
   }

   public int getTexts(String vortexclientWarmText) {
      for (int i = 0; i < this.listSize; i++) {
         if (this.vortexclientWarmTexts[i].equals(vortexclientWarmText)) {
            return i;
         }
      }

      return -1;
   }

   VortexclientWarm(String text, LoftLatheFlag loftLatheFlag, int[] vortexclientWarmValues, List<ForeEar> list, ForeEar foreEar) {
      this.vortexclientWarmText = text;
      this.vortexclientValue = loftLatheFlag.atlasWidth;
      this.vortexclientTextureValue = loftLatheFlag.atlasHeight;
      this.vortexclientValues = vortexclientWarmValues != null ? vortexclientWarmValues : loftLatheFlag.loftLatheTable;
      this.vortexclientWarmValue = loftLatheFlag.getMax();
      this.listSize = list.size();
      this.vortexclientWarmTexts = new String[this.listSize];
      this.vortexclientWarmTable = new int[this.listSize];
      this.personCustomTable = new float[3 * this.listSize];
      this.vectorEmitFloats = new float[this.listSize][];
      int value = 0;
      float amount = Float.MAX_VALUE;
      float wrapDegreesBound = Float.MAX_VALUE;
      float maxValue3 = Float.MAX_VALUE;
      float maxValue4 = -Float.MAX_VALUE;
      float pilotStatusBound = -Float.MAX_VALUE;
      float maxValue6 = -Float.MAX_VALUE;

      for (int i = 0; i < this.listSize; i++) {
         ForeEar atla = (ForeEar)list.get(i);
         this.vortexclientWarmTexts[i] = atla.foreEarText;
         this.vortexclientWarmTable[i] = atla.foreEar == null ? -1 : atla.foreEar.boneIndex;
         if (this.vortexclientWarmTable[i] >= i) {
            throw new IllegalStateException("bone " + atla.foreEarText + " declared before its parent");
         }

         this.personCustomTable[3 * i] = atla.offsetX;
         this.personCustomTable[3 * i + 1] = atla.foreEarAmount;
         this.personCustomTable[3 * i + 2] = atla.shinFootAmount;
         this.vectorEmitFloats[i] = atla.rootTransform.getCopyOf();
         value += atla.rootTransform.getValue();
         float[] vortexclientWarmValues2 = this.vectorEmitFloats[i];

         for (int j = 0; j < vortexclientWarmValues2.length; j += 9) {
            amount = Math.min(amount, vortexclientWarmValues2[j]);
            maxValue4 = Math.max(maxValue4, vortexclientWarmValues2[j]);
            wrapDegreesBound = Math.min(wrapDegreesBound, vortexclientWarmValues2[j + 1]);
            pilotStatusBound = Math.max(pilotStatusBound, vortexclientWarmValues2[j + 1]);
            maxValue3 = Math.min(maxValue3, vortexclientWarmValues2[j + 2]);
            maxValue6 = Math.max(maxValue6, vortexclientWarmValues2[j + 2]);
         }
      }

      this.partIndex = value;
      this.personCustomValue = foreEar == null ? -1 : foreEar.boneIndex;
      if (value == 0) {
         maxValue6 = 0.0F;
         pilotStatusBound = 0.0F;
         maxValue4 = 0.0F;
         maxValue3 = 0.0F;
         wrapDegreesBound = 0.0F;
         amount = 0.0F;
      }

      this.boundsMaxX = amount;
      this.vortexclientWarmAmount = wrapDegreesBound;
      this.boundsMinZ = maxValue3;
      this.scaleAmount = maxValue4;
      this.boundsMaxY = pilotStatusBound;
      this.boundsMaxZ = maxValue6;
   }

   public ForeShinScale getListSize() {
      return new ForeShinScale(this.listSize);
   }

   public float[] getAmounts() {
      return new float[this.listSize * 21];
   }
}
