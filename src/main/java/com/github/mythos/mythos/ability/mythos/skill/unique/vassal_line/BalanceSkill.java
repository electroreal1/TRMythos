package com.github.mythos.mythos.ability.mythos.skill.unique.vassal_line;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class BalanceSkill extends Skill {
    public BalanceSkill(SkillType type) {
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

        Inevitable(instance, living);
    }

    @Override
    public int getMaxMastery() {
        return 2000;
    }

    @Override
    public double getObtainingEpCost() {
        return 100000;
    }

    public void Inevitable(ManasSkillInstance instance, LivingEntity entity) {
        if (isInSlot(entity)) {
            int severance = instance.isMastered(entity) ? 19 : 4;
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.SEVERANCE_BLADE.get(), 3, severance, false, false, false));
            entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }
}
