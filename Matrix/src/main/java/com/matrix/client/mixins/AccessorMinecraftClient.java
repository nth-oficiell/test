package com.matrix.client.mixins;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({MinecraftClient.class})
public interface AccessorMinecraftClient {
   @Invoker("doAttack")
   boolean matrixDoAttack();

   @Accessor("attackCooldown")
   void matrixSetAttackCooldown(int matrixAttackValue);

   @Accessor("attackCooldown")
   int matrixGetAttackCooldown();
}
