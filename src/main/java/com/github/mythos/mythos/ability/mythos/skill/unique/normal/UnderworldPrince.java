package com.github.mythos.mythos.ability.mythos.skill.unique.normal;


import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.HakiSkill;
import com.github.manasmods.tensura.ability.skill.unique.ReaperSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/underworld_prince.png");
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
    public int getMaxMastery() {
        return 1000;
    }

    private static final Random RANDOM = new Random();

    @Override
    public Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.literal("Soul Feast");
            case 2 -> Component.literal("Undead Essence");
            default -> Component.empty();
        };
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity living) {
        if (living.tickCount % 20 == 0) {
            TensuraEPCapability.getFrom(living).ifPresent(cap -> cap.setChaos(true));
            TensuraEPCapability.sync(living);

            if (instance.isMastered(living) && living.tickCount % 1200 == 0 && RANDOM.nextInt(10) == 1) {
                ReaperSkill reaperSkill = UniqueSkills.REAPER.get();
                TensuraSkillInstance noCost = new TensuraSkillInstance(reaperSkill);
                noCost.getOrCreateTag().putBoolean("NoMagiculeCost", true);
                if (SkillAPI.getSkillsFrom(living).learnSkill(noCost)) {
                    living.sendSystemMessage(Component.literal("The Underworld beckons you to embrace souls of the damned!").withStyle(ChatFormatting.DARK_PURPLE));
                }
            }
        }

        if (instance.isToggled() && living.tickCount % 20 == 0 && !living.level.isClientSide()) {
            List<LivingEntity> nearby = living.level.getEntitiesOfClass(LivingEntity.class, living.getBoundingBox().inflate(10.0),
                    e -> e != living && e.isAlive() && !e.isAlliedTo(living));

            for (LivingEntity target : nearby) {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2, false, false, true));
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1, false, false, true));

                if (living.level.random.nextFloat() < 0.10f) {
                    target.hurt(TensuraDamageSources.corrosion(living), 10.0F);
                }
            }

            if (living.level instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.SMOKE, living.getX(), living.getY() + 1, living.getZ(), 10, 0.5, 0.5, 0.5, 0.02);
                server.sendParticles(ParticleTypes.SNOWFLAKE, living.getX(), living.getY() + 1, living.getZ(), 5, 0.5, 0.5, 0.5, 0.02);
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
                    SkillHelper.gainMaxMP(entity, 10000.0);

                    entity.level.playSound(null, entity.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 0.5F);

                    instance.setCoolDown(20);
                    this.addMasteryPoint(instance, entity);
                }
            });
        }
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() == 2) {
            Level level = entity.level;

            if (heldTicks % 20 == 0) {
                level.playSound(null, entity.blockPosition(), SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 1.0F, 0.8F);
            }

            if (heldTicks % 4 == 0) {
                TensuraNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                        new RequestFxSpawningPacket(new ResourceLocation("tensura:blizzard"), entity.getId(), 0.0, 1.0, 0.0, true));
            }

            if (heldTicks % 10 == 0) {
                List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(15.0),
                        (target) -> target != entity && target.isAlive() && !target.isAlliedTo(entity));

                if (!list.isEmpty()) {
                    double ownerEP = TensuraEPCapability.getEP(entity);
                    float damage = instance.isMastered(entity) ? 300.0F : 100.0F;

                    for (LivingEntity target : list) {
                        if (target instanceof Player player && player.getAbilities().invulnerable) continue;

                        double targetEP = TensuraEPCapability.getEP(target);
                        double difference = ownerEP / Math.max(targetEP, 1.0);

                        if (difference > 2.0) {
                            int fearLevel = (int) Math.min((difference * 0.5 - 1.0), 4);

                            SkillHelper.checkThenAddEffectSource(target, entity, TensuraMobEffects.FEAR.get(), 100, fearLevel);
                            SkillHelper.checkThenAddEffectSource(target, entity, TensuraMobEffects.CHILL.get(), 100, fearLevel);

                            target.hurt(DamageSource.FREEZE, damage);
                            target.hurt(TensuraDamageSources.CORROSION, damage);

                            HakiSkill.hakiPush(target, entity, fearLevel);
                        }
                    }
                    this.addMasteryPoint(instance, entity);
                }
            }
        }
        return true;
    }
}
