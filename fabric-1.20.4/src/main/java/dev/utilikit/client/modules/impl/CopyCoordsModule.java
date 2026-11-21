package dev.utilikit.client.modules.impl;

import dev.utilikit.client.modules.Module;
import dev.utilikit.client.modules.ModuleCategory;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

public class CopyCoordsModule extends Module {
    private final KeyBinding copyKey;

    public CopyCoordsModule(KeyBinding copyKey) {
        super("copycoords", "Copier Coordonnées", "Une touche (O par défaut) copie tes coordonnées : clic sur le message pour copier directement.", ModuleCategory.PRATIQUE);
        this.copyKey = copyKey;
    }

    public static KeyBinding registerKey() {
        return net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.utilikit.copycoords", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O,
                        dev.utilikit.client.UtiliKitClient.KEY_CATEGORY));
    }

    public void registerTick() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (copyKey.wasPressed()) {
                if (enabled) {
                    execute(client);
                }
            }
        });
    }

    private void execute(MinecraftClient client) {
        if (client.player == null || client.keyboard == null || client.inGameHud == null) {
            return;
        }
        BlockPos pos = client.player.getBlockPos();
        String dimension = client.world != null ? client.world.getRegistryKey().getValue().getPath() : "overworld";
        String coords = pos.getX() + " " + pos.getY() + " " + pos.getZ();
        client.keyboard.setClipboard(coords);
        Text clickable = Text.literal(coords)
                .styled(style -> style
                        .withColor(Formatting.AQUA)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, coords))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Text.literal("Clique pour copier"))));
        Text dimensionLabel = Text.literal("[" + dimension + "] ").formatted(Formatting.GRAY);
        client.inGameHud.getChatHud().addMessage(
                Text.literal("[NTH Utils] ").formatted(Formatting.GRAY)
                        .append(dimensionLabel)
                        .append(clickable));
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
