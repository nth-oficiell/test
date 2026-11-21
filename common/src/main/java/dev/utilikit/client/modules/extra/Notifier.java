package dev.utilikit.client.modules.extra;

/**
 * File de messages NTH — désormais affichée via les toasts façon CorzClient
 * (voir CorzToasts). Le texte "Titre : détail" est découpé sur " : ".
 */
public final class Notifier {
    private Notifier() {
    }

    public static void push(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        int sep = text.indexOf(" : ");
        if (sep > 0) {
            CorzToasts.push(text.substring(0, sep), text.substring(sep + 3), true);
        } else {
            CorzToasts.push("NTH Utils", text, true);
        }
    }

    public static void push(String text, long durationMs) {
        push(text);
    }
}
