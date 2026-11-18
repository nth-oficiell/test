package dev.utilikit.client.modules;

public abstract class Setting {
    public final String id;
    public final String label;

    protected Setting(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public static class Toggle extends Setting {
        public boolean value;

        public Toggle(String id, String label, boolean defaultValue) {
            super(id, label);
            this.value = defaultValue;
        }
    }

    public static class Slider extends Setting {
        public final double min;
        public final double max;
        public double value;

        public Slider(String id, String label, double min, double max, double defaultValue) {
            super(id, label);
            this.min = min;
            this.max = max;
            this.value = defaultValue;
        }
    }
}
