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

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("utilikit.json");
    }

    public static void load(Collection<Module> modules) {
        Path file = path();
        if (!Files.exists(file)) {
            return;
        }
        try {
            JsonObject root = GSON.fromJson(Files.readString(file), JsonObject.class);
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
                JsonObject settingsJson = moduleJson.getAsJsonObject("settings");
                if (settingsJson != null) {
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
                module.enabled = enabled;
            }
        } catch (Exception e) {
            LOGGER.error("Impossible de charger la config UtiliKit", e);
        }
    }

    public static void save(Collection<Module> modules) {
        Path file = path();
        try {
            JsonObject root = new JsonObject();
            JsonObject modulesJson = new JsonObject();
            for (Module module : modules) {
                JsonObject moduleJson = new JsonObject();
                moduleJson.addProperty("enabled", module.enabled);
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
            Files.writeString(file, GSON.toJson(root));
        } catch (Exception e) {
            LOGGER.error("Impossible de sauvegarder la config UtiliKit", e);
        }
    }
}
