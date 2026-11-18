package dev.utilikit.mixin;

import dev.utilikit.client.modules.impl.NoHurtCamModule;
import dev.utilikit.client.modules.impl.ZoomModule;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void utilikit$applyZoom(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir) {
        if (ZoomModule.shouldZoom()) {
            cir.setReturnValue(ZoomModule.applyZoom(cir.getReturnValueF()));
        }
    }

    @Inject(method = "bobViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void utilikit$noHurtCam(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (NoHurtCamModule.shouldCancelHurtCam()) {
            ci.cancel();
        }
    }
}
