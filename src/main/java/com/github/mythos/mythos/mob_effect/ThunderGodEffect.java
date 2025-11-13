package com.github.mythos.mythos.mob_effect;

import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.awt.*;
import java.util.UUID;

public class ThunderGodEffect extends MobEffect {

    public ThunderGodEffect(MobEffectCategory mobEffectCategory, Color color) {
        super(MobEffectCategory.BENEFICIAL, 0x00FFFF);
        this.addAttributeModifier((Attribute) TensuraAttributeRegistry.MAX_MAGICULE.get(), "e71c5299-2acf-4076-b971-aa07c77816d4", 1.0, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier((Attribute)TensuraAttributeRegistry.MAX_AURA.get(), "0ac1aad1-9f33-403e-a9e2-ce886199d3c3", 1.0, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "dcd32ed4-58cf-4e93-b695-4c0a64835584", 0.06999999910593033, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, "5a62373a-b9f6-4d92-9d80-e0fd833dedf6", 25.0, AttributeModifier.Operation.ADDITION);

    }



    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        if (pLivingEntity instanceof Player player) {
            TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                double maxMagicule = player.getAttributeValue((Attribute)TensuraAttributeRegistry.MAX_MAGICULE.get());
                cap.setMagicule(maxMagicule);
                double maxAura = player.getAttributeValue((Attribute)TensuraAttributeRegistry.MAX_AURA.get());
                cap.setAura(maxAura);
            });
            TensuraPlayerCapability.sync(player);
            TensuraEPCapability.updateEP(player);
            if (pAmplifier >= 1) {
                if (player.isCreative() || player.isSpectator()) {
                    return;
                }

                if (player.getAbilities().mayfly) {
                    return;
                }

                player.getAbilities().mayfly = true;
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
            }
        }
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        super.applyEffectTick(entity, amplifier);
        TensuraParticleHelper.addParticlesAroundSelf(entity, (ParticleOptions) TensuraParticles.LIGHTNING_SPARK.get());
    }


    public void removeAttributeModifiers(LivingEntity entity, AttributeMap pAttributeMap, int pAmplifier) {
        if (entity instanceof Player player) {
            TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                cap.setMagicule(cap.getMagicule() / 2.0);
                cap.setAura(cap.getAura() / 2.0);
                TensuraPlayerCapability.sync(player);
            });
        }

        super.removeAttributeModifiers(entity, pAttributeMap, pAmplifier);
        TensuraEPCapability.updateEP(entity);
        if (pAmplifier >= 1 && entity instanceof Player player) {
            if (player.isCreative() || player.isSpectator()) {
                return;
            }

            if (!player.getAbilities().mayfly) {
                return;
            }

            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }

    }

    public boolean isDurationEffectTick(int pDuration, int amplifier) {
        return pDuration % 10 == 0;
    }

    public double getAttributeModifierValue(int pAmplifier, AttributeModifier pModifier) {
        return pModifier.getId().equals(UUID.fromString("0a92e00e-79ec-11ee-b962-0242ac120002")) ? pModifier.getAmount() : pModifier.getAmount() * (double)(pAmplifier + 1);
    }
}
