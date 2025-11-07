package com.github.mythos.mythos.ability.mythos.skill.extra;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.entity.magic.projectile.LightningLanceProjectile;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class ThunderRainSkill extends Skill {
    public ThunderRainSkill(SkillType type) {
        super(SkillType.EXTRA);
    }

    @Override
    public int modes() {
        return 2;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    public Component getModeName(int mode) {
        MutableComponent var10000;
        switch (mode) {
            case 1:
                var10000 = Component.translatable("trmythos.skill.thunder_rain.rain");
                break;
            case 2:
                var10000 = Component.translatable("trmythos.skill.thunder_rain.coat");
                break;
            default:
                var10000 = Component.empty();
        }
        return var10000;
    }


    public boolean meetEPRequirement(Player player, double newEP) {
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, (ManasSkill) ExtraSkills.LIGHTNING_MANIPULATION.get());
    }

    @Override
    public double getObtainingEpCost() {
        return 1000;
    }

    private void spawnThunderRain(ManasSkillInstance instance, LivingEntity entity, Level level) {
        if (instance.getMode() == 2 && !SkillHelper.outOfMagicule(entity, instance)) {
            entity.swing(InteractionHand.MAIN_HAND, true);
            addMasteryPoint(instance, entity);
            level = entity.getLevel();
            int distance = instance.isMastered(entity) ? 15 : 5;
            LivingEntity target = SkillHelper.getTargetingEntity(entity, distance, false, true);
            Vec3 pos;
            if (target != null) {
                pos = target.position();
            } else {
                BlockHitResult result = SkillHelper.getPlayerPOVHitResult(entity.getLevel(), entity, ClipContext.Fluid.NONE, distance);
                pos = result.getLocation().add(0.0D, 0.5D, 0.0D);
                if (instance.isMastered(entity)) {
                    spawnLightningSpears(instance, entity, pos, 10, 2.0D);
                    spawnLightningSpears(instance, entity, pos, 10, 4.0D);
                } else {
                    spawnLightningSpears(instance, entity, pos, 10, 3.0D);
                }
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }
    public void onPressed(ManasSkillInstance instance, LivingEntity entity, Level level) {
        if (instance.getMode() == 1) {
            entity.swing(InteractionHand.MAIN_HAND, true);
            this.addMasteryPoint(instance, entity);
            level = entity.getLevel();
            int distance = instance.isMastered(entity) ? 30 : 20;
            Entity target = SkillHelper.getTargetingEntity(entity, (double)distance, false, true);
            Vec3 pos;
            if (target != null) {
                pos = target.getEyePosition();
            } else {
                BlockHitResult result = SkillHelper.getPlayerPOVHitResult(entity.level, entity, ClipContext.Fluid.NONE, (double)distance);
                pos = result.getLocation().add(0.0, 0.5, 0.0);
            }

            if (instance.isMastered(entity)) {
                this.spawnLightningSpears(instance, entity, pos, 10, 2.0);
                this.spawnLightningSpears(instance, entity, pos, 10, 4.0);
            } else {
                this.spawnLightningSpears(instance, entity, pos, 10, 3.0);
            }

            level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
            instance.setCoolDown(15);
        } else if (instance.getMode() == 2) {
            if (!entity.hasEffect((MobEffect) MythosMobEffects.LIGHTNING_COAT.get())) {
                if (SkillHelper.outOfMagicule(entity, instance)) {
                    return;
                }

                entity.addEffect(new MobEffectInstance((MobEffect) MythosMobEffects.LIGHTNING_COAT.get(), 2400, 0, false, false, false));
                entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.PLAYERS, 1.0F, 1.0F);
            } else {
                entity.removeEffect((MobEffect)MythosMobEffects.LIGHTNING_COAT.get());
                entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }
    private void spawnLightningSpears(ManasSkillInstance instance, LivingEntity entity, Vec3 pos, int spearAmount, double distance) {
        Level level = entity.getLevel();

        for (int i = 0; i < spearAmount; i++) {
            double angle = Math.toRadians(360.0D / spearAmount * i);
            double xOffset = distance * Math.cos(angle);
            double zOffset = distance * Math.sin(angle);

            Vec3 spearPos = pos.add(xOffset, 1.5D, zOffset);

            LightningLanceProjectile spear = new LightningLanceProjectile(level, entity);
            spear.setSpeed(2.5F);
            spear.setPos(spearPos.x, spearPos.y, spearPos.z);

            Vec3 direction = pos.subtract(spearPos).normalize();
            spear.shoot(direction.x, direction.y, direction.z, 2.5F, 0.0F);

            spear.setLife(60);
            spear.setDamage(200.0F);
            spear.setMpCost(magiculeCost(entity, instance) / spearAmount);
            spear.setSkill(instance);

            level.addParticle(ParticleTypes.ELECTRIC_SPARK, spearPos.x, spearPos.y, spearPos.z, 0, 0.05, 0);
            level.addParticle(ParticleTypes.SMOKE, spearPos.x, spearPos.y - 0.5, spearPos.z, 0, 0.02, 0);


            level.addFreshEntity(spear);
        }
    }

}

