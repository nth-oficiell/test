package fr.buffy.aura;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

/** Réticule dessiné par Aura : une croix aux couleurs du thème, réglable en taille et en opacité. */
public final class AuraCrosshair {
	private AuraCrosshair() {
	}

	/** Remplace l'élément vanilla : le réticule du jeu disparaît quand celui d'Aura est actif. */
	public static void register() {
		try {
			HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, original -> (graphics, delta) -> {
				if (isEnabled()) {
					draw(graphics, graphics.guiWidth() / 2, graphics.guiHeight() / 2, AuraConfig.get(), 1f);
				} else {
					original.render(graphics, delta);
				}
			});
		} catch (IllegalArgumentException exception) {
			AuraMod.LOGGER.warn("Réticule vanilla introuvable : celui d'Aura sera dessiné par-dessus.", exception);
			HudElementRegistry.addLast(AuraMod.id("crosshair"), (graphics, delta) -> {
				if (isEnabled()) {
					draw(graphics, graphics.guiWidth() / 2, graphics.guiHeight() / 2, AuraConfig.get(), 1f);
				}
			});
		}
	}

	/** Mêmes conditions d'affichage que le réticule vanilla (F1, troisième personne, hors monde). */
	private static boolean isEnabled() {
		Minecraft client = Minecraft.getInstance();
		return AuraConfig.get().crosshair
				&& client.player != null
				&& !client.options.hideGui
				&& client.options.getCameraType().isFirstPerson();
	}

	/** Dessine la croix centrée en (centerX, centerY) ; scale sert à l'aperçu. */
	public static void draw(GuiGraphics graphics, int centerX, int centerY, AuraConfig config, float scale) {
		int size = Math.max(1, Math.round(config.crosshairSize * scale));
		int gap = Math.max(1, Math.round(size / 3f));
		int thickness = Math.max(1, Math.round(size / 4f));
		int alpha = Math.round(255f * Mth.clamp(config.crosshairOpacity, 0, 100) / 100f);

		arms(graphics, centerX, centerY, size + 1, thickness + 2, gap, Draw.alpha(0x000000, Math.min(110, alpha / 2)));
		arms(graphics, centerX, centerY, size, thickness, gap, Draw.alpha(config.theme().accent(), alpha));
	}

	private static void arms(GuiGraphics graphics, int centerX, int centerY, int size, int thickness, int gap,
							int color) {
		int left = centerX - thickness / 2;
		int top = centerY - thickness / 2;
		int radius = Math.max(1, thickness / 2);

		Draw.roundRect(graphics, left, centerY - gap - size, thickness, size, radius, color);
		Draw.roundRect(graphics, left, centerY + gap, thickness, size, radius, color);
		Draw.roundRect(graphics, centerX - gap - size, top, size, thickness, radius, color);
		Draw.roundRect(graphics, centerX + gap, top, size, thickness, radius, color);
		Draw.roundRect(graphics, left, top, thickness, thickness, radius, color);
	}
}
