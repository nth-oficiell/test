package com.matrix.client.categories.misc;

import ai.auth.Module;
import com.matrix.client.config.BooleanSetting;
import com.matrix.client.config.ConfigSetting;
import com.matrix.client.config.IntegerSetting;
import com.matrix.client.utils.IconsPngType;
import java.util.Locale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Client performance module. Every setting here is consumed by a hot path:
 * the scan budget by {@link com.matrix.client.utils.ChunkScanScheduler}, the particle
 * cap by {@code MixinParticleManager}, the culling distances by the entity and block
 * entity render managers, and the profiler by {@link ai.auth.ModuleRegistry}.
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
   private static int culledEntities;
   private static int culledBlockEntities;

   private static int particleTick = Integer.MIN_VALUE;
   private static int particleCount;

   private final BooleanSetting entityCull;
   private final IntegerSetting entityCullDistance;
   private final BooleanSetting cullPlayers;
   private final BooleanSetting blockEntityCull;
   private final IntegerSetting blockEntityCullDistance;
   private final BooleanSetting scanBudgetEnabled;
   private final IntegerSetting scanBudgetPercent;
   private final BooleanSetting particleCapEnabled;
   private final IntegerSetting particleCap;
   private final BooleanSetting profiler;

   public Optimizer() {
      super("Optimizer", IconsPngType.MISC);
      this.entityCull = new BooleanSetting(
         "Entity Cull", "Stops drawing distant mobs, items, armor stands and boats. Players are kept.", true
      );
      this.entityCullDistance = new IntegerSetting("Entity Distance", "Entities further than this are not rendered, in blocks.", 64, 16, 160, 8);
      this.cullPlayers = new BooleanSetting("Cull Players", "Also culls distant players. Off keeps every player visible.", false);
      this.blockEntityCull = new BooleanSetting(
         "Block Entity Cull", "Stops drawing distant chests, signs, banners and skulls.", true
      );
      this.blockEntityCullDistance = new IntegerSetting(
         "Block Entity Distance", "Block entities further than this are not rendered, in blocks.", 48, 8, 96, 8
      );
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
         new ConfigSetting[]{
            this.entityCull,
            this.entityCullDistance,
            this.cullPlayers,
            this.blockEntityCull,
            this.blockEntityCullDistance,
            this.scanBudgetEnabled,
            this.scanBudgetPercent,
            this.particleCapEnabled,
            this.particleCap,
            this.profiler
         }
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

   /**
    * Called from {@code EntityRenderManager.shouldRender} with the entity already translated into
    * camera space, so this stays allocation free on a path that runs for every entity every frame.
    */
   public static boolean shouldCullEntity(Entity entity, double offsetX, double offsetY, double offsetZ) {
      Optimizer optimizer = instance;
      if (optimizer == null || !optimizer.isEnabled() || !optimizer.entityCull.isEnabled()) {
         return false;
      }
      if (entity instanceof PlayerEntity && !optimizer.cullPlayers.isEnabled()) {
         return false;
      }
      double limit = optimizer.entityCullDistance.getValue();
      if (offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ <= limit * limit) {
         return false;
      }
      culledEntities++;
      return true;
   }

   /** Called from {@code BlockEntityRenderManager.render} for every block entity of the frame. */
   public static boolean shouldCullBlockEntity(BlockPos pos, Vec3d cameraPos) {
      Optimizer optimizer = instance;
      if (optimizer == null || !optimizer.isEnabled() || !optimizer.blockEntityCull.isEnabled()) {
         return false;
      }
      if (pos == null || cameraPos == null) {
         return false;
      }
      double limit = optimizer.blockEntityCullDistance.getValue();
      if (pos.getSquaredDistance(cameraPos) <= limit * limit) {
         return false;
      }
      culledBlockEntities++;
      return true;
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
         "tick %.2f/%.2f ms - render3D %.2f/%.2f ms - render2D %.2f/%.2f ms (avg/peak) - culled %d entities / %d block entities",
         tickAverageMs,
         tickPeakMs,
         render3DAverageMs,
         render3DPeakMs,
         render2DAverageMs,
         render2DPeakMs,
         culledEntities,
         culledBlockEntities
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
      culledEntities = 0;
      culledBlockEntities = 0;
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
