package dev.utilikit.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import dev.utilikit.client.modules.impl.NoHurtCamModule;
import dev.utilikit.client.modules.impl.ZoomModule;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.GameRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @ModifyReturnValue(method = "getFov", at = @At("RETURN"))
    private float utilikit$applyZoom(float fov) {
        return ZoomModule.applyZoom(fov);
    }

    @Inject(method = "bobHurt", at = @At("HEAD"), cancellable = true)
    private void utilikit$noHurtCam(PoseStack poseStack, float partialTick, CallbackInfo ci) {
        if (NoHurtCamModule.shouldCancelHurtCam()) {
            ci.cancel();
        }
    }
}
