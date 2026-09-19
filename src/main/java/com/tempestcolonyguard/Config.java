package com.tempestcolonyguard;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = TempestColonyGuard.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue BUILDING_PROTECTION = BUILDER
            .comment(
                    "[EN] Blocks natural spawns of non-vanilla hostile mobs inside colony building footprints.",
                    "Reproduces, for these mobs, the protection MineColonies already applies natively to vanilla (Enemy) mobs.",
                    "",
                    "[FR] Bloque le spawn naturel des mobs hostiles non-vanilla a l'interieur de l'empreinte des batiments de colonie.",
                    "Reproduit, pour ces mobs, la protection que MineColonies applique deja nativement aux mobs vanilla (Enemy)."
            )
            .define("enableBuildingProtection", true);

    private static final ModConfigSpec.BooleanValue DARKNESS_REQUIREMENT = BUILDER
            .comment(
                    "[EN] Forces non-vanilla hostile mobs to respect the vanilla darkness requirement to spawn,",
                    "but only within a colony's claimed territory (torches become effective against them).",
                    "",
                    "[FR] Force les mobs hostiles non-vanilla a respecter la regle vanilla d'obscurite pour spawner,",
                    "mais uniquement dans le territoire revendique par une colonie (les torches deviennent efficaces contre eux)."
            )
            .define("enableDarknessRequirement", true);

    private static final ModConfigSpec.IntValue MIN_BUILDING_LEVEL = BUILDER
            .comment(
                    "[EN] Minimum level a colony building must have to benefit from the anti-spawn protection.",
                    "[FR] Niveau minimum qu'un batiment de colonie doit avoir pour beneficier de la protection anti-spawn."
            )
            .defineInRange("minBuildingLevel", 1, 0, 5);

    private static final ModConfigSpec.IntValue MAX_BUILDING_LEVEL = BUILDER
            .comment(
                    "[EN] Maximum level a colony building can have to benefit from the anti-spawn protection.",
                    "Useful to limit the protection to colonies still in early development (e.g. set to 2 to only",
                    "protect level 1-2 buildings, and let more advanced colonies fend for themselves).",
                    "",
                    "[FR] Niveau maximum qu'un batiment de colonie peut avoir pour beneficier de la protection anti-spawn.",
                    "Utile pour limiter la protection aux colonies en debut de developpement (ex: mettre 2 pour ne",
                    "proteger que les batiments niveau 1-2, et laisser les colonies plus avancees se debrouiller seules)."
            )
            .defineInRange("maxBuildingLevel", 5, 0, 5);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean buildingProtectionEnabled = true;
    public static boolean darknessRequirementEnabled = true;
    public static int minBuildingLevel = 1;
    public static int maxBuildingLevel = 5;

    private Config() {
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        buildingProtectionEnabled = BUILDING_PROTECTION.get();
        darknessRequirementEnabled = DARKNESS_REQUIREMENT.get();
        minBuildingLevel = MIN_BUILDING_LEVEL.get();
        maxBuildingLevel = MAX_BUILDING_LEVEL.get();
    }
}
