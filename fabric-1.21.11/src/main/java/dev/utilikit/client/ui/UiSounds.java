package dev.utilikit.client.ui;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.impl.UiSoundsModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public final class UiSounds {
    private UiSounds() {
    }

    private static boolean active() {
        Module module = ModuleManager.get(UiSoundsModule.ID);
        return module == null || module.enabled;
    }

    public static void open() {
        play(0.9F);
    }

    public static void click() {
        play(1.0F);
    }

    public static void toggle() {
        play(1.25F);
    }

    private static void play(float pitch) {
        if (!active()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.getSoundManager() != null) {
            client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, pitch));
        }
    }
}
