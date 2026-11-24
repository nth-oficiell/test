package fr.buffy.aura;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class AuraConfig {
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
