package com.matrix.client.categories.misc;

import ai.auth.Module;
import com.matrix.client.config.BooleanSetting;
import com.matrix.client.config.ConfigSetting;
import com.matrix.client.config.IntegerSetting;
import com.matrix.client.utils.IconsPngType;
import java.util.Locale;
import net.minecraft.text.Text;

/**
 * Client performance module. Every setting here is consumed by a hot path:
 * the scan budget by {@link com.matrix.client.utils.ChunkScanScheduler}, the particle
 * cap by {@code MixinParticleManager}, and the profiler by {@link ai.auth.ModuleRegistry}.
 */
public class Optimizer extends Module {
   private static final double SMOOTHING = 0.05D;
   private static volatile Optimizer instance;

   private static double tickAverageMs;
   private static double render3DAverageMs;
   private static double render2DAverageMs;
   private static double tickPeakMs;
   private static double render3DPeakMs;
   private static double render2DPeakMs;
   private static long tickSamples;
   private static long render3DSamples;
   private static long render2DSamples;

   private static int particleTick = Integer.MIN_VALUE;
   private static int particleCount;

   private final BooleanSetting scanBudgetEnabled;
   private final IntegerSetting scanBudgetPercent;
   private final BooleanSetting particleCapEnabled;
   private final IntegerSetting particleCap;
   private final BooleanSetting profiler;

   public Optimizer() {
      super("Optimizer", IconsPngType.MISC);
      this.scanBudgetEnabled = new BooleanSetting(
         "Chunk Scan Budget", "Scales the shared time budget the chunk scanners may spend per tick.", true
      );
      this.scanBudgetPercent = new IntegerSetting("Scan Budget", "Percentage of the default chunk scan budget.", 100, 25, 200, 5);
      this.particleCapEnabled = new BooleanSetting(
         "Particle Cap", "Drops particles once the per-tick limit is reached to smooth particle spikes.", false
      );
      this.particleCap = new IntegerSetting("Particles / Tick", "Maximum particles spawned per tick while Particle Cap is enabled.", 1500, 100, 10000, 100);
      this.profiler = new BooleanSetting(
         "Profiler", "Measures tick and render dispatch times. The summary is printed in chat when the module is disabled.", true
      );
      this.addSettings(
         new ConfigSetting[]{this.scanBudgetEnabled, this.scanBudgetPercent, this.particleCapEnabled, this.particleCap, this.profiler}
      );
      instance = this;
   }

   public static int getScanBudgetPercent() {
      Optimizer optimizer = instance;
      if (optimizer == null || !optimizer.isEnabled() || !optimizer.scanBudgetEnabled.isEnabled()) {
         return 100;
      }
      return optimizer.scanBudgetPercent.getValue();
   }

   public static boolean isProfiling() {
      Optimizer optimizer = instance;
      return optimizer != null && optimizer.isEnabled() && optimizer.profiler.isEnabled();
   }

   public static boolean shouldDropParticle() {
      Optimizer optimizer = instance;
      if (optimizer == null || !optimizer.isEnabled() || !optimizer.particleCapEnabled.isEnabled()) {
         return false;
      }
      if (mc == null || mc.inGameHud == null) {
         return false;
      }
      int tick = mc.inGameHud.getTicks();
      if (tick != particleTick) {
         particleTick = tick;
         particleCount = 0;
      }
      return ++particleCount > optimizer.particleCap.getValue();
   }

   public static void recordTick(long nanos) {
      tickSamples++;
      tickAverageMs = blend(tickAverageMs, nanos);
      tickPeakMs = Math.max(tickPeakMs, nanos / 1_000_000.0D);
   }

   public static void recordRender3D(long nanos) {
      render3DSamples++;
      render3DAverageMs = blend(render3DAverageMs, nanos);
      render3DPeakMs = Math.max(render3DPeakMs, nanos / 1_000_000.0D);
   }

   public static void recordRender2D(long nanos) {
      render2DSamples++;
      render2DAverageMs = blend(render2DAverageMs, nanos);
      render2DPeakMs = Math.max(render2DPeakMs, nanos / 1_000_000.0D);
   }

   private static double blend(double average, long nanos) {
      double millis = nanos / 1_000_000.0D;
      return average == 0.0D ? millis : average + (millis - average) * SMOOTHING;
   }

   public static String summary() {
      if (tickSamples == 0L && render3DSamples == 0L && render2DSamples == 0L) {
         return "no samples yet";
      }
      return String.format(
         Locale.ROOT,
         "tick %.2f/%.2f ms - render3D %.2f/%.2f ms - render2D %.2f/%.2f ms (avg/peak)",
         tickAverageMs,
         tickPeakMs,
         render3DAverageMs,
         render3DPeakMs,
         render2DAverageMs,
         render2DPeakMs
      );
   }

   private static void resetStats() {
      tickAverageMs = 0.0D;
      render3DAverageMs = 0.0D;
      render2DAverageMs = 0.0D;
      tickPeakMs = 0.0D;
      render3DPeakMs = 0.0D;
      render2DPeakMs = 0.0D;
      tickSamples = 0L;
      render3DSamples = 0L;
      render2DSamples = 0L;
   }

   @Override
   protected void onEnable() {
      resetStats();
   }

   @Override
   protected void onDisable() {
      if (mc == null || mc.player == null) {
         return;
      }
      mc.player.sendMessage(Text.literal("\u00a7b[Optimizer]\u00a7r " + summary()), false);
   }
}
