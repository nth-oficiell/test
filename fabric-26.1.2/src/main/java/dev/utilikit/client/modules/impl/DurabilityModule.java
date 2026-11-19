package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

public class DurabilityModule extends Module {
    private final Setting.Toggle background;

    public DurabilityModule() {
        super("durability", "Durabilité", "Affiche la durabilité de l'objet en main en haut à droite de l'écran.", ModuleCategory.AFFICHAGE);
        background = new Setting.Toggle("background", "Fond semi-transparent", true);
        settings.add(background);
    }

    public static void renderHud(GuiGraphicsExtractor graphics, Minecraft client) {
        Module base = ModuleManager.get("durability");
        if (!(base instanceof DurabilityModule hud) || !hud.enabled) {
            return;
        }
        if (client.player == null) {
            return;
        }
        ItemStack stack = client.player.getMainHandItem();
        if (stack == null || stack.isEmpty() || stack.getMaxDamage() <= 0) {
            return;
        }
        int remaining = stack.getMaxDamage() - stack.getDamageValue();
        double ratio = (double) remaining / (double) stack.getMaxDamage();
        int color = ratio > 0.5D ? 0xFF55FF55 : (ratio > 0.2D ? 0xFFFFAA00 : 0xFFFF5555);
        String text = "Durabilité : " + remaining + " / " + stack.getMaxDamage();
        int y = 6;
        Module pingBase = ModuleManager.get("ping");
        if (pingBase != null && pingBase.enabled) {
            y = 20;
        }
        int x = client.getWindow().getGuiScaledWidth() - client.font.width(text) - 6;
        if (hud.background.value) {
            graphics.fill(x - 3, y - 2, client.getWindow().getGuiScaledWidth() - 3, y + 12, 0x90000000);
        }
        graphics.text(client.font, text, x, y, color, true);
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
