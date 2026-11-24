package fr.buffy.aura;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class AuraHud {
	private static final int MARGIN = 8;
	private static final int PADDING = 9;

	private AuraHud() {
	}

	public static void render(GuiGraphics graphics, DeltaTracker delta) {
		Minecraft client = Minecraft.getInstance();
		AuraConfig config = AuraConfig.get();

		if (!config.hud || client.player == null || client.level == null || client.options.hideGui) {
			return;
		}

		List<String> lines = new ArrayList<>(3);

		if (config.coords) {
			lines.add("X " + client.player.getBlockX()
					+ "   Y " + client.player.getBlockY()
					+ "   Z " + client.player.getBlockZ());
		}

		if (config.fps) {
			lines.add("FPS " + client.getFps());
		}

		if (config.time) {
			long dayTime = client.level.getDayTime();
			lines.add(Component.translatable("aura.hud.day", dayTime / 24000L + 1L).getString()
					+ "  " + clock(dayTime));
		}

		if (lines.isEmpty()) {
			return;
		}

		AuraTheme theme = config.theme();
		String title = "AURA";
		int textWidth = client.font.width(title) + 12;
		for (String line : lines) {
			textWidth = Math.max(textWidth, client.font.width(line));
		}

		int width = textWidth + PADDING * 2;
		int height = PADDING * 2 + client.font.lineHeight + 4 + lines.size() * (client.font.lineHeight + 3);
		int x = MARGIN;
		int y = MARGIN;

		Draw.roundRect(graphics, x + 2, y + 3, width, height, 7, 0x40000000);
		Draw.roundRect(graphics, x, y, width, height, 7, 0xC80D111A);
		Draw.roundRect(graphics, x, y + 6, 2, height - 12, 1, theme.accent());

		Draw.dot(graphics, x + PADDING + 2, y + PADDING + client.font.lineHeight / 2f, 2.2f, theme.accent());
		graphics.drawString(client.font, title, x + PADDING + 8, y + PADDING, theme.accent(), false);

		int lineY = y + PADDING + client.font.lineHeight + 4;
		for (String line : lines) {
			graphics.drawString(client.font, line, x + PADDING, lineY, 0xFFE6EAF2, false);
			lineY += client.font.lineHeight + 3;
		}
	}

	/** Heure du monde au format HH:MM, independante de la locale du systeme. */
	private static String clock(long dayTime) {
		int hours = (int) ((dayTime / 1000L + 6L) % 24L);
		int minutes = (int) ((dayTime % 1000L) * 60L / 1000L);
		return twoDigits(hours) + ":" + twoDigits(minutes);
	}

	private static String twoDigits(int value) {
		return value < 10 ? "0" + value : Integer.toString(value);
	}
}
