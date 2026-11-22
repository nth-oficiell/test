package com.matrix.client.utils;

import com.matrix.client.config.ConfigSetting;
import com.matrix.client.utils.MinmaxEnumType;

public class PaletteBase extends ConfigSetting {
   @Override
   public MinmaxEnumType getSettingType() {
      return MinmaxEnumType.PALETTE;
   }

   public PaletteBase(String settingName, String description) {
      super(settingName, description);
   }
}
