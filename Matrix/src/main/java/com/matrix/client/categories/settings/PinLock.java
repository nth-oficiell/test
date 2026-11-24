package com.matrix.client.categories.settings;

import ai.auth.Module;
import com.matrix.client.config.ConfigSetting;
import com.matrix.client.config.StringBase;
import com.matrix.client.utils.IconsPngType;

public class PinLock extends Module {
   private final StringBase pinCode;

   public PinLock() {
      super("PinLock", IconsPngType.SETTING);
      this.pinCode = new StringBase("Code", "Digits you must type to open the menu (empty = no lock)", "1337");
      this.addSettings(new ConfigSetting[]{this.pinCode});
   }

   public String getCode() {
      String currentText = this.pinCode.getCurrentText();
      return currentText == null ? "" : currentText.replaceAll("\\D", "");
   }

   public boolean isCodeLongEnough() {
      return !this.getCode().isEmpty();
   }
}
