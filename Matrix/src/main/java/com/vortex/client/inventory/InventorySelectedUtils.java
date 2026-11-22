package com.vortex.client.inventory;

import com.vortex.client.mixins.AccessorClientPlayerInteractionManager;
import net.minecraft.client.MinecraftClient;
import com.vortex.client.categories.combat.ObsidianOptimizer;

public final class InventorySelectedUtils {
   public static void handleSelectedSlot(int selectedSlot) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.player != null && instance.interactionManager != null && selectedSlot >= 0 && selectedSlot <= 8) {
         if (instance.player.getInventory().getSelectedSlot() != selectedSlot) {
            instance.player.getInventory().setSelectedSlot(selectedSlot);
            ObsidianOptimizer.syncSelectedSlot(selectedSlot);
            ((AccessorClientPlayerInteractionManager)instance.interactionManager).vortexSyncSelectedSlot();
         }
      }
   }

   private InventorySelectedUtils() {
   }
}
