package com.matrix.client.categories.visuals;

import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.matrix.client.config.BooleanSetting;
import com.matrix.client.config.ConfigSetting;
import com.matrix.client.gui.DisabledEnabled;
import com.matrix.client.utils.IconsPngType;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class DeathMarker extends Module {
   private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
   private final BooleanSetting soundSetting;
   private boolean wasDead;

   public DeathMarker() {
      super("DeathMarker", IconsPngType.VISUAL);
      this.soundSetting = new BooleanSetting("Ping", "Play a sound when a death marker is placed", true);
      this.addSettings(new ConfigSetting[]{this.soundSetting});
   }

   @Override
   protected void onEnable() {
      this.wasDead = mc.player != null && mc.player.isDead();
   }

   @Override
   protected void onReset() {
      boolean dead = mc.player != null && mc.player.isDead();
      if (dead && !this.wasDead) {
         Vec3d entityPos = mc.player.getEntityPos();
         String deathText = "Death " + LocalTime.now().format(TIME_FORMAT);
         Waypoints waypoints = (Waypoints)ModuleRegistry.getModule(Waypoints.class);
         if (waypoints != null && waypoints.addExternalWaypoint(deathText, entityPos.x, entityPos.y, entityPos.z, 15737031)) {
            DisabledEnabled.getInstance()
               .handleDisabledEnabled(
                  "Death Marker", String.format("%s  %.0f %.0f %.0f", deathText, entityPos.x, entityPos.y, entityPos.z), false
               );
            if (this.soundSetting.isEnabled()) {
               mc.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 0.6F));
            }
         }
      }

      this.wasDead = dead;
   }
}
