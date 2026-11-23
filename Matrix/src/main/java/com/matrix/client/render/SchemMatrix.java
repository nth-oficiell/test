package com.matrix.client.render;

import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

final class SchemMatrix implements AutoCloseable {
   BufferGpu matrixSchemOverlay;
   int visitedChunkCount;
   final int matrixStackValue;
   int bufferCloseValue;
   int[] intArray;
   BufferGpu matrixSchemGhostChest;
   BufferGpu matrixSchemOutline;
   double distanceSq;
   final long longValue;
   private static final int[] schemMatrixTable = new int[0];
   long[] longArray;
   int chunkArrayCount;
   final int arrayHashValue;
   int[] hashOpenTable;
   final int schemMatrixValue;
   boolean fmsRebuildActive;
   byte[] unpackExtraTable;
   private static final byte[] table = new byte[0];
   int fmsRebuildCount;
   boolean chunkArrayActive;
   boolean hashOpenActive;
   final Box box;
   final BufferGpu[] transformsDynamicBufferGpus;
   int schemMatrixCount;
   static final long[] chunkArrayTable = new long[0];
   int chunkArrayRebuiltCount;
   int arrayHashCount;

   BufferGpu getSchemOverlay() {
      if (this.matrixSchemOverlay == null) {
         this.matrixSchemOverlay = new BufferGpu("matrix_schem_overlay");
      }

      return this.matrixSchemOverlay;
   }

   double getAmount(double schemMatrixValue, double schemMatrixValue2, double schemMatrixValue3) {
      double schemMatrixFactor = this.schemMatrixValue + 8.0 - schemMatrixValue;
      double schemMatrixFactor2 = this.matrixStackValue + 8.0 - schemMatrixValue2;
      double schemMatrixFactor3 = this.arrayHashValue + 8.0 - schemMatrixValue3;
      return schemMatrixFactor * schemMatrixFactor + schemMatrixFactor2 * schemMatrixFactor2 + schemMatrixFactor3 * schemMatrixFactor3;
   }

   @Override
   public void close() {
      for (int i = 0; i < this.transformsDynamicBufferGpus.length; i++) {
         if (this.transformsDynamicBufferGpus[i] != null) {
            this.transformsDynamicBufferGpus[i].close();
            this.transformsDynamicBufferGpus[i] = null;
         }
      }

      if (this.matrixSchemGhostChest != null) {
         this.matrixSchemGhostChest.close();
         this.matrixSchemGhostChest = null;
      }

      if (this.matrixSchemOverlay != null) {
         this.matrixSchemOverlay.close();
         this.matrixSchemOverlay = null;
      }

      if (this.matrixSchemOutline != null) {
         this.matrixSchemOutline.close();
         this.matrixSchemOutline = null;
      }
   }

   BufferGpu getGhostSchem() {
      if (this.matrixSchemGhostChest == null) {
         this.matrixSchemGhostChest = new BufferGpu("matrix_schem_ghost_chest");
      }

      return this.matrixSchemGhostChest;
   }

   BufferGpu getGpusTransforms(BlockRenderLayer blockRenderLayer) {
      int ordinal = blockRenderLayer.ordinal();
      BufferGpu gpusTransforms = this.transformsDynamicBufferGpus[ordinal];
      if (gpusTransforms == null) {
         gpusTransforms = new BufferGpu("matrix_schem_ghost_" + blockRenderLayer.getName());
         this.transformsDynamicBufferGpus[ordinal] = gpusTransforms;
      }

      return gpusTransforms;
   }

   SchemMatrix(long longValue, int value, int value2, int value3) {
      this.hashOpenTable = schemMatrixTable;
      this.unpackExtraTable = table;
      this.intArray = schemMatrixTable;
      this.longArray = chunkArrayTable;
      this.hashOpenActive = true;
      this.transformsDynamicBufferGpus = new BufferGpu[BlockRenderLayer.values().length];
      this.longValue = longValue;
      this.schemMatrixValue = value;
      this.matrixStackValue = value2;
      this.arrayHashValue = value3;
      this.box = new Box(value, value2, value3, value + 16, value2 + 16, value3 + 16);
   }

   static long getTime(int schemMatrixValue, int schemMatrixValue2, int schemMatrixValue3) {
      return BlockPos.asLong(schemMatrixValue >> 4, schemMatrixValue2 >> 4, schemMatrixValue3 >> 4);
   }

   boolean isSchemMatrix() {
      for (BufferGpu bufferGpu : this.transformsDynamicBufferGpus) {
         if (bufferGpu != null && !bufferGpu.isBuffer()) {
            return false;
         }
      }

      return this.matrixSchemGhostChest != null && !this.matrixSchemGhostChest.isBuffer()
         ? false
         : (this.matrixSchemOverlay == null || this.matrixSchemOverlay.isBuffer()) && (this.matrixSchemOutline == null || this.matrixSchemOutline.isBuffer());
   }

   BufferGpu getSchemOutline() {
      if (this.matrixSchemOutline == null) {
         this.matrixSchemOutline = new BufferGpu("matrix_schem_outline");
      }

      return this.matrixSchemOutline;
   }
}
