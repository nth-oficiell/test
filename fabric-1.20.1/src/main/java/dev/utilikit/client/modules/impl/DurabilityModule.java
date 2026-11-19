package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class DurabilityModule extends Module {
    private final Setting.Toggle background;

    public DurabilityModule() {
        super("durability", "Durabilité", "Affiche la durabilité de l'objet en main en haut à droite de l'écran.", ModuleCategory.AFFICHAGE);
        background = new Setting.Toggle("background", "Fond semi-transparent", true);
        settings.add(background);
    }

    public static void renderHud(DrawContext context, MinecraftClient client) {
        Module base = ModuleManager.get("durability");
        if (!(base instanceof DurabilityModule hud) || !hud.enabled) {
            return;
        }
        if (client.player == null) {
            return;
        }
        ItemStack stack = client.player.getMainHandStack();
        if (stack == null || stack.isEmpty() || stack.getMaxDamage() <= 0) {
            return;
        }
        int remaining = stack.getMaxDamage() - stack.getDamage();
        double ratio = (double) remaining / (double) stack.getMaxDamage();
        int color = ratio > 0.5D ? 0xFF55FF55 : (ratio > 0.2D ? 0xFFFFAA00 : 0xFFFF5555);
        String text = "Durabilité : " + remaining + " / " + stack.getMaxDamage();
        int y = 6;
        Module pingBase = ModuleManager.get("ping");
        if (pingBase != null && pingBase.enabled) {
            y = 20;
        }
        int x = client.getWindow().getScaledWidth() - client.textRenderer.getWidth(text) - 6;
        if (hud.background.value) {
            context.fill(x - 3, y - 2, client.getWindow().getScaledWidth() - 3, y + 12, 0x90000000);
        }
        context.drawTextWithShadow(client.textRenderer, text, x, y, color);
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
