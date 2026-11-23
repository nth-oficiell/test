package com.vortex.client.mixins;

import net.minecraft.client.render.LightmapTextureManager;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.vortex.client.categories.visuals.CustomWorld;

@Mixin({LightmapTextureManager.class})
public class MixinLightmapCustomWorld {
   @Shadow
   private boolean dirty;

   @Inject(
      method = {"method_3314()V"},
      at = {@At("HEAD")},
      require = 0
   )
   private void vortex$keepDirty(CallbackInfo callbackInfo) {
      CustomWorld cw = CustomWorld.getCustomWorld();
      if (cw != null && cw.isCustomLightColor()) {
         this.dirty = true;
      }
   }

   @ModifyVariable(
      method = {"method_3313(F)V"},
      at = @At("STORE"),
      ordinal = 0,
      require = 0
   )
   private Vector3f vortex$tintLight(Vector3f vortextintLight) {
      CustomWorld cw = CustomWorld.getCustomWorld();
      if (cw != null && cw.isCustomLightColor()) {
         int c = cw.getColorBase();
         return new Vector3f((c >> 16 & 0xFF) / 255.0F, (c >> 8 & 0xFF) / 255.0F, (c & 0xFF) / 255.0F);
      } else {
         return vortextintLight;
      }
   }
}
