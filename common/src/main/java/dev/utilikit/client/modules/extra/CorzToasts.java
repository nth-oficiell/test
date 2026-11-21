package dev.utilikit.client.modules.extra;

import dev.utilikit.client.ui.CorzMenu;
import dev.utilikit.client.ui.Palettes;
import dev.utilikit.client.ui.Rounded;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Toasts "Module Activé / Désactivé" — recopiés du système de notifications de
 * CorzClient (classe liiiiiiiiiiiii + Iliiiiilliiiii décompilées) :
 * hauteur 34, vie 2000 ms, disparition 150 ms (ease-out quadratique),
 * apparition 130 ms ease-out + rebond d'échelle amorti sur 220 ms,
 * lissage de position exponentiel (exp(-16·dt)), empilement bas-droite
 * (marge 8 px, écart 4 px), maximum 4 toasts simultanés,
 * icône arrondie 20x20 (coin 6), barre de progression 3 px, dédoublonnage.
 */
public final class CorzToasts {
    public static final int HEIGHT = 34;
    private static final long LIFE_MS = 2000L;
    private static final long FADE_MS = 150L;
    private static final long SLIDE_MS = 130L;
    private static final long BOUNCE_MS = 220L;
    private static final float BOUNCE_AMP = 0.14F;
    private static final int MAX_TOASTS = 4;
    private static final int GAP = 4;
    private static final int MARGIN = 8;
    private static final int PAD = 8;
    private static final int ICON = 20;

    private static final int BG = 0xCB070E08;
    private static final int BG_STROKE = 0x28FFFFFF;
    private static final int TITLE_COLOR = 0xFFF1F1F5;
    private static final int SUB_COLOR = 0xFF8B8B9B;
    private static final int ICON_OFF = 0xFF3F3F4A;
    private static final int BAR_OFF = 0xFF8B8B9B;

    private static final List<Toast> TOASTS = new ArrayList<>();

    private CorzToasts() {
    }

    public static void push(String title, String subtitle, boolean enabled) {
        if (title == null || title.isEmpty()) {
            return;
        }
        long now = System.nanoTime();
        for (Toast toast : TOASTS) {
            if (toast.matches(title, subtitle, enabled)) {
                toast.reborn(now);
                return;
            }
        }
        long alive = 0L;
        for (Toast toast : TOASTS) {
            if (!toast.dying(new ToastClock(now))) {
                alive++;
            }
        }
        if (alive >= MAX_TOASTS) {
            for (Toast toast : TOASTS) {
                if (!toast.dying(new ToastClock(now))) {
                    toast.markDying(now);
                    break;
                }
            }
        }
        TOASTS.add(new Toast(title, textOrEmpty(subtitle), enabled, now));
    }

    public static boolean render(Rounded.Canvas cv, CorzMenu.Pen pen, double screenW, double screenH) {
        long now = System.nanoTime();
        Iterator<Toast> iterator = TOASTS.iterator();
        while (iterator.hasNext()) {
            Toast toast = iterator.next();
            if (toast.gone(new ToastClock(now))) {
                iterator.remove();
            }
        }
        if (TOASTS.isEmpty() || pen == null) {
            return false;
        }
        float right = (float) screenW - MARGIN;
        float bottom = (float) screenH - MARGIN;
        float cur = bottom;
        for (int i = TOASTS.size() - 1; i >= 0; i--) {
            Toast toast = TOASTS.get(i);
            toast.layout(cv, pen, now, right, cur);
            cur = cur - (HEIGHT + GAP) * toast.visFactor(new ToastClock(now));
        }
        return true;
    }

    private static String textOrEmpty(String s) {
        return s == null ? "" : s;
    }

    private static int withAlpha(int argb, float a) {
        int alpha = (int) (((argb >>> 24) & 0xFF) * a);
        if (alpha < 0) {
            alpha = 0;
        } else if (alpha > 255) {
            alpha = 255;
        }
        return (alpha << 24) | (argb & 0xFFFFFF);
    }

    private static float clamp01(float t) {
        return t < 0.0F ? 0.0F : (t > 1.0F ? 1.0F : t);
    }

    private static float easeOut(float t) {
        t = clamp01(t);
        return 1.0F - (1.0F - t) * (1.0F - t) * (1.0F - t);
    }

    private static final class ToastClock {
        final long now;

        ToastClock(long now) {
            this.now = now;
        }
    }

    private static final class Toast {
        final String title;
        final String subtitle;
        final boolean enabled;
        final long spawnOriginal;
        long spawnAt;
        long dyingSince;
        int width = 140;
        float smoothRight = Float.NaN;
        long lastFrame;

        Toast(String title, String subtitle, boolean enabled, long now) {
            this.title = title;
            this.subtitle = subtitle;
            this.enabled = enabled;
            this.spawnOriginal = now;
            this.spawnAt = now;
            this.lastFrame = now;
        }

        boolean matches(String t, String s, boolean e) {
            return enabled == e && title.equals(t) && subtitle.equals(s == null ? "" : s);
        }

        void reborn(long now) {
            spawnAt = now;
            dyingSince = 0L;
        }

        void markDying(long now) {
            if (dyingSince == 0L) {
                dyingSince = now;
            }
        }

