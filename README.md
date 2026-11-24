# Aura

Mod **client** pour **Minecraft 1.21.11** (Fabric) : un menu animé, un HUD déplaçable et trois thèmes de couleurs. Aucun bloc, item ou contenu de jeu n'est ajouté, et rien n'est requis côté serveur.

## Fonctionnalités

- **Menu** ouvert avec la touche **O** : fond animé, carte à bordure dégradée, interrupteurs animés, sélecteur de thème.
- **HUD déplaçable** : coordonnées, FPS, jour et heure du monde. On le place où on veut (glisser-déposer) et on règle son échelle de 50 % à 200 %. Position et échelle sont conservées.
- **Réticule personnalisé** : une croix aux couleurs du thème, taille et opacité réglables, avec aperçu. Le réticule du jeu est masqué tant que celui d'Aura est actif.
- **Trois thèmes** : Aurore, Océan, Émeraude, appliqués au menu, au HUD et au réticule.
- **Plein jour** : luminosité maximale à la demande, sans perdre le réglage de luminosité du joueur.
- **Réglages persistants** dans `config/aura.json` : tout est rechargé au démarrage suivant.
- **Uniquement côté client** : aucun paquet réseau, aucun mixin, aucune modification du serveur.

## Prérequis

| Élément | Version |
| --- | --- |
| Minecraft | 1.21.11 |
| Fabric Loader | 0.18.1 ou plus récent |
| Fabric API | 0.141.0 ou plus récent (testé avec 0.141.6+1.21.11) |
| Java | 21 ou plus récent |

## Installation

1. Installe **Fabric Loader** pour Minecraft 1.21.11 depuis [fabricmc.net/use/installer](https://fabricmc.net/use/installer/), puis lance une fois le profil « fabric-loader-1.21.11 » dans ton launcher pour créer les dossiers.
2. Télécharge **Fabric API** pour 1.21.11 sur [Modrinth](https://modrinth.com/mod/fabric-api/versions?g=1.21.11) ou [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api).
3. Télécharge le mod : [`artifacts/aura-1.0.0.jar`](artifacts/aura-1.0.0.jar) (bouton *Download raw file* sur GitHub).
4. Place les **deux** fichiers `.jar` dans le dossier `mods` de ton installation :
   - Windows : `%APPDATA%\.minecraft\mods`
   - macOS : `~/Library/Application Support/minecraft/mods`
   - Linux : `~/.minecraft/mods`
5. Lance le jeu avec le profil Fabric. En jeu, appuie sur **O** : si le menu s'ouvre, tout est en place.

> Le mod ne fonctionne pas avec Forge ou NeoForge, et ne doit pas être installé sur le serveur.

## Utilisation

| Action | Effet |
| --- | --- |
| **O** | Ouvrir le menu Aura |
| **Échap** ou **O** | Fermer le menu |
| Clic sur une ligne | Activer / désactiver l'option |
| Clic sur une pastille de couleur | Changer de thème |
| Clic sur « Position du HUD » | Ouvrir l'écran de placement du HUD |
| Clic sur « Réticule » | Régler l'activation, la taille et l'opacité du réticule |
| Glisser le HUD | Le déplacer (il reste dans l'écran) |
| Molette, ou **-** / **=** | Changer l'échelle du HUD par pas de 25 % |
| **Échap** (écran de placement) | Terminer et enregistrer |

Le menu ne met pas le jeu en pause : le monde continue de tourner derrière.

La touche **O** peut être remplacée dans *Options → Commandes → Aura*.

## Configuration

Fichier : `config/aura.json` (créé à la première modification). Il peut être édité à la main, jeu fermé ; les valeurs absentes ou invalides reprennent leur valeur par défaut.

| Clé | Type | Défaut | Rôle |
| --- | --- | --- | --- |
| `hud` | booléen | `true` | Afficher le HUD |
| `coords` | booléen | `true` | Ligne des coordonnées |
| `fps` | booléen | `true` | Ligne des FPS |
| `time` | booléen | `true` | Ligne jour + heure |
| `fullbright` | booléen | `false` | Plein jour |
| `theme` | entier | `0` | Thème : 0 Aurore, 1 Océan, 2 Émeraude |
| `hudX` / `hudY` | entiers | `8` / `8` | Coin supérieur gauche du HUD, en pixels GUI |
| `hudScale` | nombre | `1.0` | Échelle du HUD, bornée entre `0.5` et `2.0` |
| `crosshair` | booléen | `true` | Réticule personnalisé (remplace celui du jeu) |
| `crosshairSize` | entier | `9` | Longueur des branches du réticule, en pixels (`3` à `20`) |
| `crosshairOpacity` | entier | `85` | Opacité du réticule, en pourcentage (`20` à `100`) |
| `savedGamma` | nombre ou absent | `null` | Luminosité d'origine du joueur, mémorisée pendant le plein jour |

## Compiler depuis les sources

Prérequis : JDK 21 ou plus récent (le projet compile en `--release 21`).

```bash
git clone https://github.com/nth-oficiell/test.git
cd test
./gradlew build
```

Le jar est produit dans `build/libs/aura-1.0.0.jar` (une copie est versionnée dans `artifacts/`).

## Limites connues

- Le réticule d'Aura remplace l'élément de HUD « crosshair » du jeu ; l'indicateur d'attaque et le reste du HUD ne sont pas touchés.

- Le plein jour passe par l'option de luminosité du jeu : elle est restaurée automatiquement quand tu le désactives, y compris après un redémarrage. Si tu **supprimes le mod** alors que le plein jour est actif, `options.txt` garde la luminosité à 100 % — remets-la à la main dans les options.
- La position du HUD est enregistrée en pixels GUI : changer le réglage « Échelle de l'interface » décale légèrement le HUD, il suffit de le replacer.
- Le rendu n'a pas été vérifié en jeu par l'auteur du code dans cet environnement (pas de GPU) : seuls la compilation et le contenu du jar sont garantis.

## Licence

MIT.
