package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.Setting;
import net.minecraft.client.MinecraftClient;

public class MaxFpsModule extends Module {
    private final Setting.Slider limit;

    public MaxFpsModule() {
        super("maxfps", "Limite FPS", "Change la limite de FPS du jeu directement depuis le menu NTH Utils.", ModuleCategory.VISUEL);
        limit = new Setting.Slider("limit", "FPS max", 30.0D, 260.0D, 260.0D);
        settings.add(limit);
    }

    @Override
    protected void onEnable() {
        apply();
    }

    @Override
    protected void onDisable() {
        restore();
    }

    @Override
    public void onSettingChanged() {
        if (enabled) {
            apply();
        }
    }

    private void apply() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) {
            return;
        }
        client.options.getMaxFps().setValue((int) Math.round(limit.value));
    }

    private void restore() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) {
            return;
        }
        client.options.getMaxFps().setValue(120);
    }
}
