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

public class SusChunkFinder extends Module {
   private static final long FUZZ_MULTIPLIER = -7046029254386353131L;
   private final IntegerSetting areaRadius = new IntegerSetting("Area Radius", "Size of the sus area in chunks around its centre (2 = 5x5 chunks).", 2, 1, 8);
   private final IntegerSetting fuzz = new IntegerSetting("Fuzz", "How far (chunks) the area centre may be shifted away from the real base.", 2, 0, 8);
   private final BooleanSetting toastSetting = new BooleanSetting("Toast", "Shows a toast message when a sus area is found.", true);
   private final BooleanSetting chatSetting = new BooleanSetting("Chat Message", "Also writes the sus area into the chat.", false);
   private final BooleanSetting soundSetting = new BooleanSetting("Sound", "Plays a sound when a sus area is found.", true);
   private final IntegerSetting maxDistance = new IntegerSetting("Max Distance", "Only render sus areas within this many chunks.", 40, 1, 64);
   private final IntegerSetting yLevel = new IntegerSetting("Y Level", "Height the sus area is rendered at.", 63, -64, 320);
   private final ColorSetting sideColor = new ColorSetting("Side Color", "Fill color of the sus area", 0x23FF3C3C);
   private final ColorSetting lineColor = new ColorSetting("Line Color", "Outline color of the sus area", 0xDCFF4646);

   public SusChunkFinder() {
      super("Sus Chunk Finder", IconsPngType.DONUTSMP);
      this.addSettings(
         new ConfigSetting[]{this.areaRadius, this.fuzz, this.toastSetting, this.chatSetting, this.soundSetting, this.maxDistance, this.yLevel, this.sideColor, this.lineColor}
      );
      DonutBaseStore.onNewBase(base -> {
         if (this.isEnabled()) {
            this.announce(base);
         }
      });
   }

   private int[] center(DonutBaseStore.Base base) {
      int f = this.fuzz.getValue();
      long h = (long)base.id() * FUZZ_MULTIPLIER;
      int ox = f == 0 ? 0 : (int)Math.floorMod(h >>> 7, 2 * f + 1) - f;
      int oz = f == 0 ? 0 : (int)Math.floorMod(h >>> 29, 2 * f + 1) - f;
      return new int[]{base.chunkX() + ox, base.chunkZ() + oz};
   }

   private void announce(DonutBaseStore.Base base) {
      int[] c = this.center(base);
      int bx = c[0] * 16 + 8;
      int bz = c[1] * 16 + 8;
      if (this.toastSetting.isEnabled()) {
         DisabledEnabled.getInstance().handleDisabledEnabled("Sus Area Detected", "around X " + bx + ", Z " + bz, true);
      }
      if (this.chatSetting.isEnabled() && mc.inGameHud != null) {
         mc.inGameHud.getChatHud().addMessage(FakeCoordinates.getLiteral(Text.literal("\u00a78[\u00a7cSus Chunk Finder\u00a78] \u00a77Sus area around \u00a7cX " + bx + "\u00a77, \u00a7cZ " + bz)));
      }
      if (this.soundSetting.isEnabled() && mc.player != null) {
         mc.player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 0.6F, 0.8F);
      }
   }

   @Override
   protected void onEnable() {
      if (mc.inGameHud != null) {
         int size = DonutBaseStore.size();
         mc.inGameHud.getChatHud().addMessage(Text.literal("\u00a78[\u00a7cSus Chunk Finder\u00a78] \u00a77" + size + " sus area" + (size == 1 ? "" : "s") + " known."));
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
      int r = this.areaRadius.getValue();
      int color = this.lineColor.getColor();
      float lr = (color >> 16 & 0xFF) / 255.0F;
      float lg = (color >> 8 & 0xFF) / 255.0F;
      float lb = (color & 0xFF) / 255.0F;
      int sc = this.sideColor.getColor();
      float sr = (sc >> 16 & 0xFF) / 255.0F;
      float sg = (sc >> 8 & 0xFF) / 255.0F;
      float sb = (sc & 0xFF) / 255.0F;
      float sa = (sc >>> 24 & 0xFF) / 255.0F;
      double y = this.yLevel.getValue();
      float y1 = VertexBuilderUtils.getAmount(y);
      float y2 = VertexBuilderUtils.getAmount(y + 0.05);
      Matrix4f matrix = VertexBuilderUtils.getMatrix();
      Collection<DonutBaseStore.Base> bases = DonutBaseStore.all();

      for (DonutBaseStore.Base base : bases) {
         int[] c = this.center(base);
         if (Math.abs(c[0] - pcx) > max || Math.abs(c[1] - pcz) > max) {
            continue;
         }
         double x1 = (c[0] - r) * 16.0;
         double z1 = (c[1] - r) * 16.0;
         double x2 = (c[0] + r + 1) * 16.0;
         double z2 = (c[1] + r + 1) * 16.0;
         float ax1 = VertexBuilderUtils.getAmountX(x1);
         float ax2 = VertexBuilderUtils.getAmountX(x2);
         float az1 = VertexBuilderUtils.getAmountForDouble(z1);
         float az2 = VertexBuilderUtils.getAmountForDouble(z2);
         if (!VertexBuilderUtils.canVector(x1 + 8.0, y, z1 + 8.0, 32.0F)) {
            VertexBuilderUtils.handle(matrix, ax1, y1, az1, ax2, y2, az2, sr, sg, sb, sa);
            VertexBuilderUtils.handle(matrix, ax1, y1, az1, ax2, y2, az2, color, 2.0F);
         }
      }

      VertexBuilderUtils.updateDrawImmediate();
   }

   public String getCountText() {
      return String.valueOf(DonutBaseStore.size());
   }
}
