package dev.utilikit.client.modules;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public final String id;
    public final String name;
    public final String description;
    public final ModuleCategory category;

    public boolean enabled;
    public int keyCode = -1;
    public final List<Setting> settings = new ArrayList<>();

    protected Module(String id, String name, String description, ModuleCategory category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public void setEnabled(boolean value) {
        if (enabled == value) {
            return;
        }
        enabled = value;
        if (value) {
            onEnable();
        } else {
            onDisable();
        }
        ModuleManager.save();
    }

    public Setting.Toggle toggle(String id) {
        for (Setting setting : settings) {
            if (setting.id.equals(id) && setting instanceof Setting.Toggle toggle) {
                return toggle;
            }
        }
        return null;
    }

    public Setting.Slider slider(String id) {
        for (Setting setting : settings) {
            if (setting.id.equals(id) && setting instanceof Setting.Slider slider) {
                return slider;
            }
        }
        return null;
    }

    public void onSettingChanged() {
    }

    protected abstract void onEnable();

    protected abstract void onDisable();
}
