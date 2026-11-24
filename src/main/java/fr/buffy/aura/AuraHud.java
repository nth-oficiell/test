package fr.buffy.aura;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public final class AuraHud {
	private static final int PADDING = 9;

	private AuraHud() {
	}

	/** Rectangle occupe par la carte, echelle et position comprises. */
	public record Bounds(int x, int y, int width, int height) {
		public boolean contains(double mouseX, double mouseY) {
			return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
		}
	}

	public static void render(GuiGraphics graphics, DeltaTracker delta) {
		Minecraft client = Minecraft.getInstance();
		AuraConfig config = AuraConfig.get();

		if (!config.hud || client.player == null || client.level == null || client.options.hideGui) {
			return;
		}

		Bounds bounds = bounds(client, config);
		drawCard(graphics, client, config, bounds.x(), bounds.y());
	}

	/** Position et taille de la carte, ramenees dans l'ecran. */
	public static Bounds bounds(Minecraft client, AuraConfig config) {
		List<String> lines = lines(client, config);
		int width = Math.round(cardWidth(client.font, lines) * config.hudScale);
		int height = Math.round(cardHeight(client.font, lines) * config.hudScale);
		int maxX = Math.max(0, client.getWindow().getGuiScaledWidth() - width);
		int maxY = Math.max(0, client.getWindow().getGuiScaledHeight() - height);
		return new Bounds(Mth.clamp(config.hudX, 0, maxX), Mth.clamp(config.hudY, 0, maxY), width, height);
	}

	/** Deplace le HUD en le gardant visible, sans l'enregistrer. */
	public static void move(AuraConfig config, int x, int y) {
		Bounds bounds = bounds(Minecraft.getInstance(), config);
		int maxX = Math.max(0, Minecraft.getInstance().getWindow().getGuiScaledWidth() - bounds.width());
		int maxY = Math.max(0, Minecraft.getInstance().getWindow().getGuiScaledHeight() - bounds.height());
		config.hudX = Mth.clamp(x, 0, maxX);
		config.hudY = Mth.clamp(y, 0, maxY);
	}

	/** Dessine la carte du HUD a la position demande (sert aussi a l'ecran de placement). */
	public static void drawCard(GuiGraphics graphics, Minecraft client, AuraConfig config, int x, int y) {
		Font font = client.font;
		List<String> lines = lines(client, config);
		int width = cardWidth(font, lines);
		int height = cardHeight(font, lines);
		AuraTheme theme = config.theme();

		graphics.pose().pushMatrix();
		graphics.pose().translate(x, y);
		graphics.pose().scale(config.hudScale, config.hudScale);

		Draw.roundRect(graphics, 2, 3, width, height, 7, 0x40000000);
		Draw.roundRect(graphics, 0, 0, width, height, 7, 0xC80D111A);
		Draw.roundRect(graphics, 0, 6, 2, height - 12, 1, theme.accent());

		Draw.dot(graphics, PADDING + 2, PADDING + font.lineHeight / 2f, 2.2f, theme.accent());
		graphics.drawString(font, "AURA", PADDING + 8, PADDING, theme.accent(), false);

		int lineY = PADDING + font.lineHeight + 4;
		for (String line : lines) {
			graphics.drawString(font, line, PADDING, lineY, 0xFFE6EAF2, false);
			lineY += font.lineHeight + 3;
		}

		graphics.pose().popMatrix();
	}

	/** Valeurs affichees ; des valeurs d'exemple remplacent le joueur hors du monde. */
	private static List<String> lines(Minecraft client, AuraConfig config) {
		boolean inWorld = client.player != null && client.level != null;
		List<String> lines = new ArrayList<>(3);

		if (config.coords) {
			lines.add(inWorld
					? "X " + client.player.getBlockX()
					+ "   Y " + client.player.getBlockY()
					+ "   Z " + client.player.getBlockZ()
					: "X 0   Y 64   Z 0");
		}

		if (config.fps) {
			lines.add("FPS " + (inWorld ? client.getFps() : 120));
		}

		if (config.time) {
			long dayTime = inWorld ? client.level.getDayTime() : 0L;
			lines.add(Component.translatable("aura.hud.day", dayTime / 24000L + 1L).getString()
					+ "  " + clock(dayTime));
		}

		return lines;
	}

	private static int cardWidth(Font font, List<String> lines) {
		int textWidth = font.width("AURA") + 12;
		for (String line : lines) {
			textWidth = Math.max(textWidth, font.width(line));
		}
		return textWidth + PADDING * 2;
	}

	private static int cardHeight(Font font, List<String> lines) {
		return PADDING * 2 + font.lineHeight + 4 + lines.size() * (font.lineHeight + 3);
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
