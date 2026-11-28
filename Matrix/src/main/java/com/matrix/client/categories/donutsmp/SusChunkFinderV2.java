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

public class SusChunkFinderV2 extends Module {
   private final BooleanSetting toastSetting = new BooleanSetting("Toast", "Shows a toast message when a base chunk is found.", true);
   private final BooleanSetting chatSetting = new BooleanSetting("Chat Message", "Also writes the found chunk into the chat.", true);
   private final BooleanSetting soundSetting = new BooleanSetting("Sound", "Plays a sound when a base chunk is found.", true);
   private final IntegerSetting maxDistance = new IntegerSetting("Max Distance", "Only render base chunks within this many chunks.", 32, 1, 64);
   private final IntegerSetting yLevel = new IntegerSetting("Y Level", "Height the golden chunk is rendered at.", 64, -64, 320);
   private final IntegerSetting height = new IntegerSetting("Height", "Thickness of the golden chunk in blocks.", 1, 0, 64);
   private final ColorSetting sideColor = new ColorSetting("Side Color", "Fill color of the chunk", 0x46FFC400);
   private final ColorSetting lineColor = new ColorSetting("Line Color", "Outline color of the chunk", 0xFFFFD700);
   private final EnumSetting renderMode = new EnumSetting("Render Mode", "Chunk = golden chunk, Beam = beam into the sky in the middle of the chunk, Both.", "Chunk", "Chunk", "Beam", "Both");
   private final EnumSetting beamShape = new EnumSetting("Beam Shape", "Shape of the beam.", "Cylinder", "Cylinder", "Square", "Hexagon", "Crystal", "Tornado", "Lightning", "DNA");
   private final DoubleSetting beamWidth = new DoubleSetting("Beam Width", "Width of the beam in blocks.", 0.6, 0.1, 4.0);
   private final IntegerSetting beamBottom = new IntegerSetting("Beam Bottom", "Lowest Y of the beam.", -64, -64, 320);
   private final IntegerSetting beamTop = new IntegerSetting("Beam Top", "Highest Y of the beam.", 320, -64, 320);
   private final BooleanSetting beamGlow = new BooleanSetting("Beam Glow", "Draws a wider transparent glow around the beam.", true);
   private final DoubleSetting glowSize = new DoubleSetting("Glow Size", "How wide the glow is.", 2.5, 0.5, 8.0);
   private final EnumSetting colorMode = new EnumSetting("Beam Color Mode", "Static and Gradient use your colors, the others are animated color themes.", "Donut", "Static", "Gradient", "Rainbow", "Donut", "Fire", "Galaxy", "Ice");
   private final ColorSetting beamColor = new ColorSetting("Beam Color", "Main color. Its alpha is the transparency of the beam.", (int)0xC82979FFL);
   private final ColorSetting beamColor2 = new ColorSetting("Beam Color 2", "Second color of the Gradient mode.", (int)0xC8FF5C8AL);
   private final DoubleSetting colorLength = new DoubleSetting("Color Length", "Blocks until the colors repeat along the beam.", 40.0, 4.0, 200.0);
   private final DoubleSetting beamSpeed = new DoubleSetting("Beam Speed", "Speed of all animations (0 = still).", 1.0, 0.0, 4.0);
   private final BooleanSetting beamPulse = new BooleanSetting("Beam Pulse", "The beam pulses in width and brightness.", true);
   private final BooleanSetting beamFade = new BooleanSetting("Beam Fade", "The beam fades out towards the top.", true);
   private final BooleanSetting distanceScale = new BooleanSetting("Distance Scale", "Beams far away get wider so you can still see them.", true);
   private final EnumSetting ringShape = new EnumSetting("Rings", "Shape of the rings that rise along the beam.", "Circle", "Off", "Circle", "Square", "Hexagon", "Star");
   private final IntegerSetting ringCount = new IntegerSetting("Ring Count", "Number of rings.", 5, 0, 20);
   private final DoubleSetting ringSize = new DoubleSetting("Ring Size", "Radius of the rings.", 2.2, 0.5, 12.0);
   private final DoubleSetting ringSpeed = new DoubleSetting("Ring Speed", "Speed of the rings.", 1.0, 0.0, 5.0);
   private final DoubleSetting ringTilt = new DoubleSetting("Ring Tilt", "How much the rings wobble.", 0.25, 0.0, 1.0);
   private final IntegerSetting spiralStrands = new IntegerSetting("Spiral Strands", "Light strands that wind up around the beam.", 2, 0, 8);
   private final DoubleSetting spiralRadius = new DoubleSetting("Spiral Radius", "Radius of the strands and sparkles.", 1.6, 0.5, 10.0);
   private final IntegerSetting sparkles = new IntegerSetting("Sparkles", "Number of twinkling sparkles.", 24, 0, 150);
   private final DoubleSetting effectHeight = new DoubleSetting("Effect Height", "How high the effects reach above the ground.", 28.0, 4.0, 100.0);
   private final DoubleSetting effectOpacity = new DoubleSetting("Effect Opacity", "Opacity of rings, spirals and sparkles.", 1.0, 0.0, 1.0);
   private final EnumSetting anchor = new EnumSetting("Anchor", "Where the beam stands: Surface = on the ground, YLevel = at y-level, Player = at your height.", "Surface", "Surface", "YLevel", "Player");
   private final BooleanSetting throughWalls = new BooleanSetting("Through Walls", "Shows the beam and its effects through blocks.", true);
   private final DonutBeams beams = new DonutBeams();

