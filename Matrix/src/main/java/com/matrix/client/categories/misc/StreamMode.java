package com.matrix.client.categories.misc;

import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.matrix.client.config.BooleanSetting;
import com.matrix.client.config.ConfigSetting;
import com.matrix.client.utils.IconsPngType;

public class StreamMode extends Module {
   private final BooleanSetting hideHud;
   private final BooleanSetting hideNames;
   private Boolean streamerHudHidden;

   public StreamMode() {
      super("StreamMode", IconsPngType.MISC);
      this.hideHud = new BooleanSetting("Hide HUD", "Hide the vanilla HUD while streaming", true);
      this.hideNames = new BooleanSetting("Hide Names", "Keeps NameProtect active while streaming", true);
      this.addSettings(new ConfigSetting[]{this.hideHud, this.hideNames});
   }

   @Override
   protected void onEnable() {
      this.streamerHudHidden = null;
      if (mc.player != null && this.hideHud.isEnabled()) {
         this.streamerHudHidden = mc.options.hudHidden;
         mc.options.hudHidden = true;
      }

      if (this.hideNames.isEnabled()) {
         NameProtect nameProtect = (NameProtect)ModuleRegistry.getModule(NameProtect.class);
         if (nameProtect != null && !nameProtect.isEnabled()) {
            nameProtect.setEnabled(true);
         }
      }
   }

   @Override
   protected void onDisable() {
      if (this.streamerHudHidden != null) {
         mc.options.hudHidden = this.streamerHudHidden;
         this.streamerHudHidden = null;
      }
   }

   @Override
   protected void onReset() {
      if (this.hideHud.isEnabled() && mc.player != null) {
         if (this.streamerHudHidden == null) {
            this.streamerHudHidden = mc.options.hudHidden;
         }

         mc.options.hudHidden = true;
      }
   }
}
