package dev.utilikit.client.modules.impl;

import dev.utilikit.client.UtiliKitClient;
import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class ZoomModule extends Module {
    private final Setting.Slider factor;
    private final KeyMapping zoomKey;
    private boolean keyHeld;

    public ZoomModule(KeyMapping zoomKey) {
        super("zoom", "Zoom", "Zoome la caméra en maintenant une touche.", ModuleCategory.VISUEL);
        this.zoomKey = zoomKey;
        factor = new Setting.Slider("factor", "Facteur de zoom", 2.0D, 6.0D, 4.0D);
        settings.add(factor);
    }

    public void tick() {
        keyHeld = zoomKey.isDown();
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
        keyHeld = false;
    }

    public static boolean shouldZoom() {
        Module base = ModuleManager.get("zoom");
        if (!(base instanceof ZoomModule zoom) || !zoom.enabled) {
            return false;
        }
        Minecraft client = Minecraft.getInstance();
        return client != null && !UtiliKitClient.isScreenOpen(client) && zoom.keyHeld;
    }

    public static float applyZoom(float fov) {
        Module base = ModuleManager.get("zoom");
        if (base instanceof ZoomModule zoom) {
            return (float) (fov / zoom.factor.value);
        }
        return fov;
    }
}
