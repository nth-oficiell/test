package com.matrix.client.mixins;

import com.matrix.client.config.ThemeMusicPlayer;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SoundSystem.class})
public class MixinSoundSystemThemeMusic {
   @Inject(
      method = {"method_19762()V"},
      at = {@At("TAIL")}
   )
   private void matrix$keepThemeMusicParked(CallbackInfo callbackInfo) {
      ThemeMusicPlayer.enforcePausedState();
   }

   @Inject(
      method = {"method_43222(FLnet/minecraft/class_3419;)F"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void matrix$duckMusicUnderTheme(float matrixduckMusicValue, SoundCategory soundCategory, CallbackInfoReturnable<Float> callbackInfoReturnable) {
      if (soundCategory == SoundCategory.MUSIC) {
         float duck = ThemeMusicPlayer.getMusicDuckFactor();
         if (duck < 1.0F) {
            callbackInfoReturnable.setReturnValue(callbackInfoReturnable.getReturnValueF() * duck);
         }
      }
   }
}
