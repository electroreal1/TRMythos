package com.github.mythos.mythos.mob_effect;

import com.github.manasmods.manascore.attribute.ManasCoreAttributes;
import com.github.manasmods.tensura.capability.effects.TensuraEffectsCapability;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.effect.template.TensuraMobEffect;
import com.github.manasmods.tensura.race.RaceHelper;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.manasmods.tensura.world.TensuraGameRules;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class MammonFlareEffect extends TensuraMobEffect {
    protected static final String FEAR_SPEED_UUID = "3e34a2f8-c55b-11ed-afa1-0242ac120002";
    public MammonFlareEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "3e34a2f8-c55b-11ed-afa1-0242ac120002",
                -0.05000000074505806, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(ManasCoreAttributes.MINING_SPEED_MULTIPLIER.get(), "d9206bc8-d774-11ed-afa1-0242ac120002",
                -0.30000001192092896, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity targetEntity, int amplifier) {
        Player source = TensuraEffectsCapability.getEffectSource(targetEntity, this);
        int durabilityCorrosion = 15 * (amplifier + 1);
        float damage = 2.0F * (float)(amplifier + 1);

        if (source == null) {
            DamageSourceHelper.directSpiritualHurt(targetEntity, null, TensuraDamageSources.SOUL_CONSUMED, 70.0F);
            targetEntity.hurt(TensuraDamageSources.CORROSION, damage);
        } else {
            DamageSourceHelper.directSpiritualHurt(targetEntity, null, TensuraDamageSources.soulConsume(source), 70.0F);
            targetEntity.hurt(TensuraDamageSources.corrosion(source), damage);
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack slotStack = targetEntity.getItemBySlot(slot);
            slotStack.hurtAndBreak(durabilityCorrosion, targetEntity, (living) -> {
                living.broadcastBreakEvent(slot);
            });
        }

        if (targetEntity.isFallFlying() && amplifier >= 1) {
            targetEntity.isFallFlying();
        }

        if (amplifier >= 4) {
            if (source != null && targetEntity instanceof Player targetPlayer) {
                if (targetPlayer.distanceTo(source) < 7.0F) {
                    targetPlayer.hurt(TensuraDamageSources.insanity(source), 5.0F * (float)(amplifier - 3));
                }
            }

            if (amplifier >= 9) {
                float damage2 = 7.0F * (float)(amplifier - 8);
                if (source == null) {
                    targetEntity.hurt(TensuraDamageSources.FEAR, damage2);
                } else {
                    targetEntity.hurt(TensuraDamageSources.fear(source), damage2);
                    if (targetEntity.isAlive() && targetEntity instanceof Player player) {
                        RaceHelper.applyMajinChance(player);
                    }
                }
            }
        }
        TensuraParticleHelper.addServerParticlesAroundSelf(targetEntity, TensuraParticles.SOLAR_FLASH.get(), 1.0);

        if (source != null && targetEntity.getHealth() <= 0.0F) {
            gainEPOnKill(source, targetEntity);
        }
    }

    public static void gainEPOnKill(@NotNull LivingEntity source, @NotNull LivingEntity targetEntity) {
        if (source != null && targetEntity.getHealth() <= 0.0F) {
            TensuraEPCapability.getFrom(source).ifPresent((epCap) -> {
                double ownerEP = epCap.getEP();
                double targetEP = TensuraEPCapability.getEP(targetEntity);
                double epGainRule = source.level.getGameRules().getRule(TensuraGameRules.EP_GAIN).get();
                double gainedEP = targetEP * (epGainRule / 1.5) / 1.5;
                double finalEP = ownerEP + gainedEP;

                epCap.setEP(source, finalEP, true);
                epCap.setCurrentEP(source, finalEP);
                targetEntity.remove(Entity.RemovalReason.KILLED);

                if (source instanceof Player player) {
                    TensuraPlayerCapability.getFrom(player).ifPresent((playerCap) -> {
                        playerCap.setBaseMagicule(playerCap.getBaseMagicule() + gainedEP, source);
                        playerCap.setMagicule(playerCap.getBaseMagicule());
                        playerCap.setBaseAura(playerCap.getBaseAura() + gainedEP, source);
                        playerCap.setAura(playerCap.getBaseAura());
                    });
                    TensuraPlayerCapability.sync(player);
                }
            });
            TensuraEPCapability.updateEP(source, true);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 15 == 0 && amplifier >= 1;
    }

    @Override
    public double getAttributeModifierValue(int amplifier, @NotNull AttributeModifier modifier) {
        return amplifier <= 0 ? 0.0 : modifier.getAmount() * (double)(amplifier + 1);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }


}
