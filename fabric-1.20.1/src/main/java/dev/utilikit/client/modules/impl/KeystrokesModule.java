package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class KeystrokesModule extends Module {
    private static final int BOX = 18;
    private static final int PADDING = 2;

    private final Setting.Toggle azerty;
    private final Setting.Toggle showMouse;
    private final Setting.Toggle showSpace;

    public KeystrokesModule() {
        super("keystrokes", "Keystrokes", "Affiche tes touches de déplacement et de clic sous le crosshair, façon packs HUD.", ModuleCategory.AFFICHAGE);
        azerty = new Setting.Toggle("azerty", "Clavier AZERTY (ZQSD)", true);
        showMouse = new Setting.Toggle("mouse", "Touches de souris", true);
        showSpace = new Setting.Toggle("space", "Barre d'espace", true);
        settings.add(azerty);
        settings.add(showMouse);
        settings.add(showSpace);
    }

    private static int boxColor(boolean pressed) {
        return pressed ? 0x9033AA55 : 0x90000000;
    }

    private static int textColor(boolean pressed) {
        return pressed ? 0xFFFFFFFF : 0xFFBBBBBB;
    }

    private static void drawKeyBox(DrawContext context, MinecraftClient client,
                                   int x, int y, int w, int h, boolean pressed, String label) {
        context.fill(x, y, x + w, y + h, boxColor(pressed));
        context.drawBorder(x, y, w, h, 0xFF50506A);
        int textWidth = client.textRenderer.getWidth(label);
        context.drawTextWithShadow(client.textRenderer, label, x + (w - textWidth) / 2, y + (h - 8) / 2, textColor(pressed));
    }

    public static void renderHud(DrawContext context, MinecraftClient client) {
        Module base = ModuleManager.get("keystrokes");
        if (!(base instanceof KeystrokesModule hud) || !hud.enabled || client.options == null) {
            return;
        }
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        boolean azerty = hud.azerty.value;
        boolean mouse = hud.showMouse.value;
        boolean space = hud.showSpace.value;

        int columns = 3;
        int totalW = columns * BOX + (columns + 1) * PADDING;
        int startX = width / 2 - totalW / 2;
        int y = height - 46;
        if (space) {
            y -= 12;
        }
        if (mouse) {
            y -= BOX + PADDING;
        }

        drawKeyBox(context, client, startX + PADDING + (BOX + PADDING), y, BOX, BOX,
                client.options.forwardKey.isPressed(), azerty ? "Z" : "W");
        int midY = y + BOX + PADDING;
        drawKeyBox(context, client, startX + PADDING, midY, BOX, BOX,
                client.options.leftKey.isPressed(), azerty ? "Q" : "A");
        drawKeyBox(context, client, startX + PADDING + (BOX + PADDING), midY, BOX, BOX,
                client.options.backKey.isPressed(), "S");
        drawKeyBox(context, client, startX + PADDING + 2 * (BOX + PADDING), midY, BOX, BOX,
                client.options.rightKey.isPressed(), "D");
        int cursorY = midY + BOX + PADDING;

        if (space) {
            int spaceX = startX + PADDING + (BOX + PADDING) / 2;
            int spaceW = 2 * BOX + PADDING;
            drawKeyBox(context, client, spaceX, cursorY, spaceW, 10,
                    client.options.jumpKey.isPressed(), "· · ·");
            cursorY += 12;
        }

        if (mouse) {
            drawKeyBox(context, client, startX + PADDING, cursorY, BOX, BOX,
                    client.options.attackKey.isPressed(), azerty ? "C1" : "L");
            drawKeyBox(context, client, startX + PADDING + (BOX + PADDING), cursorY, BOX, BOX,
                    client.options.useKey.isPressed(), azerty ? "C2" : "R");
        }
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
