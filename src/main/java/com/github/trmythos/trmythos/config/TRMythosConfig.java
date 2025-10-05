//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.github.trmythos.trmythos.config;

import com.github.trmythos.trmythos.registry.race.TRMythosRaces;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class TRMythosConfig {
    public static final TRMythosConfig INSTANCE;
    public static final ForgeConfigSpec SPEC;
    public final TRMythosSkillsConfig skillsConfig;
    public final TRMythosRacesConfig racesConfig;

    private TRMythosConfig(ForgeConfigSpec.Builder builder) {
        builder.push("Skills");
        this.skillsConfig = new TRMythosSkillsConfig(builder);
        builder.pop();
        builder.push("Races");
        this.racesConfig = new TRMythosRacesConfig(builder);
        builder.pop();
    }
    static {
        Pair<TRMythosConfig, ForgeConfigSpec> pair = (new ForgeConfigSpec.Builder()).configure(TRMythosConfig::new);
        INSTANCE = (TRMythosConfig) pair.getKey();
        SPEC = (ForgeConfigSpec)pair.getValue();
    }
}
