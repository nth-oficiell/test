package dev.utilikit.client;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AutoUpdater {
    private static final String MOD_ID = "utilikit";
    private static final String SITE = "https://nth-oficiell.github.io/test/";
    private static final long MAX_BYTES = 30L * 1024 * 1024;

    private static final AtomicBoolean STARTED = new AtomicBoolean();
    private static final List<String> NOTES = new ArrayList<>();
    private static boolean notesReady = false;

    private AutoUpdater() {
    }

    public static void start() {
        cleanup();
        if (!STARTED.compareAndSet(false, true)) {
            return;
        }
        Thread thread = new Thread(AutoUpdater::run, "nth-utils-autoupdate");
        thread.setDaemon(true);
        thread.start();
    }

    public static synchronized boolean hasNotes() {
        return notesReady;
    }

    public static synchronized List<String> takeNotes() {
        notesReady = false;
        List<String> copy = new ArrayList<>(NOTES);
        NOTES.clear();
        return copy;
    }

    private static synchronized void note(String line) {
        if (line != null && !line.isEmpty() && NOTES.size() < 7) {
            NOTES.add(line);
        }
        notesReady = true;
    }

    private static void cleanup() {
        try {
            Path jar = runningJar();
            if (jar == null) {
                return;
            }
            Path mods = jar.getParent();
            if (mods == null || !Files.isDirectory(mods)) {
                return;
            }
            try (java.util.stream.Stream<Path> stream = Files.list(mods)) {
                for (Path p : stream.toList()) {
                    String name = p.getFileName().toString();
                    if (name.startsWith(MOD_ID + "-") && (name.endsWith(".jar.new") || name.endsWith(".jar.old"))) {
                        try {
                            Files.deleteIfExists(p);
                        } catch (Throwable ignored) {
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private static void run() {
        try {
            Path jar = runningJar();
            if (jar == null || !jar.getFileName().toString().startsWith(MOD_ID + "-")) {
                return;
            }
            String fileName = jar.getFileName().toString();
            String current = modVersion();
            if (current == null) {
                return;
            }
            Manifest manifest = fetchManifest();
            if (manifest == null) {
                return;
            }
            String mc = targetMc(fileName, current);
            if (mc == null) {
                return;
            }
            Build build = manifest.newestFor(mc, current);
            if (build == null) {
                return;
            }
            note("\u2B9C NTH Utils v" + build.modver + " : mise \u00E0 jour automatique\u2026");
            byte[] bytes = download(SITE + "downloads/" + build.file);
            if (bytes == null) {
                note("\u2717 Mise \u00E0 jour v" + build.modver + " impossible (r\u00E9seau) \u2014 disponible sur le site.");
                return;
            }
            build.notes = manifest.notes;
            install(jar, build, bytes);
        } catch (Throwable ignored) {
        }
    }

    private static void install(Path jar, Build build, byte[] bytes) {
        String fileName = jar.getFileName().toString();
        String wantedName = build.file.replaceFirst("^utilikit-", MOD_ID + "-");
        Path staged = jar.resolveSibling(wantedName + ".new");
        try {
            Files.deleteIfExists(staged);
            Files.write(staged, bytes);
        } catch (Throwable t) {
            note("\u2717 Mise \u00E0 jour v" + build.modver + " impossible (dossier non accessible).");
            return;
        }
        boolean replaced = false;
        try {
            Files.delete(jar);
            replaced = true;
        } catch (Throwable first) {
            try {
                Files.move(jar, jar.resolveSibling(fileName + ".old"), StandardCopyOption.REPLACE_EXISTING);
                replaced = true;
            } catch (Throwable second) {
                note("\u2717 v" + build.modver + " pr\u00EAte mais l'ancien jar est verrouill\u00E9 \u2014 mets-la par le site.");
            }
        }
        if (!replaced) {
            try {
                Files.deleteIfExists(staged);
            } catch (Throwable ignored) {
            }
            return;
        }
        try {
            Files.move(staged, jar.resolveSibling(wantedName), StandardCopyOption.REPLACE_EXISTING);
        } catch (Throwable t) {
            try {
                Files.move(staged, jar.resolveSibling(fileName), StandardCopyOption.REPLACE_EXISTING);
            } catch (Throwable ignored) {
            }
            note("\u2717 Mise \u00E0 jour v" + build.modver + " \u00E9chou\u00E9e \u2014 disponible sur le site.");
            return;
        }
        note("\u2713 NTH Utils v" + build.modver + " install\u00E9 \u2014 red\u00E9marre le jeu pour l'appliquer !");
        for (String line : build.notes) {
            note("\u2022 " + line);
        }
    }

    private static Path runningJar() {
        try {
            Path own = Paths.get(AutoUpdater.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (Files.isRegularFile(own) && own.getFileName().toString().endsWith(".jar")) {
                return own;
            }
        } catch (Throwable ignored) {
        }
        try {
            for (var container : net.fabricmc.loader.api.FabricLoader.getInstance().getAllMods()) {
                if (!MOD_ID.equals(container.getMetadata().getId())) {
                    continue;
                }
                for (Path p : container.getOrigin().getPaths()) {
                    if (Files.isRegularFile(p) && p.getFileName().toString().endsWith(".jar")) {
                        return p;
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static String modVersion() {
        try {
            return net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer(MOD_ID)
                    .map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse(null);
        } catch (Throwable t) {
            return null;
        }
    }

    private static String targetMc(String jarFileName, String version) {
        String suffix = "-" + version + ".jar";
        String name = jarFileName;
        if (!name.startsWith(MOD_ID + "-") || !name.endsWith(suffix)) {
            return null;
        }
        return name.substring(MOD_ID.length() + 1, name.length() - suffix.length());
    }

    private static Manifest fetchManifest() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SITE + "downloads/manifest.json?v=" + System.currentTimeMillis()))
                    .timeout(Duration.ofSeconds(10))
                    .header("User-Agent", "NTH-Utils")
                    .GET().build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return null;
            }
            return Manifest.parse(response.body());
        } catch (Throwable t) {
            return null;
        }
    }

    private static byte[] download(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(60))
                    .header("User-Agent", "NTH-Utils")
                    .GET().build();
            HttpResponse<InputStream> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() != 200) {
                return null;
            }
            try (InputStream in = response.body()) {
                return in.readNBytes((int) MAX_BYTES + 1);
            }
        } catch (Throwable t) {
            return null;
        }
    }

    private static final class Manifest {
        final List<Build> builds = new ArrayList<>();
        final List<String> notes = new ArrayList<>();

        private Manifest() {
        }

        static Manifest parse(String json) {
            Manifest manifest = new Manifest();
            Matcher pair = Pattern.compile(
                    "\"file\"\\s*:\\s*\"([^\"]+)\".*?\"mc\"\\s*:\\s*\"([^\"]+)\".*?\"modver\"\\s*:\\s*\"([^\"]+)\"")
                    .matcher(json);
            while (pair.find()) {
                Build build = new Build();
                build.file = pair.group(1);
                build.mc = pair.group(2);
                build.modver = pair.group(3);
                manifest.builds.add(build);
            }
            Matcher notesBlock = Pattern.compile("\"notes\"\\s*:\\s*\\[(.*?)]").matcher(json);
            if (notesBlock.find()) {
                Matcher item = Pattern.compile("\"((?:[^\"\\\\]|\\\\.)*)\"").matcher(notesBlock.group(1));
                while (item.find()) {
                    String text = item.group(1).replace("\\\"", "\"").replace("\\\\", "\\");
                    if (!text.isEmpty()) {
                        manifest.notes.add(text);
                    }
                }
            }
            return manifest;
        }

        Build newestFor(String mc, String current) {
            Build newest = null;
            for (Build build : builds) {
                if (!build.mc.equals(mc) || !isNewer(build.modver, current)) {
                    continue;
                }
                if (newest == null || isNewer(build.modver, newest.modver)) {
                    newest = build;
                }
            }
            return newest;
        }
    }

    private static final class Build {
        String file;
        String mc;
        String modver;
        List<String> notes = new ArrayList<>();
    }

    private static boolean isNewer(String latest, String current) {
        List<Long> a = segments(latest);
        List<Long> b = segments(current);
        int size = Math.max(a.size(), b.size());
        for (int i = 0; i < size; i++) {
            long x = i < a.size() ? a.get(i) : 0L;
            long y = i < b.size() ? b.get(i) : 0L;
            if (x != y) {
                return x > y;
            }
        }
        return false;
    }

    private static List<Long> segments(String version) {
        List<Long> out = new ArrayList<>();
        for (String part : version.replaceAll("[^0-9.]", "").split("\\.")) {
            out.add(part.isEmpty() ? 0L : Long.parseLong(part));
        }
        return out;
    }
}
