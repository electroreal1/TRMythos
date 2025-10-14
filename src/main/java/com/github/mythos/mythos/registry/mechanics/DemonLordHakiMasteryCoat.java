package com.github.mythos.mythos.registry.mechanics;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.skill.extra.DemonLordHakiSkill;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class DemonLordHakiMasteryCoat extends DemonLordHakiSkill {

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.hasEffect((MobEffect) TensuraMobEffects.HAKI_COAT.get())) {
            if (SkillHelper.outOfMagicule(entity, instance)) {
                return;
            }

            int amplifier = instance.isMastered(entity) ? 6 : 0; // Stronger when mastered
            int duration = instance.isMastered(entity) ? 4800 : 2400; // Longer duration for mastery

            entity.addEffect(new MobEffectInstance(
                    (MobEffect) TensuraMobEffects.HAKI_COAT.get(),
                    duration, amplifier, false, false, false
            ));

            entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.hasEffect((MobEffect) TensuraMobEffects.HAKI_COAT.get())) {
            int level = Objects.requireNonNull(
                    entity.getEffect((MobEffect) TensuraMobEffects.HAKI_COAT.get())
            ).getAmplifier();

            if (instance.isMastered(entity)) {
                // Residual aura after deactivation for mastered users
                entity.addEffect(new MobEffectInstance(
                        (MobEffect) TensuraMobEffects.HAKI_COAT.get(),
                        200, 1, false, false, false
                ));
            } else if (level >= 6) {
                entity.removeEffect((MobEffect) TensuraMobEffects.HAKI_COAT.get());
            }

            entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }
}
