package com.vortex.client.categories.configs;

import com.vortex.client.config.BooleanSetting;
import com.vortex.client.config.ConfigListSetting;
import com.vortex.client.config.ConfigSyncUtils;
import com.vortex.client.config.ModuleConfigSerializer;
import com.vortex.client.config.PresetFileStore;
import ai.auth.Module;
import com.vortex.client.utils.IconsPngType;
public class Configs extends Module {
   private long timeMillis;
   public final BooleanSetting booleanBase = new BooleanSetting("Autosave", "Keep the active config in sync with your live setup automatically.", false);

   @Override
   public boolean isHiddenModule() {
      return true;
   }

   public Configs() {
      super("Configs", IconsPngType.CONFIGS);
      this.timeMillis = 0L;
      this.addSetting(new ConfigListSetting("Configs", "Manage local, community and downloaded configs."));
      this.addSetting(this.booleanBase);
      this.booleanBase.hideFromList();
      this.setEnabled(true);
   }

   @Override
   protected void onReset() {
      if (this.booleanBase.isEnabled()) {
         long timeMillis = System.currentTimeMillis();
         if (timeMillis - this.timeMillis >= 8000L) {
            String readString = PresetFileStore.readActivePresetName();
            if (readString != null) {
               this.timeMillis = timeMillis;

               try {
                  if (!ModuleConfigSerializer.updateSavedConfig(readString)) {
                     return;
                  }

                  if (ConfigSyncUtils.isSignedIn()) {
                     ConfigSyncUtils.uploadPrivateConfig(readString, result -> {});
                  }
               } catch (Exception e) {
               }
            }
         }
      }
   }
}
