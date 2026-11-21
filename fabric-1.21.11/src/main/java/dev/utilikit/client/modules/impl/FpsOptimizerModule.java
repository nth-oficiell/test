package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.particle.ParticlesMode;

public class FpsOptimizerModule extends Module {
    private final Setting.Toggle particles;
    private final Setting.Toggle clouds;
    private final Setting.Toggle shadows;
    private final Setting.Toggle entities;
    private final Setting.Toggle simulation;
    private final Setting.Toggle graphics;

    private Object prevParticles;
    private Object prevClouds;
    private Boolean prevShadows;
    private Double prevEntities;
    private Integer prevSimulation;
    private Object prevGraphics;

    public FpsOptimizerModule() {
        super("optifps", "Optimiseur FPS",
                "Booste les FPS d'un coup : particules réduites, nuages off, ombres d'entités off, graphismes rapides. Clic droit pour choisir chaque réglage, désactive pour restaurer.",
                ModuleCategory.PRATIQUE);
        particles = new Setting.Toggle("particules", "Particules réduites", true);
        clouds = new Setting.Toggle("nuages", "Nuages désactivés", true);
        shadows = new Setting.Toggle("ombres", "Ombres d'entités off", true);
        entities = new Setting.Toggle("entites", "Distance entités 50%", false);
        simulation = new Setting.Toggle("simulation", "Distance de simulation 5", false);
        graphics = new Setting.Toggle("graphismes", "Graphismes rapides", false);
        settings.add(particles);
        settings.add(clouds);
        settings.add(shadows);
        settings.add(entities);
        settings.add(simulation);
        settings.add(graphics);
    }

    @Override
    protected void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) {
            return;
        }
        if (particles.value) {
            prevParticles = client.options.getParticles().getValue();
            client.options.getParticles().setValue(ParticlesMode.MINIMAL);
        }
        if (clouds.value) {
            prevClouds = client.options.getCloudRenderMode().getValue();
            client.options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
        }
        if (shadows.value) {
            prevShadows = client.options.getEntityShadows().getValue();
            client.options.getEntityShadows().setValue(false);
        }
        if (entities.value) {
            prevEntities = client.options.getEntityDistanceScaling().getValue();
            client.options.getEntityDistanceScaling().setValue(0.5D);
        }
        if (simulation.value) {
            prevSimulation = client.options.getSimulationDistance().getValue();
            client.options.getSimulationDistance().setValue(5);
        }
        if (graphics.value) {
            prevGraphics = client.options.getPreset().getValue();
            client.options.getPreset().setValue(GraphicsMode.FAST);
        }
    }

    @Override
    protected void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) {
            return;
        }
        if (prevParticles != null) {
            client.options.getParticles().setValue((ParticlesMode) prevParticles);
        }
        if (prevClouds != null) {
            client.options.getCloudRenderMode().setValue((CloudRenderMode) prevClouds);
        }
        if (prevShadows != null) {
            client.options.getEntityShadows().setValue(prevShadows);
        }
        if (prevEntities != null) {
            client.options.getEntityDistanceScaling().setValue(prevEntities);
        }
        if (prevSimulation != null) {
            client.options.getSimulationDistance().setValue(prevSimulation);
        }
        if (prevGraphics != null) {
            client.options.getPreset().setValue((GraphicsMode) prevGraphics);
        }
        prevParticles = null;
        prevClouds = null;
        prevShadows = null;
        prevEntities = null;
        prevSimulation = null;
        prevGraphics = null;
    }
}
