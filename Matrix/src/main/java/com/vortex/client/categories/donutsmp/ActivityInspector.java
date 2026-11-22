package com.vortex.client.categories.donutsmp;

import com.vortex.client.config.ConfigSetting;
import com.vortex.client.config.BooleanSetting;
import com.vortex.client.config.ColorSetting;
import com.vortex.client.config.IntegerSetting;
import com.vortex.client.gui.DisabledEnabled;
import com.vortex.client.gui.TickProgress;
import com.vortex.client.categories.misc.FakeCoordinates;
import com.vortex.client.render.QueueCommand;
import com.vortex.client.render.VertexBuilderUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.text.Text;
import net.minecraft.sound.SoundEvents;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.vortex.client.utils.DonutActivityStore;
import com.vortex.client.utils.IconsPngType;

public class ActivityInspector extends Module {
   private final BooleanSetting chatSetting = new BooleanSetting("Chat Message", "Writes into the chat when new activity shows up.", true);
   private final BooleanSetting soundSetting = new BooleanSetting("Sound", "Plays a sound when new activity shows up.", false);
   private final BooleanSetting toastSetting = new BooleanSetting("Toast", "Shows a toast message when new activity shows up.", false);
   private final IntegerSetting yLevel = new IntegerSetting("Y Level", "Height the red chunk is rendered at.", 64, -64, 320);
   private final IntegerSetting height = new IntegerSetting("Height", "Thickness of the red chunk in blocks.", 1, 0, 64);
   private final ColorSetting sideColor = new ColorSetting("Side Color", "Fill color of the chunk", 0x46FF1E1E);
   private final ColorSetting lineColor = new ColorSetting("Line Color", "Outline color of the chunk", 0xFFFF2828);
   private final Set<Long> seen;

   public ActivityInspector() {
      super("Activity Inspector", IconsPngType.DONUTSMP);
      this.addSettings(
         new ConfigSetting[]{this.chatSetting, this.soundSetting, this.toastSetting, this.yLevel, this.height, this.sideColor, this.lineColor}
      );
      this.seen = new HashSet<>();
   }

   @Override
   protected void onEnable() {
      this.seen.clear();
   }

   @Override
   protected void onDisable() {
      this.seen.clear();
   }

   @Override
   protected void onReset() {
      this.seen.clear();
   }

   private void checkNewActivity() {
      List<DonutActivityStore.Activity> activities = DonutActivityStore.all();
      HashSet<Long> now = new HashSet<>();
      for (DonutActivityStore.Activity a : activities) {
         long key = (long)a.chunkX() << 32 ^ (long)a.chunkZ() & 0xFFFFFFFFL;
         now.add(key);
         if (this.seen.contains(key)) {
            continue;
         }
         String placeText = a.chunkX() * 16 + 8 + ", " + (a.chunkZ() * 16 + 8) + " (" + a.players() + (a.players() == 1 ? " player" : " players") + ")";
         if (this.chatSetting.isEnabled() && mc.inGameHud != null) {
            mc.inGameHud.getChatHud().addMessage(FakeCoordinates.getLiteral(Text.literal("\u00a78[\u00a7cActivity Inspector\u00a78] \u00a77Activity below chunk \u00a7c" + placeText)));
         }
         if (this.toastSetting.isEnabled()) {
            DisabledEnabled.getInstance().handleDisabledEnabled("Underground Activity", "X " + placeText, true);
         }
         if (this.soundSetting.isEnabled() && mc.player != null) {
            mc.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8F, 0.6F);
         }
      }
      this.seen.clear();
      this.seen.addAll(now);
   }

   @Override
   protected void onRender2D(TickProgress tickProgress) {
      this.checkNewActivity();
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (mc.world == null) {
         return;
      }
      int color = this.lineColor.getColor();
      int sc = this.sideColor.getColor();
      float sr = (sc >> 16 & 0xFF) / 255.0F;
      float sg = (sc >> 8 & 0xFF) / 255.0F;
      float sb = (sc & 0xFF) / 255.0F;
      float sa = (sc >>> 24 & 0xFF) / 255.0F;
      double y1 = this.yLevel.getValue();
      double y2 = y1 + Math.max(0.02, this.height.getValue());
      float ry1 = VertexBuilderUtils.getAmount(y1);
      float ry2 = VertexBuilderUtils.getAmount(y2);
      Matrix4f matrix = VertexBuilderUtils.getMatrix();
      List<DonutActivityStore.Activity> activities = DonutActivityStore.all();

      for (DonutActivityStore.Activity a : activities) {
         double x1 = a.chunkX() * 16.0;
         double z1 = a.chunkZ() * 16.0;
         float ax1 = VertexBuilderUtils.getAmountX(x1);
         float ax2 = VertexBuilderUtils.getAmountX(x1 + 16.0);
         float az1 = VertexBuilderUtils.getAmountForDouble(z1);
         float az2 = VertexBuilderUtils.getAmountForDouble(z1 + 16.0);
         if (!VertexBuilderUtils.canVector(x1 + 8.0, y1, z1 + 8.0, 32.0F)) {
            VertexBuilderUtils.handle(matrix, ax1, ry1, az1, ax2, ry2, az2, sr, sg, sb, sa);
            VertexBuilderUtils.handle(matrix, ax1, ry1, az1, ax2, ry2, az2, color, 2.0F);
         }
      }

      VertexBuilderUtils.updateDrawImmediate();
   }

   public String getCountText() {
      return String.valueOf(DonutActivityStore.all().size());
   }
}
