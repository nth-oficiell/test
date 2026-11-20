package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;

public class NoHurtCamModule extends Module {
    public NoHurtCamModule() {
        super("nohurtcam", "No Hurt Cam", "Désactive le tremblement de caméra quand tu prends des dégâts. Utilise le réglage vanilla : disponible sur Minecraft 1.21.2+.", ModuleCategory.VISUEL);
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
