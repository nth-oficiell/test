package fr.buffy.aura;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

/** Réglages du réticule : activation, taille et opacité, avec un aperçu agrandi. */
public class AuraCrosshairScreen extends Screen {
	private static final int PANEL_WIDTH = 360;
	private static final int PANEL_HEIGHT = 212;
	private static final int PADDING = 16;
	private static final int PREVIEW_HEIGHT = 76;
	private static final int ROW_HEIGHT = 26;
	private static final int ROW_GAP = 6;
	private static final int SWITCH_WIDTH = 36;
	private static final int SWITCH_HEIGHT = 16;
	private static final int STEP_SIZE = 16;
	private static final int VALUE_WIDTH = 46;
	private static final int OPACITY_STEP = 5;

	private final Screen parent;
	private int panelX;
	private int panelY;
	private int firstRowY;

	public AuraCrosshairScreen(Screen parent) {
		super(Component.translatable("aura.crosshair.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		panelX = (width - PANEL_WIDTH) / 2;
		panelY = (height - PANEL_HEIGHT) / 2;
		firstRowY = panelY + PADDING + PREVIEW_HEIGHT + 14;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		AuraConfig config = AuraConfig.get();
		AuraTheme theme = config.theme();

		graphics.fill(0, 0, width, height, 0xF20A0D14);

		Draw.roundRectGradient(graphics, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 14, theme.accent(), theme.accentAlt());
		Draw.roundRect(graphics, panelX + 1, panelY + 1, PANEL_WIDTH - 2, PANEL_HEIGHT - 2, 13, 0xF2131721);

		drawPreview(graphics, config);
		drawToggleRow(graphics, config, theme, mouseX, mouseY);
		drawStepperRow(graphics, theme, 1, "aura.crosshair.size",
				config.crosshairSize + " px", mouseX, mouseY);
		drawStepperRow(graphics, theme, 2, "aura.crosshair.opacity",
				config.crosshairOpacity + " %", mouseX, mouseY);

		graphics.drawCenteredString(font, Component.translatable("aura.menu.hint").getString(),
				width / 2, panelY + PANEL_HEIGHT + 12, 0xFF6F7A8C);
	}

	private void drawPreview(GuiGraphics graphics, AuraConfig config) {
		int x = panelX + PADDING;
		int y = panelY + PADDING;
		int previewWidth = PANEL_WIDTH - PADDING * 2;

		Draw.roundRect(graphics, x, y, previewWidth, PREVIEW_HEIGHT, 8, 0xFF0B0E15);
		graphics.fill(x + previewWidth / 2, y + 8, x + previewWidth / 2 + 1, y + PREVIEW_HEIGHT - 8, 0x14FFFFFF);
		graphics.fill(x + 8, y + PREVIEW_HEIGHT / 2, x + previewWidth - 8, y + PREVIEW_HEIGHT / 2 + 1, 0x14FFFFFF);

		if (config.crosshair) {
			AuraCrosshair.draw(graphics, x + previewWidth / 2, y + PREVIEW_HEIGHT / 2, config, 2f);
		}

		graphics.drawString(font, Component.translatable("aura.crosshair.preview").getString(),
				x + 8, y + PREVIEW_HEIGHT - 13, 0xFF5C6577, false);
	}

	private void drawToggleRow(GuiGraphics graphics, AuraConfig config, AuraTheme theme, int mouseX, int mouseY) {
		int top = rowY(0);
		boolean hovered = Draw.inside(mouseX, mouseY, rowLeft(), top, rowWidth(), ROW_HEIGHT);

		Draw.roundRect(graphics, rowLeft(), top, rowWidth(), ROW_HEIGHT, 6, hovered ? 0x14FFFFFF : 0x07FFFFFF);
		graphics.drawString(font, Component.translatable("aura.crosshair.enabled").getString(),
				rowLeft() + 10, top + (ROW_HEIGHT - font.lineHeight) / 2, 0xFFE6EAF2, false);
		Draw.toggleSwitch(graphics, switchX(), top + (ROW_HEIGHT - SWITCH_HEIGHT) / 2, SWITCH_WIDTH, SWITCH_HEIGHT,
				config.crosshair ? 1f : 0f, theme.accent());
	}

	private void drawStepperRow(GuiGraphics graphics, AuraTheme theme, int index, String labelKey, String value,
								int mouseX, int mouseY) {
		int top = rowY(index);
		int buttonY = top + (ROW_HEIGHT - STEP_SIZE) / 2;

		graphics.drawString(font, Component.translatable(labelKey).getString(),
				rowLeft() + 10, top + (ROW_HEIGHT - font.lineHeight) / 2, 0xFFA9B3C3, false);

		Draw.roundRect(graphics, stepMinusX(), buttonY, STEP_SIZE, STEP_SIZE, 5,
				Draw.inside(mouseX, mouseY, stepMinusX(), buttonY, STEP_SIZE, STEP_SIZE) ? 0x24FFFFFF : 0x12FFFFFF);
		graphics.drawCenteredString(font, "-", stepMinusX() + STEP_SIZE / 2, buttonY + 4, theme.accent());

		graphics.drawCenteredString(font, value, stepValueX() + VALUE_WIDTH / 2,
				top + (ROW_HEIGHT - font.lineHeight) / 2, 0xFFE6EAF2);

		Draw.roundRect(graphics, stepPlusX(), buttonY, STEP_SIZE, STEP_SIZE, 5,
				Draw.inside(mouseX, mouseY, stepPlusX(), buttonY, STEP_SIZE, STEP_SIZE) ? 0x24FFFFFF : 0x12FFFFFF);
		graphics.drawCenteredString(font, "+", stepPlusX() + STEP_SIZE / 2, buttonY + 4, theme.accent());
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (event.button() != 0) {
			return false;
		}

		int x = (int) Math.floor(event.x());
		int y = (int) Math.floor(event.y());

		if (Draw.inside(x, y, rowLeft(), rowY(0), rowWidth(), ROW_HEIGHT)) {
			AuraConfig config = AuraConfig.get();
			config.crosshair = !config.crosshair;
			AuraConfig.save();
			return true;
		}

		int sizeRowY = rowY(1) + (ROW_HEIGHT - STEP_SIZE) / 2;
		if (Draw.inside(x, y, stepMinusX(), sizeRowY, STEP_SIZE, STEP_SIZE)) {
			changeSize(-1);
			return true;
		}
		if (Draw.inside(x, y, stepPlusX(), sizeRowY, STEP_SIZE, STEP_SIZE)) {
			changeSize(1);
			return true;
		}

		int opacityRowY = rowY(2) + (ROW_HEIGHT - STEP_SIZE) / 2;
		if (Draw.inside(x, y, stepMinusX(), opacityRowY, STEP_SIZE, STEP_SIZE)) {
			changeOpacity(-OPACITY_STEP);
			return true;
		}
		if (Draw.inside(x, y, stepPlusX(), opacityRowY, STEP_SIZE, STEP_SIZE)) {
			changeOpacity(OPACITY_STEP);
			return true;
		}

		return super.mouseClicked(event, doubleClick);
	}

	private void changeSize(int delta) {
		AuraConfig config = AuraConfig.get();
		int size = Mth.clamp(config.crosshairSize + delta, AuraConfig.MIN_CROSSHAIR_SIZE, AuraConfig.MAX_CROSSHAIR_SIZE);
		if (size == config.crosshairSize) {
			return;
		}

		config.crosshairSize = size;
		AuraConfig.save();
	}

	private void changeOpacity(int delta) {
		AuraConfig config = AuraConfig.get();
		int opacity = Mth.clamp(config.crosshairOpacity + delta,
				AuraConfig.MIN_CROSSHAIR_OPACITY, AuraConfig.MAX_CROSSHAIR_OPACITY);
		if (opacity == config.crosshairOpacity) {
			return;
		}

		config.crosshairOpacity = opacity;
		AuraConfig.save();
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
			onClose();
			return true;
		}

		return super.keyPressed(event);
	}

	@Override
	public void onClose() {
		minecraft.setScreen(parent);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private int rowY(int index) {
		return firstRowY + index * (ROW_HEIGHT + ROW_GAP);
	}

	private int rowLeft() {
		return panelX + PADDING;
	}

	private int rowWidth() {
		return PANEL_WIDTH - PADDING * 2;
	}

	private int switchX() {
		return rowLeft() + rowWidth() - SWITCH_WIDTH;
	}

	private int stepPlusX() {
		return rowLeft() + rowWidth() - STEP_SIZE;
	}

	private int stepValueX() {
		return stepPlusX() - VALUE_WIDTH;
	}

	private int stepMinusX() {
		return stepValueX() - STEP_SIZE - 4;
	}
}
