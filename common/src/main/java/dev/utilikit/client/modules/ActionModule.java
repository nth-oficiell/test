package dev.utilikit.client.modules;

public abstract class ActionModule extends Module {
    protected ActionModule(String id, String name, String description, ModuleCategory category) {
        super(id, name, description, category);
    }

    protected abstract void execute();

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
