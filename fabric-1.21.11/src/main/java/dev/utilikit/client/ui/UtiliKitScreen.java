package dev.utilikit.client.ui;

import dev.utilikit.client.config.PresetManager;
import dev.utilikit.client.modules.KeybindEngine;
import dev.utilikit.client.modules.KeyNames;
import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.MouseInput;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UtiliKitScreen extends Screen {
    private static final int LIST_X = 16;
    private static final int LIST_W = 190;
    private static final int ROW_H = 22;
    private static final int HEADER_H = 18;
    private static final int GAP = 8;
    private static final int PRESET_ROW_H = 20;

    private enum View {
        MODULES,
        PRESETS,
        PARAMS
    }

    private final List<Row> rows = new ArrayList<>();
    private final List<PresetRow> presetRows = new ArrayList<>();
    private Module selected;
    private TextFieldWidget searchField;
    private String searchFilter = "";

    private View view = View.MODULES;
    private String status = null;
    private int presetScroll = 0;
    private int moduleScroll = 0;
    private int moduleMaxScroll = 0;
    private ModuleCategory activeCategory = null;
    private final java.util.Map<ModuleCategory, Integer> categoryCounts = new java.util.EnumMap<>(ModuleCategory.class);
    private double lastMouseX = 0.0D;
    private double lastMouseY = 0.0D;
    private Module bindingTarget = null;

    public UtiliKitScreen() {
        super(Text.literal("NTH Utils"));
        MenuFx.onOpen();
        UiSounds.open();
        if (!ModuleManager.all().isEmpty()) {
            selected = ModuleManager.all().iterator().next();
        }
    }

    @Override
    protected void init() {
        rows.clear();
        presetRows.clear();
        int y = listContentTop();
        int listStartY = y;
        String needle = searchFilter.trim().toLowerCase();
        for (ModuleCategory category : ModuleCategory.values()) {
            if (activeCategory != null && activeCategory != category) {
                continue;
            }
            boolean hasModule = !needle.isEmpty()
                    ? ModuleManager.all().stream().anyMatch(m -> m.category == category && m.name.toLowerCase().contains(needle))
                    : ModuleManager.all().stream().anyMatch(m -> m.category == category);
            if (!hasModule) {
                continue;
            }
            rows.add(new Row(null, category, y, HEADER_H));
            y += HEADER_H + 2;
            for (Module module : ModuleManager.ordered()) {
                if (module.category != category) {
                    continue;
                }
                if (!needle.isEmpty() && !module.name.toLowerCase().contains(needle)) {
                    continue;
                }
                rows.add(new Row(module, null, y, ROW_H));
                y += ROW_H + 2;
            }
            y += GAP;
        }
        moduleMaxScroll = Math.max(0, (y - listStartY) - (listBottom() - listStartY));
        moduleScroll = Math.max(0, Math.min(moduleScroll, moduleMaxScroll));
        categoryCounts.clear();
        for (ModuleCategory category : ModuleCategory.values()) {
            int count = 0;
            for (Module module : ModuleManager.all()) {
                if (module.category == category) {
                    count++;
                }
            }
            if (count > 0) {
                categoryCounts.put(category, count);
            }
        }

        if (view == View.MODULES) {
            searchField = new TextFieldWidget(this.textRenderer, LIST_X + 4, layoutTop() + 5, LIST_W - 10, 16, Text.literal("Recherche\u2026"));
            searchField.setMaxLength(24);
            searchField.setText(searchFilter);
            searchField.setChangedListener(v -> {
                if (!v.equals(searchFilter)) {
                    searchFilter = v;
                    rebuild();
                }
            });
            addDrawableChild(searchField);
            setFocused(searchField);
            searchField.setFocused(true);
        }
        addDrawable((context, mouseX, mouseY, delta) -> renderCustom(context, mouseX, mouseY));
        if (view == View.PRESETS) {
            addPresetWidgets();
        } else {
            addSettingsWidgets();
        }

        addDrawableChild(ButtonWidget.builder(Text.literal(view == View.MODULES ? "Presets" : "Modules"),
                        b -> {
                            view = view == View.MODULES ? View.PRESETS : View.MODULES;
                            status = null;
                            moduleScroll = 0;
                            rebuild();
                        })
                .dimensions(16, this.height - 26, 80, 18)
                .build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Fermer"), b -> close())
                .dimensions(this.width / 2 - 40, this.height - 26, 80, 18)
                .build());
    }

    private void rebuild() {
        clearChildren();
        init();
    }

    private int layoutTop() {
        return 34;
    }

    private int listContentTop() {
        return layoutTop() + (view == View.MODULES ? 44 : 0);
    }

    private int listBottom() {
        return this.height - 36;
    }

    private int settingsX() {
        return LIST_X + LIST_W + GAP;
    }

    private int settingsW() {
        return this.width - settingsX() - 16;
    }

    private void renderCustom(DrawContext context, int mouseX, int mouseY) {
        context.drawCenteredTextWithShadow(this.textRenderer, this.title,
                this.width / 2, 12, 0xFF55FFFF);

        MenuFx.tick();
        int slide = MenuFx.slideOffset(20);
        if (slide > 0) {
            context.getMatrices().pushMatrix();
            context.getMatrices().translate(0.0F, -slide);
        }

        int top = layoutTop();
        int bottom = listBottom();
        drawPanel(context, LIST_X, top, LIST_X + LIST_W, bottom);
        context.drawStrokedRectangle(LIST_X, top, LIST_W, bottom - top, 0xFF3A3A52);

        int settingsLeft = settingsX();
        int settingsWidth = settingsW();
        drawPanel(context, settingsLeft, top, settingsLeft + settingsWidth, bottom);
        context.drawStrokedRectangle(settingsLeft, top, settingsWidth, bottom - top, 0xFF3A3A52);

        if (view == View.MODULES) {
            drawChips(context);
        }
        for (Row row : rows) {
            int rowY = row.y - moduleScroll;
            if (rowY < top || rowY + row.h > bottom || rowY < listContentTop()) {
                continue;
            }
            if (row.category != null) {
                Integer count = categoryCounts.get(row.category);
                String headerLabel = row.category.label + (count == null ? "" : " (" + count + ")");
                context.fill(LIST_X + 2, rowY + 4, LIST_X + 5, rowY + row.h - 4, categoryColor(row.category));
                context.drawTextWithShadow(this.textRenderer, headerLabel,
                        LIST_X + 8, rowY + 5, categoryColor(row.category));
            } else if (row.module != null) {
                boolean hovered = isOverRow(row, rowY, mouseX, mouseY);
                float hoverFade = MenuFx.approach(row.module, hovered ? 1.0F : 0.0F);
                int bg = row.module == selected ? 0x50FFFFFF
                        : (hoverFade > 0.02F ? ((int) (40.0F * hoverFade)) << 24 : 0x00000000);
                if (bg != 0) {
                    context.fill(LIST_X + 2, rowY, LIST_X + LIST_W - 2, rowY + row.h, bg);
                }
                if (row.module == selected) {
                    context.drawStrokedRectangle(LIST_X + 1, rowY - 1, LIST_W - 2, row.h + 2, 0xFF61B8FF);
                }
                int statusColor = row.module.enabled ? 0xFF55FF55 : 0xFFFF5555;
                context.fill(LIST_X + 6, rowY + row.h / 2 - 1, LIST_X + 10, rowY + row.h / 2 + 3, statusColor);
                int textColor = row.module.enabled ? 0xFFFFFFFF : 0xFFAAAAAA;
                context.drawTextWithShadow(this.textRenderer, row.module.name,
                        LIST_X + 16, rowY + 7, textColor);
            }
        }
        if (moduleMaxScroll > 0) {
            int trackTop = listContentTop();
            int trackH = bottom - trackTop;
            int contentH = trackH + moduleMaxScroll;
            int thumbH = Math.max(12, trackH * trackH / contentH);
            int thumbY = trackTop + (trackH - thumbH) * moduleScroll / moduleMaxScroll;
            context.fill(LIST_X + LIST_W - 6, trackTop, LIST_X + LIST_W - 4, bottom, 0x40FFFFFF);
            context.fill(LIST_X + LIST_W - 6, thumbY, LIST_X + LIST_W - 4, thumbY + thumbH, 0xFF61B8FF);
        }

        if (view != View.PRESETS && selected != null) {
            context.drawTextWithShadow(this.textRenderer, "§l" + selected.name,
                    settingsLeft + 8, top + 6, 0xFFFFFFFF);
            int descY = top + 18;
            for (String descLine : wrap(selected.description, settingsWidth - 16)) {
                context.drawTextWithShadow(this.textRenderer, descLine, settingsLeft + 8, descY, 0xFF8A8A9E);
                descY += 11;
            }
        } else if (view == View.PRESETS) {
            context.drawTextWithShadow(this.textRenderer, "§lGestion des presets",
                    settingsLeft + 8, top + 6, 0xFFFFFFFF);
            if (status != null) {
                context.drawTextWithShadow(this.textRenderer, status,
                        settingsLeft + 8, top + 18, 0xFF55FFAA);
            }
            for (PresetRow row : presetRows) {
                if (!row.visible) {
                    continue;
                }
                String name = this.textRenderer.trimToWidth(row.name, settingsWidth - 110);
                context.drawTextWithShadow(this.textRenderer, name,
                        settingsLeft + 8, row.y + 6, 0xFFDDDDDD);
            }
        }

        context.drawTextWithShadow(this.textRenderer,
                "§7Clic gauche : activer · Clic droit : paramètres + touche",
                LIST_X, this.height - 44, 0xFF777788);
        if (slide > 0) {
            context.getMatrices().popMatrix();
        }
    }

    private List<String> wrap(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return lines;
        }
        if (this.textRenderer.getWidth(text) <= maxWidth) {
            lines.add(text);
            return lines;
        }
        StringBuilder sb = new StringBuilder();
        int lineWidth = 0;
        for (String word : text.split(" ")) {
            int w = this.textRenderer.getWidth(word + " ");
            if (lineWidth + w > maxWidth && lineWidth > 0) {
                lines.add(sb.toString().trim());
                sb.setLength(0);
                lineWidth = 0;
            }
            sb.append(word).append(' ');
            lineWidth += w;
        }
        if (!sb.isEmpty()) {
            lines.add(sb.toString().trim());
        }
        return lines;
    }

    private boolean isOverRow(Row row, int rowY, int mouseX, int mouseY) {
        return mouseX >= LIST_X && mouseX <= LIST_X + LIST_W
                && mouseY >= rowY && mouseY < rowY + row.h
                && rowY >= listContentTop() && rowY + row.h <= listBottom();
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        return mouseClickedInternal(click.x(), click.y(), click.button());
    }

    private boolean mouseClickedInternal(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(new Click(lastMouseX, lastMouseY, new MouseInput(button, 0)), false)) {
            return true;
        }
        if (view == View.MODULES && mouseY >= layoutTop() + 24 && mouseY <= layoutTop() + 41
                && mouseX >= LIST_X && mouseX <= LIST_X + LIST_W) {
            int seg = (LIST_W - 6) / 4;
            int index = (int) ((mouseX - LIST_X) / (seg + 2));
            if (index >= 0 && index <= 3) {
                activeCategory = index == 0 ? null : ModuleCategory.values()[index - 1];
                UiSounds.click();
                moduleScroll = 0;
                rebuild();
                return true;
            }
        }
        if (view == View.MODULES) {
            for (Row row : rows) {
                if (row.module == null) {
                    continue;
                }
                int rowY = row.y - moduleScroll;
                if (isOverRow(row, rowY, (int) mouseX, (int) mouseY)) {
                    selected = row.module;
                    if (button == 0) {
                        dev.utilikit.client.modules.ModuleActions.activate(row.module);
                        UiSounds.toggle();
                    } else if (button == 1) {
                        bindingTarget = null;
                        view = View.PARAMS;
                        moduleScroll = 0;
                    }
                    rebuild();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (view == View.PRESETS
                && mouseX >= settingsX() && mouseX <= settingsX() + settingsW()
                && mouseY >= layoutTop() && mouseY <= listBottom()) {
            int viewport = listBottom() - contentTop() - 40;
            int maxScroll = Math.max(0, presetRows.size() * PRESET_ROW_H - viewport + 8);
            presetScroll = Math.max(0, Math.max(0, presetScroll - (int) Math.signum(verticalAmount) * PRESET_ROW_H));
            presetScroll = Math.min(presetScroll, maxScroll);
            rebuild();
            return true;
        }
        if (mouseX >= LIST_X && mouseX <= LIST_X + LIST_W
                && mouseY >= layoutTop() && mouseY <= listBottom()) {
            moduleScroll = Math.max(0, moduleScroll - (int) Math.signum(verticalAmount) * (ROW_H + 2));
            moduleScroll = Math.min(moduleScroll, moduleMaxScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (bindingTarget != null) {
            if (input.key() == 256) {
                bindingTarget = null;
                rebuild();
                return true;
            }
            bindingTarget.keyCode = input.key();
            ModuleManager.save();
            bindingTarget = null;
            rebuild();
            return true;
        }
        return super.keyPressed(input);
    }

    private int contentTop() {
        return layoutTop() + 82;
    }

    private void addSettingsWidgets() {
        if (selected == null) {
            return;
        }
        int x = settingsX() + 8;
        int w = settingsW() - 16;
        int y = layoutTop() + 40;
        for (Setting setting : selected.settings) {
            if (setting instanceof Setting.Toggle toggle) {
                addDrawableChild(ButtonWidget.builder(
                                Text.literal(toggle.label + " : " + (toggle.value ? "OUI" : "NON")),
                                b -> {
                                    toggle.value = !toggle.value;
                                    selected.onSettingChanged();
                                    ModuleManager.save();
                                    rebuild();
                                })
                        .dimensions(x, y, w, 18)
                        .build());
                y += 22;
            } else if (setting instanceof Setting.Slider slider) {
                addDrawableChild(new ModuleSliderWidget(x, y, w, 18, selected, slider));
                y += 26;
            } else if (setting instanceof Setting.Text textSetting) {
                final Setting.Text textF = textSetting;
                TextFieldWidget field = new TextFieldWidget(this.textRenderer, x, y, w, 18, Text.literal(textF.label));
                field.setMaxLength(100);
                field.setText(textF.value);
                field.setChangedListener(v -> {
                    if (!v.equals(textF.value)) {
                        textF.value = v;
                        ModuleManager.save();
                    }
                });
                addDrawableChild(field);
                y += 26;
            }
        }
        if (view == View.PARAMS) {
            String keyLabel;
            if (bindingTarget == selected) {
                keyLabel = "Appuie sur une touche… (Échap = annuler)";
            } else {
                keyLabel = "Touche d'activation : "
                        + (selected.keyCode <= 0 ? "Aucune" : KeyNames.keyName(selected.keyCode));
            }
            addDrawableChild(ButtonWidget.builder(Text.literal(keyLabel), b -> {
                        bindingTarget = bindingTarget == selected ? null : selected;
                        rebuild();
                    })
                    .dimensions(x + 6, y + 6, w, 18)
                    .build());
            y += 28;
            addDrawableChild(ButtonWidget.builder(Text.literal("Retirer la touche"), b -> {
                        selected.keyCode = -1;
                        KeybindEngine.clear(selected.id);
                        ModuleManager.save();
                        bindingTarget = null;
                        rebuild();
                    })
                    .dimensions(x + 6, y + 6, w, 18)
                    .build());
        addDrawableChild(ButtonWidget.builder(Text.literal("\u2605 Favori : " + (selected.favorite ? "OUI" : "NON")), b -> {
                    selected.favorite = !selected.favorite;
                    ModuleManager.save();
                    rebuild();
                })
                .dimensions(x + 6, y + 34, w, 18)
                .build());
            if ("waypoints".equals(selected.id)) {
        addDrawableChild(ButtonWidget.builder(Text.literal("Ajouter un waypoint ici"), b -> {
                    dev.utilikit.client.modules.extra.Waypoints.addHere(client);
                    rebuild();
                })
                .dimensions(x + 6, y + 74, w, 18)
                .build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Retirer le dernier waypoint"), b -> {
                    dev.utilikit.client.modules.extra.Waypoints.removeLast();
                    rebuild();
                })
                .dimensions(x + 6, y + 96, w, 18)
                .build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Tout effacer les waypoints"), b -> {
                    dev.utilikit.client.modules.extra.Waypoints.clear();
                    rebuild();
                })
                .dimensions(x + 6, y + 118, w, 18)
                .build());
            }
        }
    }

    private void addPresetWidgets() {
        int x = settingsX() + 8;
        int w = settingsW() - 16;
        int y = layoutTop() + 34;

        TextFieldWidget nameField = new TextFieldWidget(this.textRenderer, x, y, w, 18,
                Text.literal("Nom du preset"));
        nameField.setMaxLength(32);
        nameField.setText("");
        addDrawableChild(nameField);
        setFocused(nameField);
        nameField.setFocused(true);
        y += 22;

        int half = (w - 6) / 2;
        addDrawableChild(ButtonWidget.builder(Text.literal("Exporter"), b -> {
                    String json = PresetManager.exportCurrent();
                    if (json != null && client != null && client.keyboard != null) {
                        client.keyboard.setClipboard(json);
                        status = "Config copiée dans le presse-papiers ✓";
                    } else {
                        status = "Export impossible";
                    }
                    rebuild();
                })
                .dimensions(x, y, half, 18)
                .build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Importer"), b -> {
                    String json = client != null && client.keyboard != null ? client.keyboard.getClipboard() : "";
                    String presetName = nameField.getText();
                    if (PresetManager.importJson(presetName, json)) {
                        status = "Preset importé ✓";
                    } else {
                        status = "Import impossible (JSON invalide ?)";
                    }
                    rebuild();
                })
                .dimensions(x + half + 6, y, w - half - 6, 18)
                .build());
        y += 22;

        addDrawableChild(ButtonWidget.builder(Text.literal("Sauvegarder la config actuelle"), b -> {
                    if (PresetManager.save(nameField.getText())) {
                        status = "Preset « " + PresetManager.sanitize(nameField.getText()) + " » sauvegardé ✓";
                    } else {
                        status = "Entre d'abord un nom de preset";
                    }
                    rebuild();
                })
                .dimensions(x, y, w, 18)
                .build());
        y += 22;
        addDrawableChild(ButtonWidget.builder(Text.literal("Tout désactiver"), b -> {
                    ModuleManager.disableAll();
                    ModuleManager.save();
                    rebuild();
                })
                .dimensions(x + 6, y, w, 18)
                .build());


        int listY = contentTop();
        List<String> names = PresetManager.names();
        int viewport = listBottom() - listY - 8;
        int halfRow = (w - 6) / 2;
        for (int i = 0; i < names.size(); i++) {
            int rowTop = listY + i * PRESET_ROW_H - presetScroll;
            boolean visibleRow = rowTop >= listY - PRESET_ROW_H && rowTop + PRESET_ROW_H <= listBottom();
            String name = names.get(i);
            if (visibleRow) {
                int finalY = rowTop;
                addDrawableChild(ButtonWidget.builder(Text.literal("Appliquer"), b -> {
                            if (PresetManager.apply(name)) {
                                status = "Preset « " + name + " » appliqué ✓";
                            } else {
                                status = "Application impossible";
                            }
                            rebuild();
                        })
                        .dimensions(x, finalY, halfRow, 17)
                        .build());
                addDrawableChild(ButtonWidget.builder(Text.literal("✕"), b -> {
                            if (PresetManager.delete(name)) {
                                status = "Preset « " + name + " » supprimé";
                            }
                            rebuild();
                        })
                        .dimensions(x + halfRow + 6, finalY, w - halfRow - 6, 17)
                        .build());
            }
            presetRows.add(new PresetRow(name, rowTop, visibleRow));
        }
    }

    private class ModuleSliderWidget extends SliderWidget {
        private Module module;
        private Setting.Slider setting;

        ModuleSliderWidget(int x, int y, int width, int height, Module module, Setting.Slider setting) {
            super(x, y, width, height, Text.literal(setting.label),
                    (setting.value - setting.min) / (setting.max - setting.min));
            this.module = module;
            this.setting = setting;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            if (setting == null) {
                return;
            }
            setMessage(Text.literal(setting.label + " : "
                    + String.format(Locale.ROOT, "%.1f", setting.value)));
        }

        @Override
        protected void applyValue() {
            if (setting == null || module == null) {
                return;
            }
            setting.value = setting.min + (setting.max - setting.min) * this.value;
            module.onSettingChanged();
            ModuleManager.save();
        }
    }

    private static int categoryColor(ModuleCategory category) {
        return switch (category) {
            case AFFICHAGE -> 0xFF55FFFF;
            case VISUEL -> 0xFF7CFF6B;
            case PRATIQUE -> 0xFFFFB14D;
        };
    }

    private static String shortLabel(ModuleCategory category) {
        return switch (category) {
            case AFFICHAGE -> "Info";
            case VISUEL -> "Visu";
            case PRATIQUE -> "Util";
        };
    }

    private void drawChips(DrawContext context) {
        int seg = (LIST_W - 6) / 4;
        int chipY = layoutTop() + 24;
        for (int i = 0; i < ModuleCategory.values().length + 1; i++) {
            ModuleCategory category = i == 0 ? null : ModuleCategory.values()[i - 1];
            boolean on = category == null ? activeCategory == null : category == activeCategory;
            int cx = LIST_X + i * (seg + 2);
            int accent = category == null ? 0xFF61B8FF : categoryColor(category);
            context.fill(cx, chipY, cx + seg, chipY + 16, on ? accent : 0x40303A55);
            if (on) {
                context.drawStrokedRectangle(cx, chipY, seg, 16, 0xFFEAEAFF);
            }
            String label = category == null ? "Tous" : shortLabel(category);
            String chipLabel = (on ? "\u25cf " : "") + label;
            context.drawTextWithShadow(this.textRenderer, chipLabel,
                    cx + (seg - this.textRenderer.getWidth(chipLabel)) / 2, chipY + 4,
                    on ? 0xFF0B0B14 : 0xFF99AABB);
        }
    }

    private void drawPanel(DrawContext context, int x1, int y1, int x2, int y2) {
        if (dev.utilikit.client.modules.extra.Gradients.active()) {
            for (int[] gband : dev.utilikit.client.modules.extra.Gradients.bands(x1, y1, x2, y2)) {
                context.fill(gband[0], gband[1], gband[2], gband[3], gband[4]);
            }
            return;
        }
        context.fill(x1, y1, x2, y2, 0xC0101018);
    }

    private static final class Row {
        final Module module;
        final ModuleCategory category;
        final int y;
        final int h;

        Row(Module module, ModuleCategory category, int y, int h) {
            this.module = module;
            this.category = category;
            this.y = y;
            this.h = h;
        }
    }

    private static final class PresetRow {
        final String name;
        final int y;
        final boolean visible;

        PresetRow(String name, int y, boolean visible) {
            this.name = name;
            this.y = y;
            this.visible = visible;
        }
    }
}
