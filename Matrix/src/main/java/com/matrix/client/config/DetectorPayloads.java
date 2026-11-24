package com.matrix.client.config;

import com.matrix.client.categories.misc.UpdateChecker;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public final class DetectorPayloads {
   private static final String RESERVED_USER = "NTH_OFF";

   public record DetectPayload(String username, String version) implements CustomPayload {
      public static final CustomPayload.Id<DetectPayload> ID = new CustomPayload.Id<>(Identifier.of("matrixclient", "detect"));
      public static final PacketCodec<RegistryByteBuf, DetectPayload> CODEC = PacketCodec.of(
         (value, buf) -> {
            buf.writeString(value.username);
            buf.writeString(value.version);
         },
         buf -> new DetectPayload(buf.readString(256), buf.readString(64))
      );

      @Override
      public CustomPayload.Id<? extends CustomPayload> getId() {
         return ID;
      }
   }

   private DetectorPayloads() {
   }

   public static void register() {
      try {
         PayloadTypeRegistry.playC2S().register(DetectPayload.ID, DetectPayload.CODEC);
      } catch (Throwable e) {
         e.printStackTrace();
      }
   }

   public static void sendDetection() {
      try {
         MinecraftClient mc = MinecraftClient.getInstance();
         String username = mc.getSession().getUsername();
         if (username == null || username.isBlank() || username.equalsIgnoreCase(RESERVED_USER)) {
            return;
         }
         ClientPlayNetworking.send(new DetectPayload(username, UpdateChecker.getCurrentVersion()));
      } catch (Throwable e) {
         e.printStackTrace();
      }
   }
}
