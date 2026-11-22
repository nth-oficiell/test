package com.matrix.client.categories.movement;

import com.matrix.client.categories.bots.AutoBuilder;
import ai.auth.Module;
import com.matrix.client.categories.movement.Scaffold;
import com.matrix.client.utils.ElytraAutoUtils;
import com.matrix.client.utils.IconsPngType;

public class Sprint extends Module {
   @Override
   protected void onReset() {
      if (!AutoBuilder.matrixGapActive) {
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
