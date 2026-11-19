package dev.utilikit.client.modules;

import java.util.HashMap;
import java.util.Map;

public final class KeybindEngine {
    private static final Map<String, Boolean> PRESSED = new HashMap<>();
    private static Module captureTarget = null;
    private static final int CANCEL_GLFW = 256;
    private static final int CANCEL_SDL = 41;

    private KeybindEngine() {
    }

    public static void beginCapture(Module module) {
        captureTarget = module;
    }

    public static void cancelCapture() {
        captureTarget = null;
    }

    public static boolean isCapturing(Module module) {
        return captureTarget != null && captureTarget == module;
    }

    public static boolean isCapturing() {
        return captureTarget != null;
    }

    public static void pollCapture(int keyCode, boolean cancel) {
        Module target = captureTarget;
        if (target == null) {
            return;
        }
        captureTarget = null;
        if (cancel) {
            return;
        }
        target.keyCode = keyCode;
        ModuleManager.save();
    }

    public static void poll(Module module, boolean pressed) {
        if (module == null || module.keyCode <= 0) {
            return;
        }
        Boolean previous = PRESSED.put(module.id, pressed);
        if (pressed && !Boolean.TRUE.equals(previous)) {
            if (module instanceof ActionModule action) {
                action.execute();
            } else {
                module.setEnabled(!module.enabled);
            }
        }
    }

    public static void clear(String moduleId) {
        PRESSED.remove(moduleId);
    }
}
