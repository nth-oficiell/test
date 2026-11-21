package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.Setting;
import net.minecraft.client.Minecraft;

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
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.options == null) {
            return;
        }
        if (previousGamma < 0.0D) {
            previousGamma = client.options.gamma().get();
        }
        client.options.gamma().set(intensity.value);
    }

    private void restore() {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.options == null) {
            previousGamma = -1.0D;
            return;
        }
        if (previousGamma >= 0.0D) {
            client.options.gamma().set(Math.min(previousGamma, 1.0D));
        }
        previousGamma = -1.0D;
    }
}
