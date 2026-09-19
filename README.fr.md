[English](README.md) | **Français**

# Tempest Colony Guard

Addon de compatibilité NeoForge pour [MineColonies](https://www.curseforge.com/minecraft/mc-mods/minecolonies), pour Minecraft 1.21.1 / NeoForge 21.1.x.

## Le problème

MineColonies ne bloque le spawn naturel de mobs hostiles à l'intérieur des bâtiments de colonie que pour les entités qui implémentent l'interface vanilla `net.minecraft.world.entity.monster.Enemy`. Beaucoup de mobs hostiles ajoutés par d'autres mods (ex : le Direwolf de [Tensura](https://www.curseforge.com/minecraft/mc-mods/tensura)) étendent `Mob`/`PathfinderMob` directement sans implémenter cette interface, et échappent donc totalement à la protection anti-spawn des colonies.

## Ce que fait ce mod

- **Protection des bâtiments** : reproduit exactement la logique native de MineColonies (bloque le spawn naturel à l'intérieur de l'empreinte des bâtiments, niveau configurable) pour tout mob hostile non-vanilla détecté.
- **Obligation d'obscurité** : force ces mêmes mobs à respecter la règle vanilla de luminosité pour spawner (comme un zombie), mais uniquement dans le territoire revendiqué par une colonie — les torches redeviennent efficaces.
- **Détection** : `MobCategory.MONSTER` par défaut, plus des tags de secours (`#tempestcolonyguard:protect_include` / `#tempestcolonyguard:protect_exclude`) pour ajuster manuellement les cas particuliers.
- **N'affecte pas les raids** (MineColonies ou addons comme le raid Direwolf de Tensura/TrNightmare) : ces mécaniques placent leurs mobs directement dans le monde sans passer par les événements de spawn naturel que ce mod écoute.
- **Entièrement configurable** : chaque mécanisme peut être désactivé indépendamment via le fichier de config serveur généré au premier lancement (`serverconfig/tempestcolonyguard-server.toml`).

## Compiler

Ce projet utilise [ModDevGradle](https://github.com/neoforged/ModDevGradle). MineColonies et Structurize sont nécessaires **uniquement à la compilation** (dépendances `compileOnly`) et ne sont pas inclus dans ce dépôt (fichiers sous copyright de leurs auteurs).

1. Télécharge les jars suivants (versions utilisées lors du développement, adapter si besoin) et place-les dans `libs/` :
   - `minecolonies-1.1.1368-1.21.1.jar` — [CurseForge](https://www.curseforge.com/minecraft/mc-mods/minecolonies) / [Modrinth](https://modrinth.com/mod/minecolonies)
   - `structurize-1.0.832-1.21.1.jar` — [CurseForge](https://www.curseforge.com/minecraft/mc-mods/structurize) / [Modrinth](https://modrinth.com/mod/structurize)
2. Le projet nécessite un JDK 21 pour lancer Gradle (le JDK système peut être plus récent et provoquer une erreur `Unsupported class file major version`). Exemple avec le runtime Java 21 fourni par PrismLauncher :
   ```
   JAVA_HOME=/chemin/vers/java-runtime-delta ./gradlew jar
   ```
3. Le jar compilé se trouve dans `build/libs/`.

## Licence

MIT — voir [LICENSE](LICENSE).
