package fr.buffy.aura;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.util.function.Predicate;

public class AuraScreen extends Screen {
	private static final int PADDING = 16;
	private static final int HEADER = 62;
	private static final int FOOTER = 50;
	private static final int ROW_GAP = 5;
	private static final int MIN_ROW_HEIGHT = 12;
	private static final int MAX_ROW_HEIGHT = 30;
	private static final int SWITCH_WIDTH = 36;
	private static final int PILL_WIDTH = 34;
	private static final int PILL_HEIGHT = 16;
	private static final int DOT_SPACING = 16;
	private static final int DOT_HIT = 14;

	@FunctionalInterface
	private interface OptionSetter {
		void apply(AuraConfig config, boolean value);
	}

	/** Une ligne du menu : son libelle, sa valeur et la facon de la modifier. */
	private enum Option {
		HUD("aura.option.hud", config -> config.hud, (config, value) -> config.hud = value),
		COORDS("aura.option.coords", config -> config.coords, (config, value) -> config.coords = value),
		FPS("aura.option.fps", config -> config.fps, (config, value) -> config.fps = value),
		TIME("aura.option.time", config -> config.time, (config, value) -> config.time = value),
		FULLBRIGHT("aura.option.fullbright", config -> config.fullbright, (config, value) -> {
			config.fullbright = value;
			AuraMod.applyFullbright(value);
		});

		private final String labelKey;
		private final Predicate<AuraConfig> getter;
		private final OptionSetter setter;

		Option(String labelKey, Predicate<AuraConfig> getter, OptionSetter setter) {
			this.labelKey = labelKey;
			this.getter = getter;
			this.setter = setter;
		}

		boolean value() {
			return getter.test(AuraConfig.get());
		}

		void toggle() {
			setter.apply(AuraConfig.get(), !value());
			AuraConfig.save();
		}
	}

	private static final Option[] OPTIONS = Option.values();
	private static final int ROW_COUNT = OPTIONS.length;

	private final Screen parent;
	private final float[] knob = new float[ROW_COUNT];
	private long openedAt;
	private long lastFrame;
	private int cardX;
	private int cardY;
	private int cardWidth;
	private int cardHeight;
	private int rowHeight;
	private int renderY;
	private int themeDotsX;
	private int themeDotsY;

