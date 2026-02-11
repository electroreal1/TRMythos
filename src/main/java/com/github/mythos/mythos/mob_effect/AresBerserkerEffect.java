package com.github.mythos.mythos.mob_effect;

import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.effect.template.SkillMobEffect;
import com.github.manasmods.tensura.effect.template.Transformation;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.util.damage.TensuraDamageSource;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AresBerserkerEffect extends SkillMobEffect implements Transformation {
    protected static final String ARES = "2bc38f17-a7ac-3f67-9c9a-b3281b552e80";

    public AresBerserkerEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
        addAttributeModifier(Attributes.MAX_HEALTH, ARES, 100.0D, AttributeModifier.Operation.ADDITION);
        addAttributeModifier(Attributes.ARMOR, ARES, 10.0D, AttributeModifier.Operation.ADDITION);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, ARES, 10.0D, AttributeModifier.Operation.ADDITION);
        addAttributeModifier(Attributes.ATTACK_SPEED, ARES, 1.0D, AttributeModifier.Operation.ADDITION);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, ARES, 0.1D, AttributeModifier.Operation.ADDITION);
        addAttributeModifier(Attributes.FLYING_SPEED, ARES, 0.1D, AttributeModifier.Operation.ADDITION);
    }

    public void applyEffectTick(LivingEntity entity, int pAmplifier) {
        if (pAmplifier < 1 && failedToActivate(entity, (MobEffect)this))
            return;
        float damage = pAmplifier * 10.0F;
        DamageSource source = (new TensuraDamageSource("out_of_energy")).setNotTensuraMagic().bypassArmor();
        if (damage > 0.0F)
            entity.hurt(source, damage);
        TensuraParticleHelper.addParticlesAroundSelf((Entity)entity, (ParticleOptions) TensuraParticles.PURPLE_LIGHTNING_SPARK.get());
    }

    public void addAttributeModifiers(LivingEntity entity, AttributeMap pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(entity, pAttributeMap, pAmplifier);
        applyDebuff(entity);
    }

    public boolean isDurationEffectTick(int pDuration, int amplifier) {
        return (pDuration % 20 == 0);
    }
}
