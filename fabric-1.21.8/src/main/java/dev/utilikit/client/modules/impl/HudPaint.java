package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.extra.Gradients;
import dev.utilikit.client.ui.Palettes;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public final class HudPaint {
    private static final int LABEL = 0xFF9C9CB6;
    private static final int PANEL_BG = 0xA6000000;

    private HudPaint() {
    }

    public static void fill(DrawContext context, int x1, int y1, int x2, int y2) {
        if (Gradients.active()) {
            for (int[] band : Gradients.bands(x1, y1, x2, y2)) {
                context.fill(band[0], band[1], band[2], band[3], band[4]);
            }
        } else {
            context.fill(x1, y1, x2, y2, PANEL_BG);
        }
    }

    public static void panel(DrawContext context, int x1, int y1, int x2, int y2) {
        if (x2 <= x1 || y2 <= y1) {
            return;
        }
        dev.utilikit.client.ui.Rounded.Canvas cv =
                (ax1, ay1, ax2, ay2, col) -> context.fill(ax1, ay1, ax2, ay2, col);
        fill(context, x1 + 1, y1 + 1, x2 - 1, y2 - 1);
        dev.utilikit.client.ui.Rounded.stroke(cv, x1, y1, x2, y2, 8, 0x30FFFFFF);
    }

    public static void text(DrawContext context, TextRenderer font, String text, int x, int y, int valueColor) {
        int sep = text.indexOf(" : ");
        if (sep > 0) {
            String label = text.substring(0, sep + 3);
            String value = text.substring(sep + 3);
            context.drawTextWithShadow(font, label, x, y, LABEL);
            context.drawTextWithShadow(font, value, x + font.getWidth(label), y, valueColor);
        } else {
            context.drawTextWithShadow(font, text, x, y, valueColor);
        }
    }
}
