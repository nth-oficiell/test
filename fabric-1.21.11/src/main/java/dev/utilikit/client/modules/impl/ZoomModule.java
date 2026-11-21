package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;

public class ZoomModule extends Module {
    private final Setting.Slider factor;
    private final KeyBinding zoomKey;
    private boolean zooming;
    private int prevFov;

    public ZoomModule(KeyBinding zoomKey) {
        super("zoom", "Zoom", "Zoome la caméra en modifiant le FOV vanilla avec une touche (sans mixin).", ModuleCategory.VISUEL);
        this.zoomKey = zoomKey;
        factor = new Setting.Slider("factor", "Facteur de zoom", 2.0D, 6.0D, 4.0D);
        settings.add(factor);
    }

    public void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) {
            return;
        }
        SimpleOption<Integer> fov = client.options.getFov();
        boolean active = enabled && zoomKey.isPressed() && client.currentScreen == null;
        if (active && !zooming) {
            prevFov = fov.getValue();
            zooming = true;
        } else if (!active && zooming) {
            zooming = false;
            applyFov(fov, prevFov);
        }
        if (zooming) {
            applyFov(fov, (int) Math.max(30, Math.round(prevFov / factor.value)));
        }
    }

    private static void applyFov(SimpleOption<Integer> fov, int value) {
        if (fov.getValue() != value) {
            fov.setValue(value);
        }
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
        if (zooming) {
            zooming = false;
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.options != null) {
                applyFov(client.options.getFov(), prevFov);
            }
        }
    }
}
