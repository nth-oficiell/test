package dev.utilikit.client.modules;

import java.util.HashMap;
import java.util.Map;

public final class KeybindEngine {
    private static final Map<String, Boolean> PRESSED = new HashMap<>();

    private KeybindEngine() {
    }

    public static void poll(Module module, boolean pressed) {
        if (module == null || module.keyCode <= 0) {
            return;
        }
        Boolean previous = PRESSED.put(module.id, pressed);
        if (pressed && !Boolean.TRUE.equals(previous)) {
            module.setEnabled(!module.enabled);
        }
    }

    public static void clear(String moduleId) {
        PRESSED.remove(moduleId);
    }
}
