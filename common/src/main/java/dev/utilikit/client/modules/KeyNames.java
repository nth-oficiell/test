package dev.utilikit.client.modules;

import java.util.HashMap;
import java.util.Map;

public final class KeyNames {
    private static final Map<Integer, String> NAMES = new HashMap<>();

    private KeyNames() {
    }

    static {
        NAMES.put(32, "Espace");
        NAMES.put(39, "Apostrophe");
        NAMES.put(44, "Virgule");
        NAMES.put(45, "Moins");
        NAMES.put(46, "Point");
        NAMES.put(47, "Slash");
        NAMES.put(59, "Point-virgule");
        NAMES.put(61, "Egal");
        NAMES.put(91, "Crochet gauche");
        NAMES.put(92, "Backslash");
        NAMES.put(93, "Crochet droit");
        NAMES.put(96, "Accent grave");
        NAMES.put(256, "Echap");
        NAMES.put(257, "Entree");
        NAMES.put(258, "Tab");
        NAMES.put(259, "Retour arriere");
        NAMES.put(260, "Inser");
        NAMES.put(261, "Suppr");
        NAMES.put(262, "Fleche droite");
        NAMES.put(263, "Fleche gauche");
        NAMES.put(264, "Fleche bas");
        NAMES.put(265, "Fleche haut");
        NAMES.put(266, "Page haut");
        NAMES.put(267, "Page bas");
        NAMES.put(268, "Debut");
        NAMES.put(269, "Fin");
        NAMES.put(284, "Impr ecran");
        NAMES.put(285, "Pause");
        for (int i = 0; i < 12; i++) {
            NAMES.put(290 + i, "F" + (i + 1));
        }
        NAMES.put(330, "Pave num .");
        NAMES.put(331, "Pave num /");
        NAMES.put(332, "Pave num *");
        NAMES.put(333, "Pave num -");
        NAMES.put(334, "Pave num +");
        NAMES.put(335, "Pave num Entree");
        for (int i = 0; i < 10; i++) {
            NAMES.put(320 + i, "Pave num " + i);
        }
        NAMES.put(340, "Maj gauche");
        NAMES.put(341, "Ctrl gauche");
        NAMES.put(342, "Alt gauche");
        NAMES.put(343, "Meta gauche");
        NAMES.put(344, "Maj droite");
        NAMES.put(345, "Ctrl droit");
        NAMES.put(346, "Alt droit");
        NAMES.put(347, "Meta droit");
    }

    public static String keyName(int keyCode) {
        if (keyCode >= 48 && keyCode <= 57) {
            return String.valueOf((char) ('0' + keyCode - 48));
        }
        if (keyCode >= 65 && keyCode <= 90) {
            return String.valueOf((char) ('A' + keyCode - 65));
        }
        String name = NAMES.get(keyCode);
        return name == null ? "Touche " + keyCode : name;
    }
}
