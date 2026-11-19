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
    private View view = View.MODULES;
    private String status = null;
    private int presetScroll = 0;
    private double lastMouseX = 0.0D;
    private double lastMouseY = 0.0D;
    private Module bindingTarget = null;

    public UtiliKitScreen() {
        super(Text.literal("NTH Utils"));
        if (!ModuleManager.all().isEmpty()) {
            selected = ModuleManager.all().iterator().next();
        }
    }

    @Override
    protected void init() {
        rows.clear();
        presetRows.clear();
        int y = layoutTop();
        for (ModuleCategory category : ModuleCategory.values()) {
            boolean hasModule = ModuleManager.all().stream().anyMatch(m -> m.category == category);
            if (!hasModule) {
                continue;
            }
            rows.add(new Row(null, category, y, HEADER_H));
            y += HEADER_H + 2;
            for (Module module : ModuleManager.ordered()) {
                if (module.category != category) {
                    continue;
                }
                rows.add(new Row(module, null, y, ROW_H));
                y += ROW_H + 2;
            }
            y += GAP;
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
                this.width / 2, 12, 0x55FFFF);

        int top = layoutTop();
        int bottom = listBottom();
        context.fill(LIST_X, top, LIST_X + LIST_W, bottom, 0xC0101018);
        context.drawStrokedRectangle(LIST_X, top, LIST_W, bottom - top, 0xFF3A3A52);

        int settingsLeft = settingsX();
        int settingsWidth = settingsW();
        context.fill(settingsLeft, top, settingsLeft + settingsWidth, bottom, 0xC0101018);
        context.drawStrokedRectangle(settingsLeft, top, settingsWidth, bottom - top, 0xFF3A3A52);

        for (Row row : rows) {
            if (row.category != null) {
                context.drawTextWithShadow(this.textRenderer, row.category.label,
                        LIST_X + 6, row.y + 5, 0x9A9AB0);
            } else if (row.module != null) {
                boolean hovered = isOverRow(row, mouseX, mouseY);
                int bg = row.module == selected ? 0x50FFFFFF : (hovered ? 0x28FFFFFF : 0x00000000);
                if (bg != 0) {
                    context.fill(LIST_X + 2, row.y, LIST_X + LIST_W - 2, row.y + row.h, bg);
                }
                int statusColor = row.module.enabled ? 0xFF55FF55 : 0xFFFF5555;
                context.fill(LIST_X + 6, row.y + row.h / 2 - 1, LIST_X + 10, row.y + row.h / 2 + 3, statusColor);
                int textColor = row.module.enabled ? 0xFFFFFF : 0xAAAAAA;
                context.drawTextWithShadow(this.textRenderer, row.module.name,
                        LIST_X + 16, row.y + 7, textColor);
            }
        }

        if (view != View.PRESETS && selected != null) {
            context.drawTextWithShadow(this.textRenderer, "§l" + selected.name,
                    settingsLeft + 8, top + 6, 0xFFFFFF);
            int descY = top + 18;
            for (String descLine : wrap(selected.description, settingsWidth - 16)) {
                context.drawTextWithShadow(this.textRenderer, descLine, settingsLeft + 8, descY, 0x8A8A9E);
                descY += 11;
            }
        } else if (view == View.PRESETS) {
            context.drawTextWithShadow(this.textRenderer, "§lGestion des presets",
                    settingsLeft + 8, top + 6, 0xFFFFFF);
            if (status != null) {
                context.drawTextWithShadow(this.textRenderer, status,
                        settingsLeft + 8, top + 18, 0x55FFAA);
            }
            for (PresetRow row : presetRows) {
                if (!row.visible) {
                    continue;
                }
                String name = this.textRenderer.trimToWidth(row.name, settingsWidth - 110);
                context.drawTextWithShadow(this.textRenderer, name,
                        settingsLeft + 8, row.y + 6, 0xDDDDDD);
            }
        }

        context.drawTextWithShadow(this.textRenderer,
                "§7Clic gauche : activer · Clic droit : paramètres + touche",
                LIST_X, this.height - 44, 0x777788);
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

    private boolean isOverRow(Row row, int mouseX, int mouseY) {
        return mouseX >= LIST_X && mouseX <= LIST_X + LIST_W && mouseY >= row.y && mouseY < row.y + row.h;
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
        if (view == View.MODULES) {
            for (Row row : rows) {
                if (row.module == null) {
                    continue;
                }
                if (isOverRow(row, (int) mouseX, (int) mouseY)) {
                    selected = row.module;
                    if (button == 0) {
                        row.module.setEnabled(!row.module.enabled);
                    } else if (button == 1) {
                        bindingTarget = null;
                        view = View.PARAMS;
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
