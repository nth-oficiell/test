package dev.utilikit.client.modules.extra;

import dev.utilikit.client.modules.InfoModule;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.ModuleManager;

public final class ExtraModules {
    private static final String[] ATTACK_FIELDS = {"attackKey", "keyAttack", "key_mouseLeft"};
    private static final String[] USE_FIELDS = {"useKey", "keyUse", "key_mouseRight"};
    private static final String[] PRESSED_METHODS = {"isPressed", "isDown"};

    private static final int WINDOW = 20;
    private static final int[] CLICKS_LEFT = new int[WINDOW];
    private static final int[] CLICKS_RIGHT = new int[WINDOW];
    private static int clicksIndex;
    private static boolean prevLeft;
    private static boolean prevRight;

    private static double lastX;
    private static double lastZ;
    private static double kmh;

    private ExtraModules() {
    }

    public static void register() {
        ModuleManager.register(cpsModule());
        ModuleManager.register(speedKmhModule());
        ModuleManager.register(waypointsModule());
        Waypoints.load();
    }

    public static void tick(Object game) {
        CPS.tick(game);
        Speed.tick(game);
    }

    private static InfoModule cpsModule() {
        return new InfoModule("cps", "CPS",
                "Affiche le nombre de clics par seconde (clic gauche et clic droit).",
                ModuleCategory.AFFICHAGE,
                game -> {
                    int[] counts = CPS.snapshot();
                    return "CPS : " + counts[0] + " G · " + counts[1] + " D";
                });
    }

    private static InfoModule speedKmhModule() {
        return new InfoModule("speedkmh", "Vitesse km/h",
                "Affiche ta vitesse horizontale en kilomètres par heure.",
                ModuleCategory.AFFICHAGE,
                game -> String.format("Vitesse : %.1f km/h", kmh));
    }

    private static InfoModule waypointsModule() {
        return new InfoModule("waypoints", "Waypoints",
                "Affiche tes waypoints enregistrés avec la distance et la direction. "
                        + "Dans les paramètres du module : Ajouter ici / Retirer le dernier / Tout effacer.",
                ModuleCategory.AFFICHAGE,
                game -> Waypoints.line(game));
    }

    private static final class CPS {
        static void tick(Object game) {
            clicksIndex = (clicksIndex + 1) % WINDOW;
            CLICKS_LEFT[clicksIndex] = 0;
            CLICKS_RIGHT[clicksIndex] = 0;
            Boolean left = keyHeld(game, ATTACK_FIELDS);
            Boolean right = keyHeld(game, USE_FIELDS);
            if (left == null || right == null) {
                prevLeft = left != null;
                prevRight = right != null;
                return;
            }
            if (left && !prevLeft) {
                CLICKS_LEFT[clicksIndex]++;
            }
            if (right && !prevRight) {
                CLICKS_RIGHT[clicksIndex]++;
            }
            prevLeft = left;
            prevRight = right;
        }

        static int[] snapshot() {
            int left = 0;
            int right = 0;
            for (int i = 0; i < WINDOW; i++) {
                left += CLICKS_LEFT[i];
                right += CLICKS_RIGHT[i];
            }
            return new int[] {left, right};
        }

        private static Boolean keyHeld(Object game, String[] fieldNames) {
            try {
                Object options = game.getClass().getField("options").get(game);
                if (options == null) {
                    return null;
                }
                Object key = null;
                for (String fieldName : fieldNames) {
                    try {
                        key = options.getClass().getField(fieldName).get(options);
                        if (key != null) {
                            break;
                        }
                    } catch (Exception ignored) {
                    }
                }
                if (key == null) {
                    return null;
                }
                for (String methodName : PRESSED_METHODS) {
                    try {
                        return (Boolean) key.getClass().getMethod(methodName).invoke(key);
                    } catch (Exception ignored) {
                    }
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        }
    }

    private static final class Speed {
        static void tick(Object game) {
            double[] pos = position(game);
            if (pos == null) {
                kmh = 0.0D;
                return;
            }
            if (lastX == 0.0D && lastZ == 0.0D) {
                lastX = pos[0];
                lastZ = pos[2];
                return;
            }
            double dx = pos[0] - lastX;
            double dz = pos[2] - lastZ;
            lastX = pos[0];
            lastZ = pos[2];
            kmh = Math.sqrt(dx * dx + dz * dz) * 20.0D * 3.6D;
        }

        private static double[] position(Object game) {
            try {
                Object player = game.getClass().getField("player").get(game);
                if (player == null) {
                    return null;
                }
                Class<?> type = player.getClass();
                double x = ((Number) type.getMethod("getX").invoke(player)).doubleValue();
                double y = ((Number) type.getMethod("getY").invoke(player)).doubleValue();
                double z = ((Number) type.getMethod("getZ").invoke(player)).doubleValue();
                return new double[] {x, y, z};
            } catch (Exception e) {
                return null;
            }
        }
    }
}
