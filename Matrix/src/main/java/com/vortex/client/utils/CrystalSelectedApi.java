package com.vortex.client.utils;

import com.vortex.client.entity.ImmutablePos;

public interface CrystalSelectedApi {
   boolean isFlag();

   boolean canLedgerHandswap(ImmutablePos immutablePos);

   void handleSelectedInventory(int selectedInventoryValue);

   default void update() {
   }

   default boolean isFlag2() {
      return false;
   }

   default boolean canSelectedInventory(int selectedInventoryValue) {
      return false;
   }

   default float[] getActionCrystal(float actionCrystalValue, float actionCrystalValue2) {
      return null;
   }

   void handleCrystal(float crystalValue, float f2);

   float[] getTrackAction(float actionValue, float clamp, float f, float boxLengthBound, boolean actionFlag);
}
