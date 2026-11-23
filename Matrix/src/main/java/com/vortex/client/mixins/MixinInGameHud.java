package com.vortex.client.mixins;

import com.vortex.client.gui.ScreenSoundsUtils;
import com.vortex.client.gui.TickProgress;
import com.vortex.client.render.ClampMatrixUtils;
import com.vortex.client.render.VortexRoundedUtils;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.vortex.client.categories.misc.CustomCrosshair;
import com.vortex.client.categories.movement.Freecam;
import com.vortex.client.utils.HotbarHud;
import com.vortex.client.categories.visuals.ShowHUD;
import ai.auth.Module;
import ai.auth.ModuleRegistry;

@Mixin({InGameHud.class})
public class MixinInGameHud {
   @Unique
   private boolean vortexTextLifted;

   @Inject(
      at = {@At("HEAD")},
      method = {"method_1753(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V"},
      cancellable = true
   )
   private void onRender(DrawContext context, RenderTickCounter renderTickCounter, CallbackInfo callbackInfo) {
      ShowHUD.updateTotemsHotbar();
      if (ScreenSoundsUtils.isScreenGuiApiOpen()) {
         ClampMatrixUtils.renderFlag(context, renderTickCounter.getTickProgress(false));
         callbackInfo.cancel();
      } else if (ScreenSoundsUtils.isNoScreenOpen()) {
         TickProgress event = new TickProgress(context, renderTickCounter.getTickProgress(false));
         VortexRoundedUtils.update();

         try {
            ModuleRegistry.dispatchRender2D(event);
         } finally {
            VortexRoundedUtils.updateBufferCloseCount();
         }
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"method_55802(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V"},
      cancellable = true
   )
   private void onRenderChat(DrawContext drawContext, RenderTickCounter renderTickCounter, CallbackInfo callbackInfo) {
      if (ScreenSoundsUtils.isGuiClickApiOpen()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      at = {@At("HEAD")},
      cancellable = true,
      method = {"method_55803(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V", "method_70837(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V", "method_55800(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V", "method_55801(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V", "method_55804(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V"}
   )
   private void onRenderServerHudLayer(DrawContext drawContext, RenderTickCounter renderTickCounter, CallbackInfo callbackInfo) {
      if (ScreenSoundsUtils.isGuiClickApiOpen()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"method_1736(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V"},
      cancellable = true
   )
   private void onRenderCrosshair(DrawContext drawContext, RenderTickCounter renderTickCounter, CallbackInfo callbackInfo) {
      if (MinecraftClient.getInstance().currentScreen != null || CustomCrosshair.isFlag()) {
         callbackInfo.cancel();
      }
   }

   @ModifyExpressionValue(
      method = {"method_1736(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_5498;method_31034()Z"
      )}
   )
   private boolean vortexFreecamCrosshair(boolean vortexFreecamFlag) {
      return vortexFreecamFlag || Freecam.isBaseInstance();
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"method_1765(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V"},
      cancellable = true
   )
   private void onRenderStatusEffectOverlay(DrawContext drawContext, RenderTickCounter renderTickCounter, CallbackInfo callbackInfo) {
      Module m = ModuleRegistry.getModule(ShowHUD.class);
      if (m != null && m.isEnabled()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"method_1735(Lnet/minecraft/class_332;Lnet/minecraft/class_1297;)V"},
      cancellable = true
   )
   private void onRenderVignetteOverlay(DrawContext drawContext, Entity entity, CallbackInfo callbackInfo) {
      Module m = ModuleRegistry.getModule(ShowHUD.class);
      if (m != null && m.isEnabled()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      at = {@At("HEAD")},
      cancellable = true,
      require = 0,
      method = {"method_1759(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V"}
   )
   private void vortexCustomHotbar(DrawContext context, RenderTickCounter renderTickCounter, CallbackInfo callbackInfo) {
      if (ShowHUD.isSpectatorHidden() && HotbarHud.canInstanceFlag(context, renderTickCounter)) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"method_1760(Lnet/minecraft/class_332;)V"},
      cancellable = true,
      require = 0
   )
   private void vortexHideStatusBars(DrawContext drawContext, CallbackInfo callbackInfo) {
      if (ShowHUD.isKeybindsLevel()) {
         callbackInfo.cancel();
      }
   }

   @ModifyExpressionValue(
      method = {"method_55805(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V", "method_70842()Lnet/minecraft/class_329$class_11220;"},
      require = 0,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_636;method_2913()Z"
      )}
   )
   private boolean vortexHideExperienceBar(boolean vortexHideFlag) {
      return vortexHideFlag && !ShowHUD.isKeybindsLevel();
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"method_1749(Lnet/minecraft/class_332;)V"},
      require = 0
   )
   private void vortexLiftHeldItemTooltip(DrawContext drawContext, CallbackInfo callbackInfo) {
      this.vortexLiftText(drawContext);
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"method_1749(Lnet/minecraft/class_332;)V"},
      require = 0
   )
   private void vortexDropHeldItemTooltip(DrawContext drawContext, CallbackInfo callbackInfo) {
      this.vortexDropText(drawContext);
   }

   @Inject(
      at = {@At("HEAD")},
      require = 0,
      method = {"method_55800(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V"}
   )
   private void vortexLiftOverlayMessage(DrawContext drawContext, RenderTickCounter renderTickCounter, CallbackInfo callbackInfo) {
      if (!ScreenSoundsUtils.isGuiClickApiOpen()) {
         this.vortexLiftText(drawContext);
      }
   }

   @Inject(
      at = {@At("RETURN")},
      require = 0,
      method = {"method_55800(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V"}
   )
   private void vortexDropOverlayMessage(DrawContext drawContext, RenderTickCounter renderTickCounter, CallbackInfo callbackInfo) {
      this.vortexDropText(drawContext);
   }

   @Unique
   private void vortexLiftText(DrawContext drawContext) {
      MinecraftClient mc = MinecraftClient.getInstance();
      float lift = ShowHUD.getMax(mc.interactionManager != null && mc.interactionManager.hasStatusBars());
      if (!(lift <= 0.0F)) {
         drawContext.getMatrices().pushMatrix();
         drawContext.getMatrices().translate(0.0F, -lift);
         this.vortexTextLifted = true;
      }
   }

   @Unique
   private void vortexDropText(DrawContext drawContext) {
      if (this.vortexTextLifted) {
         this.vortexTextLifted = false;
         drawContext.getMatrices().popMatrix();
      }
   }
}
