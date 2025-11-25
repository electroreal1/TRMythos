package com.github.mythos.mythos.registry.mechanics;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class SkillEvolutionMechanics {
    private static UUID crimsonTyrantOwner = null;
    public static boolean enableUniqueEvolution = true;
    public static boolean loseUniqueOnEvolution = true;


    public static void onCrimsonTyrantMastered(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (!(entity instanceof Player player)) return;

        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        Skill crimsonTyrantSkill = Skills.CRIMSON_TYRANT.get();
        Skill carnageSkill = Skills.CARNAGE.get();

        if (!SkillUtils.isSkillMastered(player, crimsonTyrantSkill)) return;

        if (crimsonTyrantOwner != null && !crimsonTyrantSkill.equals(player.getUUID())) {
            player.displayClientMessage(Component.translatable("trmythos.skill.learn_failed").withStyle(ChatFormatting.RED), false);
            return;
        }
        if (!enableUniqueEvolution) return;

        if (loseUniqueOnEvolution) {
            storage.getSkill(crimsonTyrantSkill).ifPresent(storage::forgetSkill);
        }
        if (!MythosSkillsConfig.enableUniqueEvolution()) return;
        if (MythosSkillsConfig.loseUniqueOnEvolution()) {
            storage.getSkill(crimsonTyrantSkill).ifPresent(storage::forgetSkill);
        }
        storage.learnSkill((ManasSkill) carnageSkill);

        player.displayClientMessage(
                Component.translatable("trmythos.skill.carnage.obtainment",
                        crimsonTyrantSkill.getName(),
                        carnageSkill.getName()
                        ).withStyle(ChatFormatting.GOLD), false
        );
        storage.learnSkill(carnageSkill);
        crimsonTyrantOwner = player.getUUID();
    }
}
