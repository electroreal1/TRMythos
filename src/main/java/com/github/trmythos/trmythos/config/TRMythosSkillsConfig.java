package com.github.trmythos.trmythos.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class TRMythosSkillsConfig {
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> allowedUltimates = null;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> obtainableUltimates = null;
    public static ForgeConfigSpec.BooleanValue canAttackOtherPlayers;
    public static ForgeConfigSpec.BooleanValue enableUniqueSkillCompatibilityForUltimates;

    public TRMythosSkillsConfig(ForgeConfigSpec.Builder builder) {
        builder.push("availableUltimateSkills");
        allowedUltimates = builder.comment("The Ultimate Skills that are allowed to be used.").comment("Default: [\"trmysticism:adephaga\", \"trmysticism:ame_no_uzume_no_mikoto\", \"trmysticism:amaterasu\", \"trmysticism:antaeus\", \"trmysticism:antevorta\",\n                                \"trmysticism:antithesis\", \"trmysticism:apollo\", \"trmysticism:asmodeus\", \"trmysticism:beelzebub\", \"trmysticism:belphegor\",\n                                \"trmysticism:bushyasta\", \"trmysticism:dionysus\", \"trmysticism:galileo\", \"trmysticism:gilgamesh\", \"trmysticism:hades\",\n                                \"trmysticism:heracles\", \"trmysticism:hestia\", \"trmysticism:ignis\", \"trmysticism:invictus\", \"trmysticism:laverna\",\n                                \"trmysticism:mephisto\", \"trmysticism:oizys\", \"trmysticism:pernida\", \"trmysticism:sandalphon\",\n                                \"trmysticism:sariel\", \"trmysticism:satanael\", \"trmysticism:sephirot\", \"trmysticism:susanoo\",\n                                \"trmysticism:takemikazuchi\", \"trmysticism:tsukuyomi\", \"trmysticism:uriel\", \"trmysticism:viciel\",\n                                \"trmysticism:xezbeth\"]").defineList("availableUltimateSkills", Arrays.asList("trmysticism:adephaga", "trmysticism:ame_no_uzume_no_mikoto", "trmysticism:amaterasu", "trmysticism:antaeus", "trmysticism:antevorta", "trmysticism:antithesis", "trmysticism:apollo", "trmysticism:asmodeus", "trmysticism:beelzebub", "trmysticism:belphegor", "trmysticism:bushyasta", "trmysticism:dionysus", "trmysticism:galileo", "trmysticism:gilgamesh", "trmysticism:hades", "trmysticism:heracles", "trmysticism:hestia", "trmysticism:ignis", "trmysticism:invictus", "trmysticism:laverna", "trmysticism:mephisto", "trmysticism:oizys", "trmysticism:pernida", "trmysticism:sandalphon", "trmysticism:sariel", "trmysticism:satanael", "trmysticism:sephirot", "trmysticism:susanoo", "trmysticism:takemikazuchi", "trmysticism:tsukuyomi", "trmysticism:uriel", "trmysticism:viciel", "trmysticism:xezbeth"), (check) -> {
            return true;
        });
        obtainableUltimates = builder.comment("The Ultimate Skills that can be obtained.").comment("Default: [\"trmysticism:adephaga\", \"trmysticism:ame_no_uzume_no_mikoto\", \"trmysticism:amaterasu\", \"trmysticism:antaeus\", \"trmysticism:antevorta\",\n                                \"trmysticism:antithesis\", \"trmysticism:apollo\", \"trmysticism:asmodeus\", \"trmysticism:beelzebub\", \"trmysticism:belphegor\",\n                                \"trmysticism:bushyasta\", \"trmysticism:dionysus\", \"trmysticism:galileo\", \"trmysticism:gilgamesh\", \"trmysticism:hades\",\n                                \"trmysticism:heracles\", \"trmysticism:hestia\", \"trmysticism:ignis\", \"trmysticism:invictus\", \"trmysticism:laverna\",\n                                \"trmysticism:mephisto\", \"trmysticism:oizys\", \"trmysticism:pernida\", \"trmysticism:sandalphon\",\n                                \"trmysticism:sariel\", \"trmysticism:satanael\", \"trmysticism:sephirot\", \"trmysticism:susanoo\",\n                                \"trmysticism:takemikazuchi\", \"trmysticism:tsukuyomi\", \"trmysticism:uriel\", \"trmysticism:viciel\",\n                                \"trmysticism:xezbeth\"]").defineList("obtainableUltimates", Arrays.asList("trmysticism:adephaga", "trmysticism:ame_no_uzume_no_mikoto", "trmysticism:amaterasu", "trmysticism:antaeus", "trmysticism:antevorta", "trmysticism:antithesis", "trmysticism:apollo", "trmysticism:asmodeus", "trmysticism:beelzebub", "trmysticism:belphegor", "trmysticism:bushyasta", "trmysticism:dionysus", "trmysticism:galileo", "trmysticism:gilgamesh", "trmysticism:hades", "trmysticism:heracles", "trmysticism:hestia", "trmysticism:ignis", "trmysticism:invictus", "trmysticism:laverna", "trmysticism:mephisto", "trmysticism:oizys", "trmysticism:pernida", "trmysticism:sandalphon", "trmysticism:sariel", "trmysticism:satanael", "trmysticism:sephirot", "trmysticism:susanoo", "trmysticism:takemikazuchi", "trmysticism:tsukuyomi", "trmysticism:uriel", "trmysticism:viciel", "trmysticism:xezbeth"), (check) -> {
            return true;
        });
        builder.pop();
        builder.push("SkillsConfig");
        canAttackOtherPlayers = builder.comment("If set to true, players with Ame-no-Uzume-no-Mikoto will force other players to attack the spotlit target. For more info, please check the wiki on what the effect does.").comment("Default: true").comment("Allowed values: \"true/false\"").define("canAttackOtherPlayers", true);
        enableUniqueSkillCompatibilityForUltimates = builder.comment("If set to true, players with Guardian or Gourmet has a small chance to receive the other skill needed to create Beelzebub from Royal Orcs.").comment("If set to true, players with Captivator or Bewilder has a small chance to receive the other skill needed to create Ame-no-Uzume-no-Mikoto from Ai Hoshino and Kirara Mizutani respectively.").comment("Default: true").comment("Allowed values: \"true/false\"").define("enableUniqueSkillCompatibilityForUltimates", true);
    }

    public static List<? extends String> getAllowedUltimates() {
        return (List)allowedUltimates.get();
    }

    public static List<? extends String> getObtainableUltimates() {
        return (List)obtainableUltimates.get();
    }

    public static boolean getAmeNoUzumePlayerAttack() {
        return (Boolean)canAttackOtherPlayers.get();
    }

    public static boolean getSkillUltimateCompatibility() {
        return (Boolean)enableUniqueSkillCompatibilityForUltimates.get();
    }
}

