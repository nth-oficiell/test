package com.vortex.client.mixins;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.vortex.client.categories.donutsmp.FakePay;
import com.vortex.client.categories.donutsmp.FakeStats;
import com.vortex.client.categories.visuals.SpawnerFinder;
import ai.auth.ModuleRegistry;

@Mixin({ClientPlayNetworkHandler.class})
public class MixinClientPlayNetworkHandler {
   @Inject(
      method = {"method_45730(Ljava/lang/String;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void vortexFakePay(String vortexFakeText, CallbackInfo callbackInfo) {
      FakePay fakePay = (FakePay)ModuleRegistry.getModule(FakePay.class);
      if (fakePay != null && fakePay.isEnabled() && fakePay.interceptPayCommand(vortexFakeText)) {
         callbackInfo.cancel();
      } else {
         FakeStats fakeStats = (FakeStats)ModuleRegistry.getModule(FakeStats.class);
         if (fakeStats != null && fakeStats.isEnabled() && fakeStats.interceptBalanceCommand(vortexFakeText)) {
            callbackInfo.cancel();
         }
      }
   }

   @Inject(
      method = {"method_11094(Lnet/minecraft/class_2622;)V"},
      at = {@At("TAIL")}
   )
   private void vortexSpawnerFinderBlockEntity(BlockEntityUpdateS2CPacket blockEntityUpdateS2CPacket, CallbackInfo callbackInfo) {
      SpawnerFinder.handleInstance(blockEntityUpdateS2CPacket.getPos());
   }
}
