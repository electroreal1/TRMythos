package com.github.mythos.mythos.mob_effect;

import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.effect.template.SkillMobEffect;
import com.github.manasmods.tensura.effect.template.Transformation;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class ChildOfThePlaneEffect extends SkillMobEffect implements Transformation {
    protected static final String CHILD_OF_THE_PLANE = "0f94bbda-31e5-4ecf-a497-a7ac6c78ece8";


    public ChildOfThePlaneEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "0f94bbda-31e5-4ecf-a497-a7ac6c78ece8", 12, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.ARMOR, "0f94bbda-31e5-4ecf-a497-a7ac6c78ece8", 20, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "0f94bbda-31e5-4ecf-a497-a7ac6c78ece8", 0.05000000074505806, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier((Attribute)TensuraAttributeRegistry.MAX_MAGICULE.get(), "0f94bbda-31e5-4ecf-a497-a7ac6c78ece8", 1.0, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier((Attribute)TensuraAttributeRegistry.MAX_AURA.get(), "0f94bbda-31e5-4ecf-a497-a7ac6c78ece8", 1.0, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        if (pLivingEntity instanceof Player player) {
            TensuraEPCapability.updateEP(player);
        }

    }

    public void applyEffectTick(LivingEntity entity, int pAmplifier) {
        if (!this.failedToActivate(entity, this)) {
            TensuraParticleHelper.addParticlesAroundSelf(entity, (ParticleOptions) TensuraParticles.YELLOW_LIGHTNING_SPARK.get());
        }
    }

    public void removeAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        if (pLivingEntity instanceof Player player) {
            TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                cap.setMagicule(cap.getMagicule() / 2.0);
                cap.setAura(cap.getAura() / 2.0);
                TensuraPlayerCapability.sync(player);
            });
        }

        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        TensuraEPCapability.updateEP(pLivingEntity);
        pLivingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 1, false, false));
        pLivingEntity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.FRAGILITY.get(), 600, 1, false, false));
        pLivingEntity.addEffect(new MobEffectInstance((MobEffect)TensuraMobEffects.PARALYSIS.get(), 600, 1, false, false));
    }

    public boolean isDurationEffectTick(int pDuration, int amplifier) {
        return pDuration % 10 == 0;
    }


}
