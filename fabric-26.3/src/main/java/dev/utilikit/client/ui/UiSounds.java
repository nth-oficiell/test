package dev.utilikit.client.ui;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.impl.UiSoundsModule;
import net.minecraft.client.Minecraft;

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
        try {
            Minecraft client = Minecraft.getInstance();
            if (client == null) {
                return;
            }
            Object soundManager = client.getClass().getMethod("getSoundManager").invoke(client);
            Object holder = Class.forName("net.minecraft.sounds.SoundEvents").getField("UI_BUTTON_CLICK").get(null);
            Object soundEvent = holder.getClass().getMethod("value").invoke(holder);
            Class<?> soundInstance = Class.forName("net.minecraft.client.resources.sounds.SoundInstance");
            Object instance = Class.forName("net.minecraft.client.resources.sounds.SimpleSoundInstance")
                    .getMethod("forUI", Class.forName("net.minecraft.sounds.SoundEvent"), float.class)
                    .invoke(null, soundEvent, pitch);
            soundManager.getClass().getMethod("play", soundInstance).invoke(soundManager, instance);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }
    }
}
