package fr.buffy.aura;

import net.minecraft.client.gui.GuiGraphics;

/** Petits utilitaires de dessin, sans texture : tout est trace en rectangles. */
public final class Draw {
	private Draw() {
	}

	public static int alpha(int rgb, int alpha) {
		return (alpha << 24) | (rgb & 0xFFFFFF);
	}

	public static int lerpColor(int from, int to, float t) {
		t = Math.max(0f, Math.min(1f, t));
		int a = (int) (((from >>> 24) & 0xFF) + (((to >>> 24) & 0xFF) - ((from >>> 24) & 0xFF)) * t);
		int r = (int) (((from >> 16) & 0xFF) + (((to >> 16) & 0xFF) - ((from >> 16) & 0xFF)) * t);
		int g = (int) (((from >> 8) & 0xFF) + (((to >> 8) & 0xFF) - ((from >> 8) & 0xFF)) * t);
		int b = (int) ((from & 0xFF) + ((to & 0xFF) - (from & 0xFF)) * t);
		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	public static void roundRect(GuiGraphics graphics, int x, int y, int width, int height, int radius, int color) {
		roundRectGradient(graphics, x, y, width, height, radius, color, color);
	}

	public static void roundRectGradient(GuiGraphics graphics, int x, int y, int width, int height, int radius,
										int top, int bottom) {
		if (width <= 0 || height <= 0 || ((top >>> 24) == 0 && (bottom >>> 24) == 0)) {
			return;
		}

		int r = Math.max(0, Math.min(radius, Math.min(width, height) / 2));
		boolean solid = top == bottom;
		for (int row = 0; row < height; row++) {
			int inset = cornerInset(row, height, r);
			int color = solid ? top : lerpColor(top, bottom, height <= 1 ? 0f : row / (float) (height - 1));
			graphics.fill(x + inset, y + row, x + width - inset, y + row + 1, color);
		}
	}

	private static int cornerInset(int row, int height, int radius) {
		if (radius <= 0) {
			return 0;
		}

		double distance;
		if (row < radius) {
			distance = radius - (row + 0.5D);
		} else if (row >= height - radius) {
			distance = (row + 0.5D) - (height - radius);
		} else {
			return 0;
		}

		double inside = radius * (double) radius - distance * distance;
		if (inside <= 0.0D) {
			return radius;
		}
		return (int) Math.round(radius - Math.sqrt(inside));
	}

	/** Interrupteur : piste colorée selon l'état, pastille blanche qui glisse. */
	public static void toggleSwitch(GuiGraphics graphics, int x, int y, int width, int height, float progress,
									int accent) {
		roundRect(graphics, x, y, width, height, height / 2, lerpColor(0xFF2B3241, accent, progress));

		int knobSize = height - 4;
		int knobX = x + 2 + Math.round(progress * (width - 4 - knobSize));
		roundRect(graphics, knobX, y + 2, knobSize, knobSize, knobSize / 2, 0xFFF7F9FD);
	}

	public static boolean inside(int mouseX, int mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}

	public static void dot(GuiGraphics graphics, float centerX, float centerY, float radius, int color) {		int size = Math.max(1, Math.round(radius * 2f));
		roundRect(graphics, Math.round(centerX - radius), Math.round(centerY - radius), size, size, size / 2, color);
	}

	/** Halo circulaire doux, dessine ligne par ligne. */
	public static void glow(GuiGraphics graphics, float centerX, float centerY, float radius, int rgb) {
		int steps = Math.round(radius);
		int cx = Math.round(centerX);
		int cy = Math.round(centerY);

		for (int offset = -steps; offset <= steps; offset++) {
			float t = offset / radius;
			if (t < -1f || t > 1f) {
				continue;
			}

			int half = Math.round(radius * (float) Math.sqrt(Math.max(0f, 1f - t * t)));
			if (half <= 0) {
				continue;
			}

			float falloff = 1f - Math.abs(t);
			int a = (int) (34f * falloff * falloff);
			if (a <= 0) {
				continue;
			}

			int row = cy + offset;
			graphics.fill(cx - half, row, cx + half, row + 1, alpha(rgb, a));
		}
	}
}
