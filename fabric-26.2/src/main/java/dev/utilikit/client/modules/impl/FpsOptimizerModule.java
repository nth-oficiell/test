package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.Setting;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FpsOptimizerModule extends Module {
    private final Setting.Toggle particles;
    private final Setting.Toggle clouds;
    private final Setting.Toggle shadows;
    private final Setting.Toggle entities;
    private final Setting.Toggle simulation;

    private Object prevParticles;
    private Object prevClouds;
    private Object prevShadows;
    private Object prevEntities;
    private Object prevSimulation;

    public FpsOptimizerModule() {
        super("optifps", "Optimiseur FPS",
                "Booste les FPS d'un coup : particules réduites, nuages off, ombres d'entités off. Clic droit pour choisir chaque réglage, désactive pour restaurer.",
                ModuleCategory.PRATIQUE);
        particles = new Setting.Toggle("particules", "Particules réduites", true);
        clouds = new Setting.Toggle("nuages", "Nuages désactivés", true);
        shadows = new Setting.Toggle("ombres", "Ombres d'entités off", true);
        entities = new Setting.Toggle("entites", "Distance entités 50%", false);
        simulation = new Setting.Toggle("simulation", "Distance de simulation 5", false);
        settings.add(particles);
        settings.add(clouds);
        settings.add(shadows);
        settings.add(entities);
        settings.add(simulation);
    }

    @Override
    protected void onEnable() {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.options == null) {
            return;
        }
        Object options = client.options;
        if (particles.value) {
            prevParticles = applyOption(options, "particles", "MINIMAL");
        }
        if (clouds.value) {
            prevClouds = applyOption(options, "clouds", "OFF");
        }
        if (shadows.value) {
            prevShadows = applyOption(options, "entityShadows", Boolean.FALSE);
        }
        if (entities.value) {
            prevEntities = applyOption(options, "entityDistanceScaling", 0.5D);
        }
        if (simulation.value) {
            prevSimulation = applyOption(options, "simulationDistance", 5);
        }
    }

    @Override
    protected void onDisable() {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.options == null) {
            return;
        }
        Object options = client.options;
        restore(options, "particles", prevParticles);
        restore(options, "clouds", prevClouds);
        restore(options, "entityShadows", prevShadows);
        restore(options, "entityDistanceScaling", prevEntities);
        restore(options, "simulationDistance", prevSimulation);
        prevParticles = null;
        prevClouds = null;
        prevShadows = null;
        prevEntities = null;
        prevSimulation = null;
    }

    private static Object applyOption(Object options, String name, Object target) {
        try {
            Object option = option(options, name);
            if (option == null) {
                return null;
            }
            Object current = currentOf(option);
            Object value = resolve(option, current, target);
            if (value == null) {
                return null;
            }
            set(option, value);
            return current;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static void restore(Object options, String name, Object value) {
        if (value == null) {
            return;
        }
        try {
            Object option = option(options, name);
            if (option != null) {
                set(option, value);
            }
        } catch (Throwable ignored) {
        }
    }

    private static Object option(Object options, String name) throws ReflectiveOperationException {
        try {
            return options.getClass().getMethod(name).invoke(options);
        } catch (NoSuchMethodException e) {
            Field field = options.getClass().getField(name);
            return field.get(options);
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

    private static Object resolve(Object option, Object current, Object target) throws ReflectiveOperationException {
        if (target instanceof String constant && current != null && current.getClass().isEnum()) {
            for (Object constantValue : current.getClass().getEnumConstants()) {
                if (constantValue instanceof Enum<?> enumeration && enumeration.name().equals(constant)) {
                    return constantValue;
                }
            }
            return null;
        }
        return target;
    }
}
