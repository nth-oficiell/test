package dev.utilikit.client.modules.extra;

import dev.utilikit.client.modules.ActionModule;
import dev.utilikit.client.modules.InfoModule;
import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;
import dev.utilikit.client.modules.TickModule;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

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
        ModuleManager.register(autoMessageModule());
        ModuleManager.register(autoReconnectModule());
        ModuleManager.register(hudPositionModule());
        Waypoints.load();
    }

    public static void tick(Object game) {
        CPS.tick(game);
        Speed.tick(game);
        AutoReconnect.tick(game);
    }

    private static ActionModule autoMessageModule() {
        ActionModule module = new ActionModule("automsg", "Message auto",
                "Envoie ton message configurable dans le chat. Active-le depuis le menu ou assigne-lui une touche.",
                ModuleCategory.PRATIQUE) {
            @Override
            protected void execute() {
                try {
                    Object game = clientInstance();
                    Setting.Text message = textSetting();
                    if (game == null || message == null || message.value.isBlank()) {
                        Notifier.push("Message auto : écris ton message dans les paramètres");
                        return;
                    }
                    sendChatMessage(game, message.value);
                } catch (Throwable ignored) {
                }
            }
        };
        module.settings.add(new Setting.Text("msg", "Message", "gg"));
        return module;
    }

    private static Module autoReconnectModule() {
        Module module = new TickModule("autoreconnect", "Reconnexion auto",
                "Expérimental : après une déconnexion, tente de te reconnecter au dernier serveur après le délai réglé.",
                ModuleCategory.PRATIQUE,
                game -> {
                });
        module.settings.add(new Setting.Slider("delay", "Délai (secondes)", 1.0D, 10.0D, 4.0D));
        return module;
    }

    private static Module hudPositionModule() {
        Module module = new TickModule("hudpos", "Position du HUD",
                "Décale toute la colonne d'infos du HUD (offsets X et Y). Le module doit être activé.",
                ModuleCategory.VISUEL,
                game -> {
                });
        module.settings.add(new Setting.Slider("x", "Décalage X", 0.0D, 300.0D, 0.0D));
        module.settings.add(new Setting.Slider("y", "Décalage Y", 0.0D, 400.0D, 0.0D));
        return module;
    }

    public static int[] hudOffset() {
        Module module = ModuleManager.get("hudpos");
        if (module == null || !module.enabled) {
            return new int[] {0, 0};
        }
        Setting.Slider sx = module.slider("x");
        Setting.Slider sy = module.slider("y");
        return new int[] {
                sx == null ? 0 : (int) Math.round(sx.value),
                sy == null ? 0 : (int) Math.round(sy.value),
        };
    }

    static Object clientInstance() throws Exception {
        Class<?> type = null;
        try {
            type = Class.forName("net.minecraft.client.MinecraftClient");
        } catch (Exception ignored) {
        }
        if (type == null) {
            type = Class.forName("net.minecraft.client.Minecraft");
        }
        return type.getMethod("getInstance").invoke(null);
    }

    static Setting.Text textSetting() {
        Module module = ModuleManager.get("automsg");
        return module == null ? null : module.text("msg");
    }

    static void sendChatMessage(Object game, String message) {
        try {
            Object player = game.getClass().getField("player").get(game);
            if (player == null) {
                Notifier.push("Message auto : aucune partie en cours");
                return;
            }
            Object handler = null;
            for (String fieldName : new String[] {"networkHandler", "connection"}) {
                try {
                    handler = player.getClass().getField(fieldName).get(player);
                    if (handler != null) {
                        break;
                    }
                } catch (Exception ignored) {
                }
            }
            if (handler == null) {
                Notifier.push("Message auto : envoi non supporté ici");
                return;
            }
            for (String methodName : new String[] {"sendChatMessage", "sendChat"}) {
                try {
                    handler.getClass().getMethod(methodName, String.class).invoke(handler, message);
                    return;
                } catch (Exception ignored) {
                }
            }
            Notifier.push("Message auto : envoi non supporté ici");
        } catch (Throwable ignored) {
        }
    }

    private static final class AutoReconnect {
        private static boolean waiting;
        private static long when;
        private static long lastInWorld;

        static void tick(Object game) {
            try {
                Module module = ModuleManager.get("autoreconnect");
                if (module == null || !module.enabled) {
                    waiting = false;
                    return;
                }
                Object player = null;
                try {
                    player = game.getClass().getField("player").get(game);
                } catch (Exception ignored) {
                }
                long now = System.currentTimeMillis();
                if (player != null) {
                    lastInWorld = now;
                    waiting = false;
                    return;
                }
                if (lastInWorld == 0L || now - lastInWorld > 120000L) {
                    return;
                }
                Object screen = null;
                for (String fieldName : new String[] {"currentScreen", "screen"}) {
                    try {
                        screen = game.getClass().getField(fieldName).get(game);
                        if (screen != null) {
                            break;
                        }
                    } catch (Exception ignored) {
                    }
                }
                boolean disconnected = screen != null
                        && screen.getClass().getSimpleName().contains("Disconnected");
                if (!waiting) {
                    if (disconnected) {
                        waiting = true;
                        Setting.Slider delay = module.slider("delay");
                        when = now + (long) (delay == null ? 4.0D : Math.max(delay.min, Math.min(delay.max, delay.value))) * 1000L;
                    }
                    return;
                }
                if (!disconnected) {
                    waiting = false;
                    return;
                }
                if (now < when) {
                    return;
                }
                waiting = false;
                rejoin(game, screen);
            } catch (Throwable ignored) {
            }
        }

        private static void rejoin(Object game, Object screen) {
            try {
                Object entry = null;
                for (String fieldName : new String[] {"currentServerEntry", "previousServer", "lastServerEntry"}) {
                    try {
                        entry = game.getClass().getField(fieldName).get(game);
                        if (entry != null) {
                            break;
                        }
                    } catch (Exception ignored) {
                    }
                }
                if (entry == null) {
                    Notifier.push("Reconnexion impossible : serveur inconnu");
                    return;
                }
                String addressText = null;
                for (String fieldName : new String[] {"address", "ip"}) {
                    try {
                        addressText = (String) entry.getClass().getField(fieldName).get(entry);
                        if (addressText != null) {
                            break;
                        }
                    } catch (Exception ignored) {
                    }
                }
                Class<?> serverAddress = forNames("net.minecraft.client.network.ServerAddress",
                        "net.minecraft.client.multiplayer.resolver.ServerAddress");
                Object parsed = null;
                if (serverAddress != null && addressText != null) {
                    try {
                        parsed = serverAddress.getMethod("parse", String.class).invoke(null, addressText);
                    } catch (Exception ignored) {
                    }
                }
                Class<?> connectScreen = forNames("net.minecraft.client.gui.screen.multiplayer.ConnectScreen",
                        "net.minecraft.client.gui.screens.ConnectScreen");
                if (connectScreen == null) {
                    Notifier.push("Reconnexion non supportée sur cette version");
                    return;
                }
                for (Method method : connectScreen.getMethods()) {
                    if (!"connect".equals(method.getName()) || !Modifier.isStatic(method.getModifiers())) {
                        continue;
                    }
                    Class<?>[] parameters = method.getParameterTypes();
                    if (parameters.length < 4 || parameters.length > 8) {
                        continue;
                    }
                    Object[] args = new Object[parameters.length];
                    boolean assignable = true;
                    for (int i = 0; i < parameters.length; i++) {
                        Class<?> parameter = parameters[i];
                        if (parameter == boolean.class) {
                            args[i] = false;
                        } else if (parameter == int.class) {
                            args[i] = 0;
                        } else if (parameter == long.class) {
                            args[i] = 0L;
                        } else if (parameter == double.class || parameter == float.class) {
                            args[i] = 0.0D;
                        } else if (i == 0) {
                            args[i] = screen;
                        } else if (i == 1) {
                            args[i] = game;
                        } else if (parsed != null && parameter.isAssignableFrom(parsed.getClass())) {
                            args[i] = parsed;
                        } else if (entry != null && parameter.isAssignableFrom(entry.getClass())) {
                            args[i] = entry;
                        } else if (parameter.isAssignableFrom(game.getClass())) {
                            args[i] = game;
                        } else {
                            assignable = false;
                            break;
                        }
                    }
                    if (!assignable) {
                        continue;
                    }
                    try {
                        method.invoke(null, args);
                        Notifier.push("Reconnexion en cours\u2026");
                        return;
                    } catch (Throwable ignored) {
                    }
                }
                Notifier.push("Reconnexion non supportée sur cette version");
            } catch (Throwable ignored) {
            }
        }

        private static Class<?> forNames(String... names) {
            for (String name : names) {
                try {
                    return Class.forName(name);
                } catch (Exception ignored) {
                }
            }
            return null;
        }
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
