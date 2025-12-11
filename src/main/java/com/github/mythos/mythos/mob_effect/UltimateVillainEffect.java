package com.github.mythos.mythos.mob_effect;

import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.effect.template.SkillMobEffect;
import com.github.manasmods.tensura.effect.template.Transformation;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public class UltimateVillainEffect extends SkillMobEffect implements Transformation {
    protected static final String ULTIMATE_VILLAIN = "1f23abde-12cd-4fa1-bfa2-8ab4e1234567";
    public UltimateVillainEffect(MobEffectCategory category, int color) {
        super(category, color);

        this.addAttributeModifier(TensuraAttributeRegistry.MAX_AURA.get(),
                ULTIMATE_VILLAIN, 0.83, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(TensuraAttributeRegistry.MAX_MAGICULE.get(),
                ULTIMATE_VILLAIN, 0.83, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        super.addAttributeModifiers(entity, attributes, amplifier);

        if (entity instanceof Player player) {
            TensuraEPCapability.updateEP(player);
        }
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
                float mpDrain = (float) (cap.getMagicule() * 0.025);
                mpDrain /= 20.0;
                if (cap.getMagicule() > mpDrain) {
                    cap.setMagicule(cap.getMagicule() - mpDrain);
                } else {
                    float hpDrain = mpDrain;
                    entity.hurt(TensuraDamageSources.BLOOD_DRAIN.bypassMagic().bypassInvul(), hpDrain);
                    cap.setMagicule(0);
                }
                TensuraPlayerCapability.sync(player);
            });
        }

        TensuraParticleHelper.addParticlesAroundSelf(entity, (ParticleOptions) TensuraParticles.DARK_RED_LIGHTNING_SPARK.get());
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }


}
