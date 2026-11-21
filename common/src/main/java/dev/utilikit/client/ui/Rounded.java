package dev.utilikit.client.ui;

public final class Rounded {
    public interface Canvas {
        void fill(int x1, int y1, int x2, int y2, int color);
    }

    private Rounded() {
    }

    public static void rect(Canvas canvas, int x1, int y1, int x2, int y2, int r, int argb) {
        if (canvas == null || x2 <= x1 || y2 <= y1) {
            return;
        }
        int w = x2 - x1;
        int h = y2 - y1;
        r = Math.max(0, Math.min(r, Math.min(w, h) / 2));
        if (r == 0) {
            canvas.fill(x1, y1, x2, y2, argb);
            return;
        }
        canvas.fill(x1 + r, y1, x2 - r, y2, argb);
        canvas.fill(x1, y1 + r, x1 + r, y2 - r, argb);
        canvas.fill(x2 - r, y1 + r, x2, y2 - r, argb);
        cornerFill(canvas, x1, y1, r, 1, 1, argb);
        cornerFill(canvas, x2, y1, r, -1, 1, argb);
        cornerFill(canvas, x1, y2, r, 1, -1, argb);
        cornerFill(canvas, x2, y2, r, -1, -1, argb);
    }

    public static void stroke(Canvas canvas, int x1, int y1, int x2, int y2, int r, int argb) {
        stroke(canvas, x1, y1, x2, y2, r, argb, 1);
    }

    public static void stroke(Canvas canvas, int x1, int y1, int x2, int y2, int r, int argb, int thickness) {
        if (canvas == null || x2 <= x1 || y2 <= y1 || thickness <= 0) {
            return;
        }
        int w = x2 - x1;
        int h = y2 - y1;
        r = Math.max(0, Math.min(r, Math.min(w, h) / 2));
        if (r == 0) {
            canvas.fill(x1, y1, x2, y1 + thickness, argb);
            canvas.fill(x1, y2 - thickness, x2, y2, argb);
            canvas.fill(x1, y1 + thickness, x1 + thickness, y2 - thickness, argb);
            canvas.fill(x2 - thickness, y1 + thickness, x2, y2 - thickness, argb);
            return;
        }
        canvas.fill(x1 + r, y1, x2 - r, y1 + thickness, argb);
        canvas.fill(x1 + r, y2 - thickness, x2 - r, y2, argb);
        canvas.fill(x1, y1 + r, x1 + thickness, y2 - r, argb);
        canvas.fill(x2 - thickness, y1 + r, x2, y2 - r, argb);
        cornerRing(canvas, x1, y1, r, thickness, 1, 1, argb);
        cornerRing(canvas, x2, y1, r, thickness, -1, 1, argb);
        cornerRing(canvas, x1, y2, r, thickness, 1, -1, argb);
        cornerRing(canvas, x2, y2, r, thickness, -1, -1, argb);
    }

    public static void glow(Canvas canvas, int x1, int y1, int x2, int y2, int r, int argb, int layers, float alpha) {
        if (canvas == null || layers <= 0 || alpha <= 0.0F) {
            return;
        }
        for (int i = 1; i <= layers; i++) {
            float fade = 1.0F - i / (float) (layers + 1);
            int rgb = argb & 0x00FFFFFF;
            int a = Math.max(0, Math.min(255, Math.round(((argb >>> 24) & 0xFF) * alpha * fade)));
            stroke(canvas, x1 - i, y1 - i, x2 + i, y2 + i, r + i, (a << 24) | rgb);
        }
    }

    private static void cornerFill(Canvas canvas, int ex, int ey, int r, int sx, int sy, int argb) {
        for (int i = 0; i < r; i++) {
            int colX = sx > 0 ? ex + i : ex - 1 - i;
            double dx = r - i - 0.5D;
            double dy = Math.sqrt(r * (double) r - dx * dx);
            double boundary = sy > 0 ? ey + r - dy : ey - r + dy;
            double frac = boundary - Math.floor(boundary);
            if (sy > 0) {
                int top = (int) Math.ceil(boundary);
                if (top < ey + r) {
                    canvas.fill(colX, top, colX + 1, ey + r, argb);
                }
                coveragePixel(canvas, colX, top - 1, 1.0D - frac, argb);
            } else {
                int stop = (int) Math.floor(boundary);
                if (stop > ey - r) {
                    canvas.fill(colX, ey - r, colX + 1, stop, argb);
                }
                coveragePixel(canvas, colX, stop, frac, argb);
            }
        }
    }

    private static void cornerRing(Canvas canvas, int ex, int ey, int r, int t, int sx, int sy, int argb) {
        for (int i = 0; i < r; i++) {
            int colX = sx > 0 ? ex + i : ex - 1 - i;
            double dx = r - i - 0.5D;
            double outerDy = Math.sqrt(r * (double) r - dx * dx);
            double innerDy = dx < r - 1.0D ? Math.sqrt((r - t) * (double) (r - t) - dx * dx) : 0.0D;
            double outer = sy > 0 ? ey + r - outerDy : ey - r + outerDy;
            double inner = sy > 0 ? ey + r - innerDy : ey - r + innerDy;
            double from = Math.min(outer, inner);
            double to = Math.max(outer, inner);
            if (sx != 0 && dx >= r - t) {
                from = sy > 0 ? outer : ey - r;
                to = sy > 0 ? ey + r : outer;
            }
            if (sy > 0) {
                int top = (int) Math.ceil(from);
                int stop = Math.max(top, (int) Math.round(to));
                if (top < stop) {
                    canvas.fill(colX, top, colX + 1, stop, argb);
                }
                coveragePixel(canvas, colX, top - 1, 1.0D - (outer - Math.floor(outer)), argb);
            } else {
                int base = (int) Math.ceil(inner);
                int stop = Math.max(base, (int) Math.floor(outer));
                if (base < stop) {
                    canvas.fill(colX, base, colX + 1, stop, argb);
                }
                coveragePixel(canvas, colX, stop, outer - Math.floor(outer), argb);
            }
        }
    }

    private static void coveragePixel(Canvas canvas, int x, int y, double coverage, int argb) {
        int a = (int) Math.round(coverage * ((argb >>> 24) & 0xFF));
        if (y < y + 1 && a > 2) {
            canvas.fill(x, y, x + 1, y + 1, (a << 24) | (argb & 0x00FFFFFF));
        }
    }
}
