package dev.utilikit.client.modules.extra;

public final class Chrono {
    private static long startAt;

    private Chrono() {
    }

    public static String line() {
        if (startAt == 0L) {
            startAt = System.currentTimeMillis();
        }
        long ms = System.currentTimeMillis() - startAt;
        return String.format(java.util.Locale.ROOT, "Chrono : %02d:%02d.%d",
                ms / 60000L, (ms / 1000L) % 60L, (ms / 100L) % 10L);
    }

    public static void reset() {
        startAt = System.currentTimeMillis();
    }
}
