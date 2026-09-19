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
                    "Bloque le spawn naturel des mobs hostiles non-vanilla a l'interieur de l'empreinte des batiments de colonie.",
                    "Reproduit, pour ces mobs, la protection que MineColonies applique deja nativement aux mobs vanilla (Enemy)."
            )
            .define("enableBuildingProtection", true);

    private static final ModConfigSpec.BooleanValue DARKNESS_REQUIREMENT = BUILDER
            .comment(
                    "Force les mobs hostiles non-vanilla a respecter la regle vanilla d'obscurite pour spawner,",
                    "mais uniquement dans le territoire revendique par une colonie (les torches deviennent efficaces contre eux)."
            )
            .define("enableDarknessRequirement", true);

    private static final ModConfigSpec.IntValue MIN_BUILDING_LEVEL = BUILDER
            .comment("Niveau minimum qu'un batiment de colonie doit avoir pour beneficier de la protection anti-spawn.")
            .defineInRange("minBuildingLevel", 1, 0, 5);

    private static final ModConfigSpec.IntValue MAX_BUILDING_LEVEL = BUILDER
            .comment(
                    "Niveau maximum qu'un batiment de colonie peut avoir pour beneficier de la protection anti-spawn.",
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
