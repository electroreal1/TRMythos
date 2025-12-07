package com.github.mythos.mythos.ability.mythos.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DominateSkill extends Skill {
    public DominateSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    public void onLearnSkill(ManasSkillInstance instance, LivingEntity living, UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            if (living instanceof Player) {
                Player player = (Player)living;
                TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                    cap.setBlessed(true);
                });
                TensuraPlayerCapability.sync(player);
            }

        }
    }

    public double getObtainingEpCost() {
        return 100000.0;
    }

    public double learningCost() {
        return 2000.0;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 4 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 4) ? 1 : (instance.getMode() + 1);
    }

    @Override
    public int modes() {
        return 4;
    }

    public Component getModeName(int mode) {
        MutableComponent name = switch (mode) {
            case 1 -> Component.literal("Dominate: Push");
            case 2 -> Component.literal("Dominate: Down");
            case 3 -> Component.literal("Dominate: Petrify");
            case 4 -> Component.literal("Dominate: Die");
            default -> Component.empty();
        };
        return name;
    }

    @Override
    public double magiculeCost(LivingEntity entity, ManasSkillInstance instance) {
        return switch (instance.getMode()) {
            case 1, 2, 3, 4 -> 20000.0;
            default -> 0.0;
        };
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (SkillHelper.outOfMagicule(entity, instance)) return;
        if (instance.getMode() == 1) {
            if (!(entity instanceof Player player)) return;

            Level level = player.level;
            if (level.isClientSide()) return;

            double range = 30;
            double pushStrength = 10;
            double pullStrength = 10;
            double verticalBoost = 1.6;

            boolean pulling = player.isShiftKeyDown();

            List<Entity> targets = level.getEntities(player, player.getBoundingBox().inflate(range), e -> e instanceof LivingEntity && e != player);

            for (Entity target : targets) {
                Vec3 dir = target.position().subtract(player.position());
                double dist = Math.max(dir.length(), 0.001);

                Vec3 norm = dir.normalize();

                if (pulling) norm = norm.scale(-1);

                double attenuation = Math.max(0.25, 1.0 - (dist / range));
                double appliedForce = attenuation * (pulling ? pullStrength : pushStrength);

                Vec3 finalVelocity = norm.scale(appliedForce).add(0, verticalBoost, 0);

                target.setDeltaMovement(finalVelocity);
                target.hurtMarked = true;

                target.hurt(TensuraDamageSources.elementalAttack(TensuraDamageSources.GRAVITY_ATTACK, player, false), 100);


            }
            instance.setCoolDown(10);

//            ((ServerLevel) level).sendParticles( ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(), 25, 0.6, 0.3, 0.6, 0.02);

            level.playSound(null, player.blockPosition(), pulling ? SoundEvents.ENDERMAN_TELEPORT : SoundEvents.ANVIL_LAND,
                    SoundSource.PLAYERS, 1.0F, pulling ? 0.6F : 1.3F);
        }

        if (instance.getMode() == 2) {
            if (!(entity instanceof Player player)) return;

            Level level = player.level;
            if (level.isClientSide()) return;

            double range = 30;
            double downForce = 10;
            double upForce = 10;
            double radialSlowFactor = 2.8;

            boolean lifting = player.isShiftKeyDown();

            List<Entity> entities = level.getEntities(player, player.getBoundingBox().inflate(range), e -> e instanceof LivingEntity && e != player);

            for (Entity target : entities) {
                Vec3 tPos = target.position();
                double dist = Math.max(0.05, player.position().distanceTo(tPos));

                double attenuation = Math.max(0.25, 1.0 - (dist / range));

                double verticalForce = attenuation * (lifting ? upForce : downForce);

                Vec3 motion = target.getDeltaMovement();

                motion = new Vec3(motion.x * radialSlowFactor, 0, motion.z * radialSlowFactor);

                motion = motion.add(0, verticalForce, 0);

                target.setDeltaMovement(motion);
                target.hurtMarked = true;

                target.hurt(TensuraDamageSources.elementalAttack(TensuraDamageSources.GRAVITY_ATTACK, player, false), 100);

            }

//            if (level instanceof ServerLevel server) {
//
//                server.sendParticles(
//                        lifting ? ParticleTypes.ENCHANT : ParticleTypes.ASH,
//                        player.getX(), player.getY(), player.getZ(),
//                        35,
//                        0.7, 0.4, 0.7,
//                        lifting ? 0.12 : 0.02
//                );
//            }

            level.playSound(
                    null,
                    player.blockPosition(),
                    lifting ? SoundEvents.BEACON_ACTIVATE : SoundEvents.ANVIL_LAND,
                    SoundSource.PLAYERS,
                    1.0F,
                    lifting ? 1.4F : 0.8F
            );
        }

        if (instance.getMode() == 3) {
            if (!(entity instanceof Player player)) return;

            Level level = player.level;
            if (level.isClientSide()) return;

            double range = 7.5;
            int petrifyDuration = 100;

            instance.setCoolDown(20);

            List<LivingEntity> targets = level.getEntitiesOfClass(
                    LivingEntity.class,
                    player.getBoundingBox().inflate(range)
            );

            for (LivingEntity target : targets) {

                target.hurt(TensuraDamageSources.elementalAttack(TensuraDamageSources.GRAVITY_ATTACK, player, false), 150);

                target.addEffect(new MobEffectInstance(
                        TensuraMobEffects.PETRIFICATION.get(),
                        petrifyDuration,
                        0,
                        false,
                        false,
                        false
                ));
            }
        }

        if (instance.isMastered(entity) ) {
            if (instance.getMode() == 4) {
                if (!(entity instanceof Player player)) return;

                Level level = player.level;
                if (level.isClientSide()) return;
                double radius = 20;

                double userEP = TensuraPlayerCapability.getBaseEP(player);

                List<LivingEntity> list = entity.level.getEntitiesOfClass(
                        LivingEntity.class,
                        entity.getBoundingBox().inflate(radius),
                        (t) -> t != null && t != entity
                );

                for (LivingEntity target : list) {
                    double targetEP = TensuraEPCapability.getEP(target);

                    if (targetEP < userEP * 0.9) {
                        target.hurt(TensuraDamageSources.elementalAttack(TensuraDamageSources.GRAVITY_ATTACK, player, false), 1000);
                    }
                }
            }

            } else {
            if (!(entity instanceof Player player)) return;

            Level level = player.level;
            if (level.isClientSide()) return;
            entity.sendSystemMessage(Component.literal("You are too weak to utilize this power."));
        }
    }

}
