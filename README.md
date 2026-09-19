**English** | [Français](README.fr.md)

# Tempest Colony Guard

NeoForge compatibility addon for [MineColonies](https://www.curseforge.com/minecraft/mc-mods/minecolonies), for Minecraft 1.21.1 / NeoForge 21.1.x.

## The problem

MineColonies only blocks natural spawns of hostile mobs inside colony buildings for entities that implement the vanilla `net.minecraft.world.entity.monster.Enemy` interface. Many hostile mobs added by other mods (e.g. [Tensura](https://www.curseforge.com/minecraft/mc-mods/tensura)'s Direwolf) extend `Mob`/`PathfinderMob` directly without implementing that interface, so they completely bypass colony spawn protection.

## What this mod does

- **Building protection**: reproduces MineColonies' native logic exactly (blocks natural spawns inside a building's footprint, configurable level) for any detected non-vanilla hostile mob.
- **Darkness requirement**: forces these same mobs to respect the vanilla light-level spawn rule (like a zombie), but only within a colony's claimed territory — torches become effective against them again.
- **Detection**: `MobCategory.MONSTER` by default, plus fallback tags (`#tempestcolonyguard:protect_include` / `#tempestcolonyguard:protect_exclude`) to manually adjust edge cases.
- **Does not affect raids** (MineColonies' own, or addons like Tensura/TrNightmare's Direwolf raid): those mechanics place mobs directly into the world without going through the natural spawn events this mod listens to.
- **Fully configurable**: each mechanism can be toggled independently via the server config file generated on first launch (`serverconfig/tempestcolonyguard-server.toml`).

## Building

This project uses [ModDevGradle](https://github.com/neoforged/ModDevGradle). MineColonies and Structurize are needed **only at compile time** (`compileOnly` dependencies) and are not included in this repository (third-party files under their authors' copyright).

1. Download the following jars (versions used during development, adjust as needed) and place them in `libs/`:
   - `minecolonies-1.1.1368-1.21.1.jar` — [CurseForge](https://www.curseforge.com/minecraft/mc-mods/minecolonies) / [Modrinth](https://modrinth.com/mod/minecolonies)
   - `structurize-1.0.832-1.21.1.jar` — [CurseForge](https://www.curseforge.com/minecraft/mc-mods/structurize) / [Modrinth](https://modrinth.com/mod/structurize)
2. The project requires a JDK 21 to run Gradle (a newer system JDK can trigger an `Unsupported class file major version` error). Example using PrismLauncher's bundled Java 21 runtime:
   ```
   JAVA_HOME=/path/to/java-runtime-delta ./gradlew jar
   ```
3. The built jar is output to `build/libs/`.

## License

MIT — see [LICENSE](LICENSE).
