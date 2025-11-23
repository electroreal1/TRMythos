package com.github.mythos.mythos.ability.mythos.skill.unique.evolved;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.effects.TensuraEffectsCapability;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.util.damage.TensuraDamageSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;

public class PerseveranceSkill extends Skill {
    public PerseveranceSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public double getObtainingEpCost() {
        return 50000;
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public int getMaxMastery() {
        return 4000;
    }

    @Override
    public void onTick(ManasSkillInstance instance, @NotNull LivingEntity entity) {
        if (!instance.isToggled()) return;

        if (!(entity instanceof Player player)) return;

        TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
            double maxMP = player.getAttributeValue(TensuraAttributeRegistry.MAX_MAGICULE.get());
            double maxAP = player.getAttributeValue(TensuraAttributeRegistry.MAX_AURA.get());
            double regenMPPerTick = (maxMP * 0.001) / 20;
            double regenAPPerTick = (maxAP * 0.001) / 20;

            cap.setAura(Math.min(cap.getAura() + regenAPPerTick, maxAP));
            cap.setMagicule(Math.min(cap.getMagicule() + regenMPPerTick, maxMP));
        });

        TensuraPlayerCapability.sync(player);
    }

    public void onDeath(ManasSkillInstance instance, LivingDeathEvent event) {
        if (!event.isCanceled()) {
            DamageSource source = event.getSource();
            if (source != DamageSource.OUT_OF_WORLD) {
                if (source instanceof TensuraDamageSource) {
                    TensuraDamageSource damageSource = (TensuraDamageSource)source;
                    if (damageSource.getIgnoreBarrier() >= 3.0F) {
                        return;
                    }
                }

                LivingEntity entity = event.getEntity();
                if (!entity.isAlive()) {
                    if (source.getEntity() != null) {
                        if (source.getEntity() == entity) {
                            return;
                        }

                        if (source.getEntity() == SkillHelper.getSubordinateOwner(entity)) {
                            return;
                        }
                    }


                    TensuraEffectsCapability.resetEverything(entity, false, false); //THIS RESETS EFFECTS NOTHING ELSE DON'T GET IDEAS
                    TensuraEPCapability.getFrom(entity).ifPresent((cap) -> {
                        if (cap.getEP() <= 0.0) {
                            cap.setEP(entity, 100.0, false);
                        } else if (cap.getCurrentEP() <= 0.0) {
                            cap.setCurrentEP(entity, cap.getEP() * 0.5);
                        }

                        double SHP = entity.getAttributeValue((Attribute)TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());
                        if (cap.getSpiritualHealth() < SHP * 0.25) {
                            cap.setSpiritualHealth(SHP * 0.25);
                        }


                    });
                    TensuraEPCapability.sync(entity);

                    event.setCanceled(true);
                    if (!instance.onCoolDown()) {
                        instance.setCoolDown(60);
                    }

                    entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                    TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.TOTEM_OF_UNDYING, 1.0);
                    TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.TOTEM_OF_UNDYING, 2.0);
                    TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.FLASH, 1.0);
                }
            }
        }
    }


}

