package dev.utilikit.client.modules.extra;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Gradients {
    private static final int DEFAULT_1 = 0xFF7A3CFF;
    private static final int DEFAULT_2 = 0xFF00FFC8;
    private static final int STEP = 3;
    private static final int MAX_BANDS = 64;

    private Gradients() {
    }

    public static boolean active() {
        Module module = ModuleManager.get("degrades");
        return module != null && module.enabled;
    }

    public static int alpha() {
        Module module = ModuleManager.get("degrades");
        if (module == null || !module.enabled) {
            return 0;
        }
        Setting.Slider slider = module.slider("alpha");
        int value = slider == null ? 60 : (int) Math.round(slider.value);
        return Math.max(0, Math.min(100, value)) * 255 / 100;
    }

    public static int direction() {
        Module module = ModuleManager.get("degrades");
        if (module == null || !module.enabled) {
            return 0;
        }
        Setting.Slider slider = module.slider("dir");
        int value = slider == null ? 0 : (int) Math.round(slider.value);
        return Math.max(0, Math.min(2, value));
    }

    public static int color1() {
        return parse(text("c1"), DEFAULT_1);
    }

    public static int color2() {
        return parse(text("c2"), DEFAULT_2);
    }

    public static int[][] bands(int x1, int y1, int x2, int y2) {
        List<int[]> out = new ArrayList<>();
        int alpha = alpha();
        if (alpha <= 0 || x2 <= x1 || y2 <= y1) {
            return new int[0][];
        }
        int dir = direction();
        int from = withAlpha(color1(), alpha);
        int to = withAlpha(color2(), alpha);
        if (dir == 1) {
            horizontal(x1, y1, x2, y2, from, to, out);
        } else if (dir == 0) {
            vertical(x1, y1, x2, y2, from, to, out);
        } else {
            vertical(x1, y1, x2, y2, from, to, out);
            int overlayAlpha = alpha * 55 / 100;
            if (overlayAlpha > 0) {
                horizontal(x1, y1, x2, y2, withAlpha(to, overlayAlpha), to & 0x00FFFFFF, out);
            }
        }
        return out.toArray(new int[0][]);
    }

    private static void vertical(int x1, int y1, int x2, int y2, int from, int to, List<int[]> out) {
        int height = y2 - y1;
        int step = Math.max(STEP, height / MAX_BANDS);
        for (int y = y1; y < y2; y += step) {
            int end = Math.min(y + step, y2);
            double t = (y + step / 2.0D - y1) / height;
            out.add(new int[]{x1, y, x2, end, lerp(from, to, t)});
        }
    }

    private static void horizontal(int x1, int y1, int x2, int y2, int from, int to, List<int[]> out) {
        int width = x2 - x1;
        int step = Math.max(STEP, width / MAX_BANDS);
        for (int x = x1; x < x2; x += step) {
            int end = Math.min(x + step, x2);
            double t = (x + step / 2.0D - x1) / width;
            out.add(new int[]{x, y1, end, y2, lerp(from, to, t)});
        }
    }

    private static int lerp(int from, int to, double t) {
        t = Math.max(0.0D, Math.min(1.0D, t));
        int a = (int) Math.round(((from >>> 24) & 0xFF) + ((((to >>> 24) & 0xFF) - ((from >>> 24) & 0xFF))) * t);
        int r = (int) Math.round(((from >>> 16) & 0xFF) + ((((to >>> 16) & 0xFF) - ((from >>> 16) & 0xFF))) * t);
        int g = (int) Math.round(((from >>> 8) & 0xFF) + ((((to >>> 8) & 0xFF) - ((from >>> 8) & 0xFF))) * t);
        int b = (int) Math.round((from & 0xFF) + (((to & 0xFF) - (from & 0xFF))) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int withAlpha(int rgb, int alpha) {
        return (alpha << 24) | (rgb & 0x00FFFFFF);
    }

    private static String text(String id) {
        Module module = ModuleManager.get("degrades");
        if (module == null) {
            return null;
        }
        Setting.Text text = module.text(id);
        return text == null ? null : text.value;
    }

    public static int parse(String raw, int fallback) {
        if (raw == null) {
            return fallback;
        }
        String s = raw.trim().toLowerCase(Locale.ROOT);
        if (s.startsWith("#")) {
            s = s.substring(1);
        } else if (s.startsWith("0x")) {
            s = s.substring(2);
        }
        try {
            if (s.length() == 3) {
                long value = Long.parseLong(s, 16);
                int r = (int) ((value >> 8) & 0xF) * 17;
                int g = (int) ((value >> 4) & 0xF) * 17;
                int b = (int) (value & 0xF) * 17;
                return 0xFF000000 | (r << 16) | (g << 8) | b;
            }
            if (s.length() == 6) {
                return 0xFF000000 | (int) Long.parseLong(s, 16);
            }
            if (s.length() == 8) {
                return (int) Long.parseLong(s, 16);
            }
        } catch (NumberFormatException ignored) {
        }
        return fallback;
    }
}
