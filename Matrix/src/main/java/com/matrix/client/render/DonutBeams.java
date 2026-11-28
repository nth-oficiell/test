package com.matrix.client.render;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public final class DonutBeams {
   private static final long START = System.nanoTime();
   private final Map<Integer, long[]> seen = new HashMap<>();

   public record Beam(int id, double x, double z) {
   }

   public static final class Style {
      public String shape = "Cylinder";
      public double width = 0.6;
      public double bottom = -64.0;
      public double top = 320.0;
      public double speed = 1.0;
      public String colorMode = "Static";
      public int color = -603979776 | 0xFF3232;
      public int color2 = -603979776 | 0xFF9A3C;
      public double colorLength = 40.0;
      public boolean pulse = true;
      public boolean fade = true;
      public boolean distanceScale = true;
      public double glowSize = 2.5;
      public String ringShape = "Circle";
      public int ringCount = 5;
      public double ringSize = 2.2;
      public double ringSpeed = 1.0;
      public double ringTilt = 0.25;
      public int spiralStrands = 2;
      public double spiralRadius = 1.6;
      public int sparkles = 24;
      public double effectHeight = 28.0;
      public double effectOpacity = 1.0;
      public String anchor = "Surface";
      public boolean intro = true;
      public boolean throughWalls = true;
   }

   public void clear() {
      this.seen.clear();
   }

   public void render(Collection<Beam> beams, Style style, double yLevel) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player == null || mc.world == null || beams.isEmpty()) {
         return;
      }
      long now = System.nanoTime();
      double t = (now - START) / 1.0E9;
      Vec3d cam = mc.gameRenderer.getCamera().getCameraPos();
      Matrix4f matrix = VertexBuilderUtils.getMatrix();
      VertexConsumer solid = style.throughWalls ? VertexBuilderUtils.getBufferIMMEDIATE() : VertexBuilderUtils.getBuffer();
      for (Beam beam : beams) {
         long[] s = this.seen.computeIfAbsent(beam.id(), k -> new long[]{now, now});
         if (now - s[1] > 3000000000L) {
            s[0] = now;
         }
         s[1] = now;
         double grow = style.intro ? Math.min(1.0, (now - s[0]) / 1.6E9) : 1.0;
         double groundY = ground(beam, style, yLevel);
         double span = (style.top - groundY) * grow;
         if (span <= 1.0) {
            continue;
         }
         double top = groundY + span;
         double dist = Math.hypot(beam.x() - cam.x, beam.z() - cam.z);
         double scale = style.distanceScale && dist > 32.0 ? Math.min(3.0, 1.0 + (dist - 32.0) / 96.0) : 1.0;
         double pulse = style.pulse ? 1.0 + 0.22 * Math.sin(t * style.speed * 3.141592653589793) : 1.0;
         double width = style.width * scale * pulse;
         renderPrism(solid, matrix, beam.x(), beam.z(), groundY, top, style, width, t, 1.0F);
         if (style.glowSize > 0.0) {
            renderPrism(solid, matrix, beam.x(), beam.z(), groundY, top, style, width + style.glowSize * 2.0 * scale, t, 0.18F);
         }
         renderRings(solid, matrix, beam, groundY, top, style, t, grow);
         renderSpirals(matrix, beam, groundY, top, style, t, grow);
         renderSparkles(solid, matrix, beam, groundY, top, style, t, grow);
      }
   }

   private static void renderPrism(VertexConsumer buffer, Matrix4f matrix, double x, double z, double y0, double y1, Style style, double width, double t, float alphaMul) {
      int sides = sides(style);
      double r0 = width / 2.0;
      double r1 = r0 * topRadius(style, y1 - y0);
      double twist = twist(style, y1 - y0);
      for (int k = 0; k < sides; k++) {
         double a0 = 6.283185307179586 * k / sides;
         double a1 = 6.283185307179586 * (k + 1) / sides;
         double m0 = sideMul(style, k);
         double m1 = sideMul(style, k + 1);
         double j0 = jitter(style, k, t);
         double j1 = jitter(style, k + 1, t);
         double bx0 = x + Math.cos(a0) * r0 * m0 + j0;
         double bz0 = z + Math.sin(a0) * r0 * m0 + j0 * 0.4;
         double bx1 = x + Math.cos(a1) * r0 * m1 + j1;
         double bz1 = z + Math.sin(a1) * r0 * m1 + j1 * 0.4;
         double tx0 = x + Math.cos(a0 + twist) * r1 * m0 + j0;
         double tz0 = z + Math.sin(a0 + twist) * r1 * m0 + j0 * 0.4;
         double tx1 = x + Math.cos(a1 + twist) * r1 * m1 + j1;
         double tz1 = z + Math.sin(a1 + twist) * r1 * m1 + j1 * 0.4;
         int c0 = colorAt(style, y0, y0, t, alphaMul);
         int c1 = colorAt(style, y1, y0, t, alphaMul);
         VertexBuilderUtils.handleVertexColor(
            buffer, matrix,
            (float)bx0, (float)y0, (float)bz0, c0,
            (float)bx1, (float)y0, (float)bz1, c0,
            (float)tx1, (float)y1, (float)tz1, c1,
            (float)tx0, (float)y1, (float)tz0, c1
         );
      }
   }

   private static void renderRings(VertexConsumer buffer, Matrix4f matrix, Beam beam, double groundY, double top, Style style, double t, double grow) {
      if ("Off".equals(style.ringShape) || style.ringCount <= 0 || style.effectOpacity <= 0.0) {
         return;
      }
      int seg = segments(style.ringShape);
      double height = Math.min(style.effectHeight, top - groundY);
      for (int i = 0; i < style.ringCount; i++) {
         double phase = frac(t * style.ringSpeed * 0.35 + (double)i / style.ringCount);
         double y = groundY + phase * height;
         double alpha = Math.sin(phase * 3.141592653589793) * style.effectOpacity;
         if (alpha <= 0.01) {
            continue;
         }
         double radius = style.ringSize * (1.0 + style.ringTilt * 0.3 * Math.sin(t * style.ringSpeed * 2.0 + i));
         int color = colorAt(style, y, groundY, t, (float)alpha);
         double prevX = 0.0;
         double prevZ = 0.0;
         for (int k = 0; k <= seg; k++) {
            double a = 6.283185307179586 * k / seg;
            double m = "Star".equals(style.ringShape) && (k & 1) == 1 ? 0.5 : 1.0;
            double px = beam.x() + Math.cos(a) * radius * m;
            double pz = beam.z() + Math.sin(a) * radius * m;
            if (k > 0) {
               quadSegment(buffer, matrix, prevX, y, prevZ, px, y, pz, groundY, style, color, 0.14);
            }
            prevX = px;
            prevZ = pz;
         }
      }
   }

   private static void quadSegment(VertexConsumer buffer, Matrix4f matrix, double x0, double y, double z0, double x1, double y1, double z1, double groundY, Style style, int color, double band) {
      VertexBuilderUtils.handleVertexColor(
         buffer, matrix,
         (float)x0, (float)y, (float)z0, color,
         (float)x1, (float)y, (float)z1, color,
         (float)x1, (float)(y + band), (float)z1, color,
         (float)x0, (float)(y + band), (float)z0, color
      );
   }

   private static void renderSpirals(Matrix4f matrix, Beam beam, double groundY, double top, Style style, double t, double grow) {
      if (style.spiralStrands <= 0 || style.effectOpacity <= 0.0) {
         return;
      }
      double height = Math.min(style.effectHeight, top - groundY);
      int color = colorAt(style, groundY + height * 0.5, groundY, t, (float)(0.85 * style.effectOpacity));
      for (int strand = 0; strand < style.spiralStrands; strand++) {
         double prevX = 0.0;
         double prevZ = 0.0;
         double prevY = 0.0;
         for (int j = 0; j <= 14; j++) {
            double phase = (double)j / 14.0;
            double y = groundY + phase * height;
            double ang = t * style.speed * 2.2 + strand * 6.283185307179586 / style.spiralStrands + phase * 12.566370614359172;
            double px = beam.x() + Math.cos(ang) * style.spiralRadius;
            double pz = beam.z() + Math.sin(ang) * style.spiralRadius;
            if (j > 0) {
               VertexBuilderUtils.handle(matrix, new Vec3d(prevX, prevY, prevZ), new Vec3d(px, y, pz), color, 0.08F);
            }
            prevX = px;
            prevZ = pz;
            prevY = y;
         }
      }
   }

   private static void renderSparkles(VertexConsumer buffer, Matrix4f matrix, Beam beam, double groundY, double top, Style style, double t, double grow) {
      if (style.sparkles <= 0 || style.effectOpacity <= 0.0) {
         return;
      }
      double height = Math.min(style.effectHeight, top - groundY);
      Random rng = new Random(beam.id() * 31L + 7L);
      for (int i = 0; i < style.sparkles; i++) {
         double ang0 = rng.nextDouble() * 6.283185307179586;
         double rad = style.spiralRadius * (0.6 + rng.nextDouble() * 0.9);
         double spd = 0.5 + rng.nextDouble();
         double phase = frac(rng.nextDouble() + t * spd * 0.12);
         double y = groundY + phase * height;
         double ang = ang0 + t * spd;
         double tw = 0.5 + 0.5 * Math.sin(t * 3.0 + i * 2.1);
         double alpha = style.effectOpacity * tw;
         if (alpha <= 0.02) {
            continue;
         }
         int color = colorAt(style, y, groundY, t, (float)alpha);
         double px = beam.x() + Math.cos(ang) * rad;
         double pz = beam.z() + Math.sin(ang) * rad;
         double s = 0.1 + 0.08 * tw;
         VertexBuilderUtils.handleVertexColor(
            buffer, matrix,
            (float)(px - s), (float)y, (float)pz, color,
            (float)px, (float)y, (float)(pz - s), color,
            (float)(px + s), (float)y, (float)pz, color,
            (float)px, (float)y, (float)(pz + s), color
         );
      }
   }

   private static double ground(Beam beam, Style style, double yLevel) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if ("Player".equals(style.anchor) && mc.player != null) {
         return mc.player.getY();
      }
      if ("Surface".equals(style.anchor) && mc.world != null) {
         try {
            int bx = (int)Math.floor(beam.x());
            int bz = (int)Math.floor(beam.z());
            int y = mc.world.getChunk(bx >> 4, bz >> 4).sampleHeightmap(net.minecraft.world.Heightmap.Type.WORLD_SURFACE, bx & 15, bz & 15);
            if (y > mc.world.getBottomY()) {
               return y + 1;
            }
         } catch (Throwable ignored) {
         }
      }
      return yLevel;
   }

   private static int segments(String shape) {
      return switch (shape) {
         case "Square" -> 4;
         case "Hexagon" -> 6;
         case "Star" -> 10;
         default -> 16;
      };
   }

   private static int sides(Style style) {
      return switch (style.shape) {
         case "Square", "DNA" -> 4;
         case "Hexagon", "Crystal" -> 6;
         default -> 10;
      };
   }

   private static double topRadius(Style style, double span) {
      return switch (style.shape) {
         case "Tornado" -> Math.min(3.0, 1.0 + span / 60.0);
         case "Crystal" -> 0.45;
         default -> 1.0;
      };
   }

   private static double twist(Style style, double span) {
      return switch (style.shape) {
         case "Tornado", "DNA" -> Math.min(3.0, span / 45.0);
         default -> 0.0;
      };
   }

   private static double sideMul(Style style, int k) {
      return "Crystal".equals(style.shape) && (k & 1) == 1 ? 0.55 : 1.0;
   }

   private static double jitter(Style style, int k, double t) {
      if (!"Lightning".equals(style.shape)) {
         return 0.0;
      }
      long step = (long)Math.floor(t * 12.0);
      Random rng = new Random(k * 92837111L + step);
      return (rng.nextDouble() - 0.5) * 0.9;
   }

   private static int colorAt(Style style, double y, double groundY, double t, float alphaMul) {
      int baseAlpha = style.color >>> 24;
      int alpha = (int)Math.min(255.0, baseAlpha * alphaMul * (style.fade ? Math.max(0.0, 1.0 - (y - groundY) / 240.0) : 1.0));
      int rgb = switch (style.colorMode) {
         case "Gradient" -> mix(style.color & 0xFFFFFF, style.color2 & 0xFFFFFF, frac(y / style.colorLength + t * style.speed * 0.25));
         case "Rainbow" -> hsv(frac(y / style.colorLength + t * style.speed * 0.25), 0.85, 1.0);
         case "Donut" -> mix(0xFF6EC7, 0x7C4DFF, 0.5 - 0.5 * Math.cos(6.283185307179586 * frac(y / style.colorLength + t * style.speed * 0.25)));
         case "Fire" -> mix(0xFF3600, 0xFFC400, 0.5 - 0.5 * Math.cos(6.283185307179586 * frac(y / style.colorLength + t * style.speed * 0.25)));
         case "Galaxy" -> mix(0x7C4DFF, 0x40C4FF, 0.5 - 0.5 * Math.cos(6.283185307179586 * frac(y / style.colorLength + t * style.speed * 0.25)));
         case "Ice" -> mix(0xE0F7FF, 0x4FC3F7, 0.5 - 0.5 * Math.cos(6.283185307179586 * frac(y / style.colorLength + t * style.speed * 0.25)));
         default -> style.color & 0xFFFFFF;
      };
      return alpha << 24 | rgb & 0xFFFFFF;
   }

   private static int mix(int c0, int c1, double m) {
      m = Math.max(0.0, Math.min(1.0, m));
      int r = (int)Math.round((c0 >> 16 & 0xFF) + ((c1 >> 16 & 0xFF) - (c0 >> 16 & 0xFF)) * m);
      int g = (int)Math.round((c0 >> 8 & 0xFF) + ((c1 >> 8 & 0xFF) - (c0 >> 8 & 0xFF)) * m);
      int b = (int)Math.round((c0 & 0xFF) + ((c1 & 0xFF) - (c0 & 0xFF)) * m);
      return r << 16 | g << 8 | b;
   }

   private static int hsv(double h, double s, double v) {
      double i = Math.floor(h * 6.0);
      double f = h * 6.0 - i;
      double p = v * (1.0 - s);
      double q = v * (1.0 - f * s);
      double tt = v * (1.0 - (1.0 - f) * s);
      int k = (int)Math.floor(i) % 6;
      return switch (k) {
         case 0 -> pack(v, tt, p);
         case 1 -> pack(q, v, p);
         case 2 -> pack(p, v, tt);
         case 3 -> pack(p, q, v);
         case 4 -> pack(tt, p, v);
         default -> pack(v, p, q);
      };
   }

   private static int pack(double r, double g, double b) {
      return (int)Math.round(r * 255.0) << 16 | (int)Math.round(g * 255.0) << 8 | (int)Math.round(b * 255.0);
   }

   private static double frac(double v) {
      return v - Math.floor(v);
   }
}
