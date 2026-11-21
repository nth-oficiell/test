package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;

public class UiSoundsModule extends Module {
    public static final String ID = "uisounds";

    public UiSoundsModule() {
        super(ID, "Sons d'interface", "Joue des sons discrets à l'ouverture du menu, au changement de catégorie et à l'activation d'un module.", ModuleCategory.PRATIQUE);
        enabled = true;
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
