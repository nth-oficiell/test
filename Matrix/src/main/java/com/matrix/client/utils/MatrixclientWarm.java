package com.matrix.client.utils;

import java.util.List;
import com.matrix.client.utils.AmountLoft;
import com.matrix.client.utils.ForeShinScale;
import com.matrix.client.utils.LoftLatheFlag;
import com.matrix.client.utils.ForeEar;

public final class MatrixclientWarm {
   public final float boundsMinZ;
   public final float scaleAmount;
   public final int[] matrixclientWarmTable;
   public final float matrixclientWarmAmount;
   public final int partIndex;
   public final float boundsMaxY;
   public final int[] matrixclientValues;
   public final float[] personCustomTable;
   public final String matrixclientWarmText;
   public final String[] matrixclientWarmTexts;
   public final float boundsMaxX;
   public final int matrixclientValue;
   public final int matrixclientTextureValue;
   public final int matrixclientWarmValue;
   public final int listSize;
   public final float[][] vectorEmitFloats;
   public final int personCustomValue;
   public final float boundsMaxZ;

   public boolean canFlag(int matrixclientWarmValue, int vectorEmitValue) {
      int matrixclientWarmValue2 = matrixclientWarmValue;

      while (matrixclientWarmValue2 >= 0) {
         if (matrixclientWarmValue2 == vectorEmitValue) {
            return true;
         }

         matrixclientWarmValue2 = this.matrixclientWarmTable[matrixclientWarmValue2];
      }

      return false;
   }

   private static AmountLoft getAmount(float[] matrixclientWarmValues, int index, AmountLoft amountLoft) {
      amountLoft.matrixM0F = matrixclientWarmValues[index];
      amountLoft.matrixMFloat = matrixclientWarmValues[index + 1];
      amountLoft.matrixM02 = matrixclientWarmValues[index + 2];
      amountLoft.translateX = matrixclientWarmValues[index + 3];
      amountLoft.matrixM10 = matrixclientWarmValues[index + 4];
      amountLoft.matrixM11 = matrixclientWarmValues[index + 5];
      amountLoft.matrixM12 = matrixclientWarmValues[index + 6];
      amountLoft.translateY = matrixclientWarmValues[index + 7];
      amountLoft.matrixM20 = matrixclientWarmValues[index + 8];
      amountLoft.loftAmount = matrixclientWarmValues[index + 9];
      amountLoft.matrixM22 = matrixclientWarmValues[index + 10];
      amountLoft.translateZ = matrixclientWarmValues[index + 11];
      return amountLoft;
   }

   public float getMax() {
      if (this.personCustomValue < 0) {
         return 0.0F;
      } else {
         float matrixclientWarmValue = this.personCustomTable[3 * this.personCustomValue + 1];
         float matrixclientWarmBound = 0.0F;

         for (int i = 0; i < this.listSize; i++) {
            if (this.canFlag(i, this.personCustomValue)) {
               float[] matrixclientWarmValues = this.vectorEmitFloats[i];

               for (int j = 0; j < matrixclientWarmValues.length; j += 9) {
                  matrixclientWarmBound = Math.max(matrixclientWarmBound, matrixclientWarmValue - matrixclientWarmValues[j + 1]);
               }
            }
         }

         return matrixclientWarmBound;
      }
   }

   public void handleAmounts(ForeShinScale foreShinScale, float[] matrixclientWarmValues) {
      AmountLoft amountLoft = new AmountLoft();
      AmountLoft amountLoft2 = new AmountLoft();

      for (int i = 0; i < this.listSize; i++) {
         float matrixclientWarmValue = this.personCustomTable[3 * i];
         float matrixclientWarmValue2 = this.personCustomTable[3 * i + 1];
         float matrixclientWarmValue3 = this.personCustomTable[3 * i + 2];
         amountLoft2.getAmount()
            .getAmountTranslateX(matrixclientWarmValue + foreShinScale.offsetX[i], matrixclientWarmValue2 + foreShinScale.foreShinTable[i], matrixclientWarmValue3 + foreShinScale.offsetZ[i])
            .getAmount3(foreShinScale.table[i])
            .getAmount(foreShinScale.rotationZ[i])
            .getAmountForFloat(foreShinScale.rotationX[i])
            .getAmountAmountLoft(foreShinScale.scaleX[i], foreShinScale.scaleY[i], foreShinScale.scaleZ[i])
            .getAmountTranslateX(-matrixclientWarmValue, -matrixclientWarmValue2, -matrixclientWarmValue3);
         int matrixclientWarmValue4 = this.matrixclientWarmTable[i];
         if (matrixclientWarmValue4 < 0) {
            amountLoft.getAmount(amountLoft2);
         } else {
            getAmount(matrixclientWarmValues, matrixclientWarmValue4 * 21, amountLoft).getAmountForAmountLoft(amountLoft2);
         }

         amountLoft.handleAmount(matrixclientWarmValues, i * 21);
         amountLoft.handleAmountForFloatArrayInt(matrixclientWarmValues, i * 21 + 12);
      }
   }

   public int getTexts(String matrixclientWarmText) {
      for (int i = 0; i < this.listSize; i++) {
         if (this.matrixclientWarmTexts[i].equals(matrixclientWarmText)) {
            return i;
         }
      }

      return -1;
   }

   MatrixclientWarm(String text, LoftLatheFlag loftLatheFlag, int[] matrixclientWarmValues, List<ForeEar> list, ForeEar foreEar) {
      this.matrixclientWarmText = text;
      this.matrixclientValue = loftLatheFlag.atlasWidth;
      this.matrixclientTextureValue = loftLatheFlag.atlasHeight;
      this.matrixclientValues = matrixclientWarmValues != null ? matrixclientWarmValues : loftLatheFlag.loftLatheTable;
      this.matrixclientWarmValue = loftLatheFlag.getMax();
      this.listSize = list.size();
      this.matrixclientWarmTexts = new String[this.listSize];
      this.matrixclientWarmTable = new int[this.listSize];
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
         this.matrixclientWarmTexts[i] = atla.foreEarText;
         this.matrixclientWarmTable[i] = atla.foreEar == null ? -1 : atla.foreEar.boneIndex;
         if (this.matrixclientWarmTable[i] >= i) {
            throw new IllegalStateException("bone " + atla.foreEarText + " declared before its parent");
         }

         this.personCustomTable[3 * i] = atla.offsetX;
         this.personCustomTable[3 * i + 1] = atla.foreEarAmount;
         this.personCustomTable[3 * i + 2] = atla.shinFootAmount;
         this.vectorEmitFloats[i] = atla.rootTransform.getCopyOf();
         value += atla.rootTransform.getValue();
         float[] matrixclientWarmValues2 = this.vectorEmitFloats[i];

         for (int j = 0; j < matrixclientWarmValues2.length; j += 9) {
            amount = Math.min(amount, matrixclientWarmValues2[j]);
            maxValue4 = Math.max(maxValue4, matrixclientWarmValues2[j]);
            wrapDegreesBound = Math.min(wrapDegreesBound, matrixclientWarmValues2[j + 1]);
            pilotStatusBound = Math.max(pilotStatusBound, matrixclientWarmValues2[j + 1]);
            maxValue3 = Math.min(maxValue3, matrixclientWarmValues2[j + 2]);
            maxValue6 = Math.max(maxValue6, matrixclientWarmValues2[j + 2]);
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
      this.matrixclientWarmAmount = wrapDegreesBound;
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
