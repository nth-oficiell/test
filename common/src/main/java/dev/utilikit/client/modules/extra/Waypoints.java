package dev.utilikit.client.modules.extra;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.utilikit.client.config.ConfigManager;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Waypoints {
    private static final Logger LOGGER = LoggerFactory.getLogger("utilikit");
    private static final List<Waypoint> LIST = new ArrayList<>();

    public static final class Waypoint {
        public String name;
        public double x;
        public double y;
        public double z;
    }

    private Waypoints() {
    }

    public static List<Waypoint> all() {
        return LIST;
    }

    public static Waypoint addHere(Object game) {
        Player player = Player.of(game);
        if (player == null) {
            return null;
        }
        Waypoint waypoint = new Waypoint();
        waypoint.name = "Point " + (LIST.size() + 1);
        waypoint.x = player.x;
        waypoint.y = player.y;
        waypoint.z = player.z;
        LIST.add(waypoint);
        save();
        return waypoint;
    }

    public static void removeLast() {
        if (!LIST.isEmpty()) {
            LIST.remove(LIST.size() - 1);
            save();
        }
    }

    public static void clear() {
        LIST.clear();
        save();
    }

    public static int count() {
        return LIST.size();
    }

    public static double distance(Waypoint waypoint, Object game) {
        Player player = Player.of(game);
        if (player == null) {
            return 0.0D;
        }
        double dx = waypoint.x - player.x;
        double dy = waypoint.y - player.y;
        double dz = waypoint.z - player.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static String arrow(Waypoint waypoint, Object game) {
        Player player = Player.of(game);
        if (player == null || player.yaw == null) {
            return "★";
        }
        double dx = waypoint.x - player.x;
        double dz = waypoint.z - player.z;
        double bearing = Math.toDegrees(Math.atan2(-dx, dz));
        double relative = bearing - player.yaw;
        while (relative > 180.0D) {
            relative -= 360.0D;
        }
        while (relative < -180.0D) {
            relative += 360.0D;
        }
        if (relative >= -45.0D && relative < 45.0D) {
            return "↑";
        }
        if (relative >= 45.0D && relative < 135.0D) {
            return "→";
        }
        if (relative < -45.0D && relative > -135.0D) {
            return "←";
        }
        return "↓";
    }

    public static String line(Object game) {
        StringBuilder sb = new StringBuilder();
        for (Waypoint waypoint : LIST) {
            if (sb.length() > 0) {
                sb.append("  ·  ");
            }
            int distance = (int) Math.round(distance(waypoint, game));
            sb.append(arrow(waypoint, game)).append(' ').append(waypoint.name).append(" (").append(distance).append("m)");
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("utilikit-waypoints.json");
    }

    public static void save() {
        try {
            JsonArray array = new JsonArray();
            for (Waypoint waypoint : LIST) {
                JsonObject object = new JsonObject();
                object.addProperty("name", waypoint.name);
                object.addProperty("x", waypoint.x);
                object.addProperty("y", waypoint.y);
                object.addProperty("z", waypoint.z);
                array.add(object);
            }
            JsonObject root = new JsonObject();
            root.add("waypoints", array);
            Files.writeString(file(), ConfigManager.gson().toJson(root));
        } catch (Exception e) {
            LOGGER.error("Impossible de sauvegarder les waypoints NTH Utils", e);
        }
    }

    public static void load() {
        LIST.clear();
        Path file = file();
        if (!Files.exists(file)) {
            return;
        }
        try {
            JsonObject root = ConfigManager.gson().fromJson(Files.readString(file), JsonObject.class);
            if (root == null || !root.has("waypoints") || !root.get("waypoints").isJsonArray()) {
                return;
            }
            for (JsonElement element : root.getAsJsonArray("waypoints")) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject object = element.getAsJsonObject();
                Waypoint waypoint = new Waypoint();
                waypoint.name = object.has("name") ? object.get("name").getAsString() : "Point";
                waypoint.x = object.has("x") ? object.get("x").getAsDouble() : 0.0D;
                waypoint.y = object.has("y") ? object.get("y").getAsDouble() : 0.0D;
                waypoint.z = object.has("z") ? object.get("z").getAsDouble() : 0.0D;
                LIST.add(waypoint);
            }
        } catch (Exception e) {
            LOGGER.error("Impossible de charger les waypoints NTH Utils", e);
        }
    }

    private static final class Player {
        final double x;
        final double y;
        final double z;
        final Double yaw;

        Player(double x, double y, double z, Double yaw) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
        }

        static Player of(Object game) {
            try {
                Object player = game.getClass().getField("player").get(game);
                if (player == null) {
                    return null;
                }
                Class<?> playerClass = player.getClass();
                double x = ((Number) playerClass.getMethod("getX").invoke(player)).doubleValue();
                double y = ((Number) playerClass.getMethod("getY").invoke(player)).doubleValue();
                double z = ((Number) playerClass.getMethod("getZ").invoke(player)).doubleValue();
                Double yaw = numberOrNull(playerClass, player, "getYaw", "getYRot");
                return new Player(x, y, z, yaw);
            } catch (Exception e) {
                return null;
            }
        }
    }

    private static Double numberOrNull(Class<?> owner, Object instance, String... methodNames) {
        Class<?> type = owner;
        while (type != null) {
            for (String name : methodNames) {
                try {
                    return ((Number) type.getMethod(name).invoke(instance)).doubleValue();
                } catch (Exception ignored) {
                }
            }
            type = type.getSuperclass();
        }
        return null;
    }
}
