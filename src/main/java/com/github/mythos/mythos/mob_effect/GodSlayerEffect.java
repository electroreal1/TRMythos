package com.github.mythos.mythos.mob_effect;

import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.effect.template.SkillMobEffect;
import com.github.manasmods.tensura.effect.template.Transformation;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class GodSlayerEffect extends SkillMobEffect implements Transformation {

    public static final String SLAYER = "8e4da298-0ea3-3b0d-8b07-5572a3d71c9d";
    public static final String SLAYER_ENERGY = "c99823ab-4e6a-3290-9fd7-3b8ce69a269b";

    public GodSlayerEffect(MobEffectCategory category, int color) {
        super(category, color);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, "8e4da298-0ea3-3b0d-8b07-5572a3d71c9d", 15.0D, AttributeModifier.Operation.ADDITION);
        addAttributeModifier(Attributes.ARMOR, "8e4da298-0ea3-3b0d-8b07-5572a3d71c9d", 25.0D, AttributeModifier.Operation.ADDITION);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, "8e4da298-0ea3-3b0d-8b07-5572a3d71c9d", 0.04D, AttributeModifier.Operation.ADDITION);
        addAttributeModifier((Attribute) TensuraAttributeRegistry.MAX_MAGICULE.get(), "c99823ab-4e6a-3290-9fd7-3b8ce69a269b", 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier((Attribute)TensuraAttributeRegistry.MAX_AURA.get(), "c99823ab-4e6a-3290-9fd7-3b8ce69a269b", 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        if (pLivingEntity instanceof Player) {
            Player player = (Player)pLivingEntity;
            TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
                double maxMagicule = player.getAttributeValue((Attribute)TensuraAttributeRegistry.MAX_MAGICULE.get());
                cap.setMagicule(maxMagicule);
                double maxAura = player.getAttributeValue((Attribute)TensuraAttributeRegistry.MAX_AURA.get());
                cap.setAura(maxAura);
            });
            TensuraPlayerCapability.sync(player);
            TensuraEPCapability.updateEP((LivingEntity)player);
            if (pAmplifier >= 1) {
                if (player.isSwimming() || player.isFallFlying())
                    return;
                if ((player.getAbilities()).mayfly)
                    return;
                (player.getAbilities()).mayfly = true;
                (player.getAbilities()).instabuild = true;
                player.isCreative();
            }
        }
    }

    public void applyEffectTick(LivingEntity entity, int pAmplifier) {
        if (failedToActivate(entity, (MobEffect)this))
            return;
        ParticleOptions particle = (pAmplifier >= 1) ? (ParticleOptions)TensuraParticles.YELLOW_LIGHTNING_SPARK.get() : (ParticleOptions) TensuraParticles.LIGHTNING_SPARK.get();
        TensuraParticleHelper.addParticlesAroundSelf((Entity)entity, particle);
    }

    public void removeAttributeModifiers(LivingEntity entity, AttributeMap pAttributeMap, int pAmplifier) {
        if (entity instanceof Player) {
            Player player = (Player)entity;
            TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
                cap.setMagicule(cap.getMagicule() / 2.0D);
                cap.setAura(cap.getAura() / 2.0D);
                TensuraPlayerCapability.sync(player);
            });
        }
        super.removeAttributeModifiers(entity, pAttributeMap, pAmplifier);
        TensuraEPCapability.updateEP(entity);
        applyDebuff(entity);
        if (pAmplifier >= 1 && entity instanceof Player) {
            Player player = (Player)entity;
            if (player.isSwimming() || player.isFallFlying())
                return;
            if (!(player.getAbilities()).mayfly)
                return;
            (player.getAbilities()).mayfly = false;
            (player.getAbilities()).instabuild = false;
            player.isCreative();
        }
    }

    public boolean isDurationEffectTick(int pDuration, int amplifier) {
        return (pDuration % 10 == 0);
    }

    public double getAttributeModifierValue(int pAmplifier, AttributeModifier pModifier) {
        if (pModifier.getId().equals(UUID.fromString("c99823ab-4e6a-3290-9fd7-3b8ce69a269b")))
            return pModifier.getAmount();
        return pModifier.getAmount() * (pAmplifier + 1);
    }
}