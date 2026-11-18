# UtiliKit

Mod utilitaire **client** Minecraft (Fabric 1.20.1) avec un **menu in-game** pour personnaliser ton jeu : modules activables/désactivables et réglages à la volée, sauvegardés automatiquement dans `config/utilikit.json`.

## Modules inclus (v0.1.0)
| Module | Catégorie | Description |
|---|---|---|
| FullBright | Visuel | Luminosité au-delà du maximum du jeu (slider d'intensité) |
| Zoom | Visuel | Maintiens la touche Zoom (C par défaut) pour zoomer, facteur réglable |
| HUD Infos | Affichage | FPS, coordonnées, direction, heure du monde en haut à gauche |

## Menu in-game
- Touche par défaut : **Maj droite** (rebindable dans Options > Contrôles > UtiliKit)
- **Clic** sur un module : le sélectionner pour voir ses réglages
- **Re-clic** sur le module sélectionné : l'activer / le désactiver
- Toggles et sliders à droite, sauvegarde automatique

## Compilation
Prérequis : JDK 17+.

```bash
gradle wrapper   # génère le wrapper la première fois (ou ouvre le projet dans IntelliJ)
./gradlew build
```

Le jar final se trouve dans `build/libs/utilikit-0.1.0.jar` (celui **sans** `-sources`).

## Installation
Fabric Loader >= 0.15 + Fabric API, dossier `mods/`.

## Roadmap
1. ✅ Système de modules + config JSON + menu in-game
2. Presets de config (profils sauvegardables, import/export)
3. Recherche dans le menu, catégories scrollables
4. Portage Forge / NeoForge, puis versions plus récentes (1.21.x)
5. Nouveaux modules : auto-reconnect, chat amélioré, HUD éditable, accents...

## Licence
MIT
