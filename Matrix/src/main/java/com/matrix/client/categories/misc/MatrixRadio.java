package com.matrix.client.categories.misc;

import com.matrix.client.config.ConfigSetting;
import com.matrix.client.config.RadioSetting;
import ai.auth.Module;
import com.matrix.client.utils.IconsPngType;
public class MatrixRadio extends Module {
   private final RadioSetting radioBase = new RadioSetting("Stations", "Staff playlists. Pick a station to open it.");

   @Override
   public void syncSettingHandlers() {
      this.radioBase.setSelectedStationKey("");
      super.syncSettingHandlers();
   }

   public MatrixRadio() {
      super("Matrix Radio", IconsPngType.MISC);
      this.addSettings(new ConfigSetting[]{this.radioBase});
   }
}
