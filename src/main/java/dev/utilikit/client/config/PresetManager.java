package dev.utilikit.client.config;

import com.google.gson.JsonObject;
import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleManager;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class PresetManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("utilikit");

    private PresetManager() {
    }

    public static Path dir() {
        return FabricLoader.getInstance().getConfigDir()
                .resolve("utilikit")
                .resolve("presets");
    }

    public static List<String> names() {
        try (Stream<Path> files = Files.list(dir())) {
            List<String> names = new ArrayList<>();
            files.filter(p -> p.getFileName().toString().endsWith(".json"))
                    .forEach(p -> {
                        String name = p.getFileName().toString();
                        names.add(name.substring(0, name.length() - 5));
                    });
            names.sort(String.CASE_INSENSITIVE_ORDER);
            return names;
        } catch (IOException e) {
            return List.of();
        }
    }

    public static String sanitize(String name) {
        if (name == null) {
            return null;
        }
        String clean = name.trim().replaceAll("[^\\w\\- àâäéèêëîïôöùûüçÀÂÄÉÈÊËÎÏÔÖÙÛÜÇ]", "_");
        clean = clean.replaceAll("_+", "_");
        return clean.isBlank() ? null : clean;
    }

    private static Path file(String name) {
        String clean = sanitize(name);
        if (clean == null) {
            return null;
        }
        try {
            Files.createDirectories(dir());
        } catch (IOException e) {
            return null;
        }
        return dir().resolve(clean + ".json");
    }

    public static boolean save(String name) {
        Path file = file(name);
        if (file == null) {
            return false;
        }
        try {
            Files.writeString(file, ConfigManager.gson().toJson(ConfigManager.toJson(ModuleManager.all())));
            return true;
        } catch (IOException e) {
            LOGGER.error("Impossible de sauvegarder le preset", e);
            return false;
        }
    }

    public static boolean apply(String name) {
        Path file = file(name);
        if (file == null || !Files.exists(file)) {
            return false;
        }
        try {
            Map<String, Boolean> before = new HashMap<>();
            for (Module module : ModuleManager.all()) {
                before.put(module.id, module.enabled);
            }
            JsonObject root = ConfigManager.gson().fromJson(Files.readString(file), JsonObject.class);
            ConfigManager.applyJson(root, ModuleManager.all());
            ModuleManager.syncStates(before);
            ModuleManager.save();
            return true;
        } catch (Exception e) {
            LOGGER.error("Impossible d'appliquer le preset", e);
            return false;
        }
    }

    public static boolean delete(String name) {
        Path file = file(name);
        if (file == null || !Files.exists(file)) {
            return false;
        }
        try {
            Files.delete(file);
            return true;
        } catch (IOException e) {
            LOGGER.error("Impossible de supprimer le preset", e);
            return false;
        }
    }

    public static String exportCurrent() {
        try {
            return ConfigManager.gson().toJson(ConfigManager.toJson(ModuleManager.all()));
        } catch (Exception e) {
            LOGGER.error("Export impossible", e);
            return null;
        }
    }

    public static boolean importJson(String name, String json) {
        try {
            JsonObject root = ConfigManager.gson().fromJson(json, JsonObject.class);
            if (root == null || !root.has("modules")) {
                return false;
            }
            Path file = file(name == null || name.isBlank()
                    ? "import-" + System.currentTimeMillis()
                    : name);
            if (file == null) {
                return false;
            }
            Files.writeString(file, ConfigManager.gson().toJson(root));
            return true;
        } catch (Exception e) {
            LOGGER.error("Import impossible", e);
            return false;
        }
    }
}
