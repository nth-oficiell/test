package com.matrix.client.config;

import com.matrix.client.gui.MatrixPeekUtilsRender;
import com.matrix.client.render.MatrixPeekUtilsIdentifier;
import com.matrix.client.render.MatrixRoundedUtils;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public final class MenuBackgrounds {
   private static final Identifier TEXTURE = Identifier.of("matrixclient", "textures/gui/menu_background.png");

   private MenuBackgrounds() {
   }

   public static boolean canReplace() {
      return MinecraftClient.getInstance().world == null;
   }

   public static void draw(DrawContext context, float alpha) {
      MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
      float scaledWindowWidth = context.getScaledWindowWidth();
      float scaledWindowHeight = context.getScaledWindowHeight();
      float panelOverlay = MatrixRoundedUtils.getPanelOverlay();
      MatrixRoundedUtils.handlePanelOverlay(panelOverlay * MathHelper.clamp(alpha, 0.0F, 1.0F));
      MatrixPeekUtilsIdentifier.handle(0.0F, 0.0F, scaledWindowWidth, scaledWindowHeight, TEXTURE, Color.WHITE, matrixStack);
      MatrixRoundedUtils.handlePanelOverlay(panelOverlay);
   }
}
