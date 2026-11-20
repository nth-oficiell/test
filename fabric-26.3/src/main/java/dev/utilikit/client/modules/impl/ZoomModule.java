package dev.utilikit.client.modules.impl;

import dev.utilikit.client.UtiliKitClient;
import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.Setting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class ZoomModule extends Module {
    private final Setting.Slider factor;
    private final KeyMapping zoomKey;
    private boolean zooming;
    private double prevFov;

    public ZoomModule(KeyMapping zoomKey) {
        super("zoom", "Zoom", "Zoome la caméra en modifiant le FOV vanilla avec une touche (sans mixin).", ModuleCategory.VISUEL);
        this.zoomKey = zoomKey;
        factor = new Setting.Slider("factor", "Facteur de zoom", 2.0D, 6.0D, 4.0D);
        settings.add(factor);
    }

    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.options == null) {
            return;
        }
        boolean active = enabled && zoomKey.isDown() && !UtiliKitClient.isScreenOpen(client);
        if (active && !zooming) {
            prevFov = client.options.fov().get();
            zooming = true;
        } else if (!active && zooming) {
            zooming = false;
            applyFov(client, prevFov);
        }
        if (zooming) {
            applyFov(client, Math.max(30.0D, prevFov / factor.value));
        }
    }

    private static void applyFov(Minecraft client, double value) {
        if (Math.abs(client.options.fov().get() - value) > 0.01D) {
            client.options.fov().set((int) Math.round(value));
        }
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
        if (zooming) {
            zooming = false;
            Minecraft client = Minecraft.getInstance();
            if (client != null && client.options != null) {
                applyFov(client, prevFov);
            }
        }
    }
}
