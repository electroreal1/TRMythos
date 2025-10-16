package com.github.mythos.mythos.mob_effect.debuff;

import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.capability.effects.ITensuraEffectsCapability;
import com.github.manasmods.tensura.capability.effects.TensuraEffectsCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.effect.template.SkillMobEffect;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class BloodDrainEffect extends SkillMobEffect {
    protected static final String BLOODSUCKER = "BLOODSUCKER";

    public BloodDrainEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
        addAttributeModifier(Attributes.MAX_HEALTH, "BLOODSUCKER", -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, "BLOODSUCKER", 1.0D, AttributeModifier.Operation.ADDITION);
    }

    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        if (!pLivingEntity.level.isClientSide())
            TensuraEffectsCapability.getFrom(pLivingEntity).ifPresent(cap -> {
                cap.setLockedXRot(pLivingEntity.getXRot());
                cap.setLockedYRot(pLivingEntity.getYRot());
                TensuraEffectsCapability.sync(pLivingEntity);
            });
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }

    public void applyEffectTick(LivingEntity target, int pAmplifier) {
        MobEffectInstance instance = target.getEffect((MobEffect)this);
        if (instance == null)
            return;
        lockRotation(target);
        if (target.level.isClientSide())
            return;
        TensuraParticleHelper.addServerParticlesAroundSelf((Entity)target, (ParticleOptions)TensuraParticles.YELLOW_LIGHTNING_SPARK.get());
        if (instance.getDuration() % 20 != 0)
            return;
        Player source = TensuraEffectsCapability.getEffectSource(target, (MobEffect)this);
        if (source == target) {
            LivingEntity targetingEntity = SkillHelper.getTargetingEntity(target, 2.0D, false);
            if (targetingEntity == null)
                target.removeEffect((MobEffect)this);
            return;
        }
        if (source != null) {
            double amount = (pAmplifier >= 1) ? 0.005D : 500.0D;
            if (SkillHelper.drainEnergy(target, (LivingEntity)source, amount, (pAmplifier >= 1)))
                source.level.playSound(null, source.getX(), source.getY(), source.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    public boolean m_6584_(int pDuration, int amplifier) {
        return (pDuration > 0);
    }

    public static void lockRotation(LivingEntity entity) {
        TensuraEffectsCapability.getFrom(entity).ifPresent(cap -> {
            float lockedX = cap.getLockedXRot();
            float lockedY = cap.getLockedYRot();

            // set current rotation (available methods)
            entity.setXRot(lockedX);
            entity.setYRot(lockedY);

            // set previous-tick rotation fields so interpolation doesn't fight us
            // these are fields on LivingEntity: xRotO, yRotO
            entity.xRotO = lockedX;
            entity.yRotO = lockedY;

            // lock head/body rotation so the model doesn't snap away
            entity.yHeadRot = lockedY;
            entity.yBodyRot = lockedY;

            // If you also use the head pitch / body pitch fields elsewhere, keep them in sync:
            entity.xRotO = lockedX;
        });
    }
}
