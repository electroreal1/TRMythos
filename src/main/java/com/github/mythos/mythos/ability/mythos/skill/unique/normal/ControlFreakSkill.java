package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.util.MythosUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class ControlFreakSkill extends Skill {
    public ControlFreakSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Control Freak");
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("You yearn for control after being deprived of it for so long. Now that you have the power to do just that, how will you use it?");
    }

    @Override
    public double getObtainingEpCost() {
        return 100000;
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity living, UnlockSkillEvent event) {
        SkillUtils.learnSkill(living, ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get());
    }

    @Override
    public void onSkillMastered(ManasSkillInstance instance, LivingEntity living) {
        SkillUtils.learnSkill(living, ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get());
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.isToggled()) {
            if (entity instanceof Player) {
                entity.addEffect(new MobEffectInstance(TensuraMobEffects.INSTANT_REGENERATION.get(), 240, 0, false, false, false));
                entity.addEffect(new MobEffectInstance(TensuraMobEffects.STRENGTHEN.get(), 240, 0, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 240, 0, false, false, false));
            } else {
                entity.addEffect(new MobEffectInstance(TensuraMobEffects.SELF_REGENERATION.get(), 240, 20, false, false, false));
            }
        }
    }

    @Override
    public int getMaxMastery() {
        return 1000;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 3 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 3) ? 1 : (instance.getMode() + 1);
    }

    public Component getModeName(int mode) {
        MutableComponent name = switch (mode) {
            case 1 -> Component.literal("Spiritual Domination");
            case 2 -> Component.literal("Enchain");
            case 3 -> Component.literal("Rupture");
            default -> Component.empty();
        };
        return name;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        // Spiritual Domination
        if (instance.getMode() == 1) {
            CharmSkill.charm(instance, entity);
        }

        // Enchain
        if (instance.getMode() == 2) {
            LivingEntity target = MythosUtils.getLookedAtEntity(entity, 10);
            int time = instance.isMastered(entity) ? 20 : 10;
            target.addEffect(new MobEffectInstance(MythosMobEffects.ENCHAIN_EFFECT.get(), time, 0, false, false, false));
        }

        // Rupture
        if (instance.getMode() == 3) {
            LivingEntity target = MythosUtils.getLookedAtEntity(entity, 10);
            DamageSourceHelper.directSpiritualHurt(target, entity, 100);
            instance.setCoolDown(instance.isMastered(entity) ? 1 : 5);
            instance.addMasteryPoint(entity);
        }
    }
}
