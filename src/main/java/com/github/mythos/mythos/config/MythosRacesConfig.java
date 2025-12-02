package com.github.mythos.mythos.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class MythosRacesConfig {

    public final ForgeConfigSpec.IntValue bossForValkyrie;
    public final ForgeConfigSpec.IntValue essenceForApostle;

    public MythosRacesConfig(ForgeConfigSpec.Builder builder) {
        builder.push("evolutionEPRequirements");
        builder.pop();
        builder.pop();
        builder.push("evolutionRequirements");
        this.bossForValkyrie = builder.comment("The number of Boss kills needed to evolve into Valkyrie").defineInRange("bossForValkyrie", 4, 0, 10000);
        this.essenceForApostle = builder.comment("The number of Cryptid Essence needed to evolve to Void Apostle").defineInRange("essenceForApostle", 5, 0, 10000);
    }
}