	public AuraScreen(Screen parent) {
		super(Component.translatable("aura.menu.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		if (openedAt == 0L) {
			openedAt = Util.getMillis();
		}
		lastFrame = 0L;

		int available = height - 16;
		rowHeight = Mth.clamp((available - HEADER - FOOTER - (ROW_COUNT - 1) * ROW_GAP) / ROW_COUNT,
				MIN_ROW_HEIGHT, MAX_ROW_HEIGHT);
		cardWidth = Math.min(372, width - 24);
		cardHeight = HEADER + ROW_COUNT * rowHeight + (ROW_COUNT - 1) * ROW_GAP + FOOTER;
		cardX = (width - cardWidth) / 2;
		cardY = Math.max(8, (height - cardHeight) / 2);
		renderY = cardY;

		for (Option option : OPTIONS) {
			knob[option.ordinal()] = option.value() ? 1f : 0f;
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		long now = Util.getMillis();
		float frameTime = lastFrame == 0L ? 0.016f : Math.min(0.1f, (now - lastFrame) / 1000f);
		lastFrame = now;

		float intro = Mth.clamp((now - openedAt) / 240f, 0f, 1f);
		intro = 1f - (float) Math.pow(1f - intro, 3f);
		renderY = cardY + Math.round((1f - intro) * 12f);

		drawBackdrop(graphics, now);

		float smoothing = 1f - (float) Math.exp(-frameTime * 16f);
		for (Option option : OPTIONS) {
			int index = option.ordinal();
			knob[index] = Mth.lerp(smoothing, knob[index], option.value() ? 1f : 0f);
		}

		drawCard(graphics, mouseX, mouseY);
	}

	private void drawBackdrop(GuiGraphics graphics, long now) {
		AuraTheme theme = AuraConfig.get().theme();
		graphics.fillGradient(0, 0, width, height, 0xF4070A10, 0xFC04060A);

		float seconds = now / 1000f;
		Draw.glow(graphics, width * 0.2f + Mth.sin(seconds * 18f) * 26f,
				height * 0.24f + Mth.cos(seconds * 14f) * 18f, 120f, theme.primary);
		Draw.glow(graphics, width * 0.8f + Mth.cos(seconds * 15f) * 24f,
				height * 0.78f + Mth.sin(seconds * 19f) * 20f, 140f, theme.secondary);

		graphics.fillGradient(0, 0, width, 56, 0x66000000, 0x00000000);
		graphics.fillGradient(0, height - 64, width, height, 0x00000000, 0x77000000);
	}

	private void drawCard(GuiGraphics graphics, int mouseX, int mouseY) {
		AuraTheme theme = AuraConfig.get().theme();
		int x = cardX;
		int y = renderY;

		Draw.roundRect(graphics, x + 3, y + 6, cardWidth, cardHeight, 14, 0x33000000);
		Draw.roundRectGradient(graphics, x, y, cardWidth, cardHeight, 14, theme.accent(), theme.accentAlt());
		Draw.roundRect(graphics, x + 1, y + 1, cardWidth - 2, cardHeight - 2, 13, 0xF2131721);
		graphics.fillGradient(x + 1, y + 1, x + cardWidth - 1, y + HEADER,
				Draw.alpha(theme.primary, 0x1A), 0x00000000);

		Draw.glow(graphics, x + PADDING + 44, y + 24, 48f, theme.primary);

		graphics.pose().pushMatrix();
		graphics.pose().translate(x + PADDING, y + 12);
		graphics.pose().scale(2f, 2f);
		drawGradientText(graphics, "AURA", 0, 0, theme.accent(), theme.accentAlt());
		graphics.pose().popMatrix();

		graphics.drawString(font, Component.translatable("aura.menu.subtitle").getString(),
				x + PADDING, y + 36, 0xFF8B94A7, false);

		drawClosePill(graphics, mouseX, mouseY);
		drawRows(graphics, theme, mouseX, mouseY);
		drawFooter(graphics, theme);
	}

	private void drawClosePill(GuiGraphics graphics, int mouseX, int mouseY) {
		int pillX = pillX();
		int pillY = pillY();
		boolean hovered = isInside(mouseX, mouseY, pillX, pillY, PILL_WIDTH, PILL_HEIGHT);

		Draw.roundRect(graphics, pillX, pillY, PILL_WIDTH, PILL_HEIGHT, 8, hovered ? 0x2AFFFFFF : 0x14FFFFFF);
		graphics.drawCenteredString(font, "ESC", pillX + PILL_WIDTH / 2, pillY + (PILL_HEIGHT - font.lineHeight) / 2 + 1,
				hovered ? 0xFFE8EDF6 : 0xFF9AA5B8);
	}

	private void drawRows(GuiGraphics graphics, AuraTheme theme, int mouseX, int mouseY) {
		int rowX = cardX + PADDING - 4;
		int rowWidth = cardWidth - (PADDING - 4) * 2;

		for (Option option : OPTIONS) {
			int index = option.ordinal();
			int top = rowTop(index);
			boolean enabled = option.value();
			boolean hovered = isInside(mouseX, mouseY, rowX, top, rowWidth, rowHeight);

			Draw.roundRect(graphics, rowX, top, rowWidth, rowHeight, 8, hovered ? 0x16FFFFFF : 0x08FFFFFF);
			Draw.dot(graphics, cardX + PADDING + 3, top + rowHeight / 2f, 2.2f,
					enabled ? theme.accent() : 0xFF47505F);

			graphics.drawString(font, Component.translatable(option.labelKey).getString(),
					cardX + PADDING + 13, top + (rowHeight - font.lineHeight) / 2,
					enabled ? 0xFFF1F5FB : 0xFFA9B3C3, false);

			int switchHeight = Math.min(16, rowHeight - 6);
			int switchX = rowX + rowWidth - SWITCH_WIDTH - 8;
			int switchY = top + (rowHeight - switchHeight) / 2;
			float progress = knob[index];

			Draw.roundRect(graphics, switchX, switchY, SWITCH_WIDTH, switchHeight, switchHeight / 2,
					Draw.lerpColor(0xFF2B3241, theme.accent(), progress));

			int knobSize = switchHeight - 4;
			int knobX = switchX + 2 + Math.round(progress * (SWITCH_WIDTH - 4 - knobSize));
			Draw.roundRect(graphics, knobX, switchY + 2, knobSize, knobSize, knobSize / 2, 0xFFF7F9FD);
		}
	}

	private void drawFooter(GuiGraphics graphics, AuraTheme theme) {
		int x = cardX;
		int footerY = renderY + cardHeight - 34;

		graphics.fillGradient(x + PADDING, footerY, x + cardWidth - PADDING, footerY + 1,
				Draw.alpha(theme.primary, 0x55), Draw.alpha(theme.secondary, 0x55));

		String label = Component.translatable("aura.menu.theme").getString();
		int textY = footerY + 12;
		graphics.drawString(font, label, x + PADDING, textY, 0xFF8F99AB, false);

		themeDotsX = x + PADDING + font.width(label) + 14;
		themeDotsY = textY + font.lineHeight / 2;

		for (int i = 0; i < AuraTheme.count(); i++) {
			AuraTheme candidate = AuraTheme.byIndex(i);
			boolean selected = candidate == theme;
			Draw.dot(graphics, themeDotsX + i * DOT_SPACING, themeDotsY, selected ? 5f : 3.8f,
					selected ? candidate.accent() : Draw.alpha(candidate.primary, 0x88));
			if (selected) {
				Draw.dot(graphics, themeDotsX + i * DOT_SPACING, themeDotsY, 1.6f, 0xFF121620);
			}
		}

		String hint = Component.translatable("aura.menu.hint").getString();
		graphics.drawString(font, hint, x + cardWidth - PADDING - font.width(hint), textY, 0xFF697384, false);
	}

	private void drawGradientText(GuiGraphics graphics, String text, int x, int y, int from, int to) {
		int cursor = x;
		for (int i = 0; i < text.length(); i++) {
			String character = String.valueOf(text.charAt(i));
			float t = text.length() <= 1 ? 0f : i / (float) (text.length() - 1);
			graphics.drawString(font, character, cursor, y, Draw.lerpColor(from, to, t), false);
			cursor += font.width(character) + 1;
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (event.button() != 0) {
			return false;
		}

		int mouseX = (int) Math.floor(event.x());
		int mouseY = (int) Math.floor(event.y());

		if (isInside(mouseX, mouseY, pillX(), pillY(), PILL_WIDTH, PILL_HEIGHT)) {
			onClose();
			return true;
		}

		if (clickTheme(mouseX, mouseY)) {
			return true;
		}

		int rowX = cardX + PADDING - 4;
		int rowWidth = cardWidth - (PADDING - 4) * 2;
		for (Option option : OPTIONS) {
			if (isInside(mouseX, mouseY, rowX, rowTop(option.ordinal()), rowWidth, rowHeight)) {
				option.toggle();
				return true;
			}
		}

		return super.mouseClicked(event, doubleClick);
	}

	private boolean clickTheme(int mouseX, int mouseY) {
		for (int i = 0; i < AuraTheme.count(); i++) {
			int dotX = themeDotsX + i * DOT_SPACING;
			if (isInside(mouseX, mouseY, dotX - DOT_HIT / 2, themeDotsY - DOT_HIT / 2, DOT_HIT, DOT_HIT)) {
				AuraConfig config = AuraConfig.get();
				if (config.theme != i) {
					config.theme = i;
					AuraConfig.save();
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
			onClose();
			return true;
		}

		if (AuraMod.menuKey() != null && AuraMod.menuKey().matches(event)) {
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

	private int rowTop(int index) {
		return renderY + HEADER + index * (rowHeight + ROW_GAP);
	}

	private int pillX() {
		return cardX + cardWidth - PADDING - PILL_WIDTH;
	}

	private int pillY() {
		return renderY + 13;
	}

	private static boolean isInside(int mouseX, int mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}
}
