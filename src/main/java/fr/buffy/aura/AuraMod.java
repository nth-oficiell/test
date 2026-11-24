package fr.buffy.aura;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuraMod implements ClientModInitializer {
	public static final String MOD_ID = "aura";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(id("main"));

	private static KeyMapping menuKey;

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitializeClient() {
		AuraConfig.load();

		menuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.aura.menu",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_O,
				KEY_CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (menuKey.consumeClick()) {
				if (client.screen == null) {
					client.setScreen(new AuraScreen(null));
				}
			}
		});

		HudElementRegistry.addLast(id("hud"), AuraHud::render);
		AuraCrosshair.register();
		applyFullbright(AuraConfig.get().fullbright);
	}

	public static KeyMapping menuKey() {
		return menuKey;
	}

	/** Active ou coupe le plein jour en gardant la luminosite d'origine du joueur. */
	public static void applyFullbright(boolean enabled) {
		Minecraft client = Minecraft.getInstance();
		if (client.options == null) {
			return;
		}

		AuraConfig config = AuraConfig.get();

		if (enabled) {
			if (config.savedGamma == null) {
				config.savedGamma = client.options.gamma().get();
				AuraConfig.save();
			}
			client.options.gamma().set(1.0D);
		} else if (config.savedGamma != null) {
			client.options.gamma().set(config.savedGamma);
			config.savedGamma = null;
			AuraConfig.save();
		}
	}
}
