package com.github.mythos.mythos.ability.mythos.skill.extra;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.entity.magic.projectile.LightningLanceProjectile;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ThunderRainSkill extends Skill {
    public ThunderRainSkill() {
        super(SkillType.EXTRA);
    }

    @Override
    public int modes() {
        return 2;
    }

    public int nextMode(@NotNull LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    public @NotNull Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.translatable("trmythos.skill.thunder_rain.rain");
            case 2 -> Component.translatable("trmythos.skill.thunder_rain.coat");
            default -> Component.empty();
        };
    }

    @Override
    public void onTick(@NotNull ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.hasEffect(MythosMobEffects.LIGHTNING_COAT.get())) {
            if (SkillHelper.outOfMagicule(entity, instance)) {
                return;
            }

            entity.addEffect(new MobEffectInstance(MythosMobEffects.LIGHTNING_COAT.get(), 2400, 0, false, false, false));
            entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    public boolean meetEPRequirement(@NotNull Player player, double newEP) {
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, ExtraSkills.LIGHTNING_MANIPULATION.get());
    }

    @Override
    public double getObtainingEpCost() {
        return 1000;
    }

    private void spawnThunderRain(ManasSkillInstance instance, LivingEntity entity, Vec3 pos, double distance) {
        int arrowRot = 360 / 10;

        for(int i = 0; i < 10; ++i) {
            Vec3 arrowPos = entity.getEyePosition().add((new Vec3(0.0, distance, 0.0))
                    .zRot(((float)(arrowRot * i) - (float)arrowRot / 2.0F) * 0.017453292F)
                    .xRot(-entity.getXRot() * 0.017453292F)
                    .yRot(-entity.getYRot() * 0.017453292F));
            LightningLanceProjectile arrow = new LightningLanceProjectile(entity.getLevel(), entity);
            arrow.setSpeed(2.0F);
            arrow.setPos(arrowPos);
            arrow.shootFromRot(pos.subtract(arrowPos).normalize());
            arrow.setLife(50);
            arrow.setDamage(50);
            arrow.setMpCost(this.magiculeCost(entity, instance) / (double) 10);
            arrow.setSpiritAttack(false);
            arrow.setSkill(instance);
            entity.getLevel().addFreshEntity(arrow);
        }

    }
    public void onPressed(ManasSkillInstance instance, @NotNull LivingEntity entity) {
        if (instance.getMode() == 1) {
            entity.swing(InteractionHand.MAIN_HAND, true);
            this.addMasteryPoint(instance, entity);
            Level level = entity.getLevel();
            int distance = instance.isMastered(entity) ? 30 : 20;
            Entity target = SkillHelper.getTargetingEntity(entity, distance, false, true);
            Vec3 pos;
            if (target != null) {
                pos = target.getEyePosition();
            } else {
                BlockHitResult result = SkillHelper.getPlayerPOVHitResult(entity.level, entity, ClipContext.Fluid.NONE, distance);
                pos = result.getLocation().add(0.0, 0.5, 0.0);
            }

            if (instance.isMastered(entity)) {
                this.spawnThunderRain(instance, entity, pos, 2.0);
                this.spawnThunderRain(instance, entity, pos, 4.0);
            } else {
                this.spawnThunderRain(instance, entity, pos, 3.0);
            }

            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
            instance.setCoolDown(15);
        } else if (instance.getMode() == 2) {
            if (!entity.hasEffect(MythosMobEffects.LIGHTNING_COAT.get())) {
                if (SkillHelper.outOfMagicule(entity, instance)) {
                    return;
                }

                entity.addEffect(new MobEffectInstance(MythosMobEffects.LIGHTNING_COAT.get(), 2400, 0, false, false, false));
                entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.PLAYERS, 1.0F, 1.0F);
            } else {
                entity.removeEffect(MythosMobEffects.LIGHTNING_COAT.get());
                entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }

}

