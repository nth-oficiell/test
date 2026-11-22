# Vortex

Client utilitaire Minecraft Fabric (1.21.11) — rebrand de CorzClient → **Vortex**.

## Contenu
- `vortex-client-2.0.1.jar` — le mod compilé, prêt à mettre dans `.minecraft/mods`
- `Vortex/` — sources complètes (Gradle / Fabric Loom)

## Changements
- Modules shader supprimés : `HandShader`, `MotionBlur` (+ mixin `MixinGameRendererMotionBlur`, util `MotionBlurTexture`) — plus de crash de shader
- Tout rebrand « Corz » → « Vortex » (nom affiché, mod id `vortexclient`, packages `com.vortex.client`, assets `assets/vortexclient`)
- **Config conservée** : les modules restent sauvegardés dans `.minecraft/config/corzclient` avec les mêmes noms de modules/réglages, ta config existante se recharge telle quelle
- Licence et appareil : constants d'authentification conservés (aucune influence sur la config)

## Mise à jour : modules sus chunk remplacés (donutlarp)
- Ancienne logique « seed feature » des modules Sus retirée
- `Sus Chunk Finder` → marque la zone sus approximative autour de chaque base (données serveur `donutlarp:bases`, fuzz par hash)
- `SusChunkFinderV2` → marque le chunk doré au-dessus de chaque base
- Nouveau module `Activity Inspector` → chunks rouges d'activité sous terre (données serveur `donutlarp:activity`), toast/chat/son
- Receptors payload Fabric enregistrés au démarrage du client ; données nettoyées à la déconnexion
- Noms de modules gardés identiques → ta config existante reste valide

## Build
```
./gradlew build
```
Nécessite JDK 25 (Loom 1.18).
