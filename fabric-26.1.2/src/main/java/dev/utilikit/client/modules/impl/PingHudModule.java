package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;

public class PingHudModule extends Module {
    private final Setting.Toggle background;

    public PingHudModule() {
        super("ping", "Ping HUD", "Affiche ta latence avec le serveur en haut à droite de l'écran.", ModuleCategory.AFFICHAGE);
        background = new Setting.Toggle("background", "Fond semi-transparent", true);
        settings.add(background);
    }

    public static void renderHud(GuiGraphicsExtractor graphics, Minecraft client) {
        Module base = ModuleManager.get("ping");
        if (!(base instanceof PingHudModule hud) || !hud.enabled) {
            return;
        }
        if (client.getConnection() == null || client.player == null) {
            return;
        }
        PlayerInfo entry = client.getConnection().getPlayerInfo(client.player.getUUID());
        int latency = entry != null ? entry.getLatency() : 0;
        String text = "Ping : " + latency + " ms";
        int color = latency < 100 ? 0xFF55FF55 : (latency < 250 ? 0xFFFFAA00 : 0xFFFF5555);
        int x = client.getWindow().getGuiScaledWidth() - client.font.width(text) - 6;
        if (hud.background.value) {
            if (dev.utilikit.client.modules.extra.Gradients.active()) {
                for (int[] gband : dev.utilikit.client.modules.extra.Gradients.bands(x - 3, 4, client.getWindow().getGuiScaledWidth() - 3, 18)) {
                    graphics.fill(gband[0], gband[1], gband[2], gband[3], gband[4]);
                }
            } else {
                graphics.fill(x - 3, 4, client.getWindow().getGuiScaledWidth() - 3, 18, 0x90000000);
            }
        }
        graphics.text(client.font, text, x, 6, color, true);
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
