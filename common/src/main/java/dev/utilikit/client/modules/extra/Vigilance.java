package dev.utilikit.client.modules.extra;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;

import java.util.Locale;

public final class Vigilance {
    private static final long ALERT_COOLDOWN = 15000L;
    private static final long FLASH_DURATION = 320L;
    private static long lastAlert;
    private static long flashUntil;

    private Vigilance() {
    }

    public static void tick(Object game) {
        Module module = ModuleManager.get("vigilance");
        if (module == null || !module.enabled) {
            flashUntil = 0L;
            return;
        }
        try {
            Object player = game.getClass().getField("player").get(game);
            if (player == null) {
                flashUntil = 0L;
                return;
            }
            Double health = doubleByName(player, "getHealth");
            if (health == null) {
                flashUntil = 0L;
                return;
            }
            Setting.Slider threshold = module.slider("seuil");
            double limit = threshold == null ? 6.0D
                    : Math.max(threshold.min, Math.min(threshold.max, threshold.value));
            long now = System.currentTimeMillis();
            if (health > limit) {
                flashUntil = 0L;
                return;
            }
            flashUntil = now + FLASH_DURATION;
            if (now - lastAlert >= ALERT_COOLDOWN) {
                lastAlert = now;
                Notifier.push("\u26a0 Alerte vie : " + String.format(Locale.ROOT, "%.1f", health));
            }
        } catch (Throwable ignored) {
        }
    }

    public static boolean flashActive() {
        return System.currentTimeMillis() < flashUntil;
    }

    private static Double doubleByName(Object owner, String... methodNames) {
        for (String methodName : methodNames) {
            try {
                return ((Number) owner.getClass().getMethod(methodName).invoke(owner)).doubleValue();
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
