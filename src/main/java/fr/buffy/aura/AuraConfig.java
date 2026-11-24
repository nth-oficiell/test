package fr.buffy.aura;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Mth;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class AuraConfig {
	public static final float MIN_SCALE = 0.5f;
	public static final float MAX_SCALE = 2.0f;
	public static final int MIN_CROSSHAIR_SIZE = 3;
	public static final int MAX_CROSSHAIR_SIZE = 20;
	public static final int MIN_CROSSHAIR_OPACITY = 20;
	public static final int MAX_CROSSHAIR_OPACITY = 100;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("aura.json");
	private static final Path TEMP = PATH.resolveSibling("aura.json.tmp");

	private static AuraConfig instance = new AuraConfig();

	public boolean hud = true;
	public boolean coords = true;
	public boolean fps = true;
	public boolean time = true;
	public boolean fullbright = false;
	public int theme = 0;
	/** Coin superieur gauche du HUD, en pixels GUI. */
	public int hudX = 8;
	public int hudY = 8;
	/** Echelle du HUD, entre MIN_SCALE et MAX_SCALE. */
	public float hudScale = 1f;
	/** Reticule personnalise, taille en pixels et opacite en pourcentage. */
	public boolean crosshair = true;
	public int crosshairSize = 9;
	public int crosshairOpacity = 85;
	/** Luminosite d'origine du joueur, conservee pour la restituer meme apres un redemarrage. */
	public Double savedGamma = null;

	private AuraConfig() {
	}

	public static AuraConfig get() {
		return instance;
	}

	/** Relit le fichier champ par champ : un fichier partiel ou abime ne remet pas tout a zero. */
	public static void load() {
		if (!Files.exists(PATH)) {
			return;
		}

		try (Reader reader = Files.newBufferedReader(PATH)) {
			JsonElement root = JsonParser.parseReader(reader);
			if (!root.isJsonObject()) {
				return;
			}

			JsonObject json = root.getAsJsonObject();
			AuraConfig loaded = new AuraConfig();
			loaded.hud = readBoolean(json, "hud", loaded.hud);
			loaded.coords = readBoolean(json, "coords", loaded.coords);
			loaded.fps = readBoolean(json, "fps", loaded.fps);
			loaded.time = readBoolean(json, "time", loaded.time);
			loaded.fullbright = readBoolean(json, "fullbright", loaded.fullbright);
			loaded.theme = Math.floorMod(readInt(json, "theme", loaded.theme), AuraTheme.count());
			loaded.hudX = Math.max(0, readInt(json, "hudX", loaded.hudX));
			loaded.hudY = Math.max(0, readInt(json, "hudY", loaded.hudY));
			loaded.hudScale = Mth.clamp(readFloat(json, "hudScale", loaded.hudScale), MIN_SCALE, MAX_SCALE);
			loaded.crosshair = readBoolean(json, "crosshair", loaded.crosshair);
			loaded.crosshairSize = Mth.clamp(readInt(json, "crosshairSize", loaded.crosshairSize),
					MIN_CROSSHAIR_SIZE, MAX_CROSSHAIR_SIZE);
			loaded.crosshairOpacity = Mth.clamp(readInt(json, "crosshairOpacity", loaded.crosshairOpacity),
					MIN_CROSSHAIR_OPACITY, MAX_CROSSHAIR_OPACITY);
			loaded.savedGamma = readGamma(json);
			instance = loaded;
		} catch (Exception exception) {
			AuraMod.LOGGER.warn("Lecture de la configuration impossible : {}", PATH, exception);
		}
	}

	/** Ecrit dans un fichier temporaire puis le deplace : jamais de config tronquee. */
	public static void save() {
		try {
			Files.createDirectories(PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(TEMP)) {
				GSON.toJson(instance, writer);
			}
			Files.move(TEMP, PATH, StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception exception) {
			AuraMod.LOGGER.warn("Ecriture de la configuration impossible : {}", PATH, exception);
		}
	}

	private static boolean readBoolean(JsonObject json, String key, boolean fallback) {
		JsonElement element = json.get(key);
		if (element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isBoolean()) {
			return fallback;
		}
		return element.getAsBoolean();
	}

	private static int readInt(JsonObject json, String key, int fallback) {
		JsonElement element = json.get(key);
		if (element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
			return fallback;
		}
		return element.getAsInt();
	}

	private static float readFloat(JsonObject json, String key, float fallback) {
		JsonElement element = json.get(key);
		if (element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
			return fallback;
		}
		return element.getAsFloat();
	}

	private static Double readGamma(JsonObject json) {
		JsonElement element = json.get("savedGamma");
		if (element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
			return null;
		}
		return (double) Math.max(0f, Math.min(1f, element.getAsFloat()));
	}

	public AuraTheme theme() {
		return AuraTheme.byIndex(theme);
	}
}
