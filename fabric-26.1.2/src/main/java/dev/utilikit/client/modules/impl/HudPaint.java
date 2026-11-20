package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.extra.Gradients;
import dev.utilikit.client.ui.Palettes;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class HudPaint {
    private static final int LABEL = 0xFF9C9CB6;
    private static final int PANEL_BG = 0xA6000000;

    private HudPaint() {
    }

    public static void fill(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2) {
        if (Gradients.active()) {
            for (int[] band : Gradients.bands(x1, y1, x2, y2)) {
                graphics.fill(band[0], band[1], band[2], band[3], band[4]);
            }
        } else {
            graphics.fill(x1, y1, x2, y2, PANEL_BG);
        }
    }

    public static void panel(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2) {
        if (x2 <= x1 || y2 <= y1) {
            return;
        }
        fill(graphics, x1, y1, x2, y2);
        int border = Palettes.accentAlpha(0x5A);
        graphics.fill(x1, y1, x2, y1 + 1, border);
        graphics.fill(x1, y2 - 1, x2, y2, border);
        graphics.fill(x1, y1 + 1, x1 + 1, y2 - 1, border);
        graphics.fill(x2 - 1, y1 + 1, x2, y2 - 1, border);
    }

    public static void text(GuiGraphicsExtractor graphics, Font font, String text, int x, int y, int valueColor) {
        int sep = text.indexOf(" : ");
        if (sep > 0) {
            String label = text.substring(0, sep + 3);
            String value = text.substring(sep + 3);
            graphics.text(font, label, x, y, LABEL, true);
            graphics.text(font, value, x + font.width(label), y, valueColor, true);
        } else {
            graphics.text(font, text, x, y, valueColor, true);
        }
    }
}
