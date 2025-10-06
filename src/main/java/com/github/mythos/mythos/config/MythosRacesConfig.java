package com.github.mythos.mythos.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class MythosRacesConfig {
    public MythosRacesConfig(ForgeConfigSpec.Builder builder) {
        builder.push("evolutionEPRequirements");
        builder.pop();
        builder.push("");
        builder.pop();
    }
}
