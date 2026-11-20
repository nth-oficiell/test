package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.ModuleManager;
import net.minecraft.client.MinecraftClient;

public class AutoSprintModule extends Module {
    public AutoSprintModule() {
        super("autosprint", "Auto Sprint", "Sprint automatique dès que tu marches vers l'avant.", ModuleCategory.JEU);
    }

    public static void tick() {
        Module base = ModuleManager.get("autosprint");
        if (!(base instanceof AutoSprintModule) || !base.enabled) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options == null) {
            return;
        }
        if (client.options.forwardKey.isPressed() && !client.player.isSprinting()) {
            client.player.setSprinting(true);
        }
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
