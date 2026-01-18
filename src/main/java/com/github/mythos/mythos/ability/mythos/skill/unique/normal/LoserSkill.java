package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class LoserSkill extends Skill {
    public LoserSkill(SkillType type) {
        super(type);
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Loser");
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("A pathetic creature at heart, everything that touches your wretched soul seems to wither away...");
    }

    @Override
    public double getObtainingEpCost() {
        return 50000;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        instance.addMasteryPoint(entity);
        if (SkillUtils.hasSkill(entity, UniqueSkills.CHOSEN_ONE.get())) {
            SkillStorage storage = SkillAPI.getSkillsFrom(entity);
            storage.forgetSkill(UniqueSkills.CHOSEN_ONE.get());
            SkillUtils.learnSkill(entity, Skills.FALSE_HERO.get());
        }

        if (SkillUtils.hasSkill(entity, UniqueSkills.ABSOLUTE_SEVERANCE.get())) {
            SkillStorage storage = SkillAPI.getSkillsFrom(entity);
            storage.forgetSkill(UniqueSkills.ABSOLUTE_SEVERANCE.get());
            SkillUtils.learnSkill(entity, UniqueSkills.SEVERER.get());
        }
    }

    @Override
    public int getMaxMastery() {
        return 1000;
    }
}
