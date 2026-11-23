package com.matrix.client.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.matrix.client.categories.visuals.NoFarmLag;

@Mixin({Entity.class})
public class MixinEntityFarmLag {
   @WrapWithCondition(
      method = {"method_5746()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_1937;method_8406(Lnet/minecraft/class_2394;DDDDDD)V"
      )}
   )
   private boolean matrix$noFarmLagSplash(World world, ParticleEffect particleEffect, double matrixnoFarmValue, double matrixnoFarmValue2, double matrixnoFarmValue3, double matrixnoFarmValue4, double matrixnoFarmValue5, double matrixnoFarmValue6) {
      if (!world.isClient()) {
         return true;
      } else {
         NoFarmLag module = NoFarmLag.getNoFarmLag();
         return module == null || !module.canBase((Entity)((net.minecraft.entity.Entity) (Object) (this)));
      }
   }
}
