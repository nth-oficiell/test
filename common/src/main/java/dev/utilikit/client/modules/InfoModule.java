package dev.utilikit.client.modules;

public class InfoModule extends Module {
    public interface Provider {
        String line(Object game);
    }

    public interface Ticker {
        void tick(Object game);
    }

    private final Provider provider;
    private final Ticker ticker;
    private String cachedLine;
    private long cachedAt;
    private static final long CACHE_MS = 120L;

    public InfoModule(String id, String name, String description, ModuleCategory category, Provider provider) {
        this(id, name, description, category, provider, null);
    }

    public InfoModule(String id, String name, String description, ModuleCategory category,
                      Provider provider, Ticker ticker) {
        super(id, name, description, category);
        this.provider = provider;
        this.ticker = ticker;
        settings.add(new Setting.Toggle("bg", "Fond semi-transparent", true));
    }

    public void tick(Object game) {
        if (ticker != null) {
            ticker.tick(game);
        }
    }

    public String lineCached(Object game) {
        if (!enabled) {
            cachedLine = null;
            return null;
        }
        long now = System.currentTimeMillis();
        if (cachedLine == null || now - cachedAt >= CACHE_MS) {
            cachedLine = line(game);
            cachedAt = now;
        }
        return cachedLine;
    }

    public String line(Object game) {
        if (!enabled) {
            return null;
        }
        try {
            return provider.line(game);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean hasBackground() {
        Setting background = toggle("bg");
        return background == null || ((Setting.Toggle) background).value;
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
