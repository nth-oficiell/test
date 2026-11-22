package com.vortex.client.mixins;

import com.vortex.client.world.MathClampUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.text.Text;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.vortex.client.categories.bots.AutoBuilder;
import com.vortex.client.categories.misc.AutoPromo;
import com.vortex.client.categories.movement.Flight;
import com.vortex.client.categories.movement.SpeedHack;
import ai.auth.ModuleRegistry;
import com.vortex.client.utils.ElytraAutoUtils;
import com.vortex.client.utils.ExtUtils;

@Mixin({ClientPlayerEntity.class})
public class MixinClientPlayerEntity {
   @Shadow
   protected int ticksLeftToDoubleTapSprint;
   @Unique
   private float vortexRealYaw;
   @Unique
   private float vortexRealPitch;
   @Unique
   private boolean vortexRotationSwapped;
   @Unique
   private final float vortexPrevSentYaw = Float.NaN;

   @Inject(
      method = {"method_6007()V"},
      at = {@At("HEAD")}
   )
   private void vortexGapJumpIsAWalkingHop(CallbackInfo callbackInfo) {
      if (AutoBuilder.vortexGapActive) {
         AutoBuilder builder = (AutoBuilder)ModuleRegistry.getModule(AutoBuilder.class);
         if (builder != null && builder.isEnabled()) {
            ClientPlayerEntity self = (ClientPlayerEntity)((net.minecraft.client.network.ClientPlayerEntity) (Object) (this));
            this.ticksLeftToDoubleTapSprint = 0;
            GameOptions options = MinecraftClient.getInstance().options;
            if (options != null) {
               ElytraAutoUtils.handleRawPressed(options.sprintKey, false);
            }

            if (self.isSprinting()) {
               self.setSprinting(false);
            }
         } else {
            AutoBuilder.vortexGapActive = false;
         }
      }
   }

   @Inject(
      method = {"method_6007()V"},
      at = {@At("HEAD")}
   )
   private void vortexWalkerNeedsGravity(CallbackInfo callbackInfo) {
      ClientPlayerEntity self = (ClientPlayerEntity)((net.minecraft.client.network.ClientPlayerEntity) (Object) (this));
      PlayerAbilities ab = self.getAbilities();
      AutoBuilder builder = (AutoBuilder)ModuleRegistry.getModule(AutoBuilder.class);
      if (builder != null && builder.isEnabled()) {
         if (ab.creativeMode || ab.allowFlying || ab.flying) {
            if (ab.allowFlying) {
               AutoBuilder.switchedDisableFlag = true;
            }

            ab.allowFlying = false;
            if (ab.flying) {
               ab.flying = false;
               self.sendAbilitiesUpdate();
            }
         }
      }
   }

   @Inject(
      method = {"method_6007()V"},
      at = {@At("TAIL")}
   )
   private void vortexMovementHacks(CallbackInfo callbackInfo) {
      ClientPlayerEntity self = (ClientPlayerEntity)((net.minecraft.client.network.ClientPlayerEntity) (Object) (this));
      Flight flight = (Flight)ModuleRegistry.getModule(Flight.class);
      if (flight == null || !flight.isEnabled() || !flight.canPressedInput(self)) {
         SpeedHack speed = (SpeedHack)ModuleRegistry.getModule(SpeedHack.class);
         if (speed != null && speed.isEnabled()) {
            speed.handleVelocityInput(self);
         }
      }
   }

   @Inject(
      method = {"method_7353(Lnet/minecraft/class_2561;Z)V"},
      at = {@At("HEAD")}
   )
   private void onMessage(Text text, boolean messageFlag, CallbackInfo callbackInfo) {
      if (text != null && !messageFlag) {
         ClientPlayerEntity self = (ClientPlayerEntity)((net.minecraft.client.network.ClientPlayerEntity) (Object) (this));
         AutoPromo autoPromo = (AutoPromo)ModuleRegistry.getModule(AutoPromo.class);
         if (autoPromo != null && autoPromo.isEnabled()) {
            String raw = text.getString();
            String myName = self.getName().getString();
            String lower = raw.toLowerCase();
            String lowerMe = myName.toLowerCase();
            String victim = null;
            if (lower.contains(" by " + lowerMe)) {
               int wasIdx = lower.indexOf(" was ");
               if (wasIdx > 0) {
                  victim = raw.substring(0, wasIdx).trim();
               }
            } else if (lower.startsWith(lowerMe + " killed ")) {
               victim = raw.substring((myName + " killed ").length()).trim();
            }

            if (victim != null && !victim.equalsIgnoreCase(myName)) {
               autoPromo.handleBase(victim);
            }
         }
      }
   }

   @Redirect(
      method = {"method_39759(Lnet/minecraft/class_243;)Z"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_746;method_36454()F"
      )
   )
   private float vortexCollidedSoftlyYaw(ClientPlayerEntity player) {
      return MathClampUtils.isClampSettled() ? MathClampUtils.getYaw() : player.getYaw();
   }

   @Inject(
      method = {"method_3136()V"},
      at = {@At("HEAD")}
   )
   private void vortexSilentAimHead(CallbackInfo callbackInfo) {
      this.vortexRotationSwapped = false;
      ClientPlayerEntity self = (ClientPlayerEntity)((net.minecraft.client.network.ClientPlayerEntity) (Object) (this));
      boolean silent = MathClampUtils.isClampActive();
      if (silent) {
         this.vortexRealYaw = self.getYaw();
         this.vortexRealPitch = self.getPitch();
         self.setYaw(MathClampUtils.getYaw());
         self.setPitch(MathClampUtils.getClamp());
         this.vortexRotationSwapped = true;
      }
   }

   @Inject(
      method = {"method_3136()V"},
      at = {@At("RETURN")}
   )
   private void vortexSilentAimReturn(CallbackInfo callbackInfo) {
      if (this.vortexRotationSwapped) {
         ClientPlayerEntity self = (ClientPlayerEntity)((net.minecraft.client.network.ClientPlayerEntity) (Object) (this));
         self.setYaw(this.vortexRealYaw);
         self.setPitch(this.vortexRealPitch);
         this.vortexRotationSwapped = false;
      }
   }

   @Inject(
      method = {"method_3136()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_746;method_46742()V",
         shift = Shift.AFTER
      )}
   )
   private void vortexFireSilentInventory(CallbackInfo callbackInfo) {
      ClientPlayerEntity self = (ClientPlayerEntity)((net.minecraft.client.network.ClientPlayerEntity) (Object) (this));
      PlayerInput p = self.input.playerInput;
      boolean quiet = !p.forward() && !p.backward() && !p.left() && !p.right() && !p.jump() && !p.sneak() && !self.isSprinting();
      double speed = Math.hypot(self.getVelocity().x, self.getVelocity().z);
      ExtUtils.handleActionFlag(quiet, speed, self.isOnGround());
   }
}
