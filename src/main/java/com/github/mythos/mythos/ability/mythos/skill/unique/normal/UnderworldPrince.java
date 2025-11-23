package com.github.mythos.mythos.ability.mythos.skill.unique.normal;


import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.unique.ReaperSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Random;

public class UnderworldPrince extends Skill {
    public UnderworldPrince(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity living) {
        TensuraEPCapability.getFrom(living).ifPresent((cap) -> {
            cap.setChaos(true);
        });
        TensuraEPCapability.sync(living);

        ReaperSkill reaperSkill = (ReaperSkill) UniqueSkills.REAPER.get();

        if (instance.isMastered(living)) {
            Random random = new Random();
            int randomNumber = random.nextInt(10);
            if (randomNumber == 1) {
                TensuraSkillInstance noCost = new TensuraSkillInstance((ManasSkill)reaperSkill);
                noCost.getOrCreateTag().putBoolean("NoMagiculeCost", true);
                if (SkillAPI.getSkillsFrom((Entity)living).learnSkill((ManasSkillInstance)noCost)) {
                    living.sendSystemMessage(Component.literal("The Underworld Beckons you to embrace souls of the damned!").withStyle(ChatFormatting.BLACK));
                }
            } else {
                return;
            }

        }
    }
}
