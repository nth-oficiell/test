package com.vortex.client.categories.movement;

import com.vortex.client.categories.bots.AutoBuilder;
import ai.auth.Module;
import com.vortex.client.categories.movement.Scaffold;
import com.vortex.client.utils.ElytraAutoUtils;
import com.vortex.client.utils.IconsPngType;

public class Sprint extends Module {
   @Override
   protected void onReset() {
      if (!AutoBuilder.vortexGapActive) {
         if (!Scaffold.isGodBridgeFlag()) {
            if (mc.player != null && mc.player.forwardSpeed > 0.0F) {
               ElytraAutoUtils.handleRawPressed(mc.options.sprintKey, true);
            }
         }
      }
   }

   public Sprint() {
      super("Sprint", IconsPngType.MOVEMENT);
   }
}
