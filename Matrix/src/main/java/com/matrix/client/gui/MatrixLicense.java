package com.matrix.client.gui;

import com.matrix.client.MatrixClient;
import com.matrix.client.render.CardMatrixUtils;
import com.matrix.client.render.MatrixRoundedUtils;
import com.matrix.client.render.IcoGenUtils;
import com.matrix.client.render.MatrixClearUtils;
import com.matrix.client.render.MatrixEspUtils;
import com.matrix.client.render.MatrixPeekUtilsIdentifier;
import com.matrix.client.render.MatrixStackUtilsAmount;
import com.matrix.client.render.MatrixStackUtilsAmount.Anims;
import com.matrix.client.render.MatrixStackUtilsAmount.Hits;
import com.matrix.client.render.VertexTextureFont;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ai.auth.LicenseRuntime;
import ai.auth.DeviceIdentity;

public class MatrixLicense extends Screen implements ScreenGuiApi, GuiClickApi {
   private String licenseContinueText;
   private final StringBuilder stringBuilder;
   private float introFadeAlpha;
   private float layoutScale;
   private volatile boolean secureActivationFlag;
   private boolean matrixLicenseActive;
   private float translateAmount;
   private boolean licenseMatrixFlag;
   private long lastNanoTime;
   private final boolean activationRequired;
   private long lastNanoTimeSecureActivation;
   private final Anims field;
   private static final String CORZ_CLIENT = "Matrix Client";
   private static final SoundEvent soundEvent = SoundEvent.of(Identifier.of("matrixclient", "access_granted"));
   private final ClearEdit xxxxxFocusClearEdit;
   private final long matrixLicenseValue;
   private boolean xxxxxMatrixFlag;
   private final Screen screen;
   private final MatrixStack matrixStack = new MatrixStack();
   private String secureActivationText;
   private long timeMillis;
   private boolean focus;
   private int secureActivationValue;
   private final boolean pressedActive;
   private final boolean resolutionRestoreEnabled;
   private final Hits xxxxxFocusHits;
   private volatile boolean activate;
   private static final Identifier identifier = Identifier.of("matrixclient", "textures/matrix_mark.png");
   private int secureRenderValue;
   private String secureText;

   private void handleScreenResolution(boolean screenResolutionFlag) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (screenResolutionFlag) {
         instance.setScreen(new MatrixMatricesScreen());
      } else {
         instance.setScreen(this.screen);
      }

