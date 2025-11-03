package com.github.mythos.mythos.ability.confluence.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.skill.CommonSkills;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class Excalibur extends Skill {
    public Excalibur(SkillType type) {
        super(type);
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public double getObtainingEpCost() {
        return 250000;
    }

    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        if (entity instanceof LivingEntity) {
            if (entity.hasEffect(MythosMobEffects.EXCALIBUR_REGENERATION.get())) {
                return;
            } else {
                entity.addEffect(new MobEffectInstance((MobEffect) MythosMobEffects.EXCALIBUR_REGENERATION.get(), 1200, 1, false, false, false));
            }

            if (entity.hasEffect(TensuraMobEffects.INSPIRATION.get())) {
                return;
            } else {
                entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.INSPIRATION.get(), 1200, 1, false, false, false));
            }
        }
    }

    public void onLearnSkill(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, @NotNull UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            SkillUtils.learnSkill(entity, ExtraSkills.STEEL_STRENGTH.get());
            SkillUtils.learnSkill(entity, ExtraSkills.STRENGTHEN_BODY.get());
            SkillUtils.learnSkill(entity, CommonSkills.SELF_REGENERATION.get());
            SkillUtils.learnSkill(entity, ExtraSkills.MAJESTY.get());
        }
    }

    private void gainMastery(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        int time = tag.getInt("activatedTimes");
        if (time % 12 == 0) {
            this.addMasteryPoint(instance, entity);
        }

        tag.putInt("activatedTimes", time + 1);
    }

    @Override
    public int getMaxMastery() {
        return 3000;
    }


}
