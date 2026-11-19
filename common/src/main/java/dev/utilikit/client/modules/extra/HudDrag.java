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

public final class HudDrag {
    private static final Logger LOGGER = LoggerFactory.getLogger("utilikit");
    private static int dragX;
    private static int dragY;
    private static Double lastX;
    private static Double lastY;

    private HudDrag() {
    }

    public static int[] offsets() {
        return new int[] {dragX, dragY};
    }

    public static void tick(Object game) {
        Module module = ModuleManager.get("hudedit");
        boolean held = module != null && module.enabled && module.keyCode > 0
                && dev.utilikit.client.modules.KeybindEngine.isHeld("hudedit");
        if (!held) {
            if (lastX != null) {
                lastX = null;
                lastY = null;
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
            return;
        }
        dragX = clamp(dragX + (int) Math.round(mouse[0] - lastX), 0, 600);
        dragY = clamp(dragY + (int) Math.round(mouse[1] - lastY), 0, 800);
        lastX = mouse[0];
        lastY = mouse[1];
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
        } catch (Exception e) {
            LOGGER.error("Impossible de charger la position du HUD NTH Utils", e);
        }
    }
}
