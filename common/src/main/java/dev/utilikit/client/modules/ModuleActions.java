package dev.utilikit.client.modules;

public final class ModuleActions {
    private ModuleActions() {
    }

    public static void activate(Module module) {
        if (module instanceof ActionModule action) {
            action.execute();
        } else {
            module.setEnabled(!module.enabled);
        }
    }
}
