package com.github.mythos.mythos.registry.mechanics;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.skill.extra.DemonLordHakiSkill;
import com.github.manasmods.tensura.ability.skill.extra.HeroHakiSkill;
import com.github.manasmods.tensura.ability.skill.unique.GreatSageSkill;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import io.github.Memoires.trmysticism.ability.skill.ultimate.SusanooSkill;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class HeroHakiMasteryCoat extends HeroHakiSkill {

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.hasEffect((MobEffect)TensuraMobEffects.HAKI_COAT.get())) {
            if (SkillHelper.outOfMagicule(entity, instance)) {
                return;
            }

            entity.addEffect(new MobEffectInstance((MobEffect)TensuraMobEffects.HAKI_COAT.get(), 2400, 0, false, false, false));
            entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.hasEffect((MobEffect)TensuraMobEffects.HAKI_COAT.get())) {
            int level = ((MobEffectInstance) Objects.requireNonNull(entity.getEffect((MobEffect)TensuraMobEffects.HAKI_COAT.get()))).getAmplifier();
            if (level == 6) {
                entity.removeEffect((MobEffect)TensuraMobEffects.HAKI_COAT.get());
                entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }
}
