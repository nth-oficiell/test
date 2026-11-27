# Matrix

Client utilitaire Minecraft Fabric (1.21.11) — rebrand de CorzClient → **Matrix**.

## Contenu
- `matrix-client-2.0.1.jar` — le mod compilé, prêt à mettre dans `.minecraft/mods`
- ⚠️ Supprime l'ancien `vortex-client-2.0.1.jar` de `.minecraft/mods` (l'addon donutlarp original n'est pas requis, tout est intégré)
- `Matrix/` — sources complètes (Gradle / Fabric Loom)

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

## Fix réseau sus chunks
- Les payloads `donutlarp:bases` / `donutlarp:activity` sont maintenant décodés octet par octet (buffer entier) comme l'addon original — corrige le `DecoderException: Packet custom_payload was larger than I expected`
- Enregistrement des payloads résilient si l'addon donutlarp original est aussi installé
- **Renommage complet Vortex → Matrix** (id `matrixclient`, packages `com.matrix.client`, assets, GUI)

## Build
```
cd Matrix
./gradlew build
```
Le jar remappé est écrit dans `Matrix/build/libs/matrix-client-2.0.1.jar`.
Nécessite JDK 25 pour lancer Gradle (Loom 1.18) ; le bytecode est compilé avec `--release 21`, donc le mod tourne sur un client Minecraft en Java 21+.
Le jar produit se trouve dans `Matrix/build/libs/`.

## Module `Optimizer` (Misc)
Module de perf (gains de FPS), pas de triche — tout est branché sur des chemins chauds réels :
- **Entity Cull** (activé) / **Entity Distance** (64 blocs, 16–160) : les mobs, items au sol, armor stands et bateaux au-delà de la distance ne sont plus rendus (`MixinEntityRenderManagerOptimizer` sur `shouldRender`). Les joueurs restent toujours visibles.
- **Cull Players** (désactivé) : applique la même distance aux joueurs, pour ceux qui veulent le maximum de FPS en zone bondée.
- **Block Entity Cull** (activé) / **Block Entity Distance** (48 blocs, 8–96) : coffres, panneaux, bannières, têtes et lits lointains ne sont plus rendus (`MixinBlockEntityRenderManagerOptimizer`).
- **Particle Cap** (désactivé) / **Particles / Tick** : jette les particules au-delà du quota par tick (`MixinParticleManager`) pour lisser les explosions de particules.
- **Chunk Scan Budget** / **Scan Budget** (25–200 %) : multiplie le budget de temps par tick partagé par les scanners de chunks (`ChunkScanScheduler`). Baisser = moins de travail par tick, donc moins de pics.
- **Profiler** : mesure `onGameTick`, `dispatchRender3D` et `dispatchRender2D` dans `ModuleRegistry` (EMA + pic) et compte les entités cull, résumé envoyé en chat à la désactivation du module.

Les scanners de chunks ne gardent plus qu'un nombre borné de chunks suivis (8192) au lieu de croître indéfiniment.

Le jar prêt à jouer est aussi versionné ici : `Matrix/matrix-client-2.0.1.jar` (c'est l'URL utilisée par `update.json`).

## Mise à jour : module SkinChanger (Visuals)
- Nouveau module `SkinChanger` dans Visuals : importe un PNG de skin depuis ton PC (bouton Import) ou dépose-le dans `.minecraft/config/corzclient/skins/` (bouton Reload)
- Application complète côté client : ton joueur (1ère/3ème personne), ta main, et les autres joueurs si « Apply To Others »
- Détection auto du modèle (Wide/Slim) ou forçage possible
- Le fichier importé est conservé dans la config (`Skin File`) → rechargé automatiquement à l'activation
