package com.vortex.client.categories.misc;

import com.vortex.client.config.ConfigSetting;
import com.vortex.client.config.RadioSetting;
import ai.auth.Module;
import com.vortex.client.utils.IconsPngType;
public class VortexRadio extends Module {
   private final RadioSetting radioBase = new RadioSetting("Stations", "Staff playlists. Pick a station to open it.");

   @Override
   public void syncSettingHandlers() {
      this.radioBase.setSelectedStationKey("");
      super.syncSettingHandlers();
   }

   public VortexRadio() {
      super("Vortex Radio", IconsPngType.MISC);
      this.addSettings(new ConfigSetting[]{this.radioBase});
   }
}
