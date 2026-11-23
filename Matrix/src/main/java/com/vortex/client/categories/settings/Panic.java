package com.vortex.client.categories.settings;

import com.vortex.client.config.ConfigSetting;
import com.vortex.client.config.ModuleConfigAutosave;
import com.vortex.client.gui.ButtonBase;
import com.vortex.client.gui.DisabledEnabled;
import java.util.List;
import com.vortex.client.categories.bots.AutoBuilder;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.vortex.client.utils.PanicDestructUtils;
import com.vortex.client.utils.IconsPngType;

public class Panic extends Module {
   private static boolean panicActivatedFlag;

   @Override
   public boolean isHiddenModule() {
      return true;
   }

   public Panic() {
      super("Panic", IconsPngType.SETTING);
      this.addSettings(
         new ConfigSetting[]{
            new ButtonBase(
                  "Disable all active modules immediately",
                  "Switch every active module off at once. Also available on a keybind.",
                  "Activate",
                  this::updatePanicActivated
               )
               .getFlagButtonActive()
         }
      );
   }

   public static boolean isActivatedPanic() {
      return panicActivatedFlag;
   }

   public void updatePanicActivated() {
      if (ModuleRegistry.getModule(AutoBuilder.class) instanceof AutoBuilder autoBuilder) {
         autoBuilder.leaveMultiplayerSession();
      }

      var list = PanicDestructUtils.getItems(ModuleRegistry.getModules());
      if (list.isEmpty()) {
         DisabledEnabled.getInstance().handleDisabledEnabled("Panic", "No active modules to disable", false);
      } else {
         panicActivatedFlag = true;

         try (AutoCloseable disabledEnabled = DisabledEnabled.getInstance().getDisabledEnabled()) {
            for (Module ilIllIiIll2 : list) {
               ilIllIiIll2.setEnabled(false);
            }
         } catch (Exception e) {
         } finally {
            panicActivatedFlag = false;
         }

         ModuleConfigAutosave.updateFlag();
         DisabledEnabled.getInstance()
            .handleDisabledEnabled(
               "Panic Activated", list.size() == 1 ? "1 active module was disabled" : "All " + list.size() + " active modules were disabled", false
            );
      }
   }

   @Override
   public void toggle() {
      this.updatePanicActivated();
   }
}
