package dev.utilikit.client;

import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.impl.FullBrightModule;
import dev.utilikit.client.modules.impl.HudInfoModule;
import dev.utilikit.client.modules.impl.ZoomModule;
import dev.utilikit.client.ui.UtiliKitScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtiliKitClient implements ClientModInitializer {
    public static final String KEY_CATEGORY = "key.categories.utilikit";
    private static final Logger LOGGER = LoggerFactory.getLogger("utilikit");

    private static KeyBinding menuKey;
    private static boolean initialStatesApplied = false;

    @Override
    public void onInitializeClient() {
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.utilikit.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, KEY_CATEGORY));

        KeyBinding zoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.utilikit.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, KEY_CATEGORY));

        ModuleManager.register(new FullBrightModule());
        ModuleManager.register(new ZoomModule(zoomKey));
        ModuleManager.register(new HudInfoModule());
        ModuleManager.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!initialStatesApplied) {
                initialStatesApplied = true;
                ModuleManager.applyLoadedStates();
            }
            ZoomModule zoom = (ZoomModule) ModuleManager.get("zoom");
            if (zoom != null) {
                zoom.tick();
            }
            while (menuKey.wasPressed()) {
                client.setScreen(new UtiliKitScreen());
            }
        });

        HudRenderCallback.EVENT.register((context, tickDelta) -> HudInfoModule.renderHud(context, MinecraftClient.getInstance()));

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ModuleManager.disableAll());

        LOGGER.info("[UtiliKit] Client initialisé — {} module(s) chargé(s).", ModuleManager.all().size());
    }
}
