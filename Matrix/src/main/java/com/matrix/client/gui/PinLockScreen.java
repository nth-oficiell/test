package com.matrix.client.gui;

import ai.auth.ModuleRegistry;
import com.matrix.client.categories.settings.PinLock;
import com.matrix.client.config.MenuBackgrounds;
import com.matrix.client.render.MatrixStackUtilsAmount;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class PinLockScreen extends Screen {
   private static final int CARD_WIDTH = 200;
   private static final int CARD_HEIGHT = 110;
   private final StringBuilder entered = new StringBuilder();
   private long wrongUntilMillis;

   public PinLockScreen() {
      super(Text.literal("Matrix"));
   }

   @Override
   public boolean shouldCloseOnEsc() {
      return true;
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      MenuBackgrounds.draw(context, 1.0F);
      int cardX = this.width / 2 - CARD_WIDTH / 2;
      int cardY = this.height / 2 - CARD_HEIGHT / 2;
      long currentMillis = System.currentTimeMillis();
      int shake = currentMillis < this.wrongUntilMillis ? (currentMillis / 30L % 2L == 0L ? 2 : -2) : 0;
      MatrixPeekUtilsRender.renderMatrixPeek(context, (float)(cardX + shake), (float)cardY, 200.0F, 110.0F, 9.0F, (int)0xE60E0A1CL);
      MatrixStackUtilsAmount.handlePushScale(
         "Matrix", (float)(this.width / 2) - MatrixStackUtilsAmount.getAmount3("Matrix") * 1.3F, (float)(cardY + 18), 1.3F, -920587
      );
      String title = currentMillis < this.wrongUntilMillis ? "Wrong PIN" : "Enter your PIN";
      MatrixStackUtilsAmount.handlePushScale(
         title, (float)(this.width / 2) - MatrixStackUtilsAmount.getAmount3(title) * 0.8F, (float)(cardY + 44), 0.8F, -11117467
      );
      String codeText = "*".repeat(this.entered.length());
      MatrixStackUtilsAmount.handlePushScale(
         codeText, (float)(this.width / 2) - MatrixStackUtilsAmount.getAmount3(codeText) * 1.2F, (float)(cardY + 74), 1.2F, -1
      );
   }

   @Override
   public boolean keyPressed(KeyInput keyInput) {
      int key = keyInput.key();
      PinLock pinLock = (PinLock)ModuleRegistry.getModule(PinLock.class);
      String code = pinLock != null ? pinLock.getCode() : "";
      if (key == GLFW.GLFW_KEY_BACKSPACE) {
         if (!this.entered.isEmpty()) {
            this.entered.deleteCharAt(this.entered.length() - 1);
         }
      } else if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
         this.handleUnlock(code);
      } else {
         int digit = key >= GLFW.GLFW_KEY_0 && key <= GLFW.GLFW_KEY_9
            ? key - GLFW.GLFW_KEY_0
            : (key >= GLFW.GLFW_KEY_KP_0 && key <= GLFW.GLFW_KEY_KP_9 ? key - GLFW.GLFW_KEY_KP_0 : -1);
         if (digit >= 0 && code.isEmpty() || !code.isEmpty() && this.entered.length() < code.length() && digit >= 0) {
            this.entered.append((char)(48 + digit));
            if (this.entered.length() >= code.length()) {
               this.handleUnlock(code);
            }
         }
      }

      return true;
   }

   private void handleUnlock(String code) {
      if (code.isEmpty() || code.contentEquals(this.entered)) {
         this.client.setScreen(new MatrixMatricesScreen());
      } else {
         this.entered.setLength(0);
         this.wrongUntilMillis = System.currentTimeMillis() + 900L;
      }
   }
}
