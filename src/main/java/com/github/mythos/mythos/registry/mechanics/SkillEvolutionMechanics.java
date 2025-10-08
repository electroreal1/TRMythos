package com.github.mythos.mythos.registry.mechanics;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class SkillEvolutionMechanics {

    private static UUID fighterOwner = null;

    public static void onFighterMastered(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (!(entity instanceof Player player)) return;

        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        Skill fighterSkill = UniqueSkills.FIGHTER.get();
        Skill martialMasterSkill = UniqueSkills.MARTIAL_MASTER.get();

        // Only trigger if the mastered skill is Fighter
        if (!SkillUtils.isSkillMastered(player, fighterSkill)) return;

        // Prevent multiple owners
        if (fighterOwner != null && !fighterOwner.equals(player.getUUID())) {
            storage.getSkill(fighterSkill).ifPresent(storage::forgetSkill);

            TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
                cap.setBaseMagicule(cap.getBaseMagicule() + 150_000, player);
                TensuraPlayerCapability.sync(player);
                TensuraEPCapability.updateEP(player);
            });

            player.displayClientMessage(
                    Component.translatable("trmythos.skill.learn_failed").withStyle(ChatFormatting.RED),
                    false
            );
            return;
        }

        // Forget Fighter and grant Martial Master
        storage.getSkill(fighterSkill).ifPresent(storage::forgetSkill);

        // Learn Martial Master
        storage.learnSkill((ManasSkill) martialMasterSkill);

        // Send feedback
        player.displayClientMessage(
                Component.translatable(
                        "trmythos.skill.martial_master.obtainment",
                        fighterSkill.getName(),
                        martialMasterSkill.getName()
                ).withStyle(ChatFormatting.GOLD),
                false
        );

        // Set ownership
        fighterOwner = player.getUUID();
    }
}
