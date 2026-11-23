package com.vortex.client.render;

import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

final class SchemVortex implements AutoCloseable {
   BufferGpu vortexSchemOverlay;
   int visitedChunkCount;
   final int matrixStackValue;
   int bufferCloseValue;
   int[] intArray;
   BufferGpu vortexSchemGhostChest;
   BufferGpu vortexSchemOutline;
   double distanceSq;
   final long longValue;
   private static final int[] schemVortexTable = new int[0];
   long[] longArray;
   int chunkArrayCount;
   final int arrayHashValue;
   int[] hashOpenTable;
   final int schemVortexValue;
   boolean fmsRebuildActive;
   byte[] unpackExtraTable;
   private static final byte[] table = new byte[0];
   int fmsRebuildCount;
   boolean chunkArrayActive;
   boolean hashOpenActive;
   final Box box;
   final BufferGpu[] transformsDynamicBufferGpus;
   int schemVortexCount;
   static final long[] chunkArrayTable = new long[0];
   int chunkArrayRebuiltCount;
   int arrayHashCount;

   BufferGpu getSchemOverlay() {
      if (this.vortexSchemOverlay == null) {
         this.vortexSchemOverlay = new BufferGpu("vortex_schem_overlay");
      }

      return this.vortexSchemOverlay;
   }

   double getAmount(double schemVortexValue, double schemVortexValue2, double schemVortexValue3) {
      double schemVortexFactor = this.schemVortexValue + 8.0 - schemVortexValue;
      double schemVortexFactor2 = this.matrixStackValue + 8.0 - schemVortexValue2;
      double schemVortexFactor3 = this.arrayHashValue + 8.0 - schemVortexValue3;
      return schemVortexFactor * schemVortexFactor + schemVortexFactor2 * schemVortexFactor2 + schemVortexFactor3 * schemVortexFactor3;
   }

   @Override
   public void close() {
      for (int i = 0; i < this.transformsDynamicBufferGpus.length; i++) {
         if (this.transformsDynamicBufferGpus[i] != null) {
            this.transformsDynamicBufferGpus[i].close();
            this.transformsDynamicBufferGpus[i] = null;
         }
      }

      if (this.vortexSchemGhostChest != null) {
         this.vortexSchemGhostChest.close();
         this.vortexSchemGhostChest = null;
      }

      if (this.vortexSchemOverlay != null) {
         this.vortexSchemOverlay.close();
         this.vortexSchemOverlay = null;
      }

      if (this.vortexSchemOutline != null) {
         this.vortexSchemOutline.close();
         this.vortexSchemOutline = null;
      }
   }

   BufferGpu getGhostSchem() {
      if (this.vortexSchemGhostChest == null) {
         this.vortexSchemGhostChest = new BufferGpu("vortex_schem_ghost_chest");
      }

      return this.vortexSchemGhostChest;
   }

   BufferGpu getGpusTransforms(BlockRenderLayer blockRenderLayer) {
      int ordinal = blockRenderLayer.ordinal();
      BufferGpu gpusTransforms = this.transformsDynamicBufferGpus[ordinal];
      if (gpusTransforms == null) {
         gpusTransforms = new BufferGpu("vortex_schem_ghost_" + blockRenderLayer.getName());
         this.transformsDynamicBufferGpus[ordinal] = gpusTransforms;
      }

      return gpusTransforms;
   }

   SchemVortex(long longValue, int value, int value2, int value3) {
      this.hashOpenTable = schemVortexTable;
      this.unpackExtraTable = table;
      this.intArray = schemVortexTable;
      this.longArray = chunkArrayTable;
      this.hashOpenActive = true;
      this.transformsDynamicBufferGpus = new BufferGpu[BlockRenderLayer.values().length];
      this.longValue = longValue;
      this.schemVortexValue = value;
      this.matrixStackValue = value2;
      this.arrayHashValue = value3;
      this.box = new Box(value, value2, value3, value + 16, value2 + 16, value3 + 16);
   }

   static long getTime(int schemVortexValue, int schemVortexValue2, int schemVortexValue3) {
      return BlockPos.asLong(schemVortexValue >> 4, schemVortexValue2 >> 4, schemVortexValue3 >> 4);
   }

   boolean isSchemVortex() {
      for (BufferGpu bufferGpu : this.transformsDynamicBufferGpus) {
         if (bufferGpu != null && !bufferGpu.isBuffer()) {
            return false;
         }
      }

      return this.vortexSchemGhostChest != null && !this.vortexSchemGhostChest.isBuffer()
         ? false
         : (this.vortexSchemOverlay == null || this.vortexSchemOverlay.isBuffer()) && (this.vortexSchemOutline == null || this.vortexSchemOutline.isBuffer());
   }

   BufferGpu getSchemOutline() {
      if (this.vortexSchemOutline == null) {
         this.vortexSchemOutline = new BufferGpu("vortex_schem_outline");
      }

      return this.vortexSchemOutline;
   }
}
