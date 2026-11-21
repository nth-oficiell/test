package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class HudInfoModule extends Module {
    private final Setting.Toggle showFps;
    private final Setting.Toggle showCoords;
    private final Setting.Toggle showClock;
    private final Setting.Toggle showDirection;
    private final Setting.Toggle background;

    public HudInfoModule() {
        super("hudinfo", "HUD Infos", "Affiche FPS, coordonnées, heure et direction dans un panneau centré en haut de l'écran, avec fond et dégradé.", ModuleCategory.AFFICHAGE);
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

    public static void renderHud(DrawContext context, MinecraftClient client) {
        Module base = ModuleManager.get("hudinfo");
        if (!(base instanceof HudInfoModule hud) || !hud.enabled) {
            return;
        }
        ClientPlayerEntity player = client.player;
        java.util.List<String> lines = new java.util.ArrayList<>();
        if (hud.showFps.value) {
            lines.add("FPS : " + client.getCurrentFps());
        }
        if (player != null) {
            BlockPos pos = player.getBlockPos();
            if (hud.showCoords.value) {
                lines.add("X " + pos.getX() + "  Y " + pos.getY() + "  Z " + pos.getZ());
            }
            if (hud.showDirection.value) {
                lines.add("Direction : " + directionLabel(player.getHorizontalFacing()));
            }
            if (hud.showClock.value && client.world != null) {
                lines.add("Heure : " + worldClock(client));
            }
        }
        if (lines.isEmpty()) {
            return;
        }
        int lineHeight = 12;
        int scaledWidth = client.getWindow().getScaledWidth();
        int scaledHeight = client.getWindow().getScaledHeight();
        int maxW = 0;
        for (String text : lines) {
            maxW = Math.max(maxW, client.textRenderer.getWidth(text));
        }
        int[] hudOffset = dev.utilikit.client.modules.extra.ExtraModules.hudOffset();
        int x = 4 + hudOffset[0];
        int y = 4 + hudOffset[1];
        if (hud.background.value) {
            HudPaint.panel(context, x - 6, y - 4, x + maxW + 6, y + 2 + lines.size() * lineHeight);
        }
        int yy = y + 2;
        for (String text : lines) {
            dev.utilikit.client.modules.extra.HudDrag.track("hudinfo", x, yy, maxW, lineHeight);
            HudPaint.text(context, client.textRenderer, text, x, yy, 0xFFFFFFFF);
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
            default -> direction.asString();
        };
    }

    private static String worldClock(MinecraftClient client) {
        long ticks = client.world.getTimeOfDay() % 24000L;
        int hours = (int) ((ticks / 1000L + 6L) % 24L);
        int minutes = (int) (ticks % 1000L * 60L / 1000L);
        return String.format("%02d:%02d", hours, minutes);
    }
}