        boolean dying(ToastClock clock) {
            if (dyingSince != 0L) {
                return true;
            }
            return (clock.now - spawnAt) / 1_000_000L >= LIFE_MS;
        }

        boolean gone(ToastClock clock) {
            return dyingSince != 0L
                    && (clock.now - dyingSince) / 1_000_000L >= FADE_MS;
        }

        float visFactor(ToastClock clock) {
            if (dyingSince == 0L) {
                return 1.0F;
            }
            float t = (clock.now - dyingSince) / 1_000_000.0F / FADE_MS;
            float e = clamp01(t);
            return 1.0F - e * e;
        }

        void layout(Rounded.Canvas cv, CorzMenu.Pen pen, long now, float right, float bottomEdge) {
            long ageMs = (now - spawnAt) / 1_000_000L;

            int titleW = pen.width(title);
            int subW = subtitle.isEmpty() ? 0 : pen.width(subtitle);
            int textW = Math.round(Math.max(titleW * 0.95F, subW * 0.82F));
            width = Math.min(280, Math.max(140, PAD + ICON + PAD + 8 + textW + PAD));

            if (Float.isNaN(smoothRight)) {
                smoothRight = right + width + MARGIN;
            }
            float dtMs = Math.max(1.0F, (now - lastFrame) / 1_000_000.0F);
            lastFrame = now;
            float approach = 1.0F - (float) Math.exp(-16.0F * dtMs / 1000.0F);
            smoothRight += (right - smoothRight) * approach;
            if (Math.abs(right - smoothRight) < 0.01F) {
                smoothRight = right;
            }

            float vis = visFactor(new ToastClock(now));
            if (vis <= 0.001F) {
                return;
            }

            float slide = easeOut(ageMs / (float) SLIDE_MS);
            float bounceT = Math.min(1.0F, ageMs / (float) BOUNCE_MS);
            float scale = 1.0F
                    + BOUNCE_AMP * (float) Math.sin(Math.PI * bounceT) * (1.0F - bounceT) * (1.0F - bounceT);
            scale = 0.94F + 0.06F * scale * scale * (2.0F - scale);

            float drawRight = smoothRight - (1.0F - slide) * (width + MARGIN);
            float drawTop = bottomEdge - HEIGHT;
            float cx = drawRight - width / 2.0F;
            float cy = drawTop + HEIGHT / 2.0F;
            float x1 = cx + (0.0F - width / 2.0F) * scale;
            float y1 = cy + (0.0F - HEIGHT / 2.0F) * scale;
            float x2 = cx + (width / 2.0F) * scale;
            float y2 = cy + (HEIGHT / 2.0F) * scale;
            int a1 = (int) x1;
            int b1 = (int) y1;
            int a2 = (int) x2;
            int b2 = (int) y2;

            Rounded.rect(cv, a1, b1, a2, b2, 8, withAlpha(BG, vis));
            Rounded.stroke(cv, a1, b1, a2, b2, 8, withAlpha(BG_STROKE, vis));

            int iconX = a1 + PAD;
            int iconY = b1 + 7;
            int iconBg = enabled ? withAlpha(Palettes.accent(), 0.96F * vis) : withAlpha(ICON_OFF, vis);
            Rounded.rect(cv, iconX, iconY, iconX + ICON, iconY + ICON, 6, iconBg);
            if (enabled) {
                Rounded.rect(cv, iconX + 5, iconY + 10, iconX + 8, iconY + 13, 1, withAlpha(0xFFFFFFFF, vis));
                Rounded.rect(cv, iconX + 7, iconY + 8, iconX + 10, iconY + 14, 1, withAlpha(0xFFFFFFFF, vis));
                Rounded.rect(cv, iconX + 9, iconY + 5, iconX + 12, iconY + 12, 1, withAlpha(0xFFFFFFFF, vis));
                Rounded.rect(cv, iconX + 11, iconY + 6, iconX + 14, iconY + 9, 1, withAlpha(0xFFFFFFFF, vis));
                Rounded.rect(cv, iconX + 5, iconY + 10, iconX + 12, iconY + 12, 1, withAlpha(0xFFFFFFFF, vis));
            } else {
                Rounded.rect(cv, iconX + 5, iconY + 9, iconX + 15, iconY + 11, 1, withAlpha(0xFFFFFFFF, vis));
            }

            int textX = iconX + ICON + PAD;
            pen.text(title, textX, b1 + (HEIGHT - 22) / 2 + 1, withAlpha(TITLE_COLOR, vis));
            if (!subtitle.isEmpty()) {
                pen.text(subtitle, textX, b1 + (HEIGHT - 22) / 2 + 12, withAlpha(SUB_COLOR, vis));
            }

            int barX = a1 + PAD;
            int barY = b2 - PAD + 3 - 3;
            int barMaxW = width - PAD * 2;
            float lifeT = clamp01(ageMs / (float) LIFE_MS);
            int barW = Math.round(barMaxW * (1.0F - lifeT));
            if (barW > 0) {
                int barColor = enabled ? withAlpha(Palettes.accent(), 0.70F * vis) : withAlpha(BAR_OFF, 0.70F * vis);
                Rounded.rect(cv, barX, barY, barX + barW, barY + 3, 1, barColor);
            }
        }
    }
}
