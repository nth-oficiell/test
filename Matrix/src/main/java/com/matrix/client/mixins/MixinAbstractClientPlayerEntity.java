package com.matrix.client.mixins;

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
import com.matrix.client.categories.misc.Capes;
import com.matrix.client.categories.misc.SkinProtect;
import com.matrix.client.categories.visuals.SkinChanger;
import com.matrix.client.utils.EmitVertex;
import com.matrix.client.utils.SubmitMatrixApi;

@Mixin({AbstractClientPlayerEntity.class})
public class MixinAbstractClientPlayerEntity implements SubmitMatrixApi {
   @Unique
   private EmitVertex matrix$capeFrame;

   @Override
   public EmitVertex matrix$capeFrame() {
      return this.matrix$capeFrame;
   }

   @Override
   public void matrix$capeFrame(EmitVertex matrixcapeFrame) {
      this.matrix$capeFrame = matrixcapeFrame;
   }

   @Inject(
      method = {"method_52814()Lnet/minecraft/class_8685;"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void matrixCosmeticsOverride(CallbackInfoReturnable<SkinTextures> callbackInfoReturnable) {
      AbstractClientPlayerEntity self = (AbstractClientPlayerEntity)((net.minecraft.client.network.AbstractClientPlayerEntity) (Object) (this));
      SkinTextures orig = (SkinTextures)callbackInfoReturnable.getReturnValue();
      if (orig != null) {
         SkinTextures skin = SkinProtect.getMatrixclientInstance(self);
         if (skin == null) {
            skin = SkinChanger.getOverride(self);
         }

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
