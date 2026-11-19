package dev.utilikit.client.modules.extra;

import com.google.gson.JsonObject;
import dev.utilikit.client.config.ConfigManager;
import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleManager;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class HudDrag {
    private static final Logger LOGGER = LoggerFactory.getLogger("utilikit");
    private static int dragX;
    private static int dragY;
    private static Double lastX;
    private static Double lastY;
    private static String dragId;
    private static final LinkedHashMap<String, int[]> RECTS = new LinkedHashMap<>();
    private static final Map<String, int[]> LINES = new HashMap<>();

    private HudDrag() {
    }

    public static int[] offsets() {
        return new int[] {dragX, dragY};
    }

    public static void beginFrame() {
        RECTS.clear();
    }

    public static void track(String id, int x, int y, int w, int h) {
        if (id != null && id.length() < 64 && RECTS.size() < 256) {
            RECTS.put(id, new int[] {x, y, w, h});
        }
    }

    public static int[] lineOffset(String id) {
        int[] offset = LINES.get(id);
        return offset == null ? new int[] {0, 0} : offset;
    }

    private static String hoveredLine(double mx, double my) {
        for (Map.Entry<String, int[]> entry : RECTS.entrySet()) {
            int[] rect = entry.getValue();
            if (mx >= rect[0] && mx <= rect[0] + rect[2]
                    && my >= rect[1] && my <= rect[1] + rect[3]) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void tick(Object game) {
        Module module = ModuleManager.get("hudedit");
        boolean held = module != null && module.enabled && module.keyCode > 0
                && dev.utilikit.client.modules.KeybindEngine.isHeld("hudedit");
        if (!held) {
            if (lastX != null) {
                lastX = null;
                lastY = null;
                dragId = null;
                save();
            }
            return;
        }
        double[] mouse = mouseScaled(game);
        if (mouse == null) {
            return;
        }
        if (lastX == null) {
            lastX = mouse[0];
            lastY = mouse[1];
            dragId = hoveredLine(mouse[0], mouse[1]);
            return;
        }
        int dx = (int) Math.round(mouse[0] - lastX);
        int dy = (int) Math.round(mouse[1] - lastY);
        lastX = mouse[0];
        lastY = mouse[1];
        if (dragId == null) {
            dragX = clamp(dragX + dx, 0, 600);
            dragY = clamp(dragY + dy, 0, 800);
        } else {
            int[] offset = LINES.get(dragId);
            int ox = offset == null ? 0 : offset[0];
            int oy = offset == null ? 0 : offset[1];
            LINES.put(dragId, new int[] {clamp(ox + dx, -600, 600), clamp(oy + dy, -600, 800)});
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double[] mouseScaled(Object game) {
        try {
            Object mouse = null;
            for (String fieldName : new String[] {"mouse", "mouseHandler"}) {
                try {
                    mouse = game.getClass().getField(fieldName).get(game);
                    if (mouse != null) {
                        break;
                    }
                } catch (Exception ignored) {
                }
            }
            if (mouse == null) {
                return null;
            }
            Double px = doubleMethod(mouse, "getX", "xpos", "xPos");
            Double py = doubleMethod(mouse, "getY", "ypos", "yPos");
            if (px == null || py == null) {
                return null;
            }
            Object window = game.getClass().getField("getWindow").get(game);
            Double scale = doubleMethod(window, "getScaleFactor", "getGuiScale");
            if (scale == null || scale <= 0.0D) {
                scale = 1.0D;
            }
            return new double[] {px / scale, py / scale};
        } catch (Exception e) {
            return null;
        }
    }

    private static Double doubleMethod(Object owner, String... methodNames) {
        for (String methodName : methodNames) {
            try {
                return ((Number) owner.getClass().getMethod(methodName).invoke(owner)).doubleValue();
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("utilikit-hud.json");
    }

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("x", dragX);
            root.addProperty("y", dragY);
            JsonObject lines = new JsonObject();
            for (Map.Entry<String, int[]> entry : LINES.entrySet()) {
                JsonObject offset = new JsonObject();
                offset.addProperty("x", entry.getValue()[0]);
                offset.addProperty("y", entry.getValue()[1]);
                lines.add(entry.getKey(), offset);
            }
            root.add("lines", lines);
            Files.writeString(file(), ConfigManager.gson().toJson(root));
        } catch (Exception e) {
            LOGGER.error("Impossible de sauvegarder la position du HUD NTH Utils", e);
        }
    }

    public static void load() {
        Path file = file();
        if (!Files.exists(file)) {
            return;
        }
        try {
            JsonObject root = ConfigManager.gson().fromJson(Files.readString(file), JsonObject.class);
            if (root == null) {
                return;
            }
            dragX = root.has("x") ? clamp(root.get("x").getAsInt(), 0, 600) : 0;
            dragY = root.has("y") ? clamp(root.get("y").getAsInt(), 0, 800) : 0;
            if (root.has("lines") && root.get("lines").isJsonObject()) {
                JsonObject lines = root.getAsJsonObject("lines");
                LINES.clear();
                for (String key : lines.keySet()) {
                    if (!lines.get(key).isJsonObject()) {
                        continue;
                    }
                    JsonObject offset = lines.getAsJsonObject(key);
                    LINES.put(key, new int[] {
                            clamp(offset.get("x").getAsInt(), -600, 600),
                            clamp(offset.get("y").getAsInt(), -600, 800),
                    });
                }
            }
        } catch (Exception e) {
            LOGGER.error("Impossible de charger la position du HUD NTH Utils", e);
        }
    }
}
