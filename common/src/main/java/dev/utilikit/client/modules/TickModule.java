package dev.utilikit.client.modules;

public class TickModule extends Module {
    public interface Ticker {
        void tick(Object game);
    }

    private final Ticker ticker;

    public TickModule(String id, String name, String description, ModuleCategory category, Ticker ticker) {
        super(id, name, description, category);
        this.ticker = ticker;
    }

    public void tick(Object game) {
        if (enabled && ticker != null) {
            try {
                ticker.tick(game);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
