package com.matrix.client.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class DonutBaseStore {
   private static final int MAX_ENTRIES_PER_UPDATE = 65536;
   private static final Map<Integer, Base> BASES = new ConcurrentHashMap<>();
   private static final List<Consumer<Base>> LISTENERS = new CopyOnWriteArrayList<>();
   private static final Map<Long, Spawner> SPAWNERS = new ConcurrentHashMap<>();
   private static final List<Consumer<Spawner>> SPAWNER_LISTENERS = new CopyOnWriteArrayList<>();

   private DonutBaseStore() {
   }

   public static Collection<Base> all() {
      return BASES.values();
   }

   public static int size() {
      return BASES.size();
   }

   public static void clear() {
      BASES.clear();
      SPAWNERS.clear();
   }

   public static void onNewBase(Consumer<Base> listener) {
      LISTENERS.add(listener);
   }

   public static Collection<Spawner> spawners() {
      return SPAWNERS.values();
   }

   public static void onNewSpawner(Consumer<Spawner> listener) {
      SPAWNER_LISTENERS.add(listener);
   }

   private static long key(int x, int y, int z) {
      return (long)(x & 0x3FFFFFF) << 38 | (long)(z & 0x3FFFFFF) << 12 | (long)(y & 0xFFF);
   }

   public static void handle(byte[] data) {
      ArrayList<Base> added = new ArrayList<>();
      try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(data))) {
         byte type = in.readByte();
         if (type == 0) {
            BASES.clear();
            SPAWNERS.clear();
            return;
         }
         if (type == 2) {
            return;
         }
         if (type == 3) {
            ArrayList<Spawner> newSpawners = new ArrayList<>();
            int n = in.readInt();
            if (n < 0 || n > MAX_ENTRIES_PER_UPDATE) {
               return;
            }
            for (int i = 0; i < n; i++) {
               Spawner sp = new Spawner(in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readUTF());
               if (SPAWNERS.put(key(sp.x(), sp.y(), sp.z()), sp) == null) {
                  newSpawners.add(sp);
               }
            }
            for (Spawner sp : newSpawners) {
               for (Consumer<Spawner> l : SPAWNER_LISTENERS) {
                  l.accept(sp);
               }
            }
            return;
         }
         int count = in.readInt();
         if (count < 0 || count > MAX_ENTRIES_PER_UPDATE) {
            return;
         }
         for (int i = 0; i < count; i++) {
            Base b = new Base(in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt());
            if (BASES.putIfAbsent(b.id(), b) == null) {
               added.add(b);
            }
         }
      } catch (IOException e) {
         System.err.println("[Matrix] malformed base payload: " + e.getMessage());
      }
      for (Base b : added) {
         for (Consumer<Base> l : LISTENERS) {
            l.accept(b);
         }
      }
   }

   public record Base(int id, int x, int y, int z, int sx, int sy, int sz) {
      public int chunkX() {
         return this.x + this.sx / 2 >> 4;
      }

      public int chunkZ() {
         return this.z + this.sz / 2 >> 4;
      }
   }

   public record Spawner(int x, int y, int z, int stack, String type) {
   }
}
