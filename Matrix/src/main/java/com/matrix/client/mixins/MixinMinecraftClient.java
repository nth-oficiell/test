package com.matrix.client.mixins;

import com.matrix.client.entity.ClientThreadUtils;
import com.matrix.client.render.FramebufferSimpleUtils;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.matrix.client.categories.bots.AnchorHunter;
import com.matrix.client.categories.bots.AutoAreaMiner;
import com.matrix.client.categories.bots.AutoBuilder;
import com.matrix.client.categories.bots.AutoElytraFinder;
import com.matrix.client.utils.LightPacketCache;
import com.matrix.client.categories.bots.SpawnerProtect;
import com.matrix.client.utils.Cooldown;
import ai.auth.ModuleRegistry;
import com.matrix.client.utils.ScaffoldTrade;

@Mixin({MinecraftClient.class})
public class MixinMinecraftClient {
   @Shadow
   @Nullable
   public ClientWorld world;
   @Shadow
   private int itemUseCooldown;
   @Shadow
   protected int attackCooldown;

   @Inject(
      method = {"method_1583()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onItemUseHead(CallbackInfo callbackInfo) {
      if (ClientThreadUtils.isDequeArray()) {
         ClientThreadUtils.update();
         callbackInfo.cancel();
      } else {
         ScaffoldTrade event = new ScaffoldTrade();
         ModuleRegistry.dispatchPreItemUse(event);
         if (event.isFlag()) {
            ClientThreadUtils.update();
            callbackInfo.cancel();
         }
      }
   }

   @Inject(
      method = {"method_1583()V"},
      at = {@At("RETURN")}
   )
   private void onItemUseReturn(CallbackInfo callbackInfo) {
      Cooldown event = new Cooldown(this.itemUseCooldown);
      ModuleRegistry.dispatchPostItemUse(event);
      this.itemUseCooldown = event.useCooldown;
      ClientThreadUtils.update();
   }

   @Inject(
      method = {"method_1508()V"},
      at = {@At("RETURN")}
   )
   private void matrixAfterInputEvents(CallbackInfo callbackInfo) {
      ClientThreadUtils.updateOptionsInstance();
   }

   @Inject(
      method = {"method_1574()V"},
      at = {@At("HEAD")}
   )
   private void onTick(CallbackInfo callbackInfo) {
      LightPacketCache.INSTANCE.processPendingLight();
      if (this.world != null) {
         ModuleRegistry.onGameTick();
      }
   }

   @Inject(
      method = {"method_1574()V"},
      at = {@At("RETURN")}
   )
   private void matrixAfterTick(CallbackInfo callbackInfo) {
      ClientThreadUtils.updateFlag();
   }

   @Inject(
      method = {"method_1590(Z)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void matrixBotOwnsBreak(boolean matrixBotFlag, CallbackInfo callbackInfo) {
      AutoBuilder builder = (AutoBuilder)ModuleRegistry.getModule(AutoBuilder.class);
      if (builder != null && builder.didBlockUpdateThisTick()) {
         callbackInfo.cancel();
      } else {
         SpawnerProtect spawnerProtect = (SpawnerProtect)ModuleRegistry.getModule(SpawnerProtect.class);
         if (spawnerProtect != null && spawnerProtect.isBreakingSpawner()) {
            callbackInfo.cancel();
         } else {
            AutoElytraFinder elytra = (AutoElytraFinder)ModuleRegistry.getModule(AutoElytraFinder.class);
            if (elytra != null && elytra.isBreakingBlock()) {
               callbackInfo.cancel();
            } else {
               if (ClientThreadUtils.isFlag2()) {
                  callbackInfo.cancel();
               }
            }
         }
      }
   }

   @ModifyVariable(
      method = {"method_1590(Z)V"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private boolean matrixMinerV2HoldsAttack(boolean matrixMinerFlag) {
      if (matrixMinerFlag) {
         return true;
      } else {
         AutoAreaMiner miner = (AutoAreaMiner)ModuleRegistry.getModule(AutoAreaMiner.class);
         if (miner == null || !miner.isEngineActive()) {
            AnchorHunter hunter = (AnchorHunter)ModuleRegistry.getModule(AnchorHunter.class);
            if (hunter == null || !hunter.isMiningAnchor()) {
               return false;
            }
         }

         this.attackCooldown = 0;
         return true;
      }
   }

   @Inject(
      method = {"method_1536()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void matrixSilentAimAttack(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      Boolean result = ClientThreadUtils.getMaxDeque();
      if (result != null) {
         callbackInfoReturnable.setReturnValue(result);
      }
   }

   @Inject(
      method = {"method_1536()Z"},
      at = {@At("RETURN")}
   )
   private void matrixAfterAttack(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      ClientThreadUtils.updateMax();
   }

   @ModifyReturnValue(
      method = {"method_1522()Lnet/minecraft/class_276;"},
      at = {@At("RETURN")}
   )
   private Framebuffer matrix$gui3dFramebuffer(Framebuffer framebuffer) {
      Framebuffer override = FramebufferSimpleUtils.getFramebuffer();
      return override != null ? override : framebuffer;
   }

   @WrapOperation(
      method = {"method_1523(Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_757;method_3192(Lnet/minecraft/class_9779;Z)V"
      )}
   )
   private void matrix$gui3dGuard(GameRenderer gameRenderer, RenderTickCounter renderTickCounter, boolean matrixgui3dGuardFlag, Operation<Void> operation) {
      try {
         operation.call(new Object[]{gameRenderer, renderTickCounter, matrixgui3dGuardFlag});
      } finally {
         FramebufferSimpleUtils.updateCompositeBegin();
      }
   }

   @Inject(
      method = {"method_15993()V"},
      at = {@At("HEAD")}
   )
   private void matrix$gui3dResize(CallbackInfo callbackInfo) {
      FramebufferSimpleUtils.updateCompositeBegin();
   }
}
