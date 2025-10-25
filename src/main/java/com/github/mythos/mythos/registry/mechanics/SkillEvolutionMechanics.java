package com.github.mythos.mythos.registry.mechanics;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class SkillEvolutionMechanics {

    private static UUID fighterOwner = null;
    private static UUID chefOwner = null;

    // Config flags
    public static boolean ENABLE_FIGHTER_EVOLUTION = true;
    public static boolean LOSE_SKILL_ON_FIGHTER_EVOLUTION = true;
    public static boolean ENABLE_CHEF_EVOLUTION = true;
    public static boolean LOSE_SKILL_ON_CHEF_EVOLUTION = true;

    public static void onFighterMastered(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (!ENABLE_FIGHTER_EVOLUTION || !(entity instanceof Player player)) return;

        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        Skill fighterSkill = UniqueSkills.FIGHTER.get();
        Skill martialMasterSkill = UniqueSkills.MARTIAL_MASTER.get();

        if (!SkillUtils.isSkillMastered(player, fighterSkill)) return;

        // Prevent multiple owners
        if (fighterOwner != null && !fighterOwner.equals(player.getUUID())) {
            player.displayClientMessage(Component.translatable("trmythos.skill.learn_failed").withStyle(ChatFormatting.RED), false);
            return;
        }

        // Forget Fighter if configured
        if (LOSE_SKILL_ON_FIGHTER_EVOLUTION) {
            storage.getSkill(fighterSkill).ifPresent(storage::forgetSkill);
        }
        if (!MythosSkillsConfig.isFighterEvolutionEnabled()) return;
        if (MythosSkillsConfig.loseFighterOnEvolution()) {
            storage.getSkill(fighterSkill).ifPresent(storage::forgetSkill);
        }
        storage.learnSkill((ManasSkill) martialMasterSkill);

        player.displayClientMessage(
                Component.translatable(
                        "trmythos.skill.martial_master.obtainment",
                        fighterSkill.getName(),
                        martialMasterSkill.getName()
                ).withStyle(ChatFormatting.GOLD),
                false
        );

        fighterOwner = player.getUUID();
    }

    public static void onChefMastered(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (!ENABLE_CHEF_EVOLUTION || !(entity instanceof Player player)) return;

        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        Skill chefSkill = UniqueSkills.CHEF.get();
        Skill cookSkill = UniqueSkills.COOK.get();

        if (!SkillUtils.isSkillMastered(player, chefSkill)) return;

        // Prevent multiple owners
        if (chefOwner != null && !chefOwner.equals(player.getUUID())) {
            player.displayClientMessage(Component.translatable("trmythos.skill.learn_failed").withStyle(ChatFormatting.RED), false);
            return;
        }

        if (LOSE_SKILL_ON_CHEF_EVOLUTION) {
            storage.getSkill(chefSkill).ifPresent(storage::forgetSkill);
        }

        if (!MythosSkillsConfig.isChefEvolutionEnabled()) return;
        if (MythosSkillsConfig.loseChefOnEvolution()) {
            storage.getSkill(chefSkill).ifPresent(storage::forgetSkill);
        }

        storage.learnSkill((ManasSkill) cookSkill);

        player.displayClientMessage(
                Component.translatable(
                        "trmythos.skill.cook.obtainment",
                        chefSkill.getName(),
                        cookSkill.getName()
                ).withStyle(ChatFormatting.GOLD),
                false
        );

        chefOwner = player.getUUID();
    }
}
