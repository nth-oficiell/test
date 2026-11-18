package dev.utilikit.client.modules;

import dev.utilikit.client.config.ConfigManager;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ModuleManager {
    private static final Map<String, Module> MODULES = new LinkedHashMap<>();

    private ModuleManager() {
    }

    public static void register(Module module) {
        MODULES.put(module.id, module);
    }

    public static Module get(String id) {
        return MODULES.get(id);
    }

    public static Collection<Module> all() {
        return MODULES.values();
    }

    public static void load() {
        ConfigManager.load(MODULES);
    }

    public static void save() {
        ConfigManager.save(MODULES);
    }

    public static void applyLoadedStates() {
        for (Module module : MODULES.values()) {
            if (module.enabled) {
                module.onEnable();
            }
        }
    }

    public static void disableAll() {
        for (Module module : MODULES.values()) {
            if (module.enabled) {
                module.enabled = false;
                module.onDisable();
            }
        }
    }
}
