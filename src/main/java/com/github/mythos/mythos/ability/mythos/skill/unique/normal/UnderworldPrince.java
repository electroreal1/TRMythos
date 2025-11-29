package com.github.mythos.mythos.ability.mythos.skill.unique.normal;


import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.HakiSkill;
import com.github.manasmods.tensura.ability.skill.unique.ReaperSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.config.TensuraConfig;
import com.github.manasmods.tensura.network.TensuraNetwork;
import com.github.manasmods.tensura.network.play2client.RequestFxSpawningPacket;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.Random;

public class UnderworldPrince extends Skill {
    public UnderworldPrince(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity living) {
        TensuraEPCapability.getFrom(living).ifPresent((cap) -> {
            cap.setChaos(true);
        });
        TensuraEPCapability.sync(living);

        ReaperSkill reaperSkill = (ReaperSkill) UniqueSkills.REAPER.get();

        if (instance.isMastered(living)) {
            Random random = new Random();
            int randomNumber = random.nextInt(10);
            if (randomNumber == 1) {
                TensuraSkillInstance noCost = new TensuraSkillInstance((ManasSkill)reaperSkill);
                noCost.getOrCreateTag().putBoolean("NoMagiculeCost", true);
                if (SkillAPI.getSkillsFrom((Entity)living).learnSkill((ManasSkillInstance)noCost)) {
                    living.sendSystemMessage(Component.literal("The Underworld Beckons you to embrace souls of the damned!").withStyle(ChatFormatting.BLACK));
                }
            } else {
                return;
            }

        }

        if (instance.isToggled()) {
            double range = 10;
            if (living.tickCount % 10 != 0) return;

            Level level = living.level;
            if (level.isClientSide()) return;

            List<LivingEntity> nearby = level.getEntitiesOfClass(
                    LivingEntity.class,
                    living.getBoundingBox().inflate(range),
                    e -> e != living
            );

            for (LivingEntity target : nearby) {
                target.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN,
                        40, // 2 seconds
                        2,
                        false, true, true
                ));

                target.addEffect(new MobEffectInstance(
                        MobEffects.WITHER,
                        40,
                        1,
                        false, true, true
                ));

                if (level.random.nextFloat() < 0.05f) {
                    target.hurt(TensuraDamageSources.corrosion(target), 10);
                }
            }

            if (level instanceof ServerLevel server) {
                server.sendParticles(
                        ParticleTypes.SMOKE,
                        living.getX(),
                        living.getY() + 1,
                        living.getZ(),
                        12,
                        0.5, 0.4, 0.5,
                        0.01
                );
            }
        }
    }

    @Override
    public int modes() {
        return 2;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() == 1 && entity instanceof Player player) {
            TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                int soulPoints = cap.getSoulPoints();
                if (soulPoints >= 1000000) {
                    cap.setSoulPoints(soulPoints - 1000000);
                    SkillHelper.gainMaxMP(entity, (double) 10000);
                    instance.setCoolDown(60);
                    this.addMasteryPoint(instance, entity);
                }
            });
        }
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() == 2) {

            if (heldTicks % 20 == 0) {
                entity.getLevel().playSound(null, entity.blockPosition(), SoundEvents.BEEHIVE_DRIP, SoundSource.PLAYERS, 1.0F, 1.0F);
                entity.getLevel().playSound(null, entity.blockPosition(), SoundEvents.BEEHIVE_DRIP, SoundSource.PLAYERS, 1.0F, 1.0F);
                entity.getLevel().playSound(null, entity.blockPosition(), SoundEvents.BEEHIVE_DRIP, SoundSource.PLAYERS, 1.0F, 1.0F);
                entity.getLevel().playSound(null, entity.blockPosition(), SoundEvents.BEEHIVE_DRIP, SoundSource.PLAYERS, 1.0F, 1.0F);
                entity.getLevel().playSound(null, entity.blockPosition(), SoundEvents.BEEHIVE_DRIP, SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            if (heldTicks % 2 == 0) {
                TensuraNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                        new RequestFxSpawningPacket(new ResourceLocation("tensura:blizzard"), entity.getId(), 0.0, 1.0, 0.0, true));
            }


            List<LivingEntity> list = entity.getLevel().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(15.0),
                    (living) -> !living.is(entity) && living.isAlive() && !living.isAlliedTo(entity));

            if (!list.isEmpty()) {
                double scale = instance.getTag() == null ? 0.0 : instance.getTag().getDouble("scale");
                double multiplier = scale == 0.0 ? 1.0 : Math.min(scale, 1.0);
                double ownerEP = TensuraEPCapability.getEP(entity) * multiplier;

                for (LivingEntity target : list) {
                    if (target instanceof Player player && player.getAbilities().invulnerable) continue;

                    double targetEP = TensuraEPCapability.getEP(target);
                    double difference = ownerEP / targetEP;

                    if (difference > 2.0) {
                        int fearLevel = (int) (difference * 0.5 - 1.0);
                        fearLevel = Math.min(fearLevel, TensuraConfig.INSTANCE.mobEffectConfig.maxFear.get());
                        SkillHelper.checkThenAddEffectSource(target, entity, (MobEffect) TensuraMobEffects.FEAR.get(), 200, fearLevel);
                        float damage = instance.isMastered(entity) ? 300 : 100;
                        entity.hurt(DamageSource.FREEZE, damage);
                        entity.hurt(TensuraDamageSources.CORROSION, damage);
                        HakiSkill.hakiPush(target, entity, fearLevel);
                    }
                }

            }
        }
        return true;
    }
}
