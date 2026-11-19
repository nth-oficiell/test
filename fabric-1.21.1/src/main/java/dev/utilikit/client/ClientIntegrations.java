package dev.utilikit.client;

import dev.utilikit.client.modules.InfoModule;
import dev.utilikit.client.modules.KeybindEngine;
import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;
import dev.utilikit.client.modules.TickModule;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

import org.lwjgl.glfw.GLFW;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.List;

public final class ClientIntegrations {
    private static final DateTimeFormatter IRL_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static double lastX;
    private static double lastY;
    private static double lastZ;
    private static boolean statsInit;
    private static double horizontalSpeed;
    private static double verticalSpeed;
    private static double travelled;
    private static int steps;
    private static long sessionTicks;
    private static int fpsMin = Integer.MAX_VALUE;
    private static int fpsMax;
    private static double fpsSum;
    private static int fpsSamples;

    private ClientIntegrations() {
    }

    public static void register() {
        registerInfoModules();
        ModuleManager.register(new FovLockModule());
        ModuleManager.register(new SmoothCamModule());
        ModuleManager.register(new TickModule("antirain", "Anti Pluie",
                "Supprime les visuels et sons de pluie en forçant le temps clair en local.",
                ModuleCategory.VISUEL,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    if (client.world != null) {
                        client.world.setRainGradient(0.0F);
                    }
                }));
        ModuleManager.register(new TickModule("antistorm", "Anti Orage",
                "Supprime les \u00e9clairs et le tonnerre en for\u00e7ant l'orage \u00e0 z\u00e9ro en local.",
                ModuleCategory.VISUEL,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    if (client.world != null) {
                        client.world.setThunderGradient(0.0F);
                    }
                }));


        dev.utilikit.client.modules.extra.ExtraModules.register();

        ClientTickEvents.END_CLIENT_TICK.register(ClientIntegrations::tick);
        HudRenderCallback.EVENT.register((context, tick) -> ClientIntegrations.renderOverlay(context));
    }

    public static void tick(MinecraftClient client) {
        for (Module module : ModuleManager.all()) {
            if (module.enabled && module instanceof InfoModule info) {
                info.tick(client);
            } else if (module.enabled && module instanceof TickModule tick) {
                tick.tick(client);
            }
        }
        updateStats(client);
        dev.utilikit.client.modules.extra.ExtraModules.tick(client);
    }

    public static void pollKeybinds(MinecraftClient client, boolean screenOpen) {
        if (screenOpen) {
            return;
        }
        long handle = client.getWindow().getHandle();
        for (Module module : ModuleManager.all()) {
            KeybindEngine.poll(module, GLFW.glfwGetKey(handle, module.keyCode) == GLFW.GLFW_PRESS);
        }
    }

    private static void updateStats(MinecraftClient client) {
        if (client.player != null) {
            double x = client.player.getX();
            double y = client.player.getY();
            double z = client.player.getZ();
            if (!statsInit) {
                statsInit = true;
                lastX = x;
                lastY = y;
                lastZ = z;
            }
            double dx = x - lastX;
            double dy = y - lastY;
            double dz = z - lastZ;
            horizontalSpeed = Math.sqrt(dx * dx + dz * dz) * 20.0D;
            verticalSpeed = dy * 20.0D;
            if (client.player.isOnGround() && dx * dx + dz * dz < 2.5D) {
                travelled += Math.sqrt(dx * dx + dz * dz);
                steps += (int) (Math.sqrt(dx * dx + dz * dz) / 0.8D);
            }
            lastX = x;
            lastY = y;
            lastZ = z;
            sessionTicks++;
            int fps = client.getCurrentFps();
            if (fps > 0) {
                fpsMin = Math.min(fpsMin, fps);
                fpsMax = Math.max(fpsMax, fps);
                fpsSum += fps;
                fpsSamples++;
            }
        } else {
            statsInit = false;
            sessionTicks = 0;
        }
    }

    private static void registerInfoModules() {
        ModuleManager.register(new InfoModule("coords", "Coordonnées",
                "Affiche ta position X, Y et Z en continu.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    BlockPos pos = player(game).getBlockPos();
                    return "Position : X " + pos.getX() + "  Y " + pos.getY() + "  Z " + pos.getZ();
                }));
        ModuleManager.register(new InfoModule("altitude", "Altitude",
                "Affiche uniquement ton altitude Y.",
                ModuleCategory.AFFICHAGE,
                game -> "Altitude : Y " + player(game).getBlockPos().getY()));
        ModuleManager.register(new InfoModule("chunk", "Chunk",
                "Affiche tes coordonnées de chunk en cours.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    BlockPos pos = player(game).getBlockPos();
                    return "Chunk : CX " + (pos.getX() >> 4) + "  CZ " + (pos.getZ() >> 4);
                }));
        ModuleManager.register(new InfoModule("facing", "Direction visuelle",
                "Affiche le point cardinal vers lequel tu regardes.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    ClientPlayerEntity player = player(game);
                    Direction direction = player.getHorizontalFacing();
                    return "Direction : " + directionLabel(direction) + " (" + String.format(Locale.ROOT, "%.0f", player.getYaw()) + "°)";
                }));
        ModuleManager.register(new InfoModule("rotation", "Angles de vue",
                "Affiche ton yaw et ton pitch en degrés.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    ClientPlayerEntity player = player(game);
                    return "Angles : yaw " + (int) player.getYaw() + "° / pitch " + (int) player.getPitch() + "°";
                }));
        ModuleManager.register(new InfoModule("biome", "Biome",
                "Affiche le nom du biome dans lequel tu te trouves.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    return "Biome : " + prettify(client.world.getBiome(player(game).getBlockPos())
                            .getKey().map(key -> key.getValue().getPath()).orElse("inconnu"));
                }));
        ModuleManager.register(new InfoModule("dimension", "Dimension",
                "Affiche la dimension dans laquelle tu joues.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    return "Dimension : " + prettify(client.world.getRegistryKey().getValue().getPath());
                }));
        ModuleManager.register(new InfoModule("light", "Lumière",
                "Affiche le niveau de lumière au bloc et au ciel.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    BlockPos pos = player(game).getBlockPos();
                    return "Lumière : " + client.world.getLightLevel(pos)
                            + " (bloc " + client.world.getLightLevel(LightType.BLOCK, pos)
                            + " / ciel " + client.world.getLightLevel(LightType.SKY, pos) + ")";
                }));
        ModuleManager.register(new InfoModule("gametime", "Heure du monde",
                "Affiche l'heure in-game en format horloge.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    long ticks = client.world.getTimeOfDay() % 24000L;
                    int hours = (int) ((ticks / 1000L + 6L) % 24L);
                    int minutes = (int) (ticks % 1000L * 60L / 1000L);
                    return "Heure du monde : " + String.format("%02d:%02d", hours, minutes);
                }));
        ModuleManager.register(new InfoModule("day", "Jour du monde",
                "Affiche le numéro du jour in-game.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    return "Jour : " + (client.world.getTime() / 24000L + 1L);
                }));
        ModuleManager.register(new InfoModule("fpsinfo", "FPS info",
                "Affiche un compteur FPS indépendant, séparé du HUD global.",
                ModuleCategory.AFFICHAGE,
                game -> "FPS : " + ((MinecraftClient) game).getCurrentFps()));
        ModuleManager.register(new InfoModule("fpsmm", "FPS min/moy/max",
                "Affiche le minimum, la moyenne et le maximum de FPS de la session.",
                ModuleCategory.AFFICHAGE,
                game -> "FPS min " + (fpsMin == Integer.MAX_VALUE ? 0 : fpsMin)
                        + " / moy " + (fpsSamples == 0 ? 0 : (int) (fpsSum / fpsSamples))
                        + " / max " + fpsMax));
        ModuleManager.register(new InfoModule("ram", "Mémoire (RAM)",
                "Affiche la mémoire utilisée et allouée par le jeu.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    Runtime runtime = Runtime.getRuntime();
                    long used = (runtime.totalMemory() - runtime.freeMemory()) / (1024L * 1024L);
                    long max = runtime.maxMemory() / (1024L * 1024L);
                    return "RAM : " + used + " Mo / " + max + " Mo";
                }));
        ModuleManager.register(new InfoModule("speed", "Vitesse",
                "Affiche ta vitesse de déplacement horizontale en m/s.",
                ModuleCategory.AFFICHAGE,
                game -> "Vitesse : " + String.format(Locale.ROOT, "%.1f", horizontalSpeed) + " m/s"));
        ModuleManager.register(new InfoModule("vspeed", "Vitesse verticale",
                "Affiche ta vitesse de déplacement verticale en m/s.",
                ModuleCategory.AFFICHAGE,
                game -> "Vitesse verticale : " + String.format(Locale.ROOT, "%.1f", verticalSpeed) + " m/s"));
        ModuleManager.register(new InfoModule("distance", "Distance parcourue",
                "Compte la distance totale parcourue à pied depuis le début de la session.",
                ModuleCategory.AFFICHAGE,
                game -> "Distance parcourue : " + (int) travelled + " m"));
        ModuleManager.register(new InfoModule("steps", "Compteur de pas",
                "Compte le nombre de pas effectués depuis le début de la session.",
                ModuleCategory.AFFICHAGE,
                game -> "Pas : " + steps));
        ModuleManager.register(new InfoModule("health", "Vie",
                "Affiche tes points de vie.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    ClientPlayerEntity player = player(game);
                    return "Vie : " + String.format(Locale.ROOT, "%.0f", player.getHealth())
                            + " / " + String.format(Locale.ROOT, "%.0f", player.getMaxHealth());
                }));
        ModuleManager.register(new InfoModule("hunger", "Faim",
                "Affiche tes points de faim.",
                ModuleCategory.AFFICHAGE,
                game -> "Faim : " + player(game).getHungerManager().getFoodLevel() + " / 20"));
        ModuleManager.register(new InfoModule("saturation", "Saturation",
                "Affiche ton niveau de saturation.",
                ModuleCategory.AFFICHAGE,
                game -> "Saturation : " + String.format(Locale.ROOT, "%.1f",
                        player(game).getHungerManager().getSaturationLevel())));
        ModuleManager.register(new InfoModule("air", "Air",
                "Affiche tes bulles d'air quand tu es sous l'eau.",
                ModuleCategory.AFFICHAGE,
                game -> "Air : " + player(game).getAir() + " / 300"));
        ModuleManager.register(new InfoModule("xplevel", "Niveau XP",
                "Affiche ton niveau d'expérience.",
                ModuleCategory.AFFICHAGE,
                game -> "Niveau XP : " + player(game).experienceLevel));
        ModuleManager.register(new InfoModule("xpprogress", "Progression XP",
                "Affiche ta progression vers le niveau suivant.",
                ModuleCategory.AFFICHAGE,
                game -> "Progression XP : " + (int) (player(game).experienceProgress * 100.0F) + "%"));
        ModuleManager.register(new InfoModule("xptotal", "XP total",
                "Affiche ton total de points d'expérience.",
                ModuleCategory.AFFICHAGE,
                game -> "XP total : " + player(game).totalExperience));
        ModuleManager.register(new InfoModule("armor", "Armure",
                "Affiche tes points d'armure.",
                ModuleCategory.AFFICHAGE,
                game -> "Armure : " + player(game).getArmor() + " points"));
        ModuleManager.register(new InfoModule("helditem", "Objet en main",
                "Affiche le nom de l'objet que tu tiens.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    ClientPlayerEntity player = player(game);
                    if (player.getMainHandStack() == null || player.getMainHandStack().isEmpty()) {
                        return null;
                    }
                    String name = player.getMainHandStack().getName().getString();
                    int count = player.getMainHandStack().getCount();
                    return "En main : " + name + (count > 1 ? " x" + count : "");
                }));
        ModuleManager.register(new InfoModule("helddur", "Durabilité en main",
                "Affiche la durabilité restante de l'objet tenu.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    ClientPlayerEntity player = player(game);
                    if (player.getMainHandStack() == null || !player.getMainHandStack().isDamaged()) {
                        return null;
                    }
                    int max = player.getMainHandStack().getMaxDamage();
                    int remaining = max - player.getMainHandStack().getDamage();
                    return "Durabilité : " + remaining + " / " + max
                            + " (" + (int) (remaining * 100.0D / max) + "%)";
                }));
        ModuleManager.register(new InfoModule("weather", "Météo",
                "Affiche la météo du monde.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    String weather = client.world.isThundering() ? "Orage" : (client.world.isRaining() ? "Pluie" : "Clair");
                    return "Météo : " + weather;
                }));
        ModuleManager.register(new InfoModule("server", "Serveur",
                "Affiche l'adresse du serveur sur lequel tu es connecté.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    return "Serveur : " + (client.getCurrentServerEntry() != null ? client.getCurrentServerEntry().address : "Solo");
                }));
        ModuleManager.register(new InfoModule("brand", "Marque du serveur",
                "Affiche la plateforme du serveur (vanilla, paper...).",
                ModuleCategory.AFFICHAGE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    if (client.getNetworkHandler() == null) {
                        return null;
                    }
                    String brand = serverBrand(client.getNetworkHandler());
                    return brand == null ? null : "Plateforme : " + brand;
                }));
        ModuleManager.register(new InfoModule("session", "Temps de session",
                "Compte le temps de jeu depuis ton arrivée dans un monde.",
                ModuleCategory.AFFICHAGE,
                game -> "Session : " + String.format("%02d:%02d", sessionTicks / (20L * 60L),
                        (sessionTicks / 20L) % 60L)));
        ModuleManager.register(new InfoModule("entities", "Entités proches",
                "Compte les entités présentes autour de toi (16 blocs).",
                ModuleCategory.AFFICHAGE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    ClientPlayerEntity player = player(game);
                    return "Entités proches : " + client.world.getOtherEntities(player,
                            player.getBoundingBox().expand(16.0D)).size();
                }));
        ModuleManager.register(new InfoModule("irlclock", "Heure réelle",
                "Affiche l'heure réelle de ton PC.",
                ModuleCategory.AFFICHAGE,
                game -> "Heure réelle : " + LocalTime.now().format(IRL_FORMAT)));
        ModuleManager.register(new InfoModule("playername", "Pseudo",
                "Affiche ton pseudo.",
                ModuleCategory.AFFICHAGE,
                game -> "Pseudo : " + String.valueOf(player(game).getName())));
        ModuleManager.register(new InfoModule("uuid", "UUID",
                "Affiche ton UUID.",
                ModuleCategory.AFFICHAGE,
                game -> "UUID : " + player(game).getUuidAsString()));
        ModuleManager.register(new InfoModule("nether", "Coordonnées Nether",
                "Convertit tes coordonnées en coordonnées Nether/Overworld selon la dimension.",
                ModuleCategory.PRATIQUE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    BlockPos pos = player(game).getBlockPos();
                    String dimension = client.world.getRegistryKey().getValue().getPath();
                    if (dimension.contains("nether")) {
                        return "Overworld ↔ : X " + (pos.getX() * 8) + "  Z " + (pos.getZ() * 8);
                    }
                    if (dimension.contains("overworld")) {
                        return "Nether ↔ : X " + (pos.getX() / 8) + "  Z " + (pos.getZ() / 8);
                    }
                    return null;
                }));
        ModuleManager.register(new InfoModule("gamemode", "Mode de jeu",
                "Affiche ton mode de jeu actuel.",
                ModuleCategory.PRATIQUE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    return "Mode : " + prettify(String.valueOf(client.interactionManager.getCurrentGameMode())
                            .toLowerCase(Locale.ROOT));
                }));
        ModuleManager.register(new InfoModule("targetblock", "Bloc visé",
                "Affiche le nom du bloc que tu vises.",
                ModuleCategory.PRATIQUE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    if (!(client.crosshairTarget instanceof BlockHitResult hit) || client.world == null) {
                        return null;
                    }
                    return "Bloc visé : " + client.world.getBlockState(hit.getBlockPos()).getBlock().getName().getString()
                            + " (" + hit.getBlockPos().toShortString() + ")";
                }));
        ModuleManager.register(new InfoModule("targetentity", "Entité visée",
                "Affiche le nom de l'entité que tu vises.",
                ModuleCategory.PRATIQUE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    if (client.crosshairTarget instanceof EntityHitResult hit) {
                        return "Entité visée : " + hit.getEntity().getName().getString();
                    }
                    return null;
                }));
        ModuleManager.register(new InfoModule("targetdist", "Distance de visée",
                "Affiche la distance avec ce que tu vises.",
                ModuleCategory.PRATIQUE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    ClientPlayerEntity player = player(game);
                    HitResult target = client.crosshairTarget;
                    if (target instanceof EntityHitResult hit) {
                        return "Distance cible : " + String.format(Locale.ROOT, "%.1f", player.distanceTo(hit.getEntity())) + " m";
                    }
                    if (target instanceof BlockHitResult hit) {
                        double distance = player.getEyePos().distanceTo(Vec3d.ofCenter(hit.getBlockPos()));
                        return "Distance cible : " + String.format(Locale.ROOT, "%.1f", distance) + " m";
                    }
                    return null;
                }));
        ModuleManager.register(new InfoModule("attackcd", "Recharge d'attaque",
                "Affiche la recharge de ton attaque en pourcentage.",
                ModuleCategory.PRATIQUE,
                game -> "Recharge d'attaque : " + (int) (player(game).getAttackCooldownProgress(0.0F) * 100.0F) + "%"));
        ModuleManager.register(new InfoModule("fall", "Hauteur de chute",
                "Affiche la hauteur de chute en cours.",
                ModuleCategory.PRATIQUE,
                game -> {
                    double fall = player(game).fallDistance;
                    return fall > 0.5D ? "Chute : " + String.format(Locale.ROOT, "%.1f", fall) + " m" : null;
                }));

        ModuleManager.register(new InfoModule("moon", "Phase de lune",
                "Affiche la phase actuelle de la lune.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    if (client.world == null) {
                        return null;
                    }
                    String[] phases = {"Pleine", "Gibbeuse d\u00e9croissante", "Dernier quartier", "Croissant d\u00e9croissant",
                            "Nouvelle", "Croissant croissant", "Premier quartier", "Gibbeuse croissante"};
                    int phase = (int) Math.floorMod(client.world.getTime() / 24000L, 8L);
                    return "Lune : " + phases[phase];
                }));

        ModuleManager.register(new InfoModule("daynight", "P\u00e9riode",
                "Indique si c'est le jour, la nuit, l'aube ou le cr\u00e9puscule.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    if (client.world == null) {
                        return null;
                    }
                    long ticks = client.world.getTime() % 24000L;
                    String period = ticks < 12000L ? "Jour" : (ticks < 13000L ? "Cr\u00e9puscule" : (ticks < 23000L ? "Nuit" : "Aube"));
                    return "P\u00e9riode : " + period;
                }));

        ModuleManager.register(new InfoModule("compass", "Boussole",
                "Affiche ton orientation pr\u00e9cise (Nord, Nord-Est...).",
                ModuleCategory.PRATIQUE,
                game -> {
                    ClientPlayerEntity player = player(game);
                    String[] points = {"Sud", "Sud-Ouest", "Ouest", "Nord-Ouest", "Nord", "Nord-Est", "Est", "Sud-Est"};
                    int idx = (int) Math.floor((Math.floorMod(Math.round(player.getYaw()), 360) + 22) / 45.0D) % 8;
                    return "Boussole : " + points[idx];
                }));

        ModuleManager.register(new InfoModule("window", "R\u00e9solution \u00e9cran",
                "Affiche la r\u00e9solution de ta fen\u00eatre de jeu.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    return "R\u00e9solution : " + client.getWindow().getWidth()
                            + "x" + client.getWindow().getHeight();
                }));

        ModuleManager.register(new InfoModule("guisize", "Taille interface",
                "Affiche la taille en pixels de ton interface.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    return "Interface : " + client.getWindow().getScaledWidth()
                            + "x" + client.getWindow().getScaledHeight();
                }));

        ModuleManager.register(new InfoModule("pinginfo", "Ping d\u00e9taill\u00e9",
                "Affiche ton ping (latence) vers le serveur.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    if (client.getNetworkHandler() == null || client.player == null) {
                        return null;
                    }
                    var entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
                    return entry == null ? null : "Ping : " + entry.getLatency() + " ms";
                }));

        ModuleManager.register(new InfoModule("difficulty", "Difficult\u00e9",
                "Affiche la difficult\u00e9 du monde en cours.",
                ModuleCategory.PRATIQUE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    if (client.world == null) {
                        return null;
                    }
                    return "Difficult\u00e9 : " + prettify(String.valueOf(client.world.getDifficulty())
                            .toLowerCase(Locale.ROOT));
                }));

        ModuleManager.register(new InfoModule("effects", "Effets actifs",
                "Compte tes effets de potion actifs.",
                ModuleCategory.PRATIQUE,
                game -> "Effets : " + player(game).getStatusEffects().size()));

        ModuleManager.register(new InfoModule("flying", "Vol",
                "Indique si tu es en train de voler.",
                ModuleCategory.PRATIQUE,
                game -> "Vol : " + (player(game).getAbilities().flying ? "Oui" : "Non")));

        ModuleManager.register(new InfoModule("vehicle", "V\u00e9hicule",
                "Indique si tu chevauches une entit\u00e9.",
                ModuleCategory.PRATIQUE,
                game -> "V\u00e9hicule : " + (player(game).hasVehicle() ? "Oui" : "Non")));

        ModuleManager.register(new InfoModule("sneak", "Accroupi",
                "Indique si tu es accroupi.",
                ModuleCategory.PRATIQUE,
                game -> "Accroupi : " + (player(game).isSneaking() ? "Oui" : "Non")));

        ModuleManager.register(new InfoModule("sprint", "Sprint",
                "Indique si tu sprintes.",
                ModuleCategory.PRATIQUE,
                game -> "Sprint : " + (player(game).isSprinting() ? "Oui" : "Non")));

        ModuleManager.register(new InfoModule("underwater", "Sous l'eau",
                "Indique si ta t\u00eate est sous l'eau.",
                ModuleCategory.PRATIQUE,
                game -> "Sous l'eau : " + (player(game).isSubmergedInWater() ? "Oui" : "Non")));

        ModuleManager.register(new InfoModule("onfire", "En feu",
                "Indique si tu es en feu.",
                ModuleCategory.PRATIQUE,
                game -> "En feu : " + (player(game).isOnFire() ? "Oui" : "Non")));

        ModuleManager.register(new InfoModule("java", "Version Java",
                "Affiche la version de Java utilis\u00e9e par le jeu.",
                ModuleCategory.PRATIQUE,
                game -> "Java : " + System.getProperty("java.version")));

        ModuleManager.register(new InfoModule("os", "Syst\u00e8me",
                "Affiche ton syst\u00e8me d'exploitation et ton architecture.",
                ModuleCategory.PRATIQUE,
                game -> "OS : " + System.getProperty("os.name")
                        + " (" + String.valueOf(System.getProperty("os.arch")).replace("amd64", "x64") + ")"));

        ModuleManager.register(new InfoModule("fullscreen", "Plein \u00e9cran",
                "Indique si le jeu est en plein \u00e9cran.",
                ModuleCategory.PRATIQUE,
                game -> "Plein \u00e9cran : " + (MinecraftClient.getInstance().getWindow().isFullscreen() ? "Oui" : "Non")));

        ModuleManager.register(new InfoModule("singleplayer", "Solo / Multi",
                "Indique si tu joues en solo ou en ligne.",
                ModuleCategory.PRATIQUE,
                game -> "Mode : " + (MinecraftClient.getInstance().isInSingleplayer() ? "Solo" : "En ligne")));

        ModuleManager.register(new InfoModule("heldcount", "Quantit\u00e9 en main",
                "Affiche le nombre d'objets empil\u00e9s dans ta main.",
                ModuleCategory.PRATIQUE,
                game -> "Qt\u00e9 en main : " + player(game).getMainHandStack().getCount()));

        ModuleManager.register(new InfoModule("blockunder", "Bloc sous les pieds",
                "Affiche le bloc sur lequel tu te tiens.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    if (client.world == null) {
                        return null;
                    }
                    return "Bloc dessous : " + client.world.getBlockState(player(game).getBlockPos().down())
                            .getBlock().getName().getString();
                }));

        ModuleManager.register(new InfoModule("targetpos", "Position vis\u00e9e",
                "Affiche les coordonn\u00e9es du bloc que tu vises.",
                ModuleCategory.PRATIQUE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    if (!(client.crosshairTarget instanceof BlockHitResult hit) || client.world == null) {
                        return null;
                    }
                    return "Position vis\u00e9e : " + hit.getBlockPos().toShortString();
                }));

        ModuleManager.register(new InfoModule("mouse", "Sensibilit\u00e9 souris",
                "Affiche ta sensibilit\u00e9 de souris en pourcentage.",
                ModuleCategory.VISUEL,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    double sens = client.options.getMouseSensitivity().getValue();
                    return "Souris : " + (int) Math.round(sens * 100.0D) + " %";
                }));

        ModuleManager.register(new InfoModule("bright", "Luminosit\u00e9 r\u00e9glage",
                "Affiche la valeur de ton r\u00e9glage de luminosit\u00e9.",
                ModuleCategory.VISUEL,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    double gamma = client.options.getGamma().getValue();
                    return "Luminosit\u00e9 : " + (int) Math.round(gamma * 100.0D) + " %";
                }));

        ModuleManager.register(new InfoModule("renderdist", "Distance de rendu",
                "Affiche ta distance de rendu en chunks.",
                ModuleCategory.VISUEL,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    return "Rendu : " + client.options.getViewDistance().getValue() + " chunks";
                }));

        ModuleManager.register(new InfoModule("roundedpos", "Position arrondie",
                "Affiche ta position arrondie \u00e0 la case pr\u00e8s.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    ClientPlayerEntity p = player(game);
                    return "Arrondi : " + Math.round(p.getX()) + " / " + Math.round(p.getY())
                            + " / " + Math.round(p.getZ());
                }));

        ModuleManager.register(new InfoModule("chunklocal", "Chunk local",
                "Affiche ta position \u00e0 l'int\u00e9rieur du chunk courant.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    ClientPlayerEntity p = player(game);
                    return "Chunk local : " + (p.getBlockX() & 15) + " / " + (p.getBlockY() & 15)
                            + " / " + (p.getBlockZ() & 15);
                }));

        ModuleManager.register(new InfoModule("daycount", "Jour \u00e9coul\u00e9",
                "Affiche le nombre de jours \u00e9coul\u00e9s dans le monde.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    if (client.world == null) {
                        return null;
                    }
                    return "Jour n\u00b0 " + (client.world.getTime() / 24000L + 1L);
                }));

        ModuleManager.register(new InfoModule("offhand", "Main secondaire",
                "Affiche l'objet de ta main secondaire.",
                ModuleCategory.AFFICHAGE,
                game -> {
                    net.minecraft.item.ItemStack stack = offhandStack(player(game));
                    return stack == null || stack.isEmpty() ? "Main secondaire : vide"
                            : "Main secondaire : " + stack.getName().getString();
                }));

        ModuleManager.register(new InfoModule("armorpieces", "Pi\u00e8ces d'armure",
                "Compte les pi\u00e8ces d'armure que tu portes.",
                ModuleCategory.AFFICHAGE,
                game -> "Armure \u00e9quip\u00e9e : " + wornArmor(player(game)) + " / 4"));

        ModuleManager.register(new InfoModule("stackmaxhand", "Empilement max main",
                "Affiche la taille maximale d'empilement de ton objet en main.",
                ModuleCategory.AFFICHAGE,
                game -> "Empilement max : " + player(game).getMainHandStack().getMaxCount()));

        ModuleManager.register(new InfoModule("itemrarity", "Raret\u00e9 de l'objet",
                "Affiche la raret\u00e9 de ton objet en main.",
                ModuleCategory.VISUEL,
                game -> {
                    net.minecraft.item.ItemStack stack = player(game).getMainHandStack();
                    return stack.isEmpty() ? null : "Raret\u00e9 : " + prettify(String.valueOf(stack.getRarity()));
                }));

        ModuleManager.register(new InfoModule("durabilhand", "Durabilit\u00e9 % main",
                "Affiche la durabilit\u00e9 restante de ton objet en main en pourcentage.",
                ModuleCategory.PRATIQUE,
                game -> {
                    net.minecraft.item.ItemStack stack = player(game).getMainHandStack();
                    if (stack.isEmpty()) {
                        return null;
                    }
                    int max = stack.getMaxDamage();
                    if (max <= 0) {
                        return null;
                    }
                    return "Durabilit\u00e9 : " + Math.round((max - stack.getDamage()) * 100.0D / max) + " %";
                }));

        ModuleManager.register(new InfoModule("targetface", "Face vis\u00e9e",
                "Affiche la face du bloc que tu vises.",
                ModuleCategory.PRATIQUE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    if (client.crosshairTarget instanceof BlockHitResult hit) {
                        return "Face : " + prettify(directionName(hit.getSide()));
                    }
                    return null;
                }));

        ModuleManager.register(new InfoModule("targetlife", "Vie vis\u00e9e",
                "Affiche les points de vie de l'entit\u00e9 vis\u00e9e.",
                ModuleCategory.PRATIQUE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    if (client.crosshairTarget instanceof EntityHitResult hit
                            && hit.getEntity() instanceof net.minecraft.entity.LivingEntity living) {
                        return "Vie vis\u00e9e : " + String.format(Locale.ROOT, "%.1f", living.getHealth())
                                + " / " + String.format(Locale.ROOT, "%.1f", living.getMaxHealth());
                    }
                    return null;
                }));

        ModuleManager.register(new InfoModule("targetcoords", "Entit\u00e9 vis\u00e9e (pos)",
                "Affiche la position de l'entit\u00e9 que tu vises.",
                ModuleCategory.PRATIQUE,
                game -> {
                    MinecraftClient client = (MinecraftClient) game;
                    if (client.crosshairTarget instanceof EntityHitResult hit) {
                        return "Position entit\u00e9 : " + hit.getEntity().getBlockPos().toShortString();
                    }
                    return null;
                }));

        ModuleManager.register(new InfoModule("hurt", "D\u00e9g\u00e2ts r\u00e9cents",
                "Indique si tu viens de recevoir des d\u00e9g\u00e2ts.",
                ModuleCategory.PRATIQUE,
                game -> "D\u00e9g\u00e2ts r\u00e9cents : " + (player(game).hurtTime > 0 ? "Oui" : "Non")));

        ModuleManager.register(new InfoModule("origin", "Distance au 0,0",
                "Affiche ta distance au point d'origine du monde.",
                ModuleCategory.PRATIQUE,
                game -> {
                    ClientPlayerEntity p = player(game);
                    double d = Math.sqrt((double) p.getBlockX() * (double) p.getBlockX()
                            + (double) p.getBlockZ() * (double) p.getBlockZ());
                    return d >= 1000.0D
                            ? "Origine : " + String.format(Locale.ROOT, "%.1f", d / 1000.0D) + " km"
                            : "Origine : " + (int) d + " m";
                }));

        ModuleManager.register(new InfoModule("modcount", "Mods charg\u00e9s",
                "Affiche le nombre de mods charg\u00e9s par Fabric.",
                ModuleCategory.PRATIQUE,
                game -> "Mods charg\u00e9s : "
                        + net.fabricmc.loader.api.FabricLoader.getInstance().getAllMods().size()));

        ModuleManager.register(new InfoModule("about", "\u00c0 propos",
                "Affiche la version de NTH Utils install\u00e9e.",
                ModuleCategory.PRATIQUE,
                game -> {
                    var container = net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer("utilikit");
                    String version = container
                            .map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("?");
                    return "NTH Utils v" + version;
                }));

        ModuleManager.register(new InfoModule("fabricver", "Version Fabric",
                "Affiche la version de Fabric Loader et de Fabric API.",
                ModuleCategory.PRATIQUE,
                game -> {
                    var loader = net.fabricmc.loader.api.FabricLoader.getInstance();
                    var container = loader.getModContainer("fabric-api");
                    if (container.isEmpty()) {
                        container = loader.getModContainer("fabric");
                    }
                    if (container.isEmpty()) {
                        container = loader.getModContainer("fabricloader");
                    }
                    String fapi = container.map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("absente");
                    return "Fabric API : " + fapi;
                }));
    }

    private static ClientPlayerEntity player(Object game) {
        MinecraftClient client = (MinecraftClient) game;
        if (client.player == null) {
            throw new IllegalStateException("no player");
        }
        return client.player;
    }

    private static String serverBrand(Object connection) {
        try {
            for (java.lang.reflect.Method method : connection.getClass().getMethods()) {
                if (method.getReturnType() == String.class && method.getParameterCount() == 0) {
                    String name = method.getName().toLowerCase(Locale.ROOT);
                    if (name.contains("brand")) {
                        Object result = method.invoke(connection);
                        return result == null ? null : result.toString();
                    }
                }
            }
        } catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
            return null;
        }
        return null;
    }


    private static int wornArmor(Object playerObj) {
        int worn = 0;
        try {
            for (var method : playerObj.getClass().getMethods()) {
                if (method.getParameterCount() == 0
                        && method.getName().toLowerCase(Locale.ROOT).contains("armoritems")) {
                    Object items = method.invoke(playerObj);
                    if (items instanceof Iterable<?> iterable) {
                        for (Object stack : iterable) {
                            if (stack == null || !((Boolean) stack.getClass()
                                    .getMethod("isEmpty").invoke(stack))) {
                                worn++;
                            }
                        }
                    }
                    break;
                }
            }
        } catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
            return 0;
        }
        return worn;
    }

    private static String directionName(Object direction) {
        try {
            for (var method : direction.getClass().getMethods()) {
                if (method.getParameterCount() == 0
                        && (method.getName().equals("getName") || method.getName().equals("asString"))) {
                    Object result = method.invoke(direction);
                    return result == null ? String.valueOf(direction) : result.toString();
                }
            }
        } catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
            return String.valueOf(direction);
        }
        return String.valueOf(direction);
    }

    private static net.minecraft.item.ItemStack offhandStack(Object playerObj) {
        try {
            for (var method : playerObj.getClass().getMethods()) {
                if (method.getParameterCount() == 0 && method.getReturnType() == net.minecraft.item.ItemStack.class
                        && method.getName().toLowerCase(Locale.ROOT).contains("offhand")) {
                    return (net.minecraft.item.ItemStack) method.invoke(playerObj);
                }
            }
        } catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
            return null;
        }
        return null;
    }

    private static String prettify(String path) {
        if (path == null || path.isEmpty()) {
            return "inconnu";
        }
        String raw = path.substring(path.indexOf('/') + 1);
        String[] words = raw.split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return sb.toString();
    }

    private static String directionLabel(Direction direction) {
        return switch (direction) {
            case NORTH -> "Nord (Z-)";
            case SOUTH -> "Sud (Z+)";
            case WEST -> "Ouest (X-)";
            case EAST -> "Est (X+)";
            default -> direction.asString();
        };
    }

    public static void renderOverlay(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options == null || client.player == null || client.world == null) {
            return;
        }
        dev.utilikit.client.modules.extra.HudDrag.beginFrame();
        int[] hudOffset = dev.utilikit.client.modules.extra.ExtraModules.hudOffset();
        int x = 6 + hudOffset[0];
        int y = 6 + hudOffset[1] + hudInfoHeight();
        int startY = y;
        int lineHeight = 12;
        int columnWidth = 170;
        int maxY = client.getWindow().getScaledHeight() - 50;

        for (Module module : ModuleManager.all()) {
            if (!(module instanceof InfoModule info) || !info.enabled) {
                continue;
            }
            String text = info.line(client);
            if (text == null) {
                continue;
            }
            if (y > maxY) {
                x += columnWidth;
                y = startY;
            }
            int[] lineOffset = dev.utilikit.client.modules.extra.HudDrag.lineOffset(info.id);
            int lineX = x + lineOffset[0];
            int lineY = y + lineOffset[1];
            int width = client.textRenderer.getWidth(text);
            dev.utilikit.client.modules.extra.HudDrag.track(info.id, x, y, width, lineHeight);
            if (info.hasBackground()) {
                context.fill(lineX - 3, lineY - 2, lineX + width + 3, lineY + 10, 0x90000000);
            }
            context.drawTextWithShadow(client.textRenderer, text, lineX, lineY, 0xFFFFFF);
            y += lineHeight;
        }

        List<String> notes = dev.utilikit.client.modules.extra.Notifier.active();
        if (!notes.isEmpty()) {
            int noteY = client.getWindow().getScaledHeight() - 22 - notes.size() * 12;
            for (String note : notes) {
                int noteWidth = client.textRenderer.getWidth(note);
                context.fill(4, noteY - 2, 10 + noteWidth, noteY + 10, 0x90000000);
                context.drawTextWithShadow(client.textRenderer, "\u26a1 " + note, 6, noteY, 0xFFEB55);
                noteY += 12;
            }
        }


        int[] crosshair = dev.utilikit.client.modules.extra.ExtraModules.crosshairSpec();
        if (crosshair != null) {
            int cx = client.getWindow().getScaledWidth() / 2;
            int cy = client.getWindow().getScaledHeight() / 2;
            int crossLen = Math.max(2, crosshair[1]);
            int crossTh = Math.max(1, crosshair[2]);
            int crossColor = crosshair[3];
            if (crosshair[0] == 2) {
                context.fill(cx - crossTh, cy - crossTh, cx + crossTh + 1, cy + crossTh + 1, crossColor);
            } else if (crosshair[0] == 1) {
                context.fill(cx - crossLen - crossTh, cy, cx - crossLen, cy + 1, crossColor);
                context.fill(cx + crossLen, cy, cx + crossLen + crossTh, cy + 1, crossColor);
                context.fill(cx, cy - crossLen - crossTh, cx + 1, cy - crossLen, crossColor);
                context.fill(cx, cy + crossLen, cx + 1, cy + crossLen + crossTh, crossColor);
                context.fill(cx - crossTh, cy - crossTh, cx + crossTh + 1, cy + crossTh + 1, crossColor);
            } else {
                context.fill(cx - crossLen, cy, cx + crossLen, cy + 1, crossColor);
                context.fill(cx, cy - crossLen, cx + 1, cy + crossLen, crossColor);
            }
        }

        if (dev.utilikit.client.modules.extra.Vigilance.flashActive()) {
            int flashW = client.getWindow().getScaledWidth();
            int flashH = client.getWindow().getScaledHeight();
            context.fill(0, 0, flashW, 3, 0x99FF2222);
            context.fill(0, flashH - 3, flashW, flashH, 0x99FF2222);
            context.fill(0, 0, 3, flashH, 0x99FF2222);
            context.fill(flashW - 3, 0, flashW, flashH, 0x99FF2222);
        }

        java.util.List<double[]> dots = dev.utilikit.client.modules.extra.ExtraModules.radarDots(client);
        if (dots != null) {
            int size = 64;
            int bx = client.getWindow().getScaledWidth() - size - 8;
            int by = 8;
            int half = size / 2;
            context.fill(bx, by, bx + size, by + size, 0x66000000);
            context.fill(bx, by, bx + size, by + 1, 0xB3FFFFFF);
            context.fill(bx, by + size - 1, bx + size, by + size, 0xB3FFFFFF);
            context.fill(bx, by, bx + 1, by + size, 0xB3FFFFFF);
            context.fill(bx + size - 1, by, bx + size, by + size, 0xB3FFFFFF);
            context.fill(bx + half - 1, by + half - 1, bx + half + 1, by + half + 1, 0xFF55FF55);
            for (double[] dot : dots) {
                int dotX = bx + half + (int) Math.round(dot[0] * (half - 4));
                int dotY = by + half + (int) Math.round(dot[1] * (half - 4));
                context.fill(dotX, dotY, dotX + 2, dotY + 2, 0xFFFF5555);
            }
        }
    }

    private static int hudInfoHeight() {
        Module hudInfo = ModuleManager.get("hudinfo");
        if (hudInfo == null || !hudInfo.enabled) {
            return 0;
        }
        int lines = 0;
        for (Setting setting : hudInfo.settings) {
            if (setting instanceof Setting.Toggle toggle && toggle.value && !"background".equals(toggle.id)) {
                lines++;
            }
        }
        return lines * 12 + 4;
    }

    private static class FovLockModule extends TickModule {
        private final Setting.Slider fov;
        private Integer previous;

        FovLockModule() {
            super("fovlock", "FOV personnalisé",
                    "Force un champ de vision spécifique, quel que soit ton réglage vanilla.",
                    ModuleCategory.VISUEL, game -> {
                    });
            fov = new Setting.Slider("fov", "FOV", 30.0D, 150.0D, 110.0D);
            settings.add(fov);
        }

        @Override
        public void tick(Object game) {
            if (!enabled) {
                return;
            }
            MinecraftClient client = (MinecraftClient) game;
            if (client.options != null) {
                client.options.getFov().setValue((int) Math.round(fov.value));
            }
        }

        @Override
        protected void onEnable() {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.options != null) {
                previous = client.options.getFov().getValue();
            }
        }

        @Override
        protected void onDisable() {
            MinecraftClient client = MinecraftClient.getInstance();
            if (previous != null && client != null && client.options != null) {
                client.options.getFov().setValue(Math.min(previous, 110));
            }
            previous = null;
        }

        @Override
        public void onSettingChanged() {
            if (enabled) {
                tick(MinecraftClient.getInstance());
            }
        }
    }

    private static class SmoothCamModule extends TickModule {
        SmoothCamModule() {
            super("smoothcam", "Caméra lisse",
                    "Active le mouvement de caméra cinéma (comme la touche F8) en continu.",
                    ModuleCategory.VISUEL, game -> {
                        MinecraftClient client = (MinecraftClient) game;
                        if (client.options != null) {
                            client.options.smoothCameraEnabled = true;
                        }
                    });
        }

        @Override
        protected void onDisable() {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.options != null) {
                client.options.smoothCameraEnabled = false;
            }
        }
    }
}
