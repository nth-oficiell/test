package com.vortex.client.categories.settings;

import com.vortex.client.config.ConfigSetting;
import com.vortex.client.gui.ButtonBase;
import com.vortex.client.gui.VortexLicense;
import net.minecraft.client.MinecraftClient;
import ai.auth.LicenseRuntime;
import ai.auth.Module;
import com.vortex.client.utils.IconsPngType;

public class License extends Module {

   public License() {
      super("License", IconsPngType.SETTING);
      this.addSettings(new ConfigSetting[]{new ButtonBase("License status", "Open the license panel", "View License", () -> {
         MinecraftClient instance = MinecraftClient.getInstance();
         instance.setScreen(new VortexLicense(instance.currentScreen));
      }).getSupplier(LicenseRuntime::getLicenseOwnerLabel).getSupplierForSupplier(() -> -12922502)});
   }

   @Override
   public boolean isHiddenModule() {
      return true;
   }
}
