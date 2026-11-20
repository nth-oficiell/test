package dev.utilikit.client.modules;

public enum ModuleCategory {
    AFFICHAGE("Affichage"),
    VISUEL("Visuel"),
    JEU("Jeu"),
    SYSTEME("Système"),
    PRATIQUE("Pratique");

    public final String label;

    ModuleCategory(String label) {
        this.label = label;
    }
}
