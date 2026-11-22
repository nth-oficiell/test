package com.vortex.client.mixins;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo.TextureAsset;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.vortex.client.categories.misc.Capes;
import com.vortex.client.categories.misc.SkinProtect;
import com.vortex.client.utils.EmitVertex;
import com.vortex.client.utils.SubmitMatrixApi;

@Mixin({AbstractClientPlayerEntity.class})
public class MixinAbstractClientPlayerEntity implements SubmitMatrixApi {
   @Unique
   private EmitVertex vortex$capeFrame;

   @Override
   public EmitVertex vortex$capeFrame() {
      return this.vortex$capeFrame;
   }

   @Override
   public void vortex$capeFrame(EmitVertex vortexcapeFrame) {
      this.vortex$capeFrame = vortexcapeFrame;
   }

   @Inject(
      method = {"method_52814()Lnet/minecraft/class_8685;"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void vortexCosmeticsOverride(CallbackInfoReturnable<SkinTextures> callbackInfoReturnable) {
      AbstractClientPlayerEntity self = (AbstractClientPlayerEntity)((net.minecraft.client.network.AbstractClientPlayerEntity) (Object) (this));
      SkinTextures orig = (SkinTextures)callbackInfoReturnable.getReturnValue();
      if (orig != null) {
         SkinTextures skin = SkinProtect.getVortexclientInstance(self);
         Identifier cape = Capes.getIdentifierInstance(self);
         if (skin != null || cape != null) {
            TextureAsset body = skin != null ? skin.body() : orig.body();
            PlayerSkinType model = skin != null ? skin.model() : orig.model();
            TextureAsset capeAsset = cape != null ? Capes.getIfAbsent(cape) : orig.cape();
            callbackInfoReturnable.setReturnValue(SkinTextures.create(body, capeAsset, orig.elytra(), model));
         }
      }
   }
}
