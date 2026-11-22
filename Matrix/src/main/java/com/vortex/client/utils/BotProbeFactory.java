package com.vortex.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import com.vortex.client.categories.movement.Freecam;
import com.vortex.client.utils.ConnectionTimeUtils;
import com.vortex.client.utils.BotRunSupervisor;
import com.vortex.client.utils.ServerTpsEstimator;
import com.vortex.client.utils.UserOverrideTracker;
import com.vortex.client.utils.CrystalBotModuleMonitor;
import com.vortex.client.utils.TrackFlick;

public final class BotProbeFactory {

   private BotProbeFactory() {
   }

   public static BotRunSupervisor.Probe buildBotProbe(long vehicleCreativeValue, long entityBudgetValue, ServerTpsEstimator playerTime, UserOverrideTracker playerFlag, CrystalBotModuleMonitor botCrystal, TrackFlick trackFlick) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      boolean vehicleCreativeFlag = player != null && player.isAlive() && !player.isDead() && !(instance.currentScreen instanceof DeathScreen);
      return new BotRunSupervisor.Probe(
         vehicleCreativeValue,
         entityBudgetValue,
         instance.getNetworkHandler(),
         instance.world,
         player,
         vehicleCreativeFlag,
         isScreenOrOverlayOpen(instance.currentScreen, instance.getOverlay()),
         Freecam.isInstanceFlag(),
         ConnectionTimeUtils.getTime(),
         ConnectionTimeUtils.Outbound.isFlag(),
         ConnectionTimeUtils.Inbound.isFlag(),
         playerTime != null && playerTime.isTimeStale(vehicleCreativeValue),
         playerTime == null ? 20.0 : playerTime.getEstimatedTps(),
         playerFlag != null && playerFlag.isUserOverriding(vehicleCreativeValue),
         botCrystal != null && botCrystal.isYieldingModuleActive(),
         botCrystal == null ? null : botCrystal.getConflictingModuleName(),
         botCrystal == null ? null : botCrystal.getBlockingModulesText(),
         player != null && player.isCreative(),
         player != null && player.isSpectator(),
         player != null && player.hasVehicle(),
         trackFlick == null ? 0 : trackFlick.getCommitMismatchStreak()
      );
   }

   public static boolean isScreenOrOverlayOpen(Screen screen, Overlay overlay) {
      return screen != null || overlay != null;
   }
}
