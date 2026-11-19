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
import net.minecraft.network.chat.Component;

public class HudInfoModule extends Module {
    private final Setting.Toggle showFps;
    private final Setting.Toggle showCoords;
    private final Setting.Toggle showClock;
    private final Setting.Toggle showDirection;
    private final Setting.Toggle background;

    public HudInfoModule() {
        super("hudinfo", "HUD Infos", "Affiche FPS, coordonnées, heure et direction en haut à gauche de l'écran.", ModuleCategory.AFFICHAGE);
        showFps = new Setting.Toggle("fps", "FPS", true);
        showCoords = new Setting.Toggle("coords", "Coordonnées", true);
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
        int x = 6;
        int y = 6;
        int lineHeight = 10;

        if (hud.showFps.value) {
            y += hud.line(graphics, client, "FPS : " + client.getFps(), x, y, lineHeight);
        }
        if (player != null) {
            BlockPos pos = player.blockPosition();
            if (hud.showCoords.value) {
                y += hud.line(graphics, client,
                        "X " + pos.getX() + "  Y " + pos.getY() + "  Z " + pos.getZ(), x, y, lineHeight);
            }
            if (hud.showDirection.value) {
                y += hud.line(graphics, client, "Direction : " + directionLabel(player.getDirection()), x, y, lineHeight);
            }
            if (hud.showClock.value && client.level != null) {
                y += hud.line(graphics, client, "Heure : " + worldClock(client), x, y, lineHeight);
            }
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

    private int line(GuiGraphicsExtractor graphics, Minecraft client, String text, int x, int y, int lineHeight) {
        if (background.value) {
            int textWidth = client.font.width(text);
            if (dev.utilikit.client.modules.extra.Gradients.active()) {
                for (int[] gband : dev.utilikit.client.modules.extra.Gradients.bands(x - 3, y - 2, x + textWidth + 3, y + lineHeight - 2)) {
                    graphics.fill(gband[0], gband[1], gband[2], gband[3], gband[4]);
                }
            } else {
                graphics.fill(x - 3, y - 2, x + textWidth + 3, y + lineHeight - 2, 0x90000000);
            }
        }
        graphics.text(client.font, text, x, y, 0xFFFFFFFF, true);
        return lineHeight + 2;
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
