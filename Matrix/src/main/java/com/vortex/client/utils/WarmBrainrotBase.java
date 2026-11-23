package com.vortex.client.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import com.vortex.client.utils.VortexclientWarm;
import com.vortex.client.utils.LatheLoft;
import com.vortex.client.utils.PoseProgress;
import com.vortex.client.utils.ForeShinScale;

public abstract class WarmBrainrotBase {
   private volatile VortexclientWarm classVortexclientWarm;
   private final AtomicBoolean atomicBoolean = new AtomicBoolean();
   private volatile VortexclientWarm classVortexclient;
   private volatile VortexclientWarm classThrowableThread;
   private volatile Throwable throwable;
   private static final ExecutorService threadExecutor = Executors.newSingleThreadExecutor(runnable -> {
      Thread thread = new Thread(runnable, "vortex-brainrot-warm");
      thread.setDaemon(true);
      thread.setPriority(1);
      return thread;
   });

   public abstract String formatTungDinString();

   public float getAmount(VortexclientWarm vortexclientWarm) {
      return vortexclientWarm.vortexclientWarmAmount + 0.42F * (vortexclientWarm.boundsMaxY - vortexclientWarm.vortexclientWarmAmount);
   }

   public String formatNoteBlock() {
      return null;
   }

   public final VortexclientWarm getClassThrowableThread() {
      return this.classThrowableThread;
   }

   public final Throwable getThrowable() {
      return this.throwable;
   }

   public abstract String formatTungDin();

   public final synchronized VortexclientWarm getClassClassVortexclientWarm() {
      if (this.classVortexclientWarm == null) {
         this.classVortexclientWarm = this.getClassForFloatIntArray(1.0F, null);
      }

      return this.classVortexclientWarm;
   }

   public int getValue() {
      return 1;
   }

   protected abstract void handleShinFoot(LatheLoft latheLoft);

   public final synchronized VortexclientWarm getVortexclient() {
      if (this.classVortexclient == null) {
         this.classVortexclient = this.getClassForFloatIntArray(0.5F, this.getClassClassVortexclientWarm().vortexclientValues);
      }

      return this.classVortexclient;
   }

   public final VortexclientWarm getClassVortexclient() {
      return this.classVortexclient;
   }

   public float getAmountFloat() {
      return 1.0F;
   }

   public abstract void handleShinProgress(PoseProgress poseProgress, ForeShinScale ifAbsent);

   protected int getValueInt() {
      return 512;
   }

   private VortexclientWarm getClassForFloatIntArray(float classValue, int[] classValues) {
      LatheLoft latheLoft = new LatheLoft(this.getValueInt(), this.getValueInt(), classValue, classValues != null);
      this.handleShinFoot(latheLoft);
      return latheLoft.getMax(this.formatTungDin(), classValues);
   }

   public final void updateThrowableThread() {
      if (this.classThrowableThread == null && this.throwable == null && this.atomicBoolean.compareAndSet(false, true)) {
         threadExecutor.execute(() -> {
            try {
               this.getVortexclientClassThrowableThread();
            } catch (Throwable e) {
               this.throwable = e;
            }
         });
      }
   }

   public final synchronized VortexclientWarm getVortexclientClassThrowableThread() {
      if (this.classThrowableThread == null) {
         this.getVortexclient();
         this.classThrowableThread = this.getClassForFloatIntArray(0.25F, this.getClassClassVortexclientWarm().vortexclientValues);
      }

      return this.classThrowableThread;
   }

   public final VortexclientWarm getClassVortexclientWarm() {
      return this.classVortexclientWarm;
   }
}
