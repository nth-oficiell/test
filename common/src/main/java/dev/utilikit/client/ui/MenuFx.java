package dev.utilikit.client.ui;

import java.util.HashMap;
import java.util.Map;

public final class MenuFx {
    private static final long DURATION = 260_000_000L;
    private static final Map<Object, Float> fades = new HashMap<>();
    private static long openedAt;
    private static long lastFrame;
    private static float progress = 1.0F;

    private MenuFx() {
    }

    public static void onOpen() {
        openedAt = System.nanoTime();
        lastFrame = openedAt;
        progress = 0.0F;
        fades.clear();
    }

    public static void tick() {
        long now = System.nanoTime();
        if (progress < 1.0F) {
            float t = (float) ((double) (now - openedAt) / (double) DURATION);
            progress = t >= 1.0F ? 1.0F : t;
        }
        lastFrame = now;
    }

    public static float easedOpen() {
        float t = progress;
        if (t >= 1.0F) {
            return 1.0F;
        }
        float inv = 1.0F - t;
        return 1.0F - inv * inv * inv;
    }

    public static int slideOffset(int maxPixels) {
        float eased = easedOpen();
        if (eased >= 1.0F) {
            return 0;
        }
        return (int) ((1.0D - eased) * (double) maxPixels);
    }

    public static float approach(Object key, float target) {
        float dt = Math.min(0.1F, (System.nanoTime() - lastFrame) / 1.0E9F);
        if (dt <= 0.0F) {
            dt = 0.016F;
        }
        float current = fades.getOrDefault(key, 0.0F);
        float step = 9.0F * dt;
        if (current < target) {
            current = Math.min(target, current + step);
        } else if (current > target) {
            current = Math.max(target, current - step);
        }
        if (current <= 0.001F && target <= 0.001F) {
            fades.remove(key);
        } else {
            fades.put(key, current);
        }
        return current;
    }
}
