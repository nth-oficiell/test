package dev.utilikit.client.ui;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
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

    private final List<Row> rows = new ArrayList<>();
    private Module selected;

    public UtiliKitScreen() {
        super(Text.literal("UtiliKit"));
        if (!ModuleManager.all().isEmpty()) {
            selected = ModuleManager.all().iterator().next();
        }
    }

    @Override
    protected void init() {
        rows.clear();
        int y = layoutTop();
        for (ModuleCategory category : ModuleCategory.values()) {
            boolean hasModule = ModuleManager.all().stream().anyMatch(m -> m.category == category);
            if (!hasModule) {
                continue;
            }
            rows.add(new Row(null, category, y, HEADER_H));
            y += HEADER_H + 2;
            for (Module module : ModuleManager.all()) {
                if (module.category != category) {
                    continue;
                }
                rows.add(new Row(module, null, y, ROW_H));
                y += ROW_H + 2;
            }
            y += GAP;
        }

        addDrawable((context, mouseX, mouseY, delta) -> renderCustom(context, mouseX, mouseY));
        addSettingsWidgets();

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
        context.drawBorder(LIST_X, top, LIST_W, bottom - top, 0xFF3A3A52);

        int settingsLeft = settingsX();
        int settingsWidth = settingsW();
        context.fill(settingsLeft, top, settingsLeft + settingsWidth, bottom, 0xC0101018);
        context.drawBorder(settingsLeft, top, settingsWidth, bottom - top, 0xFF3A3A52);

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

        if (selected != null) {
            context.drawTextWithShadow(this.textRenderer, "§l" + selected.name, settingsLeft + 8, top + 6, 0xFFFFFF);
            for (String descLine : wrap(selected.description, settingsWidth - 16).split("\n")) {
                context.drawTextWithShadow(this.textRenderer, descLine, settingsLeft + 8, top + 18, 0x8A8A9E);
            }
        }
    }

    private List<String> wrap(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 0) {
            for (Row row : rows) {
                if (row.module == null) {
                    continue;
                }
                if (isOverRow(row, (int) mouseX, (int) mouseY)) {
                    if (row.module == selected) {
                        row.module.setEnabled(!row.module.enabled);
                    } else {
                        selected = row.module;
                    }
                    rebuild();
                    return true;
                }
            }
        }
        return false;
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
}
