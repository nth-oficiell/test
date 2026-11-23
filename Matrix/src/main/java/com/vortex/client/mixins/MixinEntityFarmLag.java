package com.vortex.client.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.vortex.client.categories.visuals.NoFarmLag;

@Mixin({Entity.class})
public class MixinEntityFarmLag {
   @WrapWithCondition(
      method = {"method_5746()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_1937;method_8406(Lnet/minecraft/class_2394;DDDDDD)V"
      )}
   )
   private boolean vortex$noFarmLagSplash(World world, ParticleEffect particleEffect, double vortexnoFarmValue, double vortexnoFarmValue2, double vortexnoFarmValue3, double vortexnoFarmValue4, double vortexnoFarmValue5, double vortexnoFarmValue6) {
      if (!world.isClient()) {
         return true;
      } else {
         NoFarmLag module = NoFarmLag.getNoFarmLag();
         return module == null || !module.canBase((Entity)((net.minecraft.entity.Entity) (Object) (this)));
      }
   }
}
