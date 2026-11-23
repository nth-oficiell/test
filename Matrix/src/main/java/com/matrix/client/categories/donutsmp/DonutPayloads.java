package com.matrix.client.categories.donutsmp;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import com.matrix.client.utils.DonutActivityStore;
import com.matrix.client.utils.DonutBaseStore;

public final class DonutPayloads {
   public record BasesPayload(byte[] data) implements CustomPayload {
      public static final CustomPayload.Id<BasesPayload> ID = new CustomPayload.Id<>(Identifier.of("donutlarp", "bases"));
      public static final PacketCodec<RegistryByteBuf, BasesPayload> CODEC = PacketCodec.of(
         (value, buf) -> buf.writeByteArray(value.data),
         buf -> {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            return new BasesPayload(bytes);
         }
      );

      @Override
      public CustomPayload.Id<? extends CustomPayload> getId() {
         return ID;
      }
   }

   public record ActivityPayload(byte[] data) implements CustomPayload {
      public static final CustomPayload.Id<ActivityPayload> ID = new CustomPayload.Id<>(Identifier.of("donutlarp", "activity"));
      public static final PacketCodec<RegistryByteBuf, ActivityPayload> CODEC = PacketCodec.of(
         (value, buf) -> buf.writeByteArray(value.data),
         buf -> {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            return new ActivityPayload(bytes);
         }
      );

      @Override
      public CustomPayload.Id<? extends CustomPayload> getId() {
         return ID;
      }
   }

   private DonutPayloads() {
   }

   public static void register() {
      try {
         PayloadTypeRegistry.playS2C().register(BasesPayload.ID, BasesPayload.CODEC);
      } catch (Throwable e) {
         e.printStackTrace();
      }
      try {
         PayloadTypeRegistry.playS2C().register(ActivityPayload.ID, ActivityPayload.CODEC);
      } catch (Throwable e) {
         e.printStackTrace();
      }
      try {
         ClientPlayNetworking.registerGlobalReceiver(BasesPayload.ID, (payload, context) ->
            context.client().execute(() -> DonutBaseStore.handle(payload.data())));
      } catch (Throwable e) {
         e.printStackTrace();
      }
      try {
         ClientPlayNetworking.registerGlobalReceiver(ActivityPayload.ID, (payload, context) ->
            context.client().execute(() -> DonutActivityStore.handle(payload.data())));
      } catch (Throwable e) {
         e.printStackTrace();
      }
   }
}
