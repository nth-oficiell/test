package dev.utilikit.client.modules.extra;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleManager;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

public final class ChatLog {
    private static final Logger LOGGER = LoggerFactory.getLogger("utilikit");
    private static final Set<String> SEEN = new HashSet<>();
    private static final long MAX_BYTES = 1024L * 1024L;
    private static int counter;

    private ChatLog() {
    }

    public static void tick(Object game) {
        Module module = ModuleManager.get("chatlog");
        if (module == null || !module.enabled) {
            return;
        }
        counter++;
        if (counter % 10 != 0) {
            return;
        }
        try {
            Object hud = field(game, "inGameHud", "gui");
            if (hud == null) {
                return;
            }
            Object chat = method(hud, "getChatHud");
            if (chat == null) {
                chat = field(hud, "chat");
            }
            if (chat == null) {
                return;
            }
            Object messages = field(chat, "messages");
            if (!(messages instanceof Iterable<?> list)) {
                return;
            }
            Path file = file();
            for (Object element : list) {
                String line = stringOf(element);
                if (line == null || line.isEmpty() || SEEN.contains(line)) {
                    continue;
                }
                SEEN.add(line);
                Files.writeString(file, line + System.lineSeparator(),
                        StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
            if (SEEN.size() > 400) {
                SEEN.clear();
            }
            try {
                if (Files.exists(file) && Files.size(file) > MAX_BYTES) {
                    Files.writeString(file, "", StandardCharsets.UTF_8);
                }
            } catch (Exception ignored) {
            }
        } catch (Throwable e) {
            LOGGER.debug("Log de chat indisponible", e);
        }
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("utilikit-chatlog.txt");
    }

    private static Object field(Object owner, String... fieldNames) {
        for (String fieldName : fieldNames) {
            try {
                Object value = owner.getClass().getField(fieldName).get(owner);
                if (value != null) {
                    return value;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private static Object method(Object owner, String... methodNames) {
        for (String methodName : methodNames) {
            try {
                Object value = owner.getClass().getMethod(methodName).invoke(owner);
                if (value != null) {
                    return value;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private static String stringOf(Object element) {
        Object value = method(element, "getString", "getText", "text", "content");
        if (value instanceof String string) {
            return string;
        }
        if (value != null) {
            return value.toString();
        }
        return null;
    }
}
