package com.vortex.client.mixins;

import java.util.Map;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ItemCooldownManager.class})
public interface AccessorItemCooldownManager {
   @Accessor("entries")
   Map<Identifier, ?> vortexGetEntries();

   @Accessor("tick")
   int vortexGetTick();
}
