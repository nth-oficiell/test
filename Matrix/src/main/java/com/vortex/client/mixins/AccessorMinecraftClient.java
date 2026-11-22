package com.vortex.client.mixins;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({MinecraftClient.class})
public interface AccessorMinecraftClient {
   @Invoker("doAttack")
   boolean vortexDoAttack();

   @Accessor("attackCooldown")
   void vortexSetAttackCooldown(int vortexAttackValue);

   @Accessor("attackCooldown")
   int vortexGetAttackCooldown();
}
