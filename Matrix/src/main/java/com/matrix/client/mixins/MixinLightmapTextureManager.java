package com.matrix.client.mixins;

import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import com.matrix.client.categories.visuals.Fullbright;
import ai.auth.ModuleRegistry;

@Mixin({LightmapTextureManager.class})
public class MixinLightmapTextureManager {
   @ModifyArg(
      method = {"method_3313(F)V"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/buffers/Std140Builder;putFloat(F)Lcom/mojang/blaze3d/buffers/Std140Builder;",
         ordinal = 0
      )
   )
   private float matrix$forceAmbientLightFactor(float matrixforceAmbientValue) {
      Fullbright fullbright = (Fullbright)ModuleRegistry.getModule(Fullbright.class);
      return fullbright != null && fullbright.isEnabled() ? 1.0F : matrixforceAmbientValue;
   }
}