   public SusChunkFinderV2() {
      super("SusChunkFinderV2", IconsPngType.DONUTSMP);
      this.addSettings(
         new ConfigSetting[]{this.toastSetting, this.chatSetting, this.soundSetting, this.maxDistance, this.renderMode, this.beamShape, this.beamWidth, this.beamBottom, this.beamTop, this.beamGlow, this.glowSize, this.colorMode, this.beamColor, this.beamColor2, this.colorLength, this.beamSpeed, this.beamPulse, this.beamFade, this.distanceScale, this.ringShape, this.ringCount, this.ringSize, this.ringSpeed, this.ringTilt, this.spiralStrands, this.spiralRadius, this.sparkles, this.effectHeight, this.effectOpacity, this.anchor, this.throughWalls, this.yLevel, this.height, this.sideColor, this.lineColor}
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
      this.beams.clear();
      if (mc.inGameHud != null) {
         int size = DonutBaseStore.size();
         mc.inGameHud.getChatHud().addMessage(Text.literal(size == 0 ? "\u00a78[\u00a7eSusChunkFinderV2\u00a78] \u00a77Waiting for base data from the server..." : "\u00a78[\u00a7eSusChunkFinderV2\u00a78] \u00a77" + size + " base chunk" + (size == 1 ? "" : "s") + " known."));
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
      s.ringTilt = this.ringTilt.getValue();
      s.spiralStrands = this.spiralStrands.getValue();
      s.spiralRadius = this.spiralRadius.getValue();
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
      List<DonutBeams.Beam> beamList = mode.equals("Chunk") ? null : new ArrayList<>();
      Collection<DonutBaseStore.Base> bases = DonutBaseStore.all();

      for (DonutBaseStore.Base base : bases) {
         int cx = base.chunkX();
         int cz = base.chunkZ();
         if (Math.abs(cx - pcx) > max || Math.abs(cz - pcz) > max) {
            continue;
         }
         if (beamList != null) {
            beamList.add(new DonutBeams.Beam(base.id(), cx * 16.0 + 8.0, cz * 16.0 + 8.0));
         }
         if (mode.equals("Beam")) {
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

      if (beamList != null) {
         this.beams.render(beamList, this.style(), y1);
      }
      VertexBuilderUtils.updateDrawImmediate();
   }

   public String getCountText() {
      return String.valueOf(DonutBaseStore.size());
   }
}
