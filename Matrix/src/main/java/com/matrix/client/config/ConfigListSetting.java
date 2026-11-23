package com.matrix.client.config;
import com.matrix.client.utils.MinmaxEnumType;

public class ConfigListSetting extends ConfigSetting {
   public ConfigListSetting(String settingName, String description) {
      super(settingName, description);
   }

   @Override
   public MinmaxEnumType getSettingType() {
      return MinmaxEnumType.CONFIG_LIST;
   }
}
