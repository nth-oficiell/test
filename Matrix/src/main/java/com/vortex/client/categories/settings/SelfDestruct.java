package com.vortex.client.categories.settings;

import com.vortex.client.config.ConfigSetting;
import com.vortex.client.config.BooleanSetting;
import com.vortex.client.gui.ButtonBase;
import com.vortex.client.gui.DisabledEnabled;
import com.vortex.client.categories.bots.AutoBuilder;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.vortex.client.utils.PanicDestructUtils;
import com.vortex.client.utils.LoadingModsUtils;
import com.vortex.client.utils.VortexclientAttribUtils;
import com.vortex.client.utils.DirExtractionUtils;
import com.vortex.client.utils.IconsPngType;

public class SelfDestruct extends Module {
   private final BooleanSetting booleanBase;
   private final BooleanSetting scrubLogTraces = new BooleanSetting(
      "Scrub Log Traces",
      "Also rewrite .minecraft/logs/latest.log to remove this client's traces (mod list, mixin and console lines) for a screenshare. Your own local file only \u2014 no network, nothing else touched.",
      true
   );

   public SelfDestruct() {
      super("Self Destruct", IconsPngType.SETTING);
      this.addSettings(new ConfigSetting[]{this.scrubLogTraces});
      this.booleanBase = new BooleanSetting(
         "Delete Jar On Exit",
         "Hide the client jar from the mods folder now, then delete it from disk when Minecraft closes (crash-free). WARNING: the client is GONE afterwards and must be reinstalled \u2014 turn this off to keep the relaunch-restore behaviour instead.",
         true
      );
      this.addSettings(new ConfigSetting[]{this.booleanBase});
      this.addSettings(
         new ConfigSetting[]{
            new ButtonBase(
                  "Self Destruct",
                  "Instantly hide the whole client for a screenshare: disable-all + purge from memory, scrub logs, and (by default) delete the jar on close.",
                  "Self Destruct",
                  this::updateSelfdestructVortex
               )
               .getFlagButtonActive()
         }
      );
   }

   @Override
   public boolean isHiddenModule() {
      return true;
   }

   @Override
   public void toggle() {
      this.updateSelfdestructVortex();
   }

   private void updateSelfdestructVortex() {
      if (!DirExtractionUtils.isFlag()) {
         DirExtractionUtils.updateFlag();
         if (mc.currentScreen != null) {
            mc.setScreen(null);
         }

         try (AutoCloseable disabledEnabled = DisabledEnabled.getInstance().getDisabledEnabled()) {
            if (ModuleRegistry.getModule(AutoBuilder.class) instanceof AutoBuilder autoBuilder) {
               autoBuilder.leaveMultiplayerSession();
            }

            for (Module getItem : PanicDestructUtils.getItems(ModuleRegistry.getModules())) {
               getItem.setEnabled(false);
            }
         } catch (Exception e) {
         }

         if (this.scrubLogTraces.isEnabled()) {
            LoadingModsUtils.isLatestLogs();
         }

         if (this.booleanBase.isEnabled()) {
            VortexclientAttribUtils.update();
         }

         Thread thread = new Thread(() -> {
            for (int i = 1; i <= 4; i++) {
               System.gc();

               try {
                  Thread.sleep(100L * i);
               } catch (InterruptedException e2) {
                  Thread.currentThread().interrupt();
                  return;
               }
            }

            System.gc();
         }, "vortex-selfdestruct-gc");
         thread.setDaemon(true);
         thread.start();
      }
   }
}
