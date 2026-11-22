package com.vortex.client.categories.socials;

import com.vortex.client.config.ConfigSetting;
import ai.auth.Module;
import com.vortex.client.utils.FriendListBase;
import com.vortex.client.utils.IconsPngType;
public class Friends extends Module {
   private final FriendListBase friendListBase = new FriendListBase("Friends", "Players your modules will never target.");

   public Friends() {
      super("Friends", IconsPngType.SOCIALS);
      this.addSettings(new ConfigSetting[]{this.friendListBase});
      this.setEnabled(true);
   }

   @Override
   public boolean isHiddenModule() {
      return true;
   }
}
