package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.Setting;
import net.minecraft.client.MinecraftClient;

public class FullBrightModule extends Module {
    private final Setting.Slider intensity;
    private double previousGamma = -1.0D;

    public FullBrightModule() {
        super("fullbright", "FullBright", "Éclaircit tout de nuit en augmentant la luminosité au-delà du maximum du jeu.", ModuleCategory.VISUEL);
        intensity = new Setting.Slider("intensity", "Intensité", 1.0D, 16.0D, 14.0D);
        settings.add(intensity);
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
        if (previousGamma < 0.0D) {
            previousGamma = client.options.getGamma().getValue();
        }
        client.options.getGamma().setValue(intensity.value);
    }

    private void restore() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) {
            previousGamma = -1.0D;
            return;
        }
        if (previousGamma >= 0.0D) {
            client.options.getGamma().setValue(Math.min(previousGamma, 1.0D));
        }
        previousGamma = -1.0D;
    }
}
