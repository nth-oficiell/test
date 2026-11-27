package com.matrix.client.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class DonutActivityStore {
   private static final int MAX_ENTRIES_PER_UPDATE = 65536;
   private static volatile List<Activity> current = List.of();
   private static volatile long updated;

   private DonutActivityStore() {
   }

   public static List<Activity> all() {
      return System.currentTimeMillis() - updated > 5000L ? List.of() : current;
   }

   public static void clear() {
      current = List.of();
   }

   public static void handle(byte[] data) {
      ArrayList<Activity> list = new ArrayList<>();
      try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(data))) {
         int n = in.readInt();
         if (n < 0 || n > MAX_ENTRIES_PER_UPDATE) {
            return;
         }
         for (int i = 0; i < n; i++) {
            list.add(new Activity(in.readInt(), in.readInt(), in.readInt()));
         }
      } catch (IOException e) {
         System.err.println("[Matrix] malformed activity payload: " + e.getMessage());
         return;
      }
      current = List.copyOf(list);
      updated = System.currentTimeMillis();
   }

   public record Activity(int chunkX, int chunkZ, int players) {
   }
}
