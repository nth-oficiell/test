package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

public class PingHudModule extends Module {
    private final Setting.Toggle background;

    public PingHudModule() {
        super("ping", "Ping HUD", "Affiche ta latence avec le serveur en haut à droite de l'écran.", ModuleCategory.AFFICHAGE);
        background = new Setting.Toggle("background", "Fond semi-transparent", true);
        settings.add(background);
    }

    public static void renderHud(DrawContext context, MinecraftClient client) {
        Module base = ModuleManager.get("ping");
        if (!(base instanceof PingHudModule hud) || !hud.enabled) {
            return;
        }
        if (client.getNetworkHandler() == null || client.player == null) {
            return;
        }
        PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
        int latency = entry != null ? entry.getLatency() : 0;
        String text = "Ping : " + latency + " ms";
        int color = latency < 100 ? 0x55FF55 : (latency < 250 ? 0xFFAA00 : 0xFF5555);
        int x = client.getWindow().getScaledWidth() - client.textRenderer.getWidth(text) - 6;
        if (hud.background.value) {
            context.fill(x - 3, 4, client.getWindow().getScaledWidth() - 3, 18, 0x90000000);
        }
        context.drawTextWithShadow(client.textRenderer, text, x, 6, color);
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
