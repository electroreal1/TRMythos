package com.github.mythos.mythos.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
public class MythosConfig {
    public static final MythosConfig INSTANCE;
    public static final ForgeConfigSpec SPEC;
    public final MythosSkillsConfig skillsConfig;
    public final MythosRacesConfig racesConfig;

    private MythosConfig(ForgeConfigSpec.Builder builder) {
        builder.push("Skills");
        this.skillsConfig = new MythosSkillsConfig(builder);
        builder.pop();
        builder.push("Races");
        this.racesConfig = new MythosRacesConfig(builder);
        builder.pop();
    }
    static {
        Pair<MythosConfig, ForgeConfigSpec> pair = (new ForgeConfigSpec.Builder()).configure(MythosConfig::new);
        INSTANCE = pair.getKey();
        SPEC = pair.getValue();
    }
}
