package com.github.mythos.mythos.ability.mythos.skill.unique.vassal_line;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.block.HipokuteGrass;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CycleSkill extends Skill {
    public CycleSkill(SkillType type) {
        super(type);
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Cycle");
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("Life and Death. Creation and Destruction. All beings exist in this cycle, and as the Vassal of Cycle, you oversee that it is carried out.");
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity living) {
        if (instance.isToggled()) {
            if (instance.isToggled() && !living.getLevel().isClientSide) {
                if (!SkillHelper.outOfMagicule(living, 20.0)) {
                    Level level = living.getLevel();
                    BlockPos origin = living.blockPosition();
                    int radius = 10;
                    BlockPos.betweenClosedStream(origin.offset(-radius, -2, -radius), origin.offset(radius, 2, radius)).forEach((pos) -> {
                        BlockState state = level.getBlockState(pos);
                        Block patt4227$temp = state.getBlock();
                        int currentAge;
                        int nextAge;
                        IntegerProperty ageProp;
                        if (patt4227$temp instanceof CropBlock crop) {
                            if (!crop.isMaxAge(state) && level.random.nextFloat() < 0.5F) {
                                ageProp = crop.getAgeProperty();
                                currentAge = state.getValue(ageProp);
                                nextAge = Math.min(currentAge + 1, crop.getMaxAge());
                                level.setBlock(pos, state.setValue(ageProp, nextAge), 2);
                                level.playSound(null, origin.getX(), origin.getY(), origin.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                                ((ServerLevel) level).sendParticles(ParticleTypes.HAPPY_VILLAGER, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, 5, 0.3, 0.3, 0.3, 0.05);
                            }
                        }

                        patt4227$temp = state.getBlock();
                        if (patt4227$temp instanceof HipokuteGrass hipokuteGrass) {
                            if (state.getValue(hipokuteGrass.getAgeProperty()) < hipokuteGrass.getMaxAge() && level.random.nextFloat() < 0.1F) {
                                ageProp = hipokuteGrass.getAgeProperty();
                                currentAge = state.getValue(ageProp);
                                nextAge = Math.min(currentAge + 1, hipokuteGrass.getMaxAge());
                                level.setBlock(pos, state.setValue(ageProp, nextAge), 2);
                                level.playSound(null, origin.getX(), origin.getY(), origin.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                                ((ServerLevel) level).sendParticles(ParticleTypes.HAPPY_VILLAGER, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, 5, 0.3, 0.3, 0.3, 0.05);
                            }
                        }

                    });
                }
            }
        }
    }

    @Override
    public void onDeath(ManasSkillInstance instance, LivingDeathEvent event) {
        LivingEntity user = event.getEntity();
        if (user.level.isClientSide()) return;

        double userEP = TensuraEPCapability.getEP(user);

        double radius = 20.0;
        AABB area = user.getBoundingBox().inflate(radius);
        List<LivingEntity> nearbyEntities = user.level.getEntitiesOfClass(LivingEntity.class, area, entity -> entity != user && entity.isAlive());

        if (user.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SQUID_INK, user.getX(), user.getY() + 1, user.getZ(), 500, 2.0, 2.0, 2.0, 0.1);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, user.getX(), user.getY() + 1, user.getZ(), 100, 5.0, 5.0, 5.0, 0.05);
            serverLevel.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 2.0F, 0.5F);
        }

        for (LivingEntity target : nearbyEntities) {
            double targetEP = TensuraEPCapability.getEP(target);

            if (targetEP >= userEP) {
                target.kill();

                if (target.level instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.REVERSE_PORTAL, target.getX(), target.getY() + 1, target.getZ(), 50, 0.5, 1.0, 0.5, 0.1);
                }
            }
        }
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!SkillHelper.outOfMagicule(entity, instance)) {
            Level level = entity.getLevel();
            LivingEntity target;
            target = SkillHelper.getTargetingEntity(entity, 5.0, false);
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.setCoolDown(instance.isMastered(entity) ? 3 : 5);
            float healingHP;
            double lackedMana;
            double healingSpiritual;
            double lackedSpiritual;
            double lackedMP;
            int cost;
            if (target != null && entity.isShiftKeyDown()) {
                this.addMasteryPoint(instance, entity);
                cost = instance.isMastered(entity) ? 40 : 80;
                healingHP = target.getMaxHealth() - target.getHealth();
                lackedMana = SkillHelper.outOfMagiculeStillConsume(entity, (int) (healingHP * (float) cost));
                if (lackedMana > 0.0) {
                    healingHP = (float) ((double) healingHP - lackedMana / (double) cost);
                }

                target.heal(healingHP);
                if (this.isMastered(instance, entity)) {
                    healingSpiritual = TensuraEPCapability.getSpiritualHealth(target);
                    lackedSpiritual = target.getAttributeValue(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get()) - healingSpiritual;
                    lackedMP = SkillHelper.outOfMagiculeStillConsume(entity, (int) (lackedSpiritual * 60.0));
                    if (lackedMP > 0.0) {
                        lackedSpiritual -= lackedMP / 60.0;
                    }

                    TensuraEPCapability.setSpiritualHealth(target, healingSpiritual + lackedSpiritual);
                }

                TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.HEART, 1.0);
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
            } else {
                entity.removeEffect(TensuraMobEffects.INFECTION.get());
                cost = instance.isMastered(entity) ? 40 : 80;
                healingHP = entity.getMaxHealth() - entity.getHealth();
                lackedMana = SkillHelper.outOfMagiculeStillConsume(entity, (int) (healingHP * (float) cost));
                if (lackedMana > 0.0) {
                    healingHP = (float) ((double) healingHP - lackedMana / (double) cost);
                }

                entity.heal(healingHP);
                if (this.isMastered(instance, entity)) {
                    healingSpiritual = TensuraEPCapability.getSpiritualHealth(entity);
                    lackedSpiritual = entity.getAttributeValue(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get()) - healingSpiritual;
                    lackedMP = SkillHelper.outOfMagiculeStillConsume(entity, (int) (lackedSpiritual * 50.0));
                    if (lackedMP > 0.0) {
                        lackedSpiritual -= lackedMP / 50.0;
                    }

                    TensuraEPCapability.setSpiritualHealth(entity, healingSpiritual + lackedSpiritual);
                }

                TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.HEART, 1.0);
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
            }


        }
    }
}
