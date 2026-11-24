package fr.buffy.aura;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

/** Ecran de placement : on glisse le HUD, la molette regle l'echelle. */
public class AuraHudEditorScreen extends Screen {
	private static final float SCALE_STEP = 0.25f;
	private static final int ACCENT = 0xFF7B88A8;

	private final Screen parent;
	private boolean dragging;
	private int grabX;
	private int grabY;

	public AuraHudEditorScreen(Screen parent) {
		super(Component.translatable("aura.editor.title"));
		this.parent = parent;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		Minecraft client = Minecraft.getInstance();
		AuraConfig config = AuraConfig.get();
		AuraHud.Bounds bounds = AuraHud.bounds(client, config);

		graphics.fill(0, 0, width, height, 0x99000000);
		graphics.fill(0, height / 2, width, height / 2 + 1, 0x18FFFFFF);
		graphics.fill(width / 2, 0, width / 2 + 1, height, 0x18FFFFFF);

		AuraHud.drawCard(graphics, client, config, bounds.x(), bounds.y());

		if (dragging || bounds.contains(mouseX, mouseY)) {
			outline(graphics, bounds);
		}

		graphics.drawCenteredString(font, Component.translatable("aura.editor.hint").getString(),
				width / 2, height - 42, 0xFFB7C0D0);
		graphics.drawCenteredString(font,
				Component.translatable("aura.editor.scale", Math.round(config.hudScale * 100f) + " %").getString(),
				width / 2, height - 28, ACCENT);
	}

	private static void outline(GuiGraphics graphics, AuraHud.Bounds bounds) {
		graphics.fill(bounds.x() - 1, bounds.y() - 1, bounds.x() + bounds.width() + 1, bounds.y(), 0x88FFFFFF);
		graphics.fill(bounds.x() - 1, bounds.y() + bounds.height(), bounds.x() + bounds.width() + 1,
				bounds.y() + bounds.height() + 1, 0x88FFFFFF);
		graphics.fill(bounds.x() - 1, bounds.y(), bounds.x(), bounds.y() + bounds.height(), 0x88FFFFFF);
		graphics.fill(bounds.x() + bounds.width(), bounds.y(), bounds.x() + bounds.width() + 1,
				bounds.y() + bounds.height(), 0x88FFFFFF);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (event.button() != 0) {
			return false;
		}

		AuraHud.Bounds bounds = AuraHud.bounds(Minecraft.getInstance(), AuraConfig.get());
		if (!bounds.contains(event.x(), event.y())) {
			return super.mouseClicked(event, doubleClick);
		}

		dragging = true;
		grabX = (int) Math.round(event.x()) - bounds.x();
		grabY = (int) Math.round(event.y()) - bounds.y();
		return true;
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
		if (!dragging || event.button() != 0) {
			return super.mouseDragged(event, dragX, dragY);
		}

		AuraHud.move(AuraConfig.get(), (int) Math.round(event.x()) - grabX, (int) Math.round(event.y()) - grabY);
		return true;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (!dragging) {
			return super.mouseReleased(event);
		}

		dragging = false;
		AuraConfig.save();
		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
		if (vertical == 0.0) {
			return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
		}

		scaleBy(vertical > 0.0 ? SCALE_STEP : -SCALE_STEP);
		return true;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
			onClose();
			return true;
		}

		if (event.key() == GLFW.GLFW_KEY_MINUS) {
			scaleBy(-SCALE_STEP);
			return true;
		}

		if (event.key() == GLFW.GLFW_KEY_EQUAL) {
			scaleBy(SCALE_STEP);
			return true;
		}

		return super.keyPressed(event);
	}

	private void scaleBy(float step) {
		AuraConfig config = AuraConfig.get();
		float scale = Mth.clamp(config.hudScale + step, AuraConfig.MIN_SCALE, AuraConfig.MAX_SCALE);
		if (scale == config.hudScale) {
			return;
		}

		config.hudScale = scale;
		AuraHud.move(config, config.hudX, config.hudY);
		AuraConfig.save();
	}

	@Override
	public void onClose() {
		AuraConfig.save();
		minecraft.setScreen(parent);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
