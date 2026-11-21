package dev.utilikit.client.modules;

import dev.utilikit.client.config.ConfigManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
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

    public static List<Module> ordered() {
        List<Module> list = new ArrayList<>(MODULES.values());
        list.sort(Comparator.comparingInt((Module m) -> m.favorite ? 0 : 1));
        return list;
    }

    public static List<ModuleCategory> categories() {
        List<ModuleCategory> used = new ArrayList<>();
        for (ModuleCategory category : ModuleCategory.values()) {
            for (Module module : MODULES.values()) {
                if (module.category == category) {
                    used.add(category);
                    break;
                }
            }
        }
        return used;
    }

    public static void load() {
        ConfigManager.load(MODULES.values());
    }

    public static void save() {
        ConfigManager.save(MODULES.values());
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

    public static void syncStates(Map<String, Boolean> before) {
        for (Module module : MODULES.values()) {
            boolean target = module.enabled;
            Boolean was = before.get(module.id);
            boolean changed = was == null || was != target;
            if (changed) {
                module.enabled = !target;
                module.setEnabled(target);
            } else if (target) {
                module.onSettingChanged();
            }
        }
    }
}
