package dev.utilikit.client.ui;

import net.minecraft.client.gui.Click;
import dev.utilikit.client.modules.ModuleManager;
import dev.utilikit.client.modules.Setting;
import dev.utilikit.client.modules.extra.Waypoints;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

public class UtiliKitScreen extends Screen {
    private final CorzMenu menu = new CorzMenu();
    private DrawContext drawContext;
    private final CorzMenu.Pen pen = new CorzMenu.Pen() {
        @Override
        public void text(String s, double x, double y, int argb) {
            if (drawContext != null) {
                drawContext.drawText(textRenderer, s, (int) x, (int) y, argb, true);
            }
        }

        @Override
        public void textCenter(String s, double x, double y, int argb) {
            if (drawContext != null) {
                drawContext.drawCenteredTextWithShadow(textRenderer, s, (int) x, (int) y, argb);
            }
        }

        @Override
        public int width(String s) {
            return textRenderer.getWidth(s);
        }
    };

    public UtiliKitScreen() {
        super(Text.literal("NTH Utils"));
        MenuFx.onOpen();
        UiSounds.open();
    }

    @Override
    protected void init() {
        installHooks();
        menu.layout(this.width, this.height, pen);
        if (menu.mode == CorzMenu.Mode.MODULES && menu.searchFieldW() > 0) {
            TextFieldWidget field = new TextFieldWidget(this.textRenderer, menu.searchFieldX(),
                    menu.searchFieldY(), menu.searchFieldW(), 14, Text.literal("Recherche\u2026"));
            field.setMaxLength(48);
            field.setText(menu.search);
            field.setChangedListener(v -> {
                if (!v.equals(menu.search)) {
                    menu.search = v;
                    menu.layout(this.width, this.height, pen);
                }
            });
            addDrawableChild(field);
        } else if (menu.mode == CorzMenu.Mode.PRESETS && menu.presetFieldW() > 0) {
            TextFieldWidget field = new TextFieldWidget(this.textRenderer, menu.presetFieldX(),
                    menu.presetFieldY(), menu.presetFieldW(), 14, Text.literal("Nom du preset"));
            field.setMaxLength(24);
            field.setText(menu.presetName);
            field.setChangedListener(v -> menu.presetName = v);
            addDrawableChild(field);
        }
        for (CorzMenu.FieldBox box : menu.textFields) {
            TextFieldWidget field = new TextFieldWidget(this.textRenderer, box.x, box.y, box.w, 14,
                    Text.literal(box.setting.label));
            field.setMaxLength(240);
            field.setText(box.setting.value);
            field.setChangedListener(v -> {
                box.setting.value = v;
                ModuleManager.save();
            });
            addDrawableChild(field);
        }
        addDrawable((context, mouseX, mouseY, delta) -> {
            this.drawContext = context;
            MenuFx.tick();
            menu.draw(context::fill, mouseX, mouseY);
        });
    }

    private void installHooks() {
        menu.soundClick = UiSounds::click;
        menu.soundToggle = UiSounds::toggle;
        menu.requestClose = this::close;
        menu.onLayoutChanged = this::rebuild;
        menu.clipboardGet = () -> this.client == null ? "" : this.client.keyboard.getClipboard();
        menu.clipboardSet = v -> {
            if (this.client != null) {
                this.client.keyboard.setClipboard(v);
            }
        };
        menu.waypointAddHere = () -> Waypoints.addHere(this.client);
        menu.waypointRemoveLast = Waypoints::removeLast;
        menu.waypointClear = Waypoints::clear;
    }

    private void rebuild() {
        clearChildren();
        init();
    }

@Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (super.mouseClicked(click, doubled)) {
            return true;
        }
        boolean handled = menu.click(click.x(), click.y(), click.button());
        if (handled) {
            this.setFocused(null);
        }
        return handled;
    }

@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        menu.scroll(verticalAmount);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
@Override
    public boolean keyPressed(KeyInput input) {
        if (menu.capturing()) {
            menu.captureKey(input.key());
            return true;
        }
        if (menu.escape()) {
            return true;
        }
        return super.keyPressed(input);
    }
}
