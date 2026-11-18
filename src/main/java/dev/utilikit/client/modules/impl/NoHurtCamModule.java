package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.ModuleManager;

public class NoHurtCamModule extends Module {
    public NoHurtCamModule() {
        super("nohurtcam", "No Hurt Cam", "Désactive le tremblement de la caméra quand tu prends des dégâts.", ModuleCategory.VISUEL);
    }

    public static boolean shouldCancelHurtCam() {
        Module base = ModuleManager.get("nohurtcam");
        return base != null && base.enabled;
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
