package com.matrix.client.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ModuleIdentities {
   private static final Properties PROPERTIES = loadIdentitiesProperties();

   /**
    * Names are immutable, but {@link Properties} is a synchronized {@link java.util.Hashtable} and
    * {@code getName()} is called all over the HUD and tick paths. A {@link ClassValue} resolves each
    * module class once and is then a plain array read with no lock and no map hashing.
    */
   private static final ClassValue<String> NAMES = new ClassValue<String>() {
      @Override
      protected String computeValue(Class<?> type) {
         return PROPERTIES.getProperty(type.getName(), type.getSimpleName());
      }
   };

   private static Properties loadIdentitiesProperties() {
      Properties properties = new Properties();

      try {
         try (InputStream resourceAsStream = ModuleIdentities.class.getResourceAsStream("/META-INF/matrix-module-ids.properties")) {
            if (resourceAsStream != null) {
               properties.load(resourceAsStream);
            }
         }

         return properties;
      } catch (IOException e) {
         throw new IllegalStateException("Could not read module identities", e);
      }
   }

   private ModuleIdentities() {
   }

   public static String getProperty(Class<?> class2) {
      return NAMES.get(class2);
   }
}
