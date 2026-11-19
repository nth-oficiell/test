package dev.utilikit.client.ui;

import dev.utilikit.client.config.PresetManager;
import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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
    private static final int VISIBLE_ROWS = 8;

    private enum View {
        MODULES,
        PRESETS
    }

    private final List<Runnable> rowBuilders = new ArrayList<>();
    private Module selected;
    private View view = View.MODULES;
    private String status = null;
    private int listScroll = 0;
    private int presetScroll = 0;
    private EditBox presetNameField;

    public UtiliKitScreen() {
        super(Component.literal("UtiliKit"));
        if (!ModuleManager.all().isEmpty()) {
            selected = ModuleManager.all().iterator().next();
        }
    }

    @Override
    protected void init() {
        rowBuilders.clear();
        if (view == View.MODULES) {
            addModuleListWidgets();
            addSettingsWidgets();
        } else {
            addPresetWidgets();
        }

        Button switchButton = Button.builder(Component.literal(view == View.MODULES ? "Presets" : "Modules"),
                        b -> {
                            view = view == View.MODULES ? View.PRESETS : View.MODULES;
                            status = null;
                            rebuild();
                        })
                .bounds(LIST_X, this.height - 26, 80, 18)
                .build();
        addRenderableWidget(switchButton);

        Button closeButton = Button.builder(Component.literal("Fermer"),
                        b -> onClose())
                .bounds(this.width / 2 - 40, this.height - 26, 80, 18)
                .build();
        addRenderableWidget(closeButton);
    }

    private void addModuleListWidgets() {
        for (Runnable builder : listBuilders()) {
            builder.run();
        }
        if (rows.get(rows.size() - 1).y > listBottom()) {
            addRenderableWidget(Button.builder(Component.literal("\u25B2"), b -> {
                        listScroll = Math.max(0, listScroll - 4);
                        rebuild();
                    }).bounds(LIST_X + LIST_W / 2 - 40, listBottom() + 2, 38, 16).build());
            addRenderableWidget(Button.builder(Component.literal("\u25BC"), b -> {
                        listScroll = listScroll + 4;
                        rebuild();
                    }).bounds(LIST_X + LIST_W / 2 + 2, listBottom() + 2, 38, 16).build());
        }
    }

    private List<Row> rows = new ArrayList<>();

    private List<Runnable> listBuilders() {
        List<Runnable> builders = new ArrayList<>();
        rows = new ArrayList<>();
        int y = layoutTop();
        int visibleIndex = 0;
        for (ModuleCategory category : ModuleManager.categories()) {
            boolean hasModule = ModuleManager.all().stream().anyMatch(m -> m.category == category);
            if (!hasModule) {
                continue;
            }
            rows.add(new Row(null, category, y, HEADER_H, null));
            y += HEADER_H + 2;
            for (Module module : ModuleManager.all()) {
                if (module.category != category) {
                    continue;
                }
                if (visibleIndex >= listScroll && y + ROW_H <= listBottom()) {
                    Module moduleF = module;
                    int rowY = y;
                    builders.add(() -> {
                        Button b = Button.builder(Component.literal(moduleF.name + (moduleF.enabled ? "  \u2713" : "")),
                                        btn -> UtiliKitScreen.this.select(moduleF))
                                .bounds(LIST_X + 2, rowY, LIST_W - 4, ROW_H - 4)
                                .build();
                        addRenderableWidget(b);
                    });
                }
                visibleIndex++;
                y += ROW_H + 2;
            }
            y += GAP;
        }
        return builders;
    }

    private void select(Module module) {
        selected = module;
        rebuild();
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
                Setting.Toggle finalToggle = toggle;
                addRenderableWidget(Button.builder(
                                Component.literal(toggle.label + " : " + (toggle.value ? "OUI" : "NON")),
                                b -> {
                                    finalToggle.value = !finalToggle.value;
                                    selected.onSettingChanged();
                                    ModuleManager.save();
                                    rebuild();
                                })
                        .bounds(x, y, w, 18)
                        .build());
                y += 22;
            } else if (setting instanceof Setting.Slider slider) {
                addRenderableWidget(new ModuleSliderWidget(x, y, w, 18, selected, slider));
                y += 26;
            }
        }
        addRenderableWidget(Button.builder(
                        Component.literal((selected.enabled ? "Désactiver" : "Activer") + " : " + selected.name),
                        b -> {
                            selected.setEnabled(!selected.enabled);
                            rebuild();
                        })
                .bounds(x, y + 6, w, 20)
                .build());
    }

    private void addPresetWidgets() {
        int x = settingsX() + 8;
        int w = settingsW() - 16;
        int y = layoutTop() + 34;

        presetNameField = new EditBox(this.font, x, y, w, 18, Component.literal("Nom du preset"));
        presetNameField.setMaxLength(32);
        presetNameField.setValue("");
        presetNameField.setHint(Component.literal("Nom du preset"));
        addRenderableWidget(presetNameField);
        setInitialFocus(presetNameField);
        y += 22;

        int half = (w - 6) / 2;
        addRenderableWidget(Button.builder(Component.literal("Exporter"), b -> {
                    String json = PresetManager.exportCurrent();
                    if (json != null) {
                        this.minecraft.keyboardHandler.setClipboard(json);
                        status = "Config copiée dans le presse-papiers \u2713";
                    } else {
                        status = "Export impossible";
                    }
                    rebuild();
                })
                .bounds(x, y, half, 18)
                .build());
        addRenderableWidget(Button.builder(Component.literal("Importer"), b -> {
                    String json = this.minecraft.keyboardHandler.getClipboard();
                    String presetName = presetNameField.getValue();
                    if (PresetManager.importJson(presetName, json)) {
                        status = "Preset importé \u2713";
                    } else {
                        status = "Import impossible (JSON invalide ?)";
                    }
                    rebuild();
                })
                .bounds(x + half + 6, y, w - half - 6, 18)
                .build());
        y += 22;

        addRenderableWidget(Button.builder(Component.literal("Sauvegarder la config actuelle"), b -> {
                    if (PresetManager.save(presetNameField.getValue())) {
                        status = "Preset \u00ab " + PresetManager.sanitize(presetNameField.getValue()) + " \u00bb sauvegardé \u2713";
                    } else {
                        status = "Entre d'abord un nom de preset";
                    }
                    rebuild();
                })
                .bounds(x, y, w, 18)
                .build());

        int listY = contentTop();
        List<String> names = PresetManager.names();
        int visible = 0;
        for (int i = presetScroll; i < names.size() && visible < VISIBLE_ROWS; i++) {
            int rowTop = listY + visible * PRESET_ROW_H;
            String name = names.get(i);
            int finalY = rowTop;
            addRenderableWidget(Button.builder(Component.literal("Appliquer"), b -> {
                        if (PresetManager.apply(name)) {
                            status = "Preset \u00ab " + name + " \u00bb appliqué \u2713";
                        } else {
                            status = "Application impossible";
                        }
                        rebuild();
                    })
                    .bounds(x, finalY, half, 17)
                    .build());
            addRenderableWidget(Button.builder(Component.literal("\u2715"), b -> {
                        if (PresetManager.delete(name)) {
                            status = "Preset \u00ab " + name + " \u00bb supprimé";
                        }
                        rebuild();
                    })
                    .bounds(x + half + 6, finalY, w - half - 6, 17)
                    .build());
            visible++;
        }
        if (names.size() > VISIBLE_ROWS) {
            addRenderableWidget(Button.builder(Component.literal("\u25B2"), b -> {
                        presetScroll = Math.max(0, presetScroll - 2);
                        rebuild();
                    }).bounds(x + w / 2 - 40, listBottom() + 2, 38, 16).build());
            addRenderableWidget(Button.builder(Component.literal("\u25BC"), b -> {
                        presetScroll = Math.min(names.size() - 1, presetScroll + 2);
                        rebuild();
                    }).bounds(x + w / 2 + 2, listBottom() + 2, 38, 16).build());
        }
    }

    private void rebuild() {
        this.rebuildWidgets();
    }

    private int layoutTop() {
        return 34;
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

    private int contentTop() {
        return layoutTop() + 74;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.centeredText(this.font, this.title, this.width / 2, 12, 0xFF55FFFF);

        int top = layoutTop();
        int bottom = listBottom();
        graphics.fill(LIST_X, top, LIST_X + LIST_W, bottom, 0xC0101018);
        graphics.outline(LIST_X, top, LIST_W, bottom - top, 0xFF3A3A52);

        int settingsLeft = settingsX();
        int settingsWidth = settingsW();
        graphics.fill(settingsLeft, top, settingsLeft + settingsWidth, bottom, 0xC0101018);
        graphics.outline(settingsLeft, top, settingsWidth, bottom - top, 0xFF3A3A52);

        int visibleIndex = 0;
        for (Row row : rows) {
            if (row.category != null) {
                if (visibleIndex >= listScroll && row.y + row.h <= listBottom()) {
                    graphics.text(this.font, row.category.label + " \u2014", LIST_X + 6, row.y + 5, 0xFF9A9AB0, true);
                }
            } else if (row.module != null) {
                if (visibleIndex >= listScroll && row.y + row.h <= listBottom() && row.module == selected) {
                    graphics.outline(LIST_X + 2, row.y - 2, LIST_W - 4, row.h, 0xFF61B8FF);
                }
            }
            if (row.category == null) {
                visibleIndex++;
            }
        }

        if (view == View.MODULES && selected != null) {
            graphics.text(this.font, "\u00A7l" + selected.name, settingsLeft + 8, top + 6, 0xFFFFFFFF, true);
            int descY = top + 18;
            for (String descLine : wrap(selected.description, settingsWidth - 16)) {
                graphics.text(this.font, descLine, settingsLeft + 8, descY, 0xFF8A8A9E, true);
                descY += 11;
            }
            graphics.text(this.font, "Modules : " + ModuleManager.all().size() + " — UtiliKit",
                    settingsLeft + 8, bottom - 16, 0xFF556677, true);
        } else if (view == View.PRESETS) {
            graphics.text(this.font, "\u00A7lGestion des presets", settingsLeft + 8, top + 6, 0xFFFFFFFF, true);
            if (status != null) {
                graphics.text(this.font, status, settingsLeft + 8, top + 18, 0xFF55FFAA, true);
            }
        }
    }

    private List<String> wrap(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return lines;
        }
        if (this.font.width(text) <= maxWidth) {
            lines.add(text);
            return lines;
        }
        StringBuilder sb = new StringBuilder();
        int lineWidth = 0;
        for (String word : text.split(" ")) {
            int w = this.font.width(word + " ");
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

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(null);
        }
        super.onClose();
    }

    private class ModuleSliderWidget extends AbstractSliderButton {
        private Module module;
        private Setting.Slider setting;

        ModuleSliderWidget(int x, int y, int width, int height, Module module, Setting.Slider setting) {
            super(x, y, width, height, Component.empty(),
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
            setMessage(Component.literal(setting.label + " : " + String.format(Locale.ROOT, "%.1f", setting.value)));
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

    private static final class Row {
        final Module module;
        final ModuleCategory category;
        final int y;
        final int h;
        final Object extra;

        Row(Module module, ModuleCategory category, int y, int h, Object extra) {
            this.module = module;
            this.category = category;
            this.y = y;
            this.h = h;
            this.extra = extra;
        }
    }
}
