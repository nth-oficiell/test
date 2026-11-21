package dev.utilikit.client.ui;

import dev.utilikit.client.config.PresetManager;
import dev.utilikit.client.modules.KeybindEngine;
import dev.utilikit.client.modules.KeyNames;
import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleActions;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class CorzMenu {
    public String versionLabel = "";
    public interface Pen {
        void text(String s, double x, double y, int argb);

        void textCenter(String s, double x, double y, int argb);

        int width(String s);
    }

    public enum Mode {
        MODULES,
        PRESETS
    }

    public static final class Row {
        public final Module mod;
        public final ModuleCategory cat;
        public final int y;
        public final int h;

        Row(Module mod, ModuleCategory cat, int y, int h) {
            this.mod = mod;
            this.cat = cat;
            this.y = y;
            this.h = h;
        }
    }

    public static final class PresetRow {
        public final String name;
        public final int y;
        public final int applyX1;
        public final int applyX2;
        public final int delX1;
        public final int delX2;

        PresetRow(String name, int y, int applyX1, int applyX2, int delX1, int delX2) {
            this.name = name;
            this.y = y;
            this.applyX1 = applyX1;
            this.applyX2 = applyX2;
            this.delX1 = delX1;
            this.delX2 = delX2;
        }
    }

    public static final class ModalRow {
        public final String type;
        public final Setting setting;
        public final int y;
        public final int h;

        ModalRow(String type, Setting setting, int y, int h) {
            this.type = type;
            this.setting = setting;
            this.y = y;
            this.h = h;
        }
    }

    public static final class FieldBox {
        public final Setting.Text setting;
        public final int x;
        public final int y;
        public final int w;

        FieldBox(Setting.Text setting, int x, int y, int w) {
            this.setting = setting;
            this.x = x;
            this.y = y;
            this.w = w;
        }
    }

    private static final int RAIL_X = 10;
    private static final int RAIL_W = 108;
    private static final int RAIL_GAP = 5;
    private static final int PANEL_TOP = 32;
    private static final int PANEL_W = 236;
    private static final int ROW_H = 30;
    private static final int HEADER_H = 18;

    public Mode mode = Mode.MODULES;
    public ModuleCategory filter = null;
    public String search = "";
    public String presetName = "";
    public Module selected = null;
    public boolean binding = false;
    public String status = null;
    private long statusAt = 0L;

    public Runnable soundClick;
    public Runnable soundToggle;
    public Runnable requestClose;
    public Runnable onLayoutChanged;
    public Supplier<String> clipboardGet;
    public Consumer<String> clipboardSet;
    public Runnable waypointAddHere;
    public Runnable waypointRemoveLast;
    public Runnable waypointClear;

    private int w;
    private int h;
    private int railX = RAIL_X;
    private int railB = 22;
    private int[] railYs = new int[0];
    private int panelX;
    private int panelW;
    private int panelBottom = 200;
    private int listScroll;
    private int listMax;
    private int presetScroll;
    private int presetMax;
    private int modalX;
    private int modalW;
    private int modalTop;
    private int modalBottom;
    private int modalContentTop;
    private double mx = -1.0D;
    private double my = -1.0D;

    public final List<Row> rows = new ArrayList<>();
    public final List<PresetRow> presetRows = new ArrayList<>();
    public final List<ModalRow> modalRows = new ArrayList<>();
    public final List<FieldBox> textFields = new ArrayList<>();
    private int[] searchRect = new int[0];
    private int[] presetFieldRect = new int[0];
    private int[] saveRect = new int[0];
    private int[] importRect = new int[0];
    private int[] exportRect = new int[0];
    private int[] disableAllRect = new int[0];
    private int[] modalRect = new int[0];

    private Pen pen;

    public int railCount() {
        return ModuleCategory.values().length + 1;
    }

    public int searchFieldX() {
        return searchRect.length == 4 ? searchRect[0] : 0;
    }

    public int searchFieldY() {
        return searchRect.length == 4 ? searchRect[1] : 0;
    }

    public int searchFieldW() {
        return searchRect.length == 4 ? searchRect[2] : 0;
    }

    public int presetFieldX() {
        return presetFieldRect.length == 4 ? presetFieldRect[0] : 0;
    }

    public int presetFieldY() {
        return presetFieldRect.length == 4 ? presetFieldRect[1] : 0;
    }

    public int presetFieldW() {
        return presetFieldRect.length == 4 ? presetFieldRect[2] : 0;
    }

    public void status(String message) {
        this.status = message;
        this.statusAt = System.nanoTime();
    }

    public void layout(int width, int height, Pen measurePen) {
        this.pen = measurePen;
        this.w = width;
        this.h = height;
        int entries = railCount();
        railB = Math.max(16, Math.min(26, (height - 74 - (entries - 1) * RAIL_GAP) / entries));
        railYs = new int[entries];
        for (int i = 0; i <= 4 && i < entries; i++) {
            railYs[i] = 40 + i * (railB + RAIL_GAP);
        }
        if (entries > 5) {
            railYs[5] = railYs[4] + railB + 26;
        }
        panelW = Math.min(PANEL_W, Math.max(150, width - 158));
        // bloc menu (rail + panneau) centré horizontalement sur l'écran
        railX = Math.max(6, (width - (RAIL_W + 12 + panelW)) / 2);
        panelX = railX + RAIL_W + 12;
        panelBottom = Math.max(130, height - 32);
        buildFieldRects();
        buildModuleRows();
        buildPresetRows();
        buildModal();
    }

    private void buildFieldRects() {
        if (mode == Mode.MODULES) {
            searchRect = new int[]{panelX + 8, PANEL_TOP + 26, panelW - 16, 14};
        } else {
            searchRect = new int[0];
        }
        if (mode == Mode.PRESETS) {
            presetFieldRect = new int[]{panelX + 8, PANEL_TOP + 26, panelW - 116, 14};
            saveRect = panelW >= 150
                    ? new int[]{panelX + panelW - 100, PANEL_TOP + 22, 92, 20}
                    : new int[0];
            importRect = new int[]{panelX + 8, PANEL_TOP + 50, (panelW - 20) / 2, 18};
            exportRect = new int[]{panelX + 12 + (panelW - 20) / 2, PANEL_TOP + 50, (panelW - 20) / 2, 18};
            disableAllRect = new int[]{panelX + 8, panelBottom - 28, panelW - 16, 20};
        } else {
            presetFieldRect = new int[0];
            saveRect = new int[0];
            importRect = new int[0];
            exportRect = new int[0];
            disableAllRect = new int[0];
        }
    }

    private void buildModuleRows() {
        rows.clear();
        String needle = search == null ? "" : search.trim().toLowerCase();
        int y = PANEL_TOP + 52;
        if (filter == null) {
            for (ModuleCategory cat : ModuleManager.categories()) {
                boolean any = false;
                for (Module mod : ModuleManager.ordered()) {
                    if (mod.category == cat && matches(mod, needle)) {
                        any = true;
                        break;
                    }
                }
                if (!any) {
                    continue;
                }
                rows.add(new Row(null, cat, y, HEADER_H));
                y += HEADER_H + 3;
                for (Module mod : ModuleManager.ordered()) {
                    if (mod.category == cat && matches(mod, needle)) {
                        rows.add(new Row(mod, null, y, ROW_H));
                        y += ROW_H + 2;
                    }
                }
                y += 8;
            }
        } else {
            for (Module mod : ModuleManager.ordered()) {
                if (mod.category == filter && matches(mod, needle)) {
                    rows.add(new Row(mod, null, y, ROW_H));
                    y += ROW_H + 2;
                }
            }
        }
        int viewBottom = panelBottom - 26;
        listMax = Math.max(0, y + 4 - viewBottom);
        listScroll = Math.max(0, Math.min(listScroll, listMax));
    }

    private static boolean matches(Module mod, String needle) {
        return needle.isEmpty() || mod.name.toLowerCase().contains(needle);
    }

    private void buildPresetRows() {
        presetRows.clear();
        if (pen == null) {
            return;
        }
        int labelW = pen.width("Appliquer") + 12;
        int contentY = PANEL_TOP + 84;
        int viewBottom = panelBottom - 38;
        for (String name : PresetManager.names()) {
            presetRows.add(new PresetRow(name, contentY,
                    panelX + panelW - 28 - labelW * 2, panelX + panelW - 24 - labelW,
                    panelX + panelW - 18 - labelW, panelX + panelW - 12));
            contentY += 26;
        }
        presetMax = Math.max(0, contentY + 4 - viewBottom);
        presetScroll = Math.max(0, Math.min(presetScroll, presetMax));
    }

    private void buildModal() {
        modalRows.clear();
        textFields.clear();
        if (selected == null || pen == null) {
            modalRect = new int[0];
            return;
        }
        int freeRight = w - panelX - panelW - 20;
        if (freeRight >= 190) {
            modalW = Math.min(280, freeRight);
            modalX = panelX + panelW + 12;
        } else {
            modalW = Math.min(280, panelW - 8);
            modalX = panelX + (panelW - modalW) / 2;
        }
        if (modalX + modalW > w - 8) {
            modalW = w - modalX - 8;
        }
        modalTop = PANEL_TOP;
        int contentX = modalX + 12;
        int contentW = modalW - 24;
        int y = modalTop + 44;
        List<String> desc = wrap(selected.description, contentW, pen);
        y += desc.size() * 11;
        y += 12;
        modalContentTop = y;
        for (Setting setting : selected.settings) {
            if (setting instanceof Setting.Toggle) {
                modalRows.add(new ModalRow("toggle", setting, y, 20));
                y += 26;
            } else if (setting instanceof Setting.Slider) {
                modalRows.add(new ModalRow("slider", setting, y, 28));
                y += 32;
            } else if (setting instanceof Setting.Text) {
                modalRows.add(new ModalRow("text", setting, y, 20));
                textFields.add(new FieldBox((Setting.Text) setting, contentX, y + 10, contentW));
                y += 34;
            }
        }
        y += 6;
        modalRows.add(new ModalRow("keybind", null, y, 18));
        y += 22;
        modalRows.add(new ModalRow("unbind", null, y, 18));
        y += 22;
        modalRows.add(new ModalRow("favorite", null, y, 18));
        y += 22;
        if ("waypoints".equals(selected.id)) {
            if (waypointAddHere != null) {
                modalRows.add(new ModalRow("wp_add", null, y, 18));
                y += 22;
            }
            if (waypointRemoveLast != null) {
                modalRows.add(new ModalRow("wp_remove", null, y, 18));
                y += 22;
            }
            if (waypointClear != null) {
                modalRows.add(new ModalRow("wp_clear", null, y, 18));
                y += 22;
            }
        }
        modalBottom = Math.min(panelBottom, Math.max(modalTop + 170, y + 26));
        modalRect = new int[]{modalX, modalTop, modalX + modalW, modalBottom};
    }

    public void draw(Rounded.Canvas cv, double mouseX, double mouseY) {
        this.mx = mouseX;
        this.my = mouseY;
        float fade = MenuFx.easedOpen();
        int oy = -MenuFx.slideOffset(18);
        int accent = Palettes.accent();

        if (fade > 0.02F) {
            int a = (int) (0x88 * fade);
            if (a > 4) {
                cv.fill(0, 0, w, h, a << 24);
            }
        }

        if (pen == null) {
            return;
        }

        pen.text("\u00a7lNTH UTILS", railX + 2, 10 + oy, accent);
        if (!versionLabel.isEmpty()) {
            pen.text("\u00a7f" + versionLabel, railX + 2 + pen.width("NTH UTILS") + 5, 11 + oy, 0xFF6E6E82);
        }
        String time = java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        pen.text(time, panelX + panelW - pen.width(time), 10 + oy, 0xFF9A9AB0);

        drawRail(cv, oy, accent, fade);

        int t = PANEL_TOP + oy;
        int b = panelBottom + oy;
        drawPanelSurface(cv, panelX, t, panelX + panelW, b, 4, 0.10F);
        pen.text(panelTitle(), panelX + 10, t + 8, 0xFFFFFFFF);
        if (mode == Mode.MODULES) {
            pen.text("\u00a77Recherche\u2026", panelX + 8, t + 22, 0xFF6E6E82);
        }
        pen.text("\u00a77NTH Utils \u00b7 par NTH \u00b7 clic gauche : activer \u00b7 clic droit : r\u00e9gler",
                panelX + 10, panelBottom + oy - 14, 0xFF6E6E82);
        if (mode == Mode.MODULES) {
            drawModuleList(cv, oy, accent);
        } else {
            drawPresetList(cv, oy, accent);
        }

        if (selected != null) {
            if (fade > 0.02F) {
                int a2 = (int) (0x30 * fade);
                if (a2 > 4) {
                    cv.fill(0, 0, w, h, a2 << 24);
                }
            }
            drawModal(cv, oy, accent);
        }

        if (status != null && System.nanoTime() - statusAt < 2_500_000_000L) {
            int sw = pen.width(status);
            int sx = (w - sw) / 2;
            Rounded.rect(cv, sx - 10, h - 34, sx + sw + 10, h - 16, 8, 0xD0101020);
            Rounded.stroke(cv, sx - 10, h - 34, sx + sw + 10, h - 16, 8, Palettes.accentAlpha(0x70));
            pen.text(status, sx, h - 30, Palettes.accentAlpha(0xFF));
        }
    }

    private String panelTitle() {
        if (mode == Mode.PRESETS) {
            return "\u00a7lPresets";
        }
        int count = 0;
        for (Module mod : ModuleManager.all()) {
            if (filter == null || mod.category == filter) {
                count++;
            }
        }
        return "\u00a7l" + (filter == null ? "Tous les modules" : filter.label)
                + " \u00a77(" + count + ")";
    }

    private void drawRail(Rounded.Canvas cv, int oy, int accent, float fade) {
        int n = railCount();
        int x1 = railX - 6;
        int x2 = railX + RAIL_W + 6;
        int bg1 = railYs[0] - 16 + oy;
        int bg2 = railYs[n - 1] + railB + 10 + oy;
        Rounded.glow(cv, x1, bg1, x2, bg2, 12, accent, 3, 0.08F);
        Rounded.rect(cv, x1, bg1, x2, bg2, 12, 0xC60D0D15);
        Rounded.stroke(cv, x1, bg1, x2, bg2, 12, Palettes.accentAlpha(0x38));

        pen.text("\u00a77MODULES", railX + 2, bg1 + 5 + oy, 0xFF6E6E82);
        pen.text("\u00a77G\u00c9N\u00c9RAL", railX + 2, railYs[4] + railB + 6 + oy, 0xFF6E6E82);

        for (int i = 0; i < n; i++) {
            boolean active;
            String label;
            int color;
            if (i == n - 1) {
                active = mode == Mode.PRESETS;
                label = "Presets";
                color = 0xFFFFD44D;
            } else {
                ModuleCategory cat = ModuleCategory.values()[i];
                active = mode == Mode.MODULES && filter == cat;
                label = cat.label;
                color = categoryColor(cat);
            }
            int bx = railX;
            int by = railYs[i] + oy;
            float hover = MenuFx.approach("rail#" + i,
                    hovered(mx, my, bx, by, bx + RAIL_W, by + railB) ? 1.0F : 0.0F);
            int on = active ? (((int) (0x9E * fade)) << 24) | (color & 0xFFFFFF)
                    : (hover > 0.02F ? ((int) (40.0F * hover)) << 24 : 0);
            if (on != 0) {
                Rounded.rect(cv, bx, by, bx + RAIL_W, by + railB, 8, on);
            }
            if (active || hover > 0.02F) {
                Rounded.stroke(cv, bx, by, bx + RAIL_W, by + railB, 8,
                        active ? 0xFFEAEAFF : (((int) (hover * 190.0F)) << 24) | (color & 0xFFFFFF));
            }
            int iconColor = active ? 0xFF0B0B14 : color;
            int iconY = by + railB / 2 - 5;
            if (i == n - 1) {
                pen.text("\u2605", bx + 8, iconY + 1, iconColor);
            } else {
                drawIcon(cv, i, bx + 8, iconY, iconColor);
            }
            pen.text(label, bx + 26, by + railB / 2.0D - 4, active ? 0xFF0B0B14 : 0xFFB4BAC8);
        }
    }

    private void drawIcon(Rounded.Canvas cv, int kind, int x, int y, int color) {
        switch (kind) {
            case 0 -> {
                Rounded.stroke(cv, x - 2, y + 1, x + 12, y + 9, 4, color);
                Rounded.rect(cv, x + 3, y + 3, x + 7, y + 7, 2, color);
            }
            case 1 -> {
                Rounded.rect(cv, x + 3, y + 1, x + 7, y + 5, 2, color);
                Rounded.rect(cv, x + 4, y - 2, x + 6, y, 1, color);
                Rounded.rect(cv, x + 4, y + 6, x + 6, y + 8, 1, color);
                Rounded.rect(cv, x, y + 1, x + 2, y + 3, 1, color);
                Rounded.rect(cv, x + 8, y + 1, x + 10, y + 3, 1, color);
            }
            case 2 -> {
                for (int i = 0; i < 9; i++) {
                    Rounded.rect(cv, x + 1 + i, y + 8 - i, x + 3 + i, y + 10 - i, 0, color);
                }
                Rounded.rect(cv, x - 1, y + 6, x + 3, y + 8, 0, color);
            }
            case 3 -> {
                Rounded.rect(cv, x, y + 1, x + 10, y + 3, 1, color);
                Rounded.rect(cv, x, y + 6, x + 10, y + 8, 1, color);
                Rounded.rect(cv, x + 1, y, x + 5, y + 4, 1, color);
                Rounded.rect(cv, x + 6, y + 5, x + 10, y + 9, 1, color);
            }
            default -> {
                Rounded.rect(cv, x, y, x + 4, y + 4, 1, color);
                Rounded.rect(cv, x + 6, y, x + 10, y + 4, 1, color);
                Rounded.rect(cv, x, y + 6, x + 4, y + 10, 1, color);
                Rounded.rect(cv, x + 6, y + 6, x + 10, y + 10, 1, color);
            }
        }
    }

    private void drawModuleList(Rounded.Canvas cv, int oy, int accent) {
        int viewTop = PANEL_TOP + oy + 46;
        int viewBottom = panelBottom + oy - 26;
        for (Row row : rows) {
            int y = row.y - listScroll + oy;
            if (y + row.h <= viewTop || y >= viewBottom) {
                continue;
            }
            if (row.cat != null) {
                int c = categoryColor(row.cat);
                Rounded.rect(cv, panelX + 10, y + 3, panelX + 13, y + row.h - 3, 1, c);
                Integer count = countOf(row.cat);
                pen.text(row.cat.label + (count == null ? "" : " \u00a77(" + count + ")"),
                        panelX + 18, y + 5, c);
            } else {
                Module mod = row.mod;
                int y4 = row.y - listScroll;
                boolean hoveredRow = hovered(mx, my, panelX + 6, y4, panelX + panelW - 6, y4 + row.h);
                float hf = MenuFx.approach("row:" + mod.id, hoveredRow ? 1.0F : 0.0F);
                if (hf > 0.02F) {
                    Rounded.rect(cv, panelX + 6, y, panelX + panelW - 6, y + row.h, 7, ((int) (44.0F * hf)) << 24);
                }
                int dot = mod.enabled ? 0xFF55FF88 : 0xFFFF5566;
                Rounded.rect(cv, panelX + 13, y + row.h / 2 - 2, panelX + 17, y + row.h / 2 + 2, 2, dot);
                pen.text(mod.name, panelX + 23, y + 4, mod.enabled ? 0xFFFFFFFF : 0xFFA8A8BC);
                String sub = mod.keyCode > 0
                        ? "\u00a77Touche : \u00a7f" + KeyNames.keyName(mod.keyCode)
                        : "\u00a77" + ellipsize(mod.description, panelW - 48);
                pen.text(sub, panelX + 23, y + 17, 0xFF6E6E82);
                pen.text(mod.enabled ? "\u2713" : "\u00b7", panelX + panelW - 14, y + 11,
                        mod.enabled ? 0xFF55FF88 : 0xFF5A5A6E);
            }
        }
        if (listMax > 0) {
            drawScrollbar(cv, panelX + panelW - 5, viewTop, viewBottom, listScroll, listMax, accent);
        }
    }

    private String ellipsize(String s, int maxW) {
        if (s == null || s.isEmpty()) {
            return "clic droit : r\u00e9glages";
        }
        if (pen.width(s) <= maxW) {
            return s;
        }
        StringBuilder sb = new StringBuilder();
        for (String word : s.split(" ")) {
            if (pen.width(sb + word) > maxW - 8) {
                break;
            }
            sb.append(word).append(' ');
        }
        String out = sb.toString().trim();
        return out.isEmpty() ? shortWord(s) : out + " \u2026";
    }

    private String shortWord(String s) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < s.length(); j++) {
            if (pen.width(sb + "\u2026") > 60) {
                break;
            }
            sb.append(s.charAt(j));
        }
        return sb + "\u2026";
    }

    private Integer countOf(ModuleCategory cat) {
        int c = 0;
        for (Module mod : ModuleManager.all()) {
            if (mod.category == cat) {
                c++;
            }
        }
        return c;
    }

    private void drawPresetList(Rounded.Canvas cv, int oy, int accent) {
        if (saveRect.length == 4) {
            roundedChip(cv, saveRect[0], saveRect[1] + oy, saveRect[2], "Sauvegarder", accent, 0.9F);
        }
        roundedChip(cv, panelX + 8, PANEL_TOP + oy + 48, importRect[2], "Copier (export)", accent, 0.9F);
        roundedChip(cv, panelX + 12 + (panelW - 20) / 2, PANEL_TOP + oy + 48, exportRect[2],
                "Coller (import)", accent, 0.9F);
        int viewTop = PANEL_TOP + oy + 76;
        int viewBottom = panelBottom + oy - 40;
        for (PresetRow row : presetRows) {
            int y = row.y - presetScroll + oy;
            if (y + 24 <= viewTop || y >= viewBottom) {
                continue;
            }
            int y4 = row.y - presetScroll;
            float hf = MenuFx.approach("preset#" + row.name,
                    hovered(mx, my, panelX + 6, y4, panelX + panelW - 6, y4 + 24) ? 1.0F : 0.0F);
            if (hf > 0.02F) {
                Rounded.rect(cv, panelX + 6, y, panelX + panelW - 6, y + 24, 7, ((int) (36.0F * hf)) << 24);
            }
            Rounded.rect(cv, row.applyX1, y + 4, row.applyX2, y + 20, 5, 0x4020A868);
            pen.text("Appliquer", row.applyX1 + 6, y + 8, 0xFF63F0A8);
            Rounded.rect(cv, row.delX1, y + 4, row.delX2, y + 20, 5, 0x40A83030);
            pen.text("\u2715", row.delX1 + 6, y + 8, 0xFFFF7A7A);
            pen.text(row.name, panelX + 12, y + 8, 0xFFE2E2EE);
        }
        if (presetMax > 0) {
            drawScrollbar(cv, panelX + panelW - 5, viewTop, viewBottom, presetScroll, presetMax, accent);
        }
        roundedChip(cv, panelX + 8, panelBottom + oy - 30, panelW - 16, "\u2298 Tout d\u00e9sactiver", 0xFF9AA0B8, 0.8F);
    }

    private void drawModal(Rounded.Canvas cv, int oy, int accent) {
        if (modalRect.length != 4) {
            return;
        }
        int x1 = modalRect[0];
        int y1 = modalRect[1] + oy;
        int x2 = modalRect[2];
        int y2 = modalRect[3] + oy;
        drawPanelSurface(cv, x1, y1, x2, y2, 6, 0.16F);
        int contentX = modalX + 12;
        int contentW = modalW - 24;

        pen.text("\u00a7l" + selected.name, contentX, y1 + 10, 0xFFFFFFFF);
        pen.text((selected.enabled ? "\u00a7aactiv\u00e9" : "\u00a7cd\u00e9sactiv\u00e9") + " \u00a77\u00b7 "
                        + selected.category.label
                        + (selected.keyCode > 0 ? " \u00a77\u00b7 touche : \u00a7f" + KeyNames.keyName(selected.keyCode) : ""),
                contentX, y1 + 22, 0xFF9A9AB0);
        Rounded.rect(cv, contentX, y1 + 34, contentX + contentW, y1 + 35, 1, Palettes.accentAlpha(0x70));
        int dy = y1 + 40;
        for (String line : wrap(selected.description, contentW, pen)) {
            pen.text(line, contentX, dy, 0xFF8A8AA0);
            dy += 11;
        }
        if (binding) {
            pen.text("\u00a7eAppuie sur une touche\u2026 (\u00c9chap = annuler)", contentX, y2 - 14,
                    Palettes.accentAlpha(0xFF));
        } else {
            pen.text("\u00a77Clic gauche \u00e0 c\u00f4t\u00e9 : fermer et retourner aux modules", contentX, y2 - 14,
                    0xFF6E6E82);
        }

        for (ModalRow row : modalRows) {
            int ry = row.y + oy;
            if (ry < y1 + 36 || ry + row.h > y2 - 22) {
                continue;
            }
            drawModalRow(cv, row, contentX, contentW, ry, accent);
        }
    }

    private void drawModalRow(Rounded.Canvas cv, ModalRow row, int contentX, int contentW, int ry, int accent) {
        boolean hoveredRow = hovered(mx, my, contentX, ry, contentX + contentW, ry + row.h);
        String hoverKey = "modal:" + row.type + ":" + (row.setting != null ? row.setting.id : "core");
        float hf = MenuFx.approach(hoverKey, hoveredRow ? 1.0F : 0.0F);
        if ("toggle".equals(row.type)) {
            Setting.Toggle toggle = (Setting.Toggle) row.setting;
            if (hf > 0.02F) {
                Rounded.rect(cv, contentX - 4, ry, contentX + contentW + 4, ry + row.h, 6,
                        ((int) (30.0F * hf)) << 24);
            }
            pen.text(toggle.label, contentX, ry + 6, 0xFFD8D8E4);
            int pw = 30;
            int px = contentX + contentW - pw;
            Rounded.rect(cv, px, ry + 3, px + pw, ry + 17, 7,
                    toggle.value ? Palettes.accentAlpha(0xE0) : 0x503A3A50);
            int knob = toggle.value ? pw - 10 : 2;
            Rounded.rect(cv, px + knob, ry + 5, px + knob + 8, ry + 15, 5,
                    toggle.value ? 0xFF0B0B14 : 0xFFB8B8CC);
        } else if ("slider".equals(row.type)) {
            Setting.Slider sl = (Setting.Slider) row.setting;
            pen.text(sl.label, contentX, ry + 2, 0xFFD8D8E4);
            String value = String.format(java.util.Locale.ROOT, "%.1f", sl.value);
            pen.text(value, contentX + contentW - pen.width(value), ry + 2, Palettes.accentAlpha(0xFF));
            int trackY = ry + 13;
            Rounded.rect(cv, contentX, trackY, contentX + contentW, trackY + 4, 2, 0x503A3A50);
            float t = (float) ((sl.value - sl.min) / (sl.max - sl.min));
            t = Math.max(0.0F, Math.min(1.0F, t));
            Rounded.rect(cv, contentX, trackY, contentX + Math.round(contentW * t), trackY + 4, 2,
                    Palettes.accentAlpha(0xE0));
            int cx = contentX + Math.round(contentW * t);
            if (hf > 0.02F) {
                Rounded.glow(cv, cx - 5, trackY - 3, cx + 5, trackY + 7, 4, accent, 2, 0.4F * hf);
            }
            Rounded.rect(cv, cx - 4, trackY - 3, cx + 4, trackY + 7, 4, 0xFFEAEAFF);
        } else if (!"text".equals(row.type)) {
            String label;
            int color;
            if ("keybind".equals(row.type)) {
                label = binding ? "\u00a7eEnregistrement\u2026"
                        : "Touche d'activation : " + (selected.keyCode > 0
                        ? "\u00a7f" + KeyNames.keyName(selected.keyCode) : "\u00a77Aucune (clique ici)");
                color = binding ? Palettes.accentAlpha(0xFF) : 0xFFC8C8D6;
            } else if ("unbind".equals(row.type)) {
                label = "Retirer la touche";
                color = 0xFFE08A8A;
            } else if ("favorite".equals(row.type)) {
                label = "\u2605 Favori : " + (selected.favorite ? "OUI" : "NON");
                color = selected.favorite ? 0xFFFFD44D : 0xFFC8C8D6;
            } else if ("wp_add".equals(row.type)) {
                label = "Ajouter un waypoint ici";
                color = 0xFF7AEBB0;
            } else if ("wp_remove".equals(row.type)) {
                label = "Retirer le dernier waypoint";
                color = 0xFFE8B08A;
            } else {
                label = "Tout effacer les waypoints";
                color = 0xFFE88080;
            }
            if (hf > 0.02F) {
                Rounded.rect(cv, contentX - 4, ry, contentX + contentW + 4, ry + row.h, 6,
                        ((int) (34.0F * hf)) << 24);
            }
            pen.text(label, contentX, ry + 5, color);
        } else {
            pen.text(((Setting.Text) row.setting).label, contentX, ry + 2, 0xFFD8D8E4);
        }
    }

    private void drawScrollbar(Rounded.Canvas cv, int x, int y1, int y2, int scroll, int max, int accent) {
        if (y2 <= y1 || max <= 0) {
            return;
        }
        Rounded.rect(cv, x - 1, y1, x + 3, y2, 2, 0x30FFFFFF);
        int track = y2 - y1;
        int content = track + max;
        int thumb = Math.max(10, track * track / content);
        int ty = y1 + (track - thumb) * scroll / max;
        Rounded.rect(cv, x - 1, ty, x + 3, ty + thumb, 2, accent);
    }

    private void drawPanelSurface(Rounded.Canvas cv, int x1, int y1, int x2, int y2, int glowLayers, float glowAlpha) {
        Rounded.glow(cv, x1 + 2, y1 + 2, x2 - 2, y2 - 2, 10, Palettes.accent(), glowLayers, glowAlpha);
        Rounded.rect(cv, x1, y1, x2, y2, 10, 0xE60E0E16);
        if (dev.utilikit.client.modules.extra.Gradients.active()) {
            for (int[] band : dev.utilikit.client.modules.extra.Gradients.bands(x1 + 10, y1 + 10, x2 - 10, y2 - 10)) {
                Rounded.rect(cv, band[0], band[1], band[2], band[3], 4, band[4]);
            }
        }
        Rounded.stroke(cv, x1, y1, x2, y2, 10, Palettes.accentAlpha(0x50));
    }

    private void roundedChip(Rounded.Canvas cv, int x, int y, int w2, String label, int color, float alpha) {
        int h2 = 18;
        int ww = Math.max(4, w2);
        float hf = MenuFx.approach("chip:" + label,
                hovered(mx, my, x, y, x + ww, y + h2) ? 1.0F : 0.0F);
        Rounded.rect(cv, x, y, x + ww, y + h2, 6, (((int) (alpha * 255.0F)) << 24) | (color & 0xFFFFFF));
        if (hf > 0.02F) {
            Rounded.rect(cv, x, y, x + ww, y + h2, 6, ((int) (52.0F * hf)) << 24);
        }
        Rounded.stroke(cv, x, y, x + ww, y + h2, 6, Palettes.accentAlpha(0x38));
        pen.textCenter(label, x + ww / 2.0D, y + 5, 0xFF0B0B14);
    }

    private List<String> wrap(String text, int maxWidth, Pen p) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty() || p == null) {
            return lines;
        }
        if (p.width(text) <= maxWidth) {
            lines.add(text);
            return lines;
        }
        StringBuilder sb = new StringBuilder();
        int lineWidth = 0;
        for (String word : text.split(" ")) {
            int wordW = p.width(word + " ");
            if (lineWidth + wordW > maxWidth && lineWidth > 0) {
                lines.add(sb.toString().trim());
                sb.setLength(0);
                lineWidth = 0;
            }
            sb.append(word).append(' ');
            lineWidth += wordW;
        }
        if (sb.length() > 0) {
            lines.add(sb.toString().trim());
        }
        return lines;
    }

    public boolean click(double x, double y, int button) {
        if (pen == null || MenuFx.easedOpen() < 0.30D) {
            return true;
        }
        if (selected != null) {
            if (modalRect.length == 4 && inside(x, y, modalRect[0], modalRect[1], modalRect[2], modalRect[3])) {
                clickModal(x, y, button);
                return true;
            }
            selected = null;
            binding = false;
            layoutChanged();
            if (soundClick != null) {
                soundClick.run();
            }
            return true;
        }
        if (inside(x, y, panelX, PANEL_TOP, panelX + panelW, panelBottom)) {
            if (mode == Mode.PRESETS) {
                return clickPresets(x, y, button);
            }
            return clickRows(x, y, button);
        }
        for (int i = 0; i < railCount() && i < railYs.length; i++) {
            int by = railYs[i];
            if (inside(x, y, railX, by, railX + RAIL_W, by + railB)) {
                clickRail(i);
                return true;
            }
        }
        if (button == 0 && requestClose != null) {
            requestClose.run();
        }
        return true;
    }

    private void clickRail(int index) {
        int n = railCount();
        if (index == n - 1) {
            if (mode != Mode.PRESETS) {
                mode = Mode.PRESETS;
                presetScroll = 0;
                layoutChanged();
            }
        } else {
            ModuleCategory cat = ModuleCategory.values()[index];
            if (mode != Mode.MODULES || filter != cat) {
                mode = Mode.MODULES;
                filter = cat;
                listScroll = 0;
            } else {
                filter = null;
                listScroll = 0;
            }
            layoutChanged();
        }
        if (soundClick != null) {
            soundClick.run();
        }
    }

    private boolean clickRows(double x, double y, int button) {
        int viewTop = PANEL_TOP + 46;
        int viewBottom = panelBottom - 26;
        for (Row row : rows) {
            if (row.mod == null) {
                continue;
            }
            int y4 = row.y - listScroll;
            if (!hovered(x, y, panelX + 6, y4, panelX + panelW - 6, y4 + row.h)) {
                continue;
            }
            if (y4 < viewTop || y4 + row.h > viewBottom) {
                return true;
            }
            if (button == 0) {
                ModuleActions.activate(row.mod);
                if (soundToggle != null) {
                    soundToggle.run();
                }
            } else {
                openModal(row.mod);
            }
            return true;
        }
        return true;
    }

    public void openModal(Module mod) {
        KeybindEngine.cancelCapture();
        binding = false;
        selected = mod;
        layoutChanged();
        if (soundClick != null) {
            soundClick.run();
        }
    }

    public void closeModal() {
        if (selected != null) {
            selected = null;
            binding = false;
            layoutChanged();
        }
    }

    private void layoutChanged() {
        if (onLayoutChanged != null) {
            onLayoutChanged.run();
        }
    }

    private void clickModal(double x, double y, int button) {
        for (ModalRow row : modalRows) {
            int ry = row.y;
            if (ry < modalTop + 36 || ry + row.h > modalBottom - 22) {
                continue;
            }
            if (!hovered(x, y, modalX + 12, ry, modalX + modalW - 12, ry + row.h)) {
                continue;
            }
            if ("toggle".equals(row.type) && button == 0) {
                Setting.Toggle toggle = (Setting.Toggle) row.setting;
                toggle.value = !toggle.value;
                selected.onSettingChanged();
                ModuleManager.save();
                if (soundToggle != null) {
                    soundToggle.run();
                }
                return;
            }
            if ("slider".equals(row.type) && button == 0) {
                Setting.Slider sl = (Setting.Slider) row.setting;
                float t = (float) ((x - (modalX + 12)) / (double) (modalW - 24));
                t = Math.max(0.0F, Math.min(1.0F, t));
                sl.value = sl.min + (sl.max - sl.min) * t;
                selected.onSettingChanged();
                ModuleManager.save();
                return;
            }
            if ("keybind".equals(row.type) && button == 0) {
                binding = true;
                return;
            }
            if ("unbind".equals(row.type) && button == 0) {
                selected.keyCode = -1;
                KeybindEngine.clear(selected.id);
                ModuleManager.save();
                binding = false;
                return;
            }
            if ("favorite".equals(row.type) && button == 0) {
                selected.favorite = !selected.favorite;
                ModuleManager.save();
                return;
            }
            if (button == 0) {
                if ("wp_add".equals(row.type) && waypointAddHere != null) {
                    waypointAddHere.run();
                    status("Waypoint ajout\u00e9 \u2713");
                    return;
                }
                if ("wp_remove".equals(row.type) && waypointRemoveLast != null) {
                    waypointRemoveLast.run();
                    status("Dernier waypoint retir\u00e9 \u2713");
                    return;
                }
                if ("wp_clear".equals(row.type) && waypointClear != null) {
                    waypointClear.run();
                    status("Waypoints effac\u00e9s \u2713");
                    return;
                }
            }
            return;
        }
        if (button == 0) {
            closeModal();
            if (soundClick != null) {
                soundClick.run();
            }
        }
    }

    private boolean clickPresets(double x, double y, int button) {
        if (button != 0) {
            return true;
        }
        if (inside(x, y, saveRect)) {
            String name = presetName == null ? "" : presetName.trim();
            if (name.isEmpty()) {
                name = "Preset " + (PresetManager.names().size() + 1);
            }
            if (PresetManager.save(name)) {
                status("Preset \u00ab " + PresetManager.sanitize(name) + " \u00bb sauvegard\u00e9 \u2713");
                presetName = "";
                layoutChanged();
            } else {
                status("Sauvegarde impossible");
            }
            return true;
        }
        if (inside(x, y, exportRect)) {
            String json = PresetManager.exportCurrent();
            if (json != null && clipboardSet != null) {
                clipboardSet.accept(json);
                status("Config copi\u00e9e dans le presse-papiers \u2713");
            } else {
                status("Export impossible");
            }
            return true;
        }
        if (inside(x, y, importRect)) {
            String json = clipboardGet != null ? clipboardGet.get() : "";
            String name = presetName != null && !presetName.trim().isEmpty() ? presetName.trim() : "Import";
            if (PresetManager.importJson(name, json)) {
                status("Preset import\u00e9 \u2713");
                layoutChanged();
            } else {
                status("Import impossible (JSON invalide ?)");
            }
            return true;
        }
        if (inside(x, y, disableAllRect)) {
            ModuleManager.disableAll();
            ModuleManager.save();
            status("Tous les modules d\u00e9sactiv\u00e9s");
            return true;
        }
        int viewTop = PANEL_TOP + 76;
        int viewBottom = panelBottom - 40;
        for (PresetRow row : presetRows) {
            int ry = row.y - presetScroll;
            if (!hovered(x, y, panelX + 6, ry, panelX + panelW - 6, ry + 24)) {
                continue;
            }
            if (ry < viewTop || ry + 24 > viewBottom) {
                return true;
            }
            if (inside(x, y, row.delX1, ry + 4, row.delX2, ry + 20)) {
                if (PresetManager.delete(row.name)) {
                    status("Preset \u00ab " + row.name + " \u00bb supprim\u00e9");
                    layoutChanged();
                }
                return true;
            }
            if (inside(x, y, row.applyX1, ry + 4, row.applyX2, ry + 20)) {
                if (PresetManager.apply(row.name)) {
                    status("Preset \u00ab " + row.name + " \u00bb appliqu\u00e9 \u2713");
                } else {
                    status("Application impossible");
                }
                return true;
            }
            return true;
        }
        return true;
    }

    private boolean inside(double x, double y, int x1, int y1, int x2, int y2) {
        return x >= x1 && x < x2 && y >= y1 && y < y2;
    }

    private boolean inside(double x, double y, int[] rect) {
        return rect.length == 4 && inside(x, y, rect[0], rect[1], rect[2], rect[3]);
    }

    public void scroll(double verticalAmount) {
        int delta = -(int) Math.signum(verticalAmount) * (ROW_H + 2);
        if (selected != null) {
            return;
        }
        if (mode == Mode.PRESETS) {
            presetScroll = Math.max(0, Math.min(presetMax, presetScroll + delta));
        } else {
            listScroll = Math.max(0, Math.min(listMax, listScroll + delta));
        }
    }

    public boolean escape() {
        if (binding) {
            binding = false;
            if (selected != null) {
                selected.onSettingChanged();
            }
            return true;
        }
        if (selected != null) {
            selected = null;
            layoutChanged();
            if (soundClick != null) {
                soundClick.run();
            }
            return true;
        }
        return false;
    }

    public boolean capturing() {
        return binding;
    }

    public boolean captureKey(int key) {
        if (!binding || selected == null) {
            binding = false;
            return false;
        }
        if (key == 256) {
            binding = false;
            return true;
        }
        selected.keyCode = key;
        ModuleManager.save();
        binding = false;
        status("Touche enregistr\u00e9e : " + KeyNames.keyName(key));
        return true;
    }

    private static boolean hovered(double x, double y, int x1, int y1, int x2, int y2) {
        return x >= x1 && x < x2 && y >= y1 && y < y2;
    }

    private static int categoryColor(ModuleCategory category) {
        return switch (category) {
            case AFFICHAGE -> 0xFF55FFFF;
            case VISUEL -> 0xFF7CFF6B;
            case JEU -> 0xFFFF5566;
            case SYSTEME -> 0xFF5B9CFF;
            case PRATIQUE -> 0xFFFFB14D;
        };
    }
}
