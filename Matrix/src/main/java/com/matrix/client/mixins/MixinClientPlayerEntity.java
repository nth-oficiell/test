package com.matrix.client.mixins;

import com.matrix.client.world.MathClampUtils;
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
import com.matrix.client.categories.bots.AutoBuilder;
import com.matrix.client.categories.misc.AutoPromo;
import com.matrix.client.categories.movement.Flight;
import com.matrix.client.categories.movement.SpeedHack;
import ai.auth.ModuleRegistry;
import com.matrix.client.utils.ElytraAutoUtils;
import com.matrix.client.utils.ExtUtils;

@Mixin({ClientPlayerEntity.class})
public class MixinClientPlayerEntity {
   @Shadow
   protected int ticksLeftToDoubleTapSprint;
   @Unique
   private float matrixRealYaw;
   @Unique
   private float matrixRealPitch;
   @Unique
   private boolean matrixRotationSwapped;
   @Unique
   private final float matrixPrevSentYaw = Float.NaN;

   @Inject(
      method = {"method_6007()V"},
      at = {@At("HEAD")}
   )
   private void matrixGapJumpIsAWalkingHop(CallbackInfo callbackInfo) {
      if (AutoBuilder.matrixGapActive) {
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
            AutoBuilder.matrixGapActive = false;
         }
      }
   }

   @Inject(
      method = {"method_6007()V"},
      at = {@At("HEAD")}
   )
   private void matrixWalkerNeedsGravity(CallbackInfo callbackInfo) {
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
   private void matrixMovementHacks(CallbackInfo callbackInfo) {
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
   private float matrixCollidedSoftlyYaw(ClientPlayerEntity player) {
      return MathClampUtils.isClampSettled() ? MathClampUtils.getYaw() : player.getYaw();
   }

   @Inject(
      method = {"method_3136()V"},
      at = {@At("HEAD")}
   )
   private void matrixSilentAimHead(CallbackInfo callbackInfo) {
      this.matrixRotationSwapped = false;
      ClientPlayerEntity self = (ClientPlayerEntity)((net.minecraft.client.network.ClientPlayerEntity) (Object) (this));
      boolean silent = MathClampUtils.isClampActive();
      if (silent) {
         this.matrixRealYaw = self.getYaw();
         this.matrixRealPitch = self.getPitch();
         self.setYaw(MathClampUtils.getYaw());
         self.setPitch(MathClampUtils.getClamp());
         this.matrixRotationSwapped = true;
      }
   }

   @Inject(
      method = {"method_3136()V"},
      at = {@At("RETURN")}
   )
   private void matrixSilentAimReturn(CallbackInfo callbackInfo) {
      if (this.matrixRotationSwapped) {
         ClientPlayerEntity self = (ClientPlayerEntity)((net.minecraft.client.network.ClientPlayerEntity) (Object) (this));
         self.setYaw(this.matrixRealYaw);
         self.setPitch(this.matrixRealPitch);
         this.matrixRotationSwapped = false;
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
   private void matrixFireSilentInventory(CallbackInfo callbackInfo) {
      ClientPlayerEntity self = (ClientPlayerEntity)((net.minecraft.client.network.ClientPlayerEntity) (Object) (this));
      PlayerInput p = self.input.playerInput;
      boolean quiet = !p.forward() && !p.backward() && !p.left() && !p.right() && !p.jump() && !p.sneak() && !self.isSprinting();
      double speed = Math.hypot(self.getVelocity().x, self.getVelocity().z);
      ExtUtils.handleActionFlag(quiet, speed, self.isOnGround());
   }
}
