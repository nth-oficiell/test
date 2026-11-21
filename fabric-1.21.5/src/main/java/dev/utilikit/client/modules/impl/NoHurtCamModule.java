package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

public class NoHurtCamModule extends Module {
    private double prevTilt = -1.0D;

    public NoHurtCamModule() {
        super("nohurtcam", "No Hurt Cam", "Désactive le tremblement de la caméra quand tu prends des dégâts (réglage vanilla, sans mixin).", ModuleCategory.VISUEL);
    }

    @Override
    protected void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.options != null) {
            SimpleOption<Double> tilt = client.options.getDamageTiltStrength();
            prevTilt = tilt.getValue();
            tilt.setValue(0.0D);
        }
    }

    @Override
    protected void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.options != null && prevTilt >= 0.0D) {
            client.options.getDamageTiltStrength().setValue(prevTilt);
            prevTilt = -1.0D;
        }
    }
}
