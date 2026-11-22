package com.matrix.client.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import com.matrix.client.utils.MatrixclientWarm;
import com.matrix.client.utils.LatheLoft;
import com.matrix.client.utils.PoseProgress;
import com.matrix.client.utils.ForeShinScale;

public abstract class WarmBrainrotBase {
   private volatile MatrixclientWarm classMatrixclientWarm;
   private final AtomicBoolean atomicBoolean = new AtomicBoolean();
   private volatile MatrixclientWarm classMatrixclient;
   private volatile MatrixclientWarm classThrowableThread;
   private volatile Throwable throwable;
   private static final ExecutorService threadExecutor = Executors.newSingleThreadExecutor(runnable -> {
      Thread thread = new Thread(runnable, "matrix-brainrot-warm");
      thread.setDaemon(true);
      thread.setPriority(1);
      return thread;
   });

   public abstract String formatTungDinString();

   public float getAmount(MatrixclientWarm matrixclientWarm) {
      return matrixclientWarm.matrixclientWarmAmount + 0.42F * (matrixclientWarm.boundsMaxY - matrixclientWarm.matrixclientWarmAmount);
   }

   public String formatNoteBlock() {
      return null;
   }

   public final MatrixclientWarm getClassThrowableThread() {
      return this.classThrowableThread;
   }

   public final Throwable getThrowable() {
      return this.throwable;
   }

   public abstract String formatTungDin();

   public final synchronized MatrixclientWarm getClassClassMatrixclientWarm() {
      if (this.classMatrixclientWarm == null) {
         this.classMatrixclientWarm = this.getClassForFloatIntArray(1.0F, null);
      }

      return this.classMatrixclientWarm;
   }

   public int getValue() {
      return 1;
   }

   protected abstract void handleShinFoot(LatheLoft latheLoft);

   public final synchronized MatrixclientWarm getMatrixclient() {
      if (this.classMatrixclient == null) {
         this.classMatrixclient = this.getClassForFloatIntArray(0.5F, this.getClassClassMatrixclientWarm().matrixclientValues);
      }

      return this.classMatrixclient;
   }

   public final MatrixclientWarm getClassMatrixclient() {
      return this.classMatrixclient;
   }

   public float getAmountFloat() {
      return 1.0F;
   }

   public abstract void handleShinProgress(PoseProgress poseProgress, ForeShinScale ifAbsent);

   protected int getValueInt() {
      return 512;
   }

   private MatrixclientWarm getClassForFloatIntArray(float classValue, int[] classValues) {
      LatheLoft latheLoft = new LatheLoft(this.getValueInt(), this.getValueInt(), classValue, classValues != null);
      this.handleShinFoot(latheLoft);
      return latheLoft.getMax(this.formatTungDin(), classValues);
   }

   public final void updateThrowableThread() {
      if (this.classThrowableThread == null && this.throwable == null && this.atomicBoolean.compareAndSet(false, true)) {
         threadExecutor.execute(() -> {
            try {
               this.getMatrixclientClassThrowableThread();
            } catch (Throwable e) {
               this.throwable = e;
            }
         });
      }
   }

   public final synchronized MatrixclientWarm getMatrixclientClassThrowableThread() {
      if (this.classThrowableThread == null) {
         this.getMatrixclient();
         this.classThrowableThread = this.getClassForFloatIntArray(0.25F, this.getClassClassMatrixclientWarm().matrixclientValues);
      }

      return this.classThrowableThread;
   }

   public final MatrixclientWarm getClassMatrixclientWarm() {
      return this.classMatrixclientWarm;
   }
}
