package com.vortex.client.categories.donutsmp;

import com.vortex.client.config.ConfigSetting;
import com.vortex.client.config.BooleanSetting;
import com.vortex.client.config.ColorSetting;
import com.vortex.client.config.IntegerSetting;
import com.vortex.client.gui.DisabledEnabled;
import com.vortex.client.categories.misc.FakeCoordinates;
import com.vortex.client.render.QueueCommand;
import com.vortex.client.render.VertexBuilderUtils;
import java.util.Collection;
import net.minecraft.text.Text;
import net.minecraft.sound.SoundEvents;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.vortex.client.utils.DonutBaseStore;
import com.vortex.client.utils.IconsPngType;

public class SusChunkFinderV2 extends Module {
   private final BooleanSetting toastSetting = new BooleanSetting("Toast", "Shows a toast message when a base chunk is found.", true);
   private final BooleanSetting chatSetting = new BooleanSetting("Chat Message", "Also writes the found chunk into the chat.", true);
   private final BooleanSetting soundSetting = new BooleanSetting("Sound", "Plays a sound when a base chunk is found.", true);
   private final IntegerSetting maxDistance = new IntegerSetting("Max Distance", "Only render base chunks within this many chunks.", 32, 1, 64);
   private final IntegerSetting yLevel = new IntegerSetting("Y Level", "Height the golden chunk is rendered at.", 64, -64, 320);
   private final IntegerSetting height = new IntegerSetting("Height", "Thickness of the golden chunk in blocks.", 1, 0, 64);
   private final ColorSetting sideColor = new ColorSetting("Side Color", "Fill color of the chunk", 0x46FFC400);
   private final ColorSetting lineColor = new ColorSetting("Line Color", "Outline color of the chunk", 0xFFFFD700);

   public SusChunkFinderV2() {
      super("SusChunkFinderV2", IconsPngType.DONUTSMP);
      this.addSettings(
         new ConfigSetting[]{this.toastSetting, this.chatSetting, this.soundSetting, this.maxDistance, this.yLevel, this.height, this.sideColor, this.lineColor}
      );
      DonutBaseStore.onNewBase(base -> {
         if (this.isEnabled()) {
            this.announce(base);
         }
      });
   }

   private void announce(DonutBaseStore.Base base) {
      int bx = base.chunkX() * 16 + 8;
      int bz = base.chunkZ() * 16 + 8;
      if (this.toastSetting.isEnabled()) {
         DisabledEnabled.getInstance().handleDisabledEnabled("Base Chunk Found!", "X " + bx + ", Z " + bz, true);
      }
      if (this.chatSetting.isEnabled() && mc.inGameHud != null) {
         mc.inGameHud.getChatHud().addMessage(FakeCoordinates.getLiteral(Text.literal("\u00a78[\u00a7eSusChunkFinderV2\u00a78] \u00a77Golden chunk found at \u00a7eX " + bx + "\u00a77, \u00a7eZ " + bz)));
      }
      if (this.soundSetting.isEnabled() && mc.player != null) {
         mc.player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 0.6F, 1.2F);
      }
   }

   @Override
   protected void onEnable() {
      if (mc.inGameHud != null) {
         int size = DonutBaseStore.size();
         mc.inGameHud.getChatHud().addMessage(Text.literal(size == 0 ? "\u00a78[\u00a7eSusChunkFinderV2\u00a78] \u00a77Waiting for base data from the server..." : "\u00a78[\u00a7eSusChunkFinderV2\u00a78] \u00a77" + size + " base chunk" + (size == 1 ? "" : "s") + " known."));
      }
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (mc.player == null || mc.world == null) {
         return;
      }
      int pcx = mc.player.getBlockX() >> 4;
      int pcz = mc.player.getBlockZ() >> 4;
      int max = this.maxDistance.getValue();
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
      Collection<DonutBaseStore.Base> bases = DonutBaseStore.all();

      for (DonutBaseStore.Base base : bases) {
         int cx = base.chunkX();
         int cz = base.chunkZ();
         if (Math.abs(cx - pcx) > max || Math.abs(cz - pcz) > max) {
            continue;
         }
         double x1 = cx * 16.0;
         double z1 = cz * 16.0;
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
      return String.valueOf(DonutBaseStore.size());
   }
}
