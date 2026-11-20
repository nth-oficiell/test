package dev.utilikit.client.ui;

import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;
import dev.utilikit.client.modules.extra.Waypoints;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class UtiliKitScreen extends Screen {
    private final CorzMenu menu = new CorzMenu();
    private GuiGraphicsExtractor graphicsContext;
    private final CorzMenu.Pen pen = new CorzMenu.Pen() {
        @Override
        public void text(String s, double x, double y, int argb) {
            if (graphicsContext != null) {
                graphicsContext.text(font, s, (int) x, (int) y, argb, true);
            }
        }

        @Override
        public void textCenter(String s, double x, double y, int argb) {
            if (graphicsContext != null) {
                graphicsContext.text(font, s, (int) (x - font.width(s) / 2.0D), (int) y, argb, true);
            }
        }

        @Override
        public int width(String s) {
            return font.width(s);
        }
    };

    public UtiliKitScreen() {
        super(Component.literal("NTH Utils"));
        MenuFx.onOpen();
        UiSounds.open();
    }

    @Override
    protected void init() {
        installHooks();
        menu.layout(this.width, this.height, pen);
        if (menu.mode == CorzMenu.Mode.MODULES && menu.searchFieldW() > 0) {
            EditBox field = new EditBox(this.font, menu.searchFieldX(), menu.searchFieldY(),
                    menu.searchFieldW(), 14, Component.literal("Recherche\u2026"));
            field.setMaxLength(48);
            field.setValue(menu.search);
            field.setResponder(v -> {
                if (!v.equals(menu.search)) {
                    menu.search = v;
                    menu.layout(this.width, this.height, pen);
                }
            });
            addRenderableWidget(field);
        } else if (menu.mode == CorzMenu.Mode.PRESETS && menu.presetFieldW() > 0) {
            EditBox field = new EditBox(this.font, menu.presetFieldX(), menu.presetFieldY(),
                    menu.presetFieldW(), 14, Component.literal("Nom du preset"));
            field.setMaxLength(24);
            field.setValue(menu.presetName);
            field.setResponder(v -> menu.presetName = v);
            addRenderableWidget(field);
        }
        for (CorzMenu.FieldBox box : menu.textFields) {
            EditBox field = new EditBox(this.font, box.x, box.y, box.w, 14,
                    Component.literal(box.setting.label));
            field.setMaxLength(240);
            field.setValue(box.setting.value);
            field.setResponder(v -> {
                box.setting.value = v;
                ModuleManager.save();
            });
            addRenderableWidget(field);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        this.graphicsContext = graphics;
        MenuFx.tick();
        menu.draw(graphics::fill, mouseX, mouseY);
    }

    private void installHooks() {
        menu.soundClick = UiSounds::click;
        menu.soundToggle = UiSounds::toggle;
        menu.requestClose = this::onClose;
        menu.onLayoutChanged = this::rebuild;
        menu.clipboardGet = () -> this.minecraft == null ? "" : this.minecraft.keyboardHandler.getClipboard();
        menu.clipboardSet = v -> {
            if (this.minecraft != null) {
                this.minecraft.keyboardHandler.setClipboard(v);
            }
        };
        menu.waypointAddHere = () -> Waypoints.addHere(this.minecraft);
        menu.waypointRemoveLast = Waypoints::removeLast;
        menu.waypointClear = Waypoints::clear;
    }

    private void rebuild() {
        this.rebuildWidgets();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
        if (super.mouseClicked(event, doubled)) {
            return true;
        }
        boolean handled = menu.click(event.x(), event.y(), event.button());
        if (handled) {
            this.setFocused(null);
        }
        return handled;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        menu.scroll(scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (menu.capturing()) {
            menu.captureKey(event.key());
            return true;
        }
        if (menu.escape()) {
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(null);
        }
        super.onClose();
    }
}
