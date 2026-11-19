package dev.utilikit.client.modules.extra;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Notifier {
    private static final List<Note> NOTES = new ArrayList<>();

    private Notifier() {
    }

    public static void push(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        push(text, 2200L);
    }

    public static void push(String text, long durationMs) {
        long now = System.currentTimeMillis();
        for (Note note : NOTES) {
            if (note.text.equals(text)) {
                note.expires = now + durationMs;
                return;
            }
        }
        NOTES.add(new Note(text, now + durationMs));
        if (NOTES.size() > 5) {
            NOTES.remove(0);
        }
    }

    public static List<String> active() {
        List<String> texts = new ArrayList<>();
        long now = System.currentTimeMillis();
        Iterator<Note> iterator = NOTES.iterator();
        while (iterator.hasNext()) {
            Note note = iterator.next();
            if (note.expires <= now) {
                iterator.remove();
            } else {
                texts.add(note.text);
            }
        }
        return texts;
    }

    private static final class Note {
        final String text;
        final long expires;

        Note(String text, long expires) {
            this.text = text;
            this.expires = expires;
        }
    }
}
