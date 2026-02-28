package com.github.mythos.mythos.config;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.HashMap;
import java.util.Map;

public class MythosContagionConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final Map<String, ForgeConfigSpec.BooleanValue> ENABLED_PATHS = new HashMap<>();
    public static final Map<String, ForgeConfigSpec.IntValue> MAX_LEVELS = new HashMap<>();

    static {
        BUILDER.push("Contagion Mutations");

        String[] paths = {"Malice", "Deception", "Greed", "Pride", "Gluttony", "Lust", "Wrath", "Sloth", "Envy"};

        for (String path : paths) {
            BUILDER.push(path);
            ENABLED_PATHS.put(path, BUILDER.comment("Enable " + path + " path?").define("enabled", true));
            MAX_LEVELS.put(path, BUILDER.comment("Max level for " + path).defineInRange("max_level", 10, 1, 100));
            BUILDER.pop();
        }

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}