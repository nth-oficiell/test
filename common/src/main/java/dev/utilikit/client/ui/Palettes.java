package dev.utilikit.client.ui;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;

public final class Palettes {
    public static final String[] NAMES = {
            "NTH", "Oc\u00e9an", "Coucher", "Toxic", "Sang", "Or", "Menthe", "Givre"
    };
    private static final int[][] COLORS = {
            {0xFF7A3CFF, 0xFF00FFC8, 0xFFB44DFF},
            {0xFF2E7CFF, 0xFF37F5D8, 0xFF7AB8FF},
            {0xFFFF5E3A, 0xFFFFB84D, 0xFFFF3A7A},
            {0xFF64FF3A, 0xFF00FFA3, 0xFFBFFF3A},
            {0xFFFF3A3A, 0xFFFF6B4D, 0xFFFF3A93},
            {0xFFFFC93A, 0xFFFFA63A, 0xFFFFE88A},
            {0xFF3AFFA0, 0xFF7AFFFF, 0xFFB4FF6B},
            {0xFF9ADCFF, 0xFFE0F6FF, 0xFF6BC8FF}
    };

    private Palettes() {
    }

    public static int index() {
        Module module = ModuleManager.get("degrades");
        Setting.Slider slider = module == null ? null : module.slider("palette");
        if (slider == null) {
            return 0;
        }
        return Math.max(0, Math.min(NAMES.length - 1, (int) Math.round(slider.value)));
    }

    public static int select(int value) {
        int clamped = Math.max(0, Math.min(NAMES.length - 1, value));
        Module module = ModuleManager.get("degrades");
        Setting.Slider slider = module == null ? null : module.slider("palette");
        if (slider != null) {
            slider.value = clamped;
        }
        return clamped;
    }

    public static int[] colors() {
        return COLORS[index()];
    }

    public static int accent() {
        int[] colors = COLORS[index()];
        double phase = (System.nanoTime() / 6_000_000_000.0D) % 1.0D * colors.length;
        int i = (int) phase;
        float t = (float) (phase - i);
        return lerp(colors[i % colors.length], colors[(i + 1) % colors.length], t);
    }

    public static int accentAlpha(int alpha) {
        return (Math.max(0, Math.min(255, alpha)) << 24) | (accent() & 0x00FFFFFF);
    }

    private static int lerp(int from, int to, float t) {
        t = Math.max(0.0F, Math.min(1.0F, t));
        int a = (int) Math.round(((from >>> 24) & 0xFF) + ((((to >>> 24) & 0xFF) - ((from >>> 24) & 0xFF))) * t);
        int r = (int) Math.round(((from >>> 16) & 0xFF) + ((((to >>> 16) & 0xFF) - ((from >>> 16) & 0xFF))) * t);
        int g = (int) Math.round(((from >>> 8) & 0xFF) + ((((to >>> 8) & 0xFF) - ((from >>> 8) & 0xFF))) * t);
        int b = (int) Math.round((from & 0xFF) + (((to & 0xFF) - (from & 0xFF)) * t));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
