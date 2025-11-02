package com.github.mythos.mythos.ability.mythos.skill.extra;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class AutomaticHakiCoatSkill extends Skill {
    public AutomaticHakiCoatSkill(SkillType type) {
        super(SkillType.EXTRA);
    }


    public boolean meetEPRequirement(Player entity, double newEP) {
        SkillStorage storage = SkillAPI.getSkillsFrom(entity);
        Skill HeroHaki = ExtraSkills.HERO_HAKI.get();
        Skill DemonLordHaki = ExtraSkills.DEMON_LORD_HAKI.get();

        if (!SkillUtils.isSkillMastered(entity, HeroHaki) || !SkillUtils.isSkillMastered(entity, DemonLordHaki)) {
            return false;
        } else if (SkillUtils.isSkillMastered(entity, HeroHaki) || SkillUtils.isSkillMastered(entity, DemonLordHaki)) {
            return true;
        } else return false;
    }

    public double learningCost() {
        return 500.0;
    }

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
        return true;
    }

    public boolean canBeSlotted(ManasSkillInstance instance) {
        return instance.getMastery() < 0;
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.hasEffect((MobEffect) TensuraMobEffects.HAKI_COAT.get())) {
            if (SkillHelper.outOfMagicule(entity, instance)) {
                return;
            }

            entity.addEffect(new MobEffectInstance((MobEffect)TensuraMobEffects.HAKI_COAT.get(), 2400, 0, false, false, false));
            entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.PLAYERS, 1.0F, 1.0F);
        } else {
            entity.removeEffect((MobEffect)TensuraMobEffects.HAKI_COAT.get());
            entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }


}
