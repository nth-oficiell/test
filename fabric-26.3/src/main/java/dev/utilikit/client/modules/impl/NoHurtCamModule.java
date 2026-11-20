package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import net.minecraft.client.Minecraft;

public class NoHurtCamModule extends Module {
    private double prevTilt = -1.0D;

    public NoHurtCamModule() {
        super("nohurtcam", "No Hurt Cam", "Désactive le tremblement de la caméra quand tu prends des dégâts (réglage vanilla, sans mixin).", ModuleCategory.VISUEL);
    }

    @Override
    protected void onEnable() {
        try {
            Minecraft client = Minecraft.getInstance();
            Object opt = option(client.options, "getDamageTiltStrength");
            Object cur = currentOf(opt);
            prevTilt = cur instanceof Number number ? number.doubleValue() : -1.0D;
            set(opt, 0.0D);
        } catch (ReflectiveOperationException e) {
            prevTilt = -1.0D;
        }
    }

    @Override
    protected void onDisable() {
        if (prevTilt < 0.0D) {
            return;
        }
        try {
            Minecraft client = Minecraft.getInstance();
            set(option(client.options, "getDamageTiltStrength"), prevTilt);
        } catch (ReflectiveOperationException ignored) {
        }
        prevTilt = -1.0D;
    }

    private static Object option(Object options, String name) throws ReflectiveOperationException {
        try {
            return options.getClass().getMethod(name).invoke(options);
        } catch (NoSuchMethodException e) {
            return options.getClass().getField(name).get(options);
        }
    }

    private static Object currentOf(Object option) throws ReflectiveOperationException {
        try {
            return option.getClass().getMethod("get").invoke(option);
        } catch (NoSuchMethodException e) {
            return option.getClass().getMethod("value").invoke(option);
        }
    }

    private static void set(Object option, Object value) throws ReflectiveOperationException {
        try {
            option.getClass().getMethod("set", Object.class).invoke(option, value);
        } catch (NoSuchMethodException e) {
            option.getClass().getMethod("setValue", Object.class).invoke(option, value);
        }
    }
}
