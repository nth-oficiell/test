package com.vortex.client.mixins;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({InventoryScreen.class})
public interface AccessorInventoryScreen {
   @Invoker("drawEntity")
   static EntityRenderState vortexBuildEntityState(LivingEntity livingEntity) {
      throw new AssertionError();
   }
}
