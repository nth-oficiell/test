package com.vortex.client.utils;

import com.vortex.client.config.ConfigSetting;
import com.vortex.client.utils.MinmaxEnumType;

public class PaletteBase extends ConfigSetting {
   @Override
   public MinmaxEnumType getSettingType() {
      return MinmaxEnumType.PALETTE;
   }

   public PaletteBase(String settingName, String description) {
      super(settingName, description);
   }
}
