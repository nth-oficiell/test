# ⛏ NTH Utils

**Mod utilitaire client pour Minecraft (Fabric)** — un seul menu in-game (**Maj droite** par défaut) pour personnaliser ton jeu : modules activables d'un clic, réglages à la volée, presets exportables. Le tout en français. 🇫🇷

🌐 **Site de téléchargement** : https://nth-oficiell.github.io/test/ (choisis ton launcher + ta version, et récupère le jar).

## 📦 Versions disponibles

| Version de Minecraft | Loader | Fichier |
|---|---|---|
| 1.20.1 | Fabric (mappings Yarn) | `utilikit-1.20.1-v*` |
| 1.21.1 | Fabric (mappings Yarn) | `utilikit-1.21.1-v*` |
| 1.21.11 | Fabric (mappings Yarn) | `utilikit-1.21.11-v*` |
| 26.1.2 | Fabric (noms officiels Mojang) | `utilikit-26.1.2-v*` |
| 26.2 | Fabric (noms officiels Mojang) | `utilikit-26.2-v*` |
| 26.3 | Fabric (noms officiels Mojang) | `utilikit-26.3-v*` |

### 🔓 La solution 26.x

Depuis **26.1**, Minecraft n'est **plus obfusqué** : le jeu sort avec les noms de classes officiels de Mojang et Yarn n'est plus maintenu au-delà de 1.21.11. Les sous-projets 26.x compilent donc **sans aucune mappings** avec le nouveau plugin Loom non-remap (`net.fabricmc.fabric-loom`), en ciblant les **noms officiels Mojang** (`Minecraft`, `LocalPlayer`, `GuiGraphicsExtractor`, `HudElementRegistry`…). Résultat : les 6 versions sont supportées en parallèle, avec les mêmes 116 modules. |

> Les jars exacts et leur dernière version sont listés sur le site (ils sont générés par les builds GitHub Actions de ce dépôt).

## 🧩 Modules (99)

| Catégorie | Exemples |
|---|---|
| **Affichage (51)** | HUD Infos, Coordonnées, Altitude, Chunk, Direction, Angles de vue, Biome, Dimension, Lumière, Heure du monde, Jour du monde, FPS info, FPS min/moy/max, RAM, Vitesse, Vitesse verticale, Distance parcourue, Pas, Vie, Faim, Saturation, Air, XP (niveau / progression / total), Armure, Objet en main, Durabilité en main, Météo, Serveur, Marque du serveur, Session, Entités proches, Heure réelle, Pseudo, UUID, Ping HUD, Keystrokes, Durabilité, Phase de lune, Période, Résolution écran, Taille interface, Ping détaillé, Bloc sous les pieds, Position arrondie, Chunk local, Jour écoulé, Main secondaire, Pièces d'armure, Empilement max main |
| **Pratique (33)** | Coordonnées Nether, Mode de jeu, Bloc visé, Entité visée, Distance de visée, Recharge d'attaque, Hauteur de chute, Auto Sprint, Copier Coordonnées, Boussole, Difficulté, Effets actifs, Vol, Véhicule, Accroupi, Sprint, Sous l'eau, En feu, Java, Système, Plein écran, Solo / Multi, Quantité en main, Position visée, Durabilité % main, Face visée, Vie visée, Entité visée (pos), Dégâts récents, Distance au 0,0, Mods chargés, À propos, Version Fabric |
| **Visuel (12)** | FullBright (slider), Zoom (facteur réglable), No Hurt Cam, Limite FPS, FOV personnalisé (slider), Caméra lisse, Anti Pluie, Sensibilité souris, Luminosité réglage, Distance de rendu, Anti Orage, Rareté de l'objet |

Chaque module a ses réglages (toggles, sliders) dans le menu in-game, sauvegardés automatiquement et exportables via presets.

## 🎮 Utilisation

1. Installe **Fabric Loader** (compatible avec ton launcher) + **Fabric API** pour ta version.
2. Glisse le jar NTH Utils dans le dossier `mods`.
3. Lance le jeu, appuie sur **Maj droite** pour ouvrir le menu.
4. Dans le menu : **clic gauche** sur un module pour l'activer/désactiver, **clic droit** pour ouvrir ses paramètres complets (sliders, toggles) et y configurer une **touche d'activation** personnalisée (clique sur « Touche d'activation » puis appuie sur la touche ; Échap annule). La liste des modules se **défile à la molette** (barre de scroll à droite de la liste). Onglet **Presets** pour sauvegarder et partager ta config (export/import via le presse-papiers, touches incluses).

Raccourcis (rebindables dans les options du jeu) :
- **Maj droite** — ouvrir le menu NTH Utils
- **C** — maintenir pour zoomer (si module Zoom activé)
- **O** — copier tes coordonnées

## 🛠 Développement

Projet Gradle multi-versions : un cœur partagé (`common/`) + un sous-projet par version de Minecraft. Les sous-projets 1.20.x/1.21.x utilisent Yarn ; les 26.x utilisent les noms officiels Mojang, sans mappings.

```bash
gradle build                    # compile TOUTES les versions
gradle :fabric-1.21.1:build     # une seule version
```

Chaque sous-projet `fabric-X` contient le code client dépendant de la version (entrypoint, écran, modules impl, mixins) et compile aussi le cœur commun depuis `common/src/main/java`.

## 📄 Licence

MIT
