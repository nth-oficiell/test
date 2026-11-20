package dev.utilikit.client;

import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.impl.AutoSprintModule;
import dev.utilikit.client.modules.impl.UiSoundsModule;
import dev.utilikit.client.modules.impl.CopyCoordsModule;
import dev.utilikit.client.modules.impl.DurabilityModule;
import dev.utilikit.client.modules.impl.FullBrightModule;
import dev.utilikit.client.modules.impl.HudInfoModule;
import dev.utilikit.client.modules.impl.KeystrokesModule;
import dev.utilikit.client.modules.impl.MaxFpsModule;
import dev.utilikit.client.modules.impl.NoHurtCamModule;
import dev.utilikit.client.modules.impl.PingHudModule;
import dev.utilikit.client.modules.impl.ZoomModule;
import dev.utilikit.client.ClientIntegrations;
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

        ZoomModule zoom = new ZoomModule(zoomKey);
        CopyCoordsModule copyCoords = new CopyCoordsModule(CopyCoordsModule.registerKey());

        ModuleManager.register(new FullBrightModule());
        ModuleManager.register(zoom);
        ModuleManager.register(new NoHurtCamModule());
        ModuleManager.register(new HudInfoModule());
        ModuleManager.register(new KeystrokesModule());
        ModuleManager.register(copyCoords);
        ModuleManager.register(new AutoSprintModule());
        ModuleManager.register(new PingHudModule());
        ModuleManager.register(new DurabilityModule());
        ModuleManager.register(new MaxFpsModule());
        ModuleManager.register(new UiSoundsModule());
        ClientIntegrations.register();
        ModuleManager.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!initialStatesApplied) {
                initialStatesApplied = true;
                ModuleManager.applyLoadedStates();
            }
            if (AutoUpdater.hasNotes()) {
                for (String updateNote : AutoUpdater.takeNotes()) {
                    dev.utilikit.client.modules.extra.Notifier.push(updateNote, 12000L);
                }
            }
            zoom.tick();
            ClientIntegrations.tick(client);
            ClientIntegrations.pollKeybinds(client, client.currentScreen != null);
            AutoSprintModule.tick();
            while (menuKey.wasPressed()) {
                client.setScreen(new UtiliKitScreen());
            }
        });

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            HudInfoModule.renderHud(context, MinecraftClient.getInstance());
            KeystrokesModule.renderHud(context, MinecraftClient.getInstance());
            PingHudModule.renderHud(context, MinecraftClient.getInstance());
            DurabilityModule.renderHud(context, MinecraftClient.getInstance());
        });

        copyCoords.registerTick();

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ModuleManager.disableAll());

        AutoUpdater.start();
        LOGGER.info("[NTH Utils] Client initialisé — {} module(s) chargé(s).", ModuleManager.all().size());
    }
}
