package com.matrix.client.inventory;

import net.minecraft.item.ItemStack;
import com.matrix.client.utils.SwapPreferenceData;

public interface PlayerInventoryApi {
   boolean isBreachOwnerActive();

   SwapPreferenceData getSwapPreference();

   int getSwapPriority();

   default boolean isMaceSmashReady() {
      return false;
   }

   default boolean isSwapAllowed(Object obj, ItemStack stack) {
      return true;
   }
}
