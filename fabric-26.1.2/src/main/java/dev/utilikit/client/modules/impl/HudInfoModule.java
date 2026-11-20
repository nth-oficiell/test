package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class HudInfoModule extends Module {
    private final Setting.Toggle showFps;
    private final Setting.Toggle showCoords;
    private final Setting.Toggle showClock;
    private final Setting.Toggle showDirection;
    private final Setting.Toggle background;

    public HudInfoModule() {
        super("hudinfo", "HUD Infos", "Affiche FPS, coordonn\u00e9es, heure et direction dans un panneau centr\u00e9 en haut de l'\u00e9cran, avec fond et d\u00e9grad\u00e9.", ModuleCategory.AFFICHAGE);
        showFps = new Setting.Toggle("fps", "FPS", true);
        showCoords = new Setting.Toggle("coords", "Coordonn\u00e9es", true);
        showClock = new Setting.Toggle("clock", "Heure du monde", true);
        showDirection = new Setting.Toggle("direction", "Direction", true);
        background = new Setting.Toggle("background", "Fond semi-transparent", true);
        settings.add(showFps);
        settings.add(showCoords);
        settings.add(showClock);
        settings.add(showDirection);
        settings.add(background);
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    public static void renderHud(GuiGraphicsExtractor graphics, Minecraft client) {
        Module base = ModuleManager.get("hudinfo");
        if (!(base instanceof HudInfoModule hud) || !hud.enabled) {
            return;
        }
        LocalPlayer player = client.player;
        java.util.List<String> lines = new java.util.ArrayList<>();
        if (hud.showFps.value) {
            lines.add("FPS : " + client.getFps());
        }
        if (player != null) {
            BlockPos pos = player.blockPosition();
            if (hud.showCoords.value) {
                lines.add("X " + pos.getX() + "  Y " + pos.getY() + "  Z " + pos.getZ());
            }
            if (hud.showDirection.value) {
                lines.add("Direction : " + directionLabel(player.getDirection()));
            }
            if (hud.showClock.value && client.level != null) {
                lines.add("Heure : " + worldClock(client));
            }
        }
        if (lines.isEmpty()) {
            return;
        }
        int lineHeight = 12;
        int scaledWidth = client.getWindow().getGuiScaledWidth();
        int maxW = 0;
        for (String text : lines) {
            maxW = Math.max(maxW, client.font.width(text));
        }
        int[] hudOffset = dev.utilikit.client.modules.extra.ExtraModules.hudOffset();
        int x = scaledWidth / 2 - maxW / 2 + hudOffset[0];
        int y = 6 + hudOffset[1];
        if (hud.background.value) {
            HudPaint.panel(graphics, x - 6, y - 4, x + maxW + 6, y + 2 + lines.size() * lineHeight);
        }
        int yy = y + 2;
        for (String text : lines) {
            dev.utilikit.client.modules.extra.HudDrag.track("hudinfo", x, yy, maxW, lineHeight);
            HudPaint.text(graphics, client.font, text, x, yy, 0xFFFFFFFF);
            yy += lineHeight;
        }
    }

    public int lineCount() {
        int lines = 0;
        if (showFps.value) {
            lines++;
        }
        if (showCoords.value) {
            lines++;
        }
        if (showDirection.value) {
            lines++;
        }
        if (showClock.value) {
            lines++;
        }
        return lines;
    }

    private static String directionLabel(Direction direction) {
        return switch (direction) {
            case NORTH -> "Nord (Z-)";
            case SOUTH -> "Sud (Z+)";
            case WEST -> "Ouest (X-)";
            case EAST -> "Est (X+)";
            default -> direction.toString();
        };
    }

    private static String worldClock(Minecraft client) {
        long ticks = client.level.getOverworldClockTime() % 24000L;
        int hours = (int) ((ticks / 1000L + 6L) % 24L);
        int minutes = (int) (ticks % 1000L * 60L / 1000L);
        return String.format("%02d:%02d", hours, minutes);
    }
}
