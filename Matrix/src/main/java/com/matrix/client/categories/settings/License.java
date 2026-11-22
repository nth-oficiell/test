package com.matrix.client.categories.settings;

import com.matrix.client.config.ConfigSetting;
import com.matrix.client.gui.ButtonBase;
import com.matrix.client.gui.MatrixLicense;
import net.minecraft.client.MinecraftClient;
import ai.auth.LicenseRuntime;
import ai.auth.Module;
import com.matrix.client.utils.IconsPngType;

public class License extends Module {

   public License() {
      super("License", IconsPngType.SETTING);
      this.addSettings(new ConfigSetting[]{new ButtonBase("License status", "Open the license panel", "View License", () -> {
         MinecraftClient instance = MinecraftClient.getInstance();
         instance.setScreen(new MatrixLicense(instance.currentScreen));
      }).getSupplier(LicenseRuntime::getLicenseOwnerLabel).getSupplierForSupplier(() -> -12922502)});
   }

   @Override
   public boolean isHiddenModule() {
      return true;
   }
}
