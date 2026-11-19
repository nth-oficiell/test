package dev.utilikit.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.Setting;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

public final class ConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("utilikit");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ConfigManager() {
    }

    public static Gson gson() {
        return GSON;
    }

    public static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("utilikit.json");
    }

    public static JsonObject toJson(Collection<Module> modules) {
        JsonObject root = new JsonObject();
        JsonObject modulesJson = new JsonObject();
        for (Module module : modules) {
            JsonObject moduleJson = new JsonObject();
            moduleJson.addProperty("enabled", module.enabled);
            moduleJson.addProperty("key", module.keyCode);
            JsonObject settingsJson = new JsonObject();
            for (Setting setting : module.settings) {
                if (setting instanceof Setting.Toggle toggle) {
                    settingsJson.addProperty(setting.id, toggle.value);
                } else if (setting instanceof Setting.Slider slider) {
                    settingsJson.addProperty(setting.id, slider.value);
                }
            }
            moduleJson.add("settings", settingsJson);
            modulesJson.add(module.id, moduleJson);
        }
        root.add("modules", modulesJson);
        return root;
    }

    public static void applyJson(JsonObject root, Collection<Module> modules) {
        if (root == null) {
            return;
        }
        JsonObject modulesJson = root.getAsJsonObject("modules");
        if (modulesJson == null) {
            return;
        }
        for (Module module : modules) {
            JsonObject moduleJson = modulesJson.getAsJsonObject(module.id);
            if (moduleJson == null) {
                continue;
            }
            boolean enabled = moduleJson.has("enabled") && moduleJson.get("enabled").getAsBoolean();
            module.enabled = enabled;
            if (moduleJson.has("key") && moduleJson.get("key").isJsonPrimitive()) {
                module.keyCode = moduleJson.get("key").getAsInt();
            }
            JsonObject settingsJson = moduleJson.getAsJsonObject("settings");
            if (settingsJson == null) {
                continue;
            }
            for (Setting setting : module.settings) {
                JsonElement element = settingsJson.get(setting.id);
                if (element == null) {
                    continue;
                }
                if (setting instanceof Setting.Toggle toggle && element.isJsonPrimitive()) {
                    toggle.value = element.getAsBoolean();
                } else if (setting instanceof Setting.Slider slider && element.isJsonPrimitive()) {
                    slider.value = Math.max(slider.min, Math.min(slider.max, element.getAsDouble()));
                }
            }
        }
    }

    public static void save(Collection<Module> modules) {
        try {
            Files.writeString(path(), GSON.toJson(toJson(modules)));
        } catch (Exception e) {
            LOGGER.error("Impossible de sauvegarder la config NTH Utils", e);
        }
    }

    public static void load(Collection<Module> modules) {
        Path file = path();
        if (!Files.exists(file)) {
            return;
        }
        try {
            applyJson(GSON.fromJson(Files.readString(file), JsonObject.class), modules);
        } catch (Exception e) {
            LOGGER.error("Impossible de charger la config NTH Utils", e);
        }
    }
}
