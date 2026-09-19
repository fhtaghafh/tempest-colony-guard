package com.tempestcolonyguard;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.util.ColonyUtils;
import com.minecolonies.api.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent.PositionCheck;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent.PositionCheck.Result;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent.SpawnPlacementCheck;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * MineColonies ne bloque le spawn naturel des mobs hostiles dans les colonies que
 * pour les entites qui implementent {@link Enemy} (voir EventHandler#on(PositionCheck)
 * dans minecolonies-core). Beaucoup de mobs hostiles ajoutes par d'autres mods
 * n'implementent pas cette interface (ils etendent Mob/PathfinderMob directement),
 * donc ils ignorent la protection anti-spawn des colonies. Ce gestionnaire reproduit
 * la meme logique de protection pour ces mobs-la.
 *
 * De plus, certains mods enregistrent leurs mobs hostiles avec Mob#checkMobSpawnRules
 * au lieu de Monster#checkMonsterSpawnRules : ils spawnent alors sans respecter la
 * regle vanilla d'obscurite (les torches ne les empechent pas d'apparaitre). On force
 * cette meme regle pour ces mobs afin qu'ils reagissent a la lumiere comme un mob vanilla.
 */
@EventBusSubscriber(modid = TempestColonyGuard.MODID)
public final class ColonySpawnGuard {

    /** Force la protection sur un type d'entite, meme s'il n'est pas categorise MONSTER. */
    public static final TagKey<EntityType<?>> PROTECT_INCLUDE = TagKey.create(
            Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(TempestColonyGuard.MODID, "protect_include"));

    /** Exempte un type d'entite de cette protection additionnelle (faux positif). */
    public static final TagKey<EntityType<?>> PROTECT_EXCLUDE = TagKey.create(
            Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(TempestColonyGuard.MODID, "protect_exclude"));

    private ColonySpawnGuard() {
    }

    @SubscribeEvent
    public static void onPositionCheck(final PositionCheck event) {
        if (!Config.buildingProtectionEnabled) {
            return;
        }

        final Mob mob = event.getEntity();

        // Deja gere nativement par MineColonies.
        if (mob instanceof Enemy) {
            return;
        }

        // Deja refuse par un autre gestionnaire (MineColonies ou autre addon).
        if (event.getResult() == Result.FAIL) {
            return;
        }

        // MineColonies n'applique pas non plus sa protection aux mobs de spawner,
        // on garde le meme comportement pour rester coherent.
        if (event.getSpawnType() == MobSpawnType.SPAWNER) {
            return;
        }

        if (!isTreatedAsHostile(mob.getType())) {
            return;
        }

        final ServerLevelAccessor levelAccessor = event.getLevel();
        if (levelAccessor.isClientSide() || !(levelAccessor instanceof Level level)) {
            return;
        }

        final BlockPos pos = BlockPos.containing(event.getX(), event.getY(), event.getZ());
        if (!WorldUtil.isEntityBlockLoaded(levelAccessor, pos)) {
            return;
        }

        final LevelChunk chunk = level.getChunkAt(pos);
        final int owningColony = ColonyUtils.getOwningColony(chunk);
        if (owningColony == 0) {
            return;
        }

        final IColony colony = IColonyManager.getInstance().getColonyByWorld(owningColony, level);
        if (colony == null) {
            return;
        }

        final Map<Integer, Set<BlockPos>> claimingBuildings = ColonyUtils.getAllClaimingBuildings(chunk);
        for (final BlockPos buildingPos : claimingBuildings.getOrDefault(owningColony, Collections.emptySet())) {
            final IBuilding building = colony.getServerBuildingManager().getBuilding(buildingPos);
            if (building != null
                    && building.getBuildingLevel() >= Config.minBuildingLevel
                    && building.getBuildingLevel() <= Config.maxBuildingLevel
                    && building.isInBuilding(pos)) {
                event.setResult(Result.FAIL);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onSpawnPlacementCheck(final SpawnPlacementCheck event) {
        if (!Config.darknessRequirementEnabled) {
            return;
        }

        final EntityType<?> type = event.getEntityType();

        // Deja gere nativement par MineColonies / le mob lui-meme (Monster#checkMonsterSpawnRules).
        if (Enemy.class.isAssignableFrom(type.getBaseClass())) {
            return;
        }

        // Deja refuse par un autre gestionnaire.
        if (event.getResult() == SpawnPlacementCheck.Result.FAIL) {
            return;
        }

        if (!isTreatedAsHostile(type)) {
            return;
        }

        final ServerLevelAccessor levelAccessor = event.getLevel();

        // On ne touche a rien en dehors du territoire revendique par une colonie : cette regle
        // ne doit pas changer le comportement de spawn du mob ailleurs dans le monde.
        if (!isWithinColonyTerritory(levelAccessor, event.getPos())) {
            return;
        }

        if (!MobSpawnType.ignoresLightRequirements(event.getSpawnType())
                && !Monster.isDarkEnoughToSpawn(levelAccessor, event.getPos(), event.getRandom())) {
            event.setResult(SpawnPlacementCheck.Result.FAIL);
        }
    }

    private static boolean isTreatedAsHostile(final EntityType<?> type) {
        if (type.is(PROTECT_EXCLUDE)) {
            return false;
        }
        return type.is(PROTECT_INCLUDE) || type.getCategory() == MobCategory.MONSTER;
    }

    /**
     * Vrai si la position se trouve dans un chunk revendique par une colonie MineColonies
     * (le territoire de la colonie, plus large que la seule empreinte de ses batiments).
     */
    private static boolean isWithinColonyTerritory(final ServerLevelAccessor levelAccessor, final BlockPos pos) {
        if (levelAccessor.isClientSide() || !(levelAccessor instanceof Level level)) {
            return false;
        }
        if (!WorldUtil.isEntityBlockLoaded(levelAccessor, pos)) {
            return false;
        }
        final LevelChunk chunk = level.getChunkAt(pos);
        return ColonyUtils.getOwningColony(chunk) != 0;
    }
}
