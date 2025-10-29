package com.github.mythos.mythos.config;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MythosSkillsConfig {
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> fakerSkillRestrictedItems;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> fakerSkillReinforceEnchantments;
    public static ForgeConfigSpec.DoubleValue vassalAssemblyChance;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> purityImmuneEffects;
    public static ForgeConfigSpec.DoubleValue purityDamageCap;
    public static ForgeConfigSpec.BooleanValue enableFighterEvolution;
    public static ForgeConfigSpec.BooleanValue loseSkillOnFighterEvolution;
    public static ForgeConfigSpec.BooleanValue enableChefEvolution;
    public static ForgeConfigSpec.BooleanValue loseSkillOnChefEvolution;
    public static ForgeConfigSpec.BooleanValue VampireCarnage;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> racesThatCanCompeteForChildOfThePlane = null;



    public MythosSkillsConfig(ForgeConfigSpec.Builder builder) {


        builder.push("SkillsConfig");

        // FakerSkill restricted items
        fakerSkillRestrictedItems = builder
                .comment("Items that Faker cannot copy via projection.")
                .comment("Use item registry IDs, e.g., 'tensura:bronze_coin'.")
                .defineList(
                        "fakerSkillRestrictedItems",
                        Arrays.asList(
                                "tensura:bronze_coin", "tensura:silver_coin", "tensura:gold_coin",  "tensura:stellar_gold_coin", "tensura:hihiirokane_nugget", "tensura:charybdis_core", "tensura:zane_blood",
                                "tensura:battlewill_manual", "tensura:race_reset_reroll", "tensura:skill_reset_reroll", "tensura:character_reset_reroll",
                                "tensura:supermassive_slime_spawn_egg", "tensura:shizu_spawn_egg", "tensura:ifrit_spawn_egg", "tensura:orc_lord_spawn_egg", "tensura:orc_disaster_spawn_egg",
                                "tensura:hinata_sakaguchi_spawn_egg", "tensura_neb:carrion_spawn_egg", "tensura_neb:rimuru_ogre_fight_spawn_egg", "tensura_neb:luminous_valentine_spawn_egg",
                                "tensura:block_of_hihiirokane", "tensura:hihiirokane_ingot", "tensura:demon_essence", "tensura:dragon_essence", "tensura:elemental_essence",
                                "trnightmare:ark", "trnightmare:caliburn", "trnightmare:excalibur", "trnightmare:nodens_scythe", "trnightmare:nodens_the_world",
                                "trnightmare:the_asura", "trnightmare:the_world", "trnightmare:life_essence", "trnightmare:ender_essence", "trnightmare:elder_essence",
                                "trnightmare:holy_essence", "trnightmare:soul_essence", "trmysticism:blaze_essence", "trmysticism:cryptid_essence", "trmysticism:ice_essence",
                                "trmysticism:axe", "trmysticism:ea", "trmysticism:ritual_scythe", "stellarprism:random_amulet", "stellarprism:random_heartstone", "stellarprism:world_memory",
                                "stellarprism:reality_memory", "stellarprism:sword_of_starpower", "stellarprism:power_memory", "stellarprism:hope_memory", "stellarprism:time_memory",
                                "stellarprism:space_memory", "stellarprism:black_slasher_katana", "btrultima:random_sin", "btrultima:random_virtue",
                                "btrultima:random_unique", "btrultima:random_config", "btrultima:charity_shard", "btrultima:chastity_shard", "btrultima:diligence_shard",
                                "btrultima:envy_shard", "btrultima:glutton_shard", "btrultima:greed_shard", "btrultima:hope_shard", "btrultima:justice_shard",
                                "btrultima:lust_shard", "btrultima:patience_shard", "btrultima:pride_shard", "btrultima:slot_shard", "btrultima:wisdom_shard",
                                "btrultima:wrath_shard", "btrultima:beast_essence", "anitensura:tainted_essence", "easy_villagers:villager", "virtuoso:atropos_sword",
                                "virtuoso:ending_sealed_sword", "virtuoso:ending_unsealed_sword","mahoutsukai:attuned_diamond", "mahoutsukai:attuned_emerald",
                                "sophisticatedbackpacks:backpack", "sophisticatedbackpacks:copper_backpack", "sophisticatedbackpacks:diamond_backpack",
                                "sophisticatedbackpacks:gold_backpack", "sophisticatedbackpacks:iron_backpack", "sophisticatedbackpacks:netherite_backpack",
                                "minecraft:shulker_box", "minecraft:black_shulker_box", "minecraft:blue_shulker_box", "minecraft:brown_shulker_box", "minecraft:cyan_shulker_box",
                                "minecraft_gray_shulker_box", "minecraft:green_shulker_box", "minecraft:light_blue_shulker_box", "minecraft:light_gray_shulker_box",
                                "minecraft:lime_shulker_box", "minecraft:magenta_shulker_box", "minecraft:orange_shulker_box", "minecraft:pink_shulker_box", "minecraft:purple_shulker_box",
                                "minecraft:red_shulker_box", "minecraft:white_shulker_box", "minecraft:yellow_shulker_box", "sophisticatedstorage:shulker_box",
                                "sophisticatedstorage:copper_shulker_box", "sophisticatedstorage:iron_shulker_box", "sophisticatedstorage:gold_shulker_box", "sophisticatedstorage:diamond_shulker_box",
                                "sophisticatedstorage:netherite_shulker_box"
                        ),
                        obj -> obj instanceof String
                );
        fakerSkillReinforceEnchantments = builder
                .comment("List of enchantments Faker will reinforce with level.")
                .comment("Format: 'modid:enchantment_name:level', e.g., 'minecraft:sharpness:5'")
                .defineList(
                        "fakerSkillReinforceEnchantments",
                        Arrays.asList(
                                "minecraft:sharpness:5", "minecraft:smite:5", "minecraft:unbreaking:5", "minecraft:efficiency:5", "minecraft:looting:5",
                                "minecraft:mending:5", "minecraft:power:5", "minecraft:infinity:5", "minecraft:fortune:5", "minecraft:luck_of_the_sea:5",
                                "minecraft:respiration:5", "minecraft:protection:5", "minecraft:fire_protection:5", "minecraft:feather_falling:5",
                                "minecraft:blast_protection:5", "minecraft:projectile_protection:5", "minecraft:thorns:5", "minecraft:depth_strider:5",
                                "minecraft:frost_walker:5", "minecraft:soul_speed:5", "minecraft:sweeping_edge:5", "minecraft:silk_touch:1",
                                "minecraft:lure:5", "minecraft:aqua_affinity:5", "minecraft:knockback:5", "minecraft:fire_aspect:5",
                                "minecraft:quick_charge:5", "minecraft:multishot:5", "minecraft:piercing:5",

                                // tensura engravings
                                "tensura:holy_coat:3", "tensura:holy_weapon:2", "tensura:magic_weapon:2", "tensura:magic_interference:3",
                                "tensura:soul_eater:3", "tensura:severance:5", "tensura:barrier_piercing:2", "tensura:breathing_support:5",
                                "tensura:crushing:2", "tensura:sturdy:3", "tensura:energy_steal:2", "tensura:elemental_boost:4",
                                "tensura:elemental_resistance:4", "tensura:slotting:5", "tensura:swift:3"

                                ),
                        obj -> obj instanceof String
                );
        purityImmuneEffects = builder
                .comment("Effects that cannot be cleared by the Purity skill")
                .defineList("purityImmuneEffects",
                        List.of(
                                "tensura:anti_skill",
                                "tensura:infinite_imprisonment",
                                "tensura:soul_drain"
                        ),
                        o -> o instanceof String
                );
        purityDamageCap = builder
                .comment("maximum amount of damage that can be dealt by purity's justice ability (0.0–10000).")
                .defineInRange("purityDamageCap", 1000, 0.0, 10000.0);

        vassalAssemblyChance = builder
                .comment("Chance (0.0–1.0) for [True Passive] Vassal Assembly to trigger when damaged.")
                .defineInRange("vassalAssemblyChance", 0.2, 0.0, 1.0);

        enableFighterEvolution = builder
                .comment("Enable or disable Fighter -> Martial Master evolution.")
                .define("enableFighterEvolution", true);

        loseSkillOnFighterEvolution = builder
                .comment("If true, the Fighter skill is lost when evolving to Martial Master.")
                .define("loseSkillOnFighterEvolution", true);

        enableChefEvolution = builder
                .comment("Enable or disable Chef -> Cook evolution.")
                .define("enableChefEvolution", true);

        loseSkillOnChefEvolution = builder
                .comment("If true, the Chef skill is lost when evolving to Cook.")
                .define("loseSkillOnChefEvolution", true);

        VampireCarnage = builder
                .comment("if true, then on learning carnage you will be set to vampire race.")
                .define("VampireCarnage", true);

        racesThatCanCompeteForChildOfThePlane = builder
                .comment("List of races that can compete for Child of the Plane.")
                .defineList("eligibleRaces",
                        List.of("herald_of_ragnarok", "hound_of_hades", "jormungandr", "envoy_of_valhalla"),
                        obj -> obj instanceof String
                );

        builder.pop(); // pop SkillsConfig
    }


    // Getter
    public static List<? extends String> getFakerSkillRestrictedItems() {
        return fakerSkillRestrictedItems.get();
    }
    public static List<? extends String> getFakerSkillReinforceEnchantments() {
        return fakerSkillReinforceEnchantments.get();
    }
    public static List<MobEffect> getPurityImmuneEffects() {
        return purityImmuneEffects.get().stream()
                .map(id -> Registry.MOB_EFFECT.getOptional(new ResourceLocation((String) id)).orElse(null))
                .filter(e -> e != null)
                .collect(Collectors.toList());
    }

    public static boolean isFighterEvolutionEnabled() {
        return enableFighterEvolution.get();
    }

    public static boolean loseFighterOnEvolution() {
        return loseSkillOnFighterEvolution.get();
    }

    public static boolean isChefEvolutionEnabled() {
        return enableChefEvolution.get();
    }

    public static boolean loseChefOnEvolution() {
        return loseSkillOnChefEvolution.get();
    }
    public static boolean VampireCarnage() {
        return VampireCarnage.get();
    }
    public static List<? extends String> getRacesThatCanCompeteForChildOfThePlane() {
        return racesThatCanCompeteForChildOfThePlane.get();
    }


}



