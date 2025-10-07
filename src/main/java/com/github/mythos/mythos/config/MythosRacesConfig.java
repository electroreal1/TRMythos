package com.github.mythos.mythos.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class MythosRacesConfig {
    public final ForgeConfigSpec.IntValue bossForValkyrie;
    public MythosRacesConfig(ForgeConfigSpec.Builder builder) {
        builder.push("evolutionEPRequirements");
        builder.pop();
        builder.pop();
        builder.push("evolutionRequirements");
        this.bossForValkyrie = builder.comment("The number of Boss kills needed to evolve into Valkyrie").defineInRange("bossForValkyrie", 4, 0, 10000);
    }




}
