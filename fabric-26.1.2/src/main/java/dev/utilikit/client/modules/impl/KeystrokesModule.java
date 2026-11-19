package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

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

    private static void drawKeyBox(GuiGraphicsExtractor graphics, Minecraft client,
                                   int x, int y, int w, int h, boolean pressed, String label) {
        graphics.fill(x, y, x + w, y + h, boxColor(pressed));
        graphics.outline(x, y, w, h, 0xFF50506A);
        graphics.text(client.font, label, x + (w - client.font.width(label)) / 2, y + (h - 8) / 2, textColor(pressed), true);
    }

    public static void renderHud(GuiGraphicsExtractor graphics, Minecraft client) {
        Module base = ModuleManager.get("keystrokes");
        if (!(base instanceof KeystrokesModule hud) || !hud.enabled || client.options == null) {
            return;
        }
        int width = client.getWindow().getGuiScaledWidth();
        int height = client.getWindow().getGuiScaledHeight();
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

        drawKeyBox(graphics, client, startX + PADDING + (BOX + PADDING), y, BOX, BOX,
                client.options.keyUp.isDown(), azerty ? "Z" : "W");
        int midY = y + BOX + PADDING;
        drawKeyBox(graphics, client, startX + PADDING, midY, BOX, BOX,
                client.options.keyLeft.isDown(), azerty ? "Q" : "A");
        drawKeyBox(graphics, client, startX + PADDING + (BOX + PADDING), midY, BOX, BOX,
                client.options.keyDown.isDown(), "S");
        drawKeyBox(graphics, client, startX + PADDING + 2 * (BOX + PADDING), midY, BOX, BOX,
                client.options.keyRight.isDown(), "D");
        int cursorY = midY + BOX + PADDING;

        if (space) {
            int spaceX = startX + PADDING + (BOX + PADDING) / 2;
            int spaceW = 2 * BOX + PADDING;
            drawKeyBox(graphics, client, spaceX, cursorY, spaceW, 10,
                    client.options.keyJump.isDown(), "· · ·");
            cursorY += 12;
        }

        if (mouse) {
            drawKeyBox(graphics, client, startX + PADDING, cursorY, BOX, BOX,
                    client.options.keyAttack.isDown(), azerty ? "C1" : "L");
            drawKeyBox(graphics, client, startX + PADDING + (BOX + PADDING), cursorY, BOX, BOX,
                    client.options.keyUse.isDown(), azerty ? "C2" : "R");
        }
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
