package com.vortex.client.utils;

import net.minecraft.util.Identifier;

public enum IconsPngType {
   COMBAT("Combat", Identifier.of("vortexclient", "textures/gui/icons/combat.png")),
   MOVEMENT("Movement", Identifier.of("vortexclient", "textures/gui/icons/movement.png")),
   DONUTSMP("DonutSMP", Identifier.of("vortexclient", "textures/gui/icons/donutsmp.png")),
   VISUAL("Visuals", Identifier.of("vortexclient", "textures/gui/icons/visual.png")),
   MISC("Misc", Identifier.of("vortexclient", "textures/gui/icons/misc.png")),
   NETWORK("Network", Identifier.of("vortexclient", "textures/gui/icons/network.png")),
   BOTS("Bots", Identifier.of("vortexclient", "textures/gui/icons/bots.png")),
   SETTING("Settings", Identifier.of("vortexclient", "textures/gui/icons/settings.png")),
   CONFIGS("Configs", Identifier.of("vortexclient", "textures/gui/icons/configs.png")),
   THEME("Theme", Identifier.of("vortexclient", "textures/gui/icons/theme.png")),
   SOCIALS("Socials", Identifier.of("vortexclient", "textures/gui/icons/socials.png"));
   private final Identifier identifier;
   private final String iconsPngText;

   private IconsPngType(String text, Identifier identifier) {
      this.iconsPngText = text;
      this.identifier = identifier;
   }

   public String getText() {
      return this.iconsPngText;
   }

   public Identifier getIdentifier() {
      return this.identifier;
   }
}