      if (!(instance.currentScreen instanceof ScreenGuiApi)) {
         instance.onResolutionChanged();
      }
   }

   @Override
   protected void init() {
      if (!this.licenseMatrixFlag) {
         this.licenseMatrixFlag = true;
         MinecraftClient.getInstance().onResolutionChanged();
      } else {
         super.init();
      }
   }

   private void renderSecureActivation(DrawContext context, float secureActivationValue, float matrixStackValue, long repeatValue) {
      String secureActivationText;
      int secureActivationValue4;
      if (this.activate) {
         secureActivationText = "Contacting license server";
         secureActivationValue4 = -7630437;
      } else if (this.licenseContinueText != null) {
         secureActivationText = this.licenseContinueText;
         secureActivationValue4 = -7630437;
      } else if (this.secureActivationFlag) {
         secureActivationText = LicenseRuntime.getLicenseOwnerLabel();
         secureActivationValue4 = -12922502;
      } else {
         secureActivationText = "Secure Ed25519 activation.";
         secureActivationValue4 = -7630437;
      }

      long nanoTime = System.nanoTime();
      if (this.secureActivationText == null) {
         this.secureActivationText = secureActivationText;
         this.secureActivationValue = secureActivationValue4;
      } else if (!secureActivationText.equals(this.secureActivationText) || secureActivationValue4 != this.secureActivationValue) {
         this.secureText = this.secureActivationText;
         this.secureRenderValue = this.secureActivationValue;
         this.secureActivationText = secureActivationText;
         this.secureActivationValue = secureActivationValue4;
         this.lastNanoTimeSecureActivation = nanoTime;
      }

      float secureActivationLimit = this.lastNanoTimeSecureActivation == 0L ? 1.0F : MatrixStackUtilsAmount.getAmountForFloat((float)(nanoTime - this.lastNanoTimeSecureActivation) / 1.8E8F);
      if (secureActivationLimit >= 1.0F) {
         this.secureText = null;
      }

      float secureActivationValue5 = MatrixStackUtilsAmount.getAmount(matrixStackValue, 10.0F, 0.82F);
      if (this.secureText != null) {
         this.render(context, this.secureText, this.secureRenderValue, secureActivationValue, secureActivationValue5 - 3.0F * secureActivationLimit, 1.0F - secureActivationLimit, "");
      }

      this.render(context, this.secureActivationText, this.secureActivationValue, secureActivationValue, secureActivationValue5 + 3.0F * (1.0F - secureActivationLimit), secureActivationLimit, this.activate ? getRepeat(repeatValue) : "");
   }

   private static String getRepeat(long repeatValue) {
      int repeatValue2 = (int)(repeatValue / 350L % 4L);
      return ".".repeat(repeatValue2);
   }

   private void renderXxxxxFocus(DrawContext context, int xxxxxFocusValue, int brownValue, int releaseRightBound, int matrixStackLimit, int scaledY) {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      boolean xxxxxFocusFlag = !this.activate && MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, xxxxxFocusValue, brownValue, releaseRightBound, 24.0F);
      float releaseRight = MatrixStackUtilsAmount.getReleaseRight(
         context,
         xxxxxFocusValue,
         brownValue,
         releaseRightBound,
         24.0F,
         IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("key"),
         this.field.getAmount("focus", this.focus ? 1.0F : 0.0F, 16.0F),
         this.field.getAmount("field", xxxxxFocusFlag),
         this.field.getAmount("failed", 0.0F, 12.0F)
      );
      int abcdefghijklmnopqrstuvwxyzBrown = fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown(brownValue, 24);
      String formatText = this.formatText(this.stringBuilder.toString(), xxxxxFocusValue + releaseRightBound - 9.0F - releaseRight);
      if (this.stringBuilder.length() == 0) {
         MatrixStackUtilsAmount.handle("CORZ-XXXXX-XXXXX-XXXXX", releaseRight, abcdefghijklmnopqrstuvwxyzBrown, MatrixStackUtilsAmount.getValue(-7630437, 0.55F));
      } else {
         if (this.xxxxxMatrixFlag) {
            float materialFactor = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() + 2;
            MatrixPeekUtilsRender.renderMatrixPeek(
               context,
               releaseRight - 1.5F,
               brownValue + (24.0F - materialFactor) / 2.0F,
               MatrixStackUtilsAmount.getAmount3(formatText) + 3.0F,
               materialFactor,
               2.0F,
               CardMatrixUtils.getReleaseRight(0.32)
            );
         }

         MatrixStackUtilsAmount.handle(formatText, releaseRight, abcdefghijklmnopqrstuvwxyzBrown, -920587);
      }

      if (this.focus && !this.activate && !this.xxxxxMatrixFlag) {
         int n4 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() - 2;
         this.xxxxxFocusClearEdit
            .renderAmountTime(
               context,
               releaseRight + (this.stringBuilder.length() == 0 ? 0.0F : MatrixStackUtilsAmount.getAmount3(formatText) + 1.0F),
               brownValue + (24 - n4) / 2,
               n4,
               MatrixEspUtils.releaseRightValue,
               MatrixClearUtils.getAmount()
            );
      }

      this.xxxxxFocusHits.handle(xxxxxFocusValue, brownValue, releaseRightBound, 24.0F, this::updateXxxxxFocus);
   }

   private void renderTime(DrawContext context, int matrixLicenseValue, int matrixStackValue, int matrixLicenseDivisor, long matrixLicenseValue4) {
      float matrixLicenseValue5 = (float)(matrixLicenseValue4 - this.timeMillis);
      float nowOffset = matrixLicenseValue + 150.0F;
      float y = matrixStackValue + matrixLicenseDivisor / 2.0F - 24.0F;
      float matrixLicenseLimit = 22.0F * MatrixStackUtilsAmount.getAmount((matrixLicenseValue5 - 60.0F) / 340.0F, 1.7F);
      if (matrixLicenseLimit > 0.25F) {
         MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(
            context, nowOffset - matrixLicenseLimit, y - matrixLicenseLimit, matrixLicenseLimit * 2.0F, matrixLicenseLimit * 2.0F, matrixLicenseLimit, MatrixStackUtilsAmount.getValue(-12922502, 0.34F), 16.0F, 0.9F
         );
         MatrixPeekUtilsRender.renderMatrixPeek(context, nowOffset - matrixLicenseLimit, y - matrixLicenseLimit, matrixLicenseLimit * 2.0F, matrixLicenseLimit * 2.0F, matrixLicenseLimit, -12922502);
         float amountForFloat = MatrixStackUtilsAmount.getAmountForFloat((matrixLicenseValue5 - 220.0F) / 200.0F);
         if (amountForFloat > 0.002F) {
            float peekPositionFactor = 24.0F;
            boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(
               context, nowOffset, y, 0.6F + 0.4F * MatrixStackUtilsAmount.getAmount((matrixLicenseValue5 - 220.0F) / 300.0F, 2.0F)
            );
            MatrixPeekUtilsIdentifier.handle(
               nowOffset - peekPositionFactor / 2.0F,
               y - peekPositionFactor / 2.0F,
               peekPositionFactor,
               peekPositionFactor,
               IcoGenUtils.getIcoCheck(),
               new Color(MatrixStackUtilsAmount.getValue(-1, amountForFloat), true),
               MatrixPeekUtilsRender.getMatrixStack()
            );
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
         }
      }

      float ctxOffset = y + 22.0F + 14.0F;
      this.handlePopTranslate(
         getAmount((matrixLicenseValue5 - 360.0F) / 300.0F),
         () -> MatrixStackUtilsAmount.getAmountForDrawContextString(
            context, "Access granted", MatrixStackUtilsAmount.getAmount(nowOffset - MatrixStackUtilsAmount.getAmount("Access granted", 1.22F) / 2.0F), ctxOffset, 14.0F
         )
      );
      this.handlePopTranslate(getAmount((matrixLicenseValue5 - 560.0F) / 300.0F), () -> this.handle("Welcome to Matrix Client.", nowOffset, ctxOffset + 14.0F + 3.0F, -7630437));
   }

   @Override
   public boolean shouldPause() {
      return false;
   }

   private void updateLicenseContinue() {
      if (!this.activate && this.timeMillis <= 0L) {
         String trim = this.stringBuilder.toString().trim();
         if (trim.isEmpty()) {
            this.licenseContinueText = "Enter your license key to continue.";
         } else {
            this.licenseContinueText = null;
            this.activate = true;
            Thread thread = new Thread(() -> {
               try {
                  LicenseRuntime.activateLicense(trim);
               } finally {
                  this.activate = false;
                  this.secureActivationFlag = true;
               }
            }, "matrix-license-ui");
            thread.setDaemon(true);
            thread.start();
         }
      }
   }

   @Override
   public boolean keyPressed(KeyInput keyInput) {
      if (this.timeMillis <= 0L && this.lastNanoTime == 0L) {
         int pressedValue = keyInput.key();
         boolean pressedFlag = (keyInput.modifiers() & 10) != 0;
         if (pressedValue == 256) {
            this.updateTimeFlag();
            return true;
         } else if (this.pressedActive) {
            return true;
         } else if (pressedValue == 257 || pressedValue == 335) {
            this.updateLicenseContinue();
            return true;
         } else if (!this.focus) {
            return true;
         } else if (pressedValue == 65 && pressedFlag) {
            this.xxxxxMatrixFlag = this.stringBuilder.length() > 0;
            return true;
         } else if (pressedValue != 259 && pressedValue != 261) {
            if (pressedValue == 86 && pressedFlag) {
               try {
                  String clipboard = MinecraftClient.getInstance().keyboard.getClipboard();
                  if (clipboard != null) {
                     if (this.xxxxxMatrixFlag) {
                        this.updateBuilder();
                        this.xxxxxMatrixFlag = false;
                     }

                     this.handleBuilder(clipboard.trim());
                     this.updateSecureActivation();
                  }
               } catch (Exception e) {
               }

               return true;
            } else {
               return true;
            }
         } else {
            if (!this.xxxxxMatrixFlag && !pressedFlag) {
               if (this.stringBuilder.length() > 0) {
                  this.stringBuilder.deleteCharAt(this.stringBuilder.length() - 1);
                  this.updateSecureActivation();
               }
            } else {
               if (this.stringBuilder.length() > 0) {
                  this.updateBuilder();
                  this.updateSecureActivation();
               }

               this.xxxxxMatrixFlag = false;
            }

            return true;
         }
      } else {
         return true;
      }
   }

   private String formatText(String formatText, float matrixLicenseLimit) {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      if (!formatText.isEmpty() && !(fontRenderer.getAbcdefghijklmnopqrstuvwxyzMatrixfont(formatText) <= matrixLicenseLimit)) {
         int start = 0;

         while (start < formatText.length() && fontRenderer.getAbcdefghijklmnopqrstuvwxyzMatrixfont(formatText.substring(start)) > matrixLicenseLimit) {
            start++;
         }

         return formatText.substring(start);
      } else {
         return formatText;
      }
   }

   private void render(DrawContext context, String matrixLicenseText, int matrixLicenseValue, float matrixLicenseValue2, float priceValue, float matrixLicenseLimit, String matrixLicenseText2) {
      if (!(matrixLicenseLimit <= 0.002F)) {
         if (matrixLicenseValue == -7630437) {
            float scaleValue = MatrixStackUtilsAmount.getAmount(matrixLicenseValue2 - MatrixStackUtilsAmount.getAmount(matrixLicenseText, 0.82F) / 2.0F);
            MatrixStackUtilsAmount.handlePushScale(matrixLicenseText + matrixLicenseText2, scaleValue, priceValue, 0.82F, MatrixStackUtilsAmount.getValue(matrixLicenseValue, matrixLicenseLimit));
         } else {
            float matrixLicenseValue5 = MatrixStackUtilsAmount.getAmount(matrixLicenseValue2 - MatrixStackUtilsAmount.getAmountForString(matrixLicenseText) / 2.0F);
            MatrixStackUtilsAmount.getAmount(
               context,
               MatrixStackUtilsAmount.getValue(matrixLicenseValue, matrixLicenseLimit),
               MatrixStackUtilsAmount.getValue(CardMatrixUtils.getValue(matrixLicenseValue, -1, 0.22), matrixLicenseLimit),
               matrixLicenseText,
               matrixLicenseValue5,
               priceValue,
               matrixLicenseLimit >= 1.0F
            );
         }
      }
   }

   private void render(DrawContext context, int matrixLicenseValue, int matrixLicenseValue2, boolean matrixLicenseFlag, int matrixStackLimit, int scaledY, long repeatValue) {
      int x = matrixLicenseValue + 150;
      int xxxxxFocusValue = matrixLicenseValue + 18;
      short releaseRightBound = 264;
      this.handlePopTranslate(
         0,
         () -> {
            float y = matrixLicenseValue2 + 18 + 14.0F;
            MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(
               context, x - 1.0F, y - 1.0F, 2.0F, 2.0F, 1.0F, CardMatrixUtils.getReleaseRight(0.2 + 0.06 * getAmount(3.6)), 30.0F, 1.0F
            );
            boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(
               context, x, y, 0.82F + 0.18F * MatrixStackUtilsAmount.getAmount((this.translateAmount - 70.0F) / 380.0F, 1.8F)
            );
            MatrixPeekUtilsIdentifier.handle(
               x - 14.0F, matrixLicenseValue2 + 18, 28.0F, 28.0F, identifier, new Color(-920587, true), MatrixPeekUtilsRender.getMatrixStack()
            );
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
         }
      );
      this.handlePopTranslate(
         1,
         () -> {
            MatrixStackUtilsAmount.getAmountForDrawContextString(
               context, "Matrix Client", MatrixStackUtilsAmount.getAmount(x - MatrixStackUtilsAmount.getAmount("Matrix Client", 1.22F) / 2.0F), matrixLicenseValue2 + 54, 14.0F
            );
            this.handle(matrixLicenseFlag ? "Paste your key to unlock the client." : "License", x, matrixLicenseValue2 + 69, -7630437);
         }
      );
      int f2;
      if (matrixLicenseFlag) {
         int brownValue = matrixLicenseValue2 + 91;
         int matrixLicenseValue12 = brownValue + 24 + 6;
         this.handlePopTranslate(2, () -> this.renderXxxxxFocus(context, xxxxxFocusValue, brownValue, releaseRightBound, matrixStackLimit, scaledY));
         this.handlePopTranslate(
            3,
            () -> MatrixStackUtilsAmount.getAmount(
               context,
               this.xxxxxFocusHits,
               this.field,
               "activate",
               this.activate ? "Activating..." : "Activate",
               null,
               xxxxxFocusValue + releaseRightBound,
               matrixLicenseValue12,
               24.0F,
               releaseRightBound,
               matrixStackLimit,
               scaledY,
               !this.activate,
               this::updateLicenseContinue
            )
         );
         this.handlePopTranslate(4, () -> this.renderSecureActivation(context, x, matrixLicenseValue12 + 24 + 8, repeatValue));
         f2 = matrixLicenseValue2 + 175;
      } else {
         String matrixLicenseText = LicenseRuntime.getLicenseOwnerLabel();
         this.handlePopTranslate(2, () -> this.render(context, matrixLicenseText, true, (float)x, (float)(matrixLicenseValue2 + 91)));
         this.handlePopTranslate(3, () -> this.handle("Verified securely on this device.", x, matrixLicenseValue2 + 91 + 22 + 8, -7630437));
         f2 = matrixLicenseValue2 + 143;
      }

      int matrixStackValue = f2 + 9;
      this.handlePopTranslate(5, () -> {
         MatrixPeekUtilsRender.renderMatrixPeek(context, xxxxxFocusValue, f2, releaseRightBound, MatrixPeekUtilsRender.getAmount(), CardMatrixUtils.getValueInt());
         String matrixLicenseText2 = "Matrix";
         String matrixLicenseText3;
         if (matrixLicenseText2 == null) {
            matrixLicenseText3 = "...";
         } else if (matrixLicenseText2.length() > 10) {
            matrixLicenseText3 = matrixLicenseText2;
            boolean matrixLicenseFlag3 = false;
            byte matrixLicenseValue14 = 10;
         } else {
            matrixLicenseText3 = matrixLicenseText2;
         }

         String scaleText = matrixLicenseText3;
         this.handle("Device id", scaleText, xxxxxFocusValue, matrixStackValue, releaseRightBound);
         this.handle("Version", LicenseRuntime.getClientVersion(), xxxxxFocusValue, matrixStackValue + 14, releaseRightBound);
      });
      this.handlePopTranslate(
         6,
         () -> MatrixStackUtilsAmount.getAmount(
            context, this.xxxxxFocusHits, this.field, "close", "Close", xxxxxFocusValue + releaseRightBound, matrixStackValue + 28 + 10, 18.0F, matrixStackLimit, scaledY, false, this::updateTimeFlag
         )
      );
   }

   public MatrixLicense(Screen screen, boolean flag, boolean flag2) {
      super(Text.literal("Matrix License"));
      this.licenseMatrixFlag = false;
      this.stringBuilder = new StringBuilder();
      this.focus = true;
      this.xxxxxMatrixFlag = false;
      this.activate = false;
      this.matrixLicenseValue = System.nanoTime();
      this.timeMillis = -1L;
      this.secureActivationFlag = false;
      this.licenseContinueText = null;
      this.xxxxxFocusHits = new Hits();
      this.field = new Anims();
      this.xxxxxFocusClearEdit = new ClearEdit();
      this.translateAmount = 0.0F;
      this.introFadeAlpha = 1.0F;
      this.layoutScale = 1.0F;
      this.lastNanoTime = 0L;
      this.matrixLicenseActive = false;
      this.secureActivationText = null;
      this.secureText = null;
      this.secureActivationValue = 0;
      this.secureRenderValue = 0;
      this.lastNanoTimeSecureActivation = 0L;
      this.screen = screen;
      this.resolutionRestoreEnabled = flag;
      this.activationRequired = flag2;
      this.pressedActive = !flag2;
      DeviceIdentity.ensureDeviceId();
   }

   private void updateSecureActivation() {
      this.secureActivationFlag = false;
      this.licenseContinueText = null;
      this.xxxxxFocusClearEdit.updateTime();
   }

   private void updateBuilder() {
      for (int i = 0; i < this.stringBuilder.length(); i++) {
         this.stringBuilder.setCharAt(i, '\u0000');
      }

      this.stringBuilder.setLength(0);
   }

   @Override
   public boolean mouseClicked(Click click, boolean mouseClickedFlag) {
      if (this.timeMillis <= 0L && this.lastNanoTime == 0L) {
         if (this.xxxxxFocusHits.canAnimsPress(this.getMin(click.x(), this.width), this.getMin(click.y(), this.height))) {
            return true;
         } else if (this.pressedActive) {
            return true;
         } else {
            this.focus = false;
            this.xxxxxMatrixFlag = false;
            return true;
         }
      } else {
         return true;
      }
   }

   @Override
   public void render(DrawContext context, int matrixLicenseValue, int matrixLicenseValue2, float matrixLicenseValue3) {
      MatrixClearUtils.updateTimeAmount();
      MatrixEspUtils.updateFlagTime();
      MatrixClient.fontManager.setMatrixStack(this.matrixStack);
      MatrixPeekUtilsRender.handleMatrixStack(this.matrixStack);
      MatrixRoundedUtils.panelOverlayAmount = 1.0F;
      long timeMillis = System.currentTimeMillis();
      long nanoTime = System.nanoTime();
      if (this.lastNanoTime != 0L && (float)(nanoTime - this.lastNanoTime) / 1000000.0F >= 120.0F) {
         this.handleScreenResolution(this.matrixLicenseActive);
      } else {
         if (this.lastNanoTime == 0L && this.timeMillis < 0L && !this.pressedActive && (!this.activationRequired || this.secureActivationFlag)) {
            this.timeMillis = timeMillis;
            this.activate = false;
            this.updateBuilder();
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.ui(soundEvent, 1.0F, 1.0F));
         }

         if (this.timeMillis > 0L && timeMillis - this.timeMillis > 1500L) {
            this.close();
         } else {
            boolean matrixLicenseFlag = this.lastNanoTime != 0L;
            this.translateAmount = (float)(nanoTime - this.matrixLicenseValue) / 1000000.0F;
            float matrixStackValue = matrixLicenseFlag ? (float)(nanoTime - this.lastNanoTime) / 1.2E8F : this.translateAmount / 320.0F;
            float matrixLicenseFactor = this.timeMillis > 0L ? 1.0F - getAmount((float)(timeMillis - this.timeMillis - 1200L) / 300.0F) : 1.0F;
            boolean pressedActive2 = !this.pressedActive;
            int matrixLicenseDivisor = (pressedActive2 ? 175 : 143) + 79;
            this.layoutScale = Math.min(1.0F, Math.min((this.width - 16) / 300.0F, (float)(this.height - 16) / matrixLicenseDivisor));
            int peekValue = (this.width - 300) / 2;
            int matrixLicenseValue6 = (this.height - matrixLicenseDivisor) / 2;
            boolean matrixLicenseFlag3 = !matrixLicenseFlag && this.timeMillis <= 0L;
            int matrixStackLimit = matrixLicenseFlag3 ? this.getMin(matrixLicenseValue, this.width) : -4000;
            int scaledY = this.getMin(matrixLicenseValue2, this.height);
            float cardMatrixFactor = this.timeMillis > 0L ? MatrixStackUtilsAmount.getAmountForFloat((float)(timeMillis - this.timeMillis) / 260.0F) : 0.0F;
            this.introFadeAlpha = this.timeMillis > 0L ? 1.0F - MatrixStackUtilsAmount.getMax((float)(timeMillis - this.timeMillis) / 140.0F) : 1.0F;
            MatrixStackUtilsAmount.render(context, this.width, this.height, 0.78F * MatrixStackUtilsAmount.getAmountFloat(matrixStackValue, matrixLicenseFlag) * matrixLicenseFactor);
            this.xxxxxFocusHits.update();
            boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(
               context, this.width / 2.0F, this.height / 2.0F, MatrixStackUtilsAmount.getAmount(matrixStackValue, matrixLicenseFlag) * (0.97F + 0.03F * matrixLicenseFactor) * this.layoutScale
            );
            float panelOverlay = MatrixRoundedUtils.getPanelOverlay(MatrixStackUtilsAmount.getAmountForFloatFlag(matrixStackValue, matrixLicenseFlag) * matrixLicenseFactor);

            try {
               MatrixStackUtilsAmount.render(
                  context,
                  (float)peekValue,
                  (float)matrixLicenseValue6,
                  300.0F,
                  (float)matrixLicenseDivisor,
                  CardMatrixUtils.getValue(MatrixStackUtilsAmount.getValue(), -16050415, cardMatrixFactor),
                  CardMatrixUtils.getValue(352321535, MatrixStackUtilsAmount.getValue(-12922502, 0.42F), cardMatrixFactor)
               );
               TwoDidUtils.renderMinPeek(context, 2, peekValue, matrixLicenseValue6, 300.0F, 97.0F, 10.0F, 0.85F * (1.0F - cardMatrixFactor));
               if (this.introFadeAlpha > 0.002F) {
                  this.render(context, peekValue, matrixLicenseValue6, pressedActive2, matrixStackLimit, scaledY, timeMillis);
               }

               if (this.timeMillis > 0L) {
                  this.renderTime(context, peekValue, matrixLicenseValue6, matrixLicenseDivisor, timeMillis);
               }
            } finally {
               MatrixRoundedUtils.handlePanelOverlay(panelOverlay);
               MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
            }

            super.render(context, matrixLicenseValue, matrixLicenseValue2, matrixLicenseValue3);
         }
      }
   }

   private void updateXxxxxFocus() {
      this.focus = true;
      this.xxxxxMatrixFlag = false;
      this.xxxxxFocusClearEdit.updateTime();
   }

   public MatrixLicense(Screen screen) {
      this(screen, false);
   }

   private void handlePopTranslate(float translateValue, Runnable runnable) {
      if (!(translateValue <= 0.002F)) {
         MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
         matrixStack.push();
         matrixStack.translate(0.0F, (1.0F - translateValue) * 4.0F, 0.0F);
         float panelOverlay = MatrixRoundedUtils.getPanelOverlay(translateValue);

         try {
            runnable.run();
         } finally {
            MatrixRoundedUtils.handlePanelOverlay(panelOverlay);
            matrixStack.pop();
         }
      }
   }

   private void handlePopTranslate(int translateValue, Runnable runnable) {
      float translateFactor = MatrixStackUtilsAmount.getAmountForFloat((this.translateAmount - 70.0F - translateValue * 38.0F) / 230.0F);
      float panelOverlayLimit = translateFactor * this.introFadeAlpha;
      if (!(panelOverlayLimit <= 0.002F)) {
         MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
         matrixStack.push();
         matrixStack.translate(0.0F, (1.0F - translateFactor) * 4.0F, 0.0F);
         float panelOverlay = MatrixRoundedUtils.getPanelOverlay(panelOverlayLimit);

         try {
            runnable.run();
         } finally {
            MatrixRoundedUtils.handlePanelOverlay(panelOverlay);
            matrixStack.pop();
         }
      }
   }

   @Override
   public boolean charTyped(CharInput charInput) {
      if (this.timeMillis > 0L || this.pressedActive || this.lastNanoTime != 0L) {
         return true;
      } else if (!this.focus) {
         return false;
      } else {
         char codepoint = (char)charInput.codepoint();
         if (codepoint >= ' ' && codepoint != 127) {
            if (this.xxxxxMatrixFlag) {
               this.updateBuilder();
               this.xxxxxMatrixFlag = false;
            }

            if (this.stringBuilder.length() < 128) {
               this.stringBuilder.append(codepoint);
               this.updateSecureActivation();
               return true;
            }
         }

         return false;
      }
   }

   private static float getAmount(float matrixLicenseFactor) {
      matrixLicenseFactor = MatrixStackUtilsAmount.getMax(matrixLicenseFactor);
      return matrixLicenseFactor * matrixLicenseFactor * (3.0F - 2.0F * matrixLicenseFactor);
   }

   private void render(DrawContext context, String matrixLicenseText, boolean matrixLicenseFlag, float matrixLicenseValue, float matrixLicenseValue2) {
      int matrixStackValue = matrixLicenseFlag ? -12922502 : -1750963;
      float materialFactor = 6.0F;
      float matrixLicenseValue5 = 6.0F;
      float matrixLicenseValue6 = 10.0F;
      float matrixLicenseValue7 = 12.0F;
      float matrixLicenseValue8 = matrixLicenseValue6 + materialFactor + matrixLicenseValue5 + (float)Math.ceil(MatrixStackUtilsAmount.getAmount3(matrixLicenseText)) + matrixLicenseValue7;
      float matrixPeekValue = MatrixStackUtilsAmount.getAmount(matrixLicenseValue - matrixLicenseValue8 / 2.0F);
      float matrixLicenseValue10 = getAmount(matrixLicenseFlag ? 2.4 : 3.4);
      MatrixPeekUtilsRender.render(
         context, matrixPeekValue, matrixLicenseValue2, matrixLicenseValue8, 22.0F, 11.0F, MatrixStackUtilsAmount.getValue(matrixStackValue, 0.13F), MatrixStackUtilsAmount.getValue(matrixStackValue, 0.42F)
      );
      float matrixLicenseValue11 = matrixPeekValue + matrixLicenseValue6;
      float configsLikeValue = matrixLicenseValue2 + (22.0F - materialFactor) / 2.0F + 0.5F;
      MatrixPeekUtilsRender.renderMatrixPeek(
         context, matrixLicenseValue11, configsLikeValue, materialFactor, materialFactor, materialFactor / 2.0F, MatrixStackUtilsAmount.getValue(matrixStackValue, 0.3F + 0.34F * matrixLicenseValue10), 3.0F + 2.5F * matrixLicenseValue10, 0.8F
      );
      MatrixPeekUtilsRender.renderMatrixPeek(context, matrixLicenseValue11, configsLikeValue, materialFactor, materialFactor, materialFactor / 2.0F, matrixStackValue);
      MatrixStackUtilsAmount.handle(
         matrixLicenseText,
         matrixLicenseValue11 + materialFactor + matrixLicenseValue5,
         MatrixStackUtilsAmount.getFontRenderer().getAbcdefghijklmnopqrstuvwxyzBrown(Math.round(matrixLicenseValue2), 22),
         CardMatrixUtils.getValue(matrixStackValue, -1, 0.3)
      );
   }

   private int getMin(double matrixLicenseValue, int matrixLicenseValue2) {
      return (int)Math.round(matrixLicenseValue2 / 2.0 + (matrixLicenseValue - matrixLicenseValue2 / 2.0) / this.layoutScale);
   }

   public MatrixLicense(Screen screen, boolean matrixLicenseFlag) {
      this(screen, matrixLicenseFlag, false);
   }

   private void updateTimeFlag() {
      if (this.lastNanoTime == 0L) {
         this.lastNanoTime = System.nanoTime();
         this.matrixLicenseActive = this.resolutionRestoreEnabled;
      }
   }

   private void handle(String matrixLicenseText, String scaleText, int matrixLicenseValue, int matrixStackValue, int releaseRightBound) {
      float priceValue = MatrixStackUtilsAmount.getAmount(matrixStackValue, 14.0F, 0.82F);
      MatrixStackUtilsAmount.handlePushScale(matrixLicenseText, matrixLicenseValue, priceValue, 0.82F, -7630437);
      MatrixStackUtilsAmount.handlePushScale(scaleText, MatrixStackUtilsAmount.getAmount(matrixLicenseValue + releaseRightBound - MatrixStackUtilsAmount.getAmount(scaleText, 0.82F)), priceValue, 0.82F, -920587);
   }

   private static float getAmount(double matrixLicenseDivisor) {
      return (float)((Math.sin(System.nanoTime() / 1.0E9 * Math.PI * 2.0 / matrixLicenseDivisor) + 1.0) / 2.0);
   }

   private void handleBuilder(String builderText) {
      for (int i = 0; i < builderText.length() && this.stringBuilder.length() < 128; i++) {
         char charAt = builderText.charAt(i);
         if (charAt >= ' ' && charAt != 127) {
            this.stringBuilder.append(charAt);
         }
      }
   }

   private void handle(String matrixLicenseText, float matrixLicenseValue, float matrixStackValue, int matrixLicenseValue3) {
      MatrixStackUtilsAmount.handlePushScale(
         matrixLicenseText,
         MatrixStackUtilsAmount.getAmount(matrixLicenseValue - MatrixStackUtilsAmount.getAmount(matrixLicenseText, 0.82F) / 2.0F),
         MatrixStackUtilsAmount.getAmount(matrixStackValue, 10.0F, 0.82F),
         0.82F,
         matrixLicenseValue3
      );
   }

   @Override
   public void renderBackground(DrawContext context, int backgroundValue, int backgroundValue2, float backgroundValue3) {
   }

   @Override
   public void close() {
      this.handleScreenResolution(this.resolutionRestoreEnabled);
   }
}
