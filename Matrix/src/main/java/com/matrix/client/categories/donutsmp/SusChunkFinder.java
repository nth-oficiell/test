package com.matrix.client.categories.donutsmp;

import com.matrix.client.config.ConfigSetting;
import com.matrix.client.config.BooleanSetting;
import com.matrix.client.config.ColorSetting;
import com.matrix.client.config.DoubleSetting;
import com.matrix.client.config.EnumSetting;
import com.matrix.client.config.IntegerSetting;
import com.matrix.client.gui.DisabledEnabled;
import com.matrix.client.categories.misc.FakeCoordinates;
import com.matrix.client.render.DonutBeams;
import com.matrix.client.render.QueueCommand;
import com.matrix.client.render.VertexBuilderUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.text.Text;
import net.minecraft.sound.SoundEvents;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.matrix.client.utils.DonutBaseStore;
import com.matrix.client.utils.IconsPngType;

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
   private final EnumSetting renderMode = new EnumSetting("Render Mode", "Area = flat square, Laser = beam into the sky in the middle of the area, Both.", "Area", "Area", "Laser", "Both");
   private final EnumSetting beamShape = new EnumSetting("Beam Shape", "Shape of the laser.", "Hexagon", "Cylinder", "Square", "Hexagon", "Crystal", "Tornado", "Lightning", "DNA");
   private final DoubleSetting beamWidth = new DoubleSetting("Beam Width", "Width of the laser in blocks.", 0.6, 0.1, 4.0);
   private final IntegerSetting beamBottom = new IntegerSetting("Beam Bottom", "Lowest Y of the laser.", -64, -64, 320);
   private final IntegerSetting beamTop = new IntegerSetting("Beam Top", "Highest Y of the laser.", 320, -64, 320);
   private final BooleanSetting beamGlow = new BooleanSetting("Beam Glow", "Draws a wider transparent glow around the laser.", true);
   private final DoubleSetting glowSize = new DoubleSetting("Glow Size", "How wide the glow is.", 2.5, 0.5, 8.0);
   private final EnumSetting colorMode = new EnumSetting("Beam Color Mode", "Static and Gradient use your colors, the others are animated color themes.", "Gradient", "Static", "Gradient", "Rainbow", "Donut", "Fire", "Galaxy", "Ice");
   private final ColorSetting beamColor = new ColorSetting("Beam Color", "Main color. Its alpha is the transparency of the laser.", (int)0xC8FF3232L);
   private final ColorSetting beamColor2 = new ColorSetting("Beam Color 2", "Second color of the Gradient mode.", (int)0xC8FF9A3CL);
   private final DoubleSetting colorLength = new DoubleSetting("Color Length", "Blocks until the colors repeat along the laser.", 40.0, 4.0, 200.0);
   private final DoubleSetting beamSpeed = new DoubleSetting("Beam Speed", "Speed of all animations (0 = still).", 1.0, 0.0, 4.0);
   private final BooleanSetting beamPulse = new BooleanSetting("Beam Pulse", "The laser pulses in width and brightness.", true);
   private final BooleanSetting beamFade = new BooleanSetting("Beam Fade", "The laser fades out towards the top.", true);
   private final BooleanSetting distanceScale = new BooleanSetting("Distance Scale", "Lasers far away get wider so you can still see them.", true);
   private final EnumSetting ringShape = new EnumSetting("Rings", "Shape of the rings that rise along the laser.", "Hexagon", "Off", "Circle", "Square", "Hexagon", "Star");
   private final IntegerSetting ringCount = new IntegerSetting("Ring Count", "Number of rings.", 4, 0, 20);
   private final DoubleSetting ringSize = new DoubleSetting("Ring Size", "Radius of the rings.", 2.2, 0.5, 12.0);
   private final DoubleSetting ringSpeed = new DoubleSetting("Ring Speed", "Speed of the rings.", 1.0, 0.0, 5.0);
   private final IntegerSetting sparkles = new IntegerSetting("Sparkles", "Number of twinkling sparkles.", 24, 0, 150);
   private final DoubleSetting effectHeight = new DoubleSetting("Effect Height", "How high the effects reach above the ground.", 28.0, 4.0, 100.0);
   private final DoubleSetting effectOpacity = new DoubleSetting("Effect Opacity", "Opacity of rings, spirals and sparkles.", 1.0, 0.0, 1.0);
   private final EnumSetting anchor = new EnumSetting("Anchor", "Where the laser stands: Surface = on the ground, YLevel = at y-level, Player = at your height.", "Surface", "Surface", "YLevel", "Player");
   private final BooleanSetting throughWalls = new BooleanSetting("Through Walls", "Shows the laser and its effects through blocks.", true);
   private final DonutBeams beams = new DonutBeams();

   public SusChunkFinder() {
      super("Sus Chunk Finder", IconsPngType.DONUTSMP);
      this.addSettings(
         new ConfigSetting[]{this.areaRadius, this.fuzz, this.toastSetting, this.chatSetting, this.soundSetting, this.maxDistance, this.renderMode, this.beamShape, this.beamWidth, this.beamBottom, this.beamTop, this.beamGlow, this.glowSize, this.colorMode, this.beamColor, this.beamColor2, this.colorLength, this.beamSpeed, this.beamPulse, this.beamFade, this.distanceScale, this.ringShape, this.ringCount, this.ringSize, this.ringSpeed, this.sparkles, this.effectHeight, this.effectOpacity, this.anchor, this.throughWalls, this.yLevel, this.sideColor, this.lineColor}
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
      this.beams.clear();
      if (mc.inGameHud != null) {
         int size = DonutBaseStore.size();
         mc.inGameHud.getChatHud().addMessage(Text.literal("\u00a78[\u00a7cSus Chunk Finder\u00a78] \u00a77" + size + " sus area" + (size == 1 ? "" : "s") + " known."));
      }
   }

   @Override
   protected void onDisable() {
      this.beams.clear();
   }

   private DonutBeams.Style style() {
      DonutBeams.Style s = new DonutBeams.Style();
      s.shape = this.beamShape.getCurrentOption();
      s.width = this.beamWidth.getValue();
      s.bottom = this.beamBottom.getValue();
      s.top = this.beamTop.getValue();
      s.speed = this.beamSpeed.getValue();
      s.colorMode = this.colorMode.getCurrentOption();
      s.color = this.beamColor.getColor();
      s.color2 = this.beamColor2.getColor();
      s.colorLength = this.colorLength.getValue();
      s.pulse = this.beamPulse.isEnabled();
      s.fade = this.beamFade.isEnabled();
      s.distanceScale = this.distanceScale.isEnabled();
      s.glowSize = this.beamGlow.isEnabled() ? this.glowSize.getValue() : 0.0;
      s.ringShape = this.ringShape.getCurrentOption();
      s.ringCount = this.ringCount.getValue();
      s.ringSize = this.ringSize.getValue();
      s.ringSpeed = this.ringSpeed.getValue();
      s.ringTilt = 0.0;
      s.spiralStrands = 0;
      s.sparkles = this.sparkles.getValue();
      s.effectHeight = this.effectHeight.getValue();
      s.effectOpacity = this.effectOpacity.getValue();
      s.anchor = this.anchor.getCurrentOption();
      s.throughWalls = this.throughWalls.isEnabled();
      return s;
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (mc.player == null || mc.world == null) {
         return;
      }
      String mode = this.renderMode.getCurrentOption();
      int pcx = mc.player.getBlockX() >> 4;
      int pcz = mc.player.getBlockZ() >> 4;
      int max = this.maxDistance.getValue();
      int r = this.areaRadius.getValue();
      int color = this.lineColor.getColor();
      int sc = this.sideColor.getColor();
      float sr = (sc >> 16 & 0xFF) / 255.0F;
      float sg = (sc >> 8 & 0xFF) / 255.0F;
      float sb = (sc & 0xFF) / 255.0F;
      float sa = (sc >>> 24 & 0xFF) / 255.0F;
      double y = this.yLevel.getValue();
      float y1 = VertexBuilderUtils.getAmount(y);
      float y2 = VertexBuilderUtils.getAmount(y + 0.05);
      Matrix4f matrix = VertexBuilderUtils.getMatrix();
      List<DonutBeams.Beam> beamList = mode.equals("Area") ? null : new ArrayList<>();
      Collection<DonutBaseStore.Base> bases = DonutBaseStore.all();

      for (DonutBaseStore.Base base : bases) {
         int[] c = this.center(base);
         if (Math.abs(c[0] - pcx) > max || Math.abs(c[1] - pcz) > max) {
            continue;
         }
         if (beamList != null) {
            beamList.add(new DonutBeams.Beam(base.id(), c[0] * 16.0 + 8.0, c[1] * 16.0 + 8.0));
         }
         if (mode.equals("Laser")) {
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

      if (beamList != null) {
         this.beams.render(beamList, this.style(), y);
      }
      VertexBuilderUtils.updateDrawImmediate();
   }

   public String getCountText() {
      return String.valueOf(DonutBaseStore.size());
   }
}
