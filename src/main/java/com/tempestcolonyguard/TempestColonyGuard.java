package com.tempestcolonyguard;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(TempestColonyGuard.MODID)
public class TempestColonyGuard {

    public static final String MODID = "tempestcolonyguard";

    public TempestColonyGuard(final ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }
}
