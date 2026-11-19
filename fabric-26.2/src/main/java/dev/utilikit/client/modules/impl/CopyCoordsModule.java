package dev.utilikit.client.modules.impl;

import com.mojang.blaze3d.platform.InputConstants;

import dev.utilikit.client.UtiliKitClient;
import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class CopyCoordsModule extends Module {
    private final KeyMapping copyKey;

    public CopyCoordsModule(KeyMapping copyKey) {
        super("copycoords", "Copier Coordonnées", "Une touche (O par défaut) copie tes coordonnées dans le presse-papiers.", ModuleCategory.PRATIQUE);
        this.copyKey = copyKey;
    }

    public static KeyMapping registerKey() {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.utilikit.copycoords", InputConstants.Type.KEYBOARD, GLFW.GLFW_KEY_O, UtiliKitClient.KEY_CATEGORY));
    }

    public void registerTick() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (copyKey.consumeClick()) {
                if (enabled) {
                    execute(client);
                }
            }
        });
    }

    private void execute(Minecraft client) {
        if (client.player == null || client.level == null) {
            return;
        }
        var pos = client.player.blockPosition();
        String dimension = client.level.dimension().location().getPath();
        String coords = pos.getX() + " " + pos.getY() + " " + pos.getZ();
        client.keyboardHandler.setClipboard(coords);
        client.gui.getChat().addMessage(Component.literal("[UtiliKit] [" + dimension + "] ")
                .withStyle(style -> style.withColor(0x55FFAA))
                .append(Component.literal("Coordonnées copiées : " + coords)));
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
