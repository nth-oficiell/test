# ⛏ UtiliKit

**Mod utilitaire client pour Minecraft (Fabric)** — un seul menu in-game (**Maj droite** par défaut) pour personnaliser ton jeu : modules activables d'un clic, réglages à la volée, presets exportables. Le tout en français. 🇫🇷

🌐 **Site de téléchargement** : https://nth-oficiell.github.io/test/ (choisis ton launcher + ta version, et récupère le jar).

## 📦 Versions disponibles

| Version de Minecraft | Loader | Fichier |
|---|---|---|
| 1.20.1 | Fabric | `utilikit-1.20.1-v*` |
| 1.21.1 | Fabric | `utilikit-1.21.1-v*` |
| 1.21.11 | Fabric | `utilikit-1.21.11-v*` |
| 26.1.2 / 26.2 / 26.3 | Fabric | 🚧 dès que les mappings Yarn sortent |

> Les jars exacts et leur dernière version sont listés sur le site (ils sont générés par les builds GitHub Actions de ce dépôt).

## 🧩 Modules

| Module | Catégorie | Description |
|---|---|---|
| **FullBright** | VISUEL | Luminosité max extension — slider d'intensité 1 à 16 |
| **Zoom** | VISUEL | Zoom caméra en maintenant la touche (`C`) — facteur réglable |
| **No Hurt Cam** | VISUEL | Désactive le tremblement de caméra quand tu prends des dégâts |
| **Limite FPS** | VISUEL | Slider de limite de FPS appliqué immédiatement au jeu |
| **HUD Infos** | AFFICHAGE | FPS, coordonnées, direction, heure du monde — toggles séparés |
| **Keystrokes** | AFFICHAGE | ZQSD/WASD + clics affichés sous le crosshair, façon packs HUD |
| **Ping HUD** | AFFICHAGE | Latence live avec le serveur, colorée (vert/orange/rouge) |
| **Durabilité** | AFFICHAGE | Durabilité de l'objet en main, colorée selon l'usure |
| **Auto Sprint** | PRATIQUE | Sprint automatique dès que tu avances |
| **Copier Coordonnées** | PRATIQUE | Touche `O` : copie tes coords + message cliquable dans le chat |
| **Presets** | — | Profils de config : sauvegarder / appliquer / supprimer / écraser-coller |

## 🎮 Utilisation

1. Installe **Fabric Loader** (compatible avec ton launcher) + **Fabric API** pour ta version.
2. Glisse le jar UtiliKit dans le dossier `mods`.
3. Lance le jeu, appuie sur **Maj droite** pour ouvrir le menu.
4. Dans le menu : clique sur un module pour le sélectionner, re-clique pour l'activer/désactiver, règle les sliders/toggles à droite. Onglet **Presets** pour sauvegarder et partager ta config (export/import via le presse-papiers).

Raccourcis (rebindables dans les options du jeu) :
- **Maj droite** — ouvrir le menu UtiliKit
- **C** — maintenir pour zoomer (si module Zoom activé)
- **O** — copier tes coordonnées

## 🛠 Développement

Projet Gradle multi-versions : un cœur partagé (`common/`) + un sous-projet par version de Minecraft.

```bash
gradle build                    # compile TOUTES les versions
gradle :fabric-1.21.1:build     # une seule version
```

Chaque sous-projet `fabric-X` contient le code client dépendant de la version (entrypoint, écran, modules impl, mixins) et compile aussi le cœur commun depuis `common/src/main/java`.

## 📄 Licence

MIT
