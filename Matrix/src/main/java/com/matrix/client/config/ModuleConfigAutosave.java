package com.matrix.client.config;

import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import ai.auth.ModuleRegistry;
import com.matrix.client.utils.DirExtractionUtils;

public class ModuleConfigAutosave {
   private static long lastSaveTimeMs = 0L;
   private static boolean savePending = false;

   public static void saveModuleConfig() {
      if (!DirExtractionUtils.isFlag()) {
         try {
            ModulesJsonStorage.saveModules(getConfigDir(), ModuleRegistry.getModules());
            lastSaveTimeMs = System.currentTimeMillis();
         } catch (Exception e) {
            System.err.println("[C" + e.getMessage());
         }
      }
   }

   private static Path getConfigDir() {
      try {
         return FabricLoader.getInstance().getConfigDir().resolve("corzclient");
      } catch (Throwable e) {
         return Path.of(System.getProperty("user.dir"), "config", "matrixclient");
      }
   }

   /**
    * Marks the config dirty instead of writing it. Settings change in bursts (a slider drag calls
    * this on every step), and a full synchronous save per change stalls the client thread.
    */
   public static void updateFlag() {
      savePending = true;
   }

   public static void saveNow() {
      saveModuleConfig();
      savePending = false;
   }

   public static void loadModuleConfig() {
      ModulesJsonStorage.loadModulesForEnvironment(getConfigDir(), ModuleRegistry.getModules(), ModuleConfigAutosave::updateFlag);
   }

   public static void updateFlag2() {
      savePending = true;
   }

   public static void saveIfPendingAndDue() {
      if (savePending && System.currentTimeMillis() - lastSaveTimeMs > 500L) {
         saveNow();
      }
   }
}
