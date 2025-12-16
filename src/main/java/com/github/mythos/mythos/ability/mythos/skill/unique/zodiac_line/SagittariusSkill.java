package com.github.mythos.mythos.ability.mythos.skill.unique.zodiac_line;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

public class SagittariusSkill extends Skill {
    public SagittariusSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public double getObtainingEpCost() {
        return 20000;
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent event) {
        if (!instance.isToggled()) return;
        DamageSource source = event.getSource();

        if (!source.isProjectile()) return;

        Entity direct = source.getDirectEntity();
        if (!(direct instanceof Projectile projectile)) return;

        Entity owner = projectile.getOwner();
        if (!(owner instanceof Player player)) return;

        event.setAmount(event.getAmount() * 3.0F);
    }

    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        if (!event.isCanceled()) {
            LivingEntity entity = event.getEntity();
            boolean futureVision = entity.hasEffect((MobEffect) TensuraMobEffects.FUTURE_VISION.get());
            if (futureVision) {
                DamageSource damageSource = event.getSource();
                if (!damageSource.isBypassInvul()) {
                    if (damageSource.getDirectEntity() != null && damageSource.getDirectEntity() == damageSource.getEntity()) {
                        entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 2.0F, 1.0F);
                        event.setCanceled(true);
                        if (SkillUtils.canNegateDodge(entity, damageSource)) {
                            event.setCanceled(false);
                        }

                    }
                }
            }
        }
    }

    public void onProjectileHit(ManasSkillInstance instance, LivingEntity entity, ProjectileImpactEvent event) {
        boolean futureVision = entity.hasEffect((MobEffect)TensuraMobEffects.FUTURE_VISION.get());
        if (futureVision) {
            if (!SkillUtils.isProjectileAlwaysHit(event.getProjectile())) {
                entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 2.0F, 1.0F);
                event.setCanceled(true);
            }
        }
    }

    @Override
    public int modes() {
        return 2;
    }

    public @NotNull Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.translatable("trmythos.skill.sagittarius.future");
            case 2 -> Component.translatable("trmythos.skill.sagittarius.heal");
            default -> Component.empty();
        };
    }

    public int nextMode(@NotNull LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (instance.isMastered(entity)) {
            return instance.getMode() == 2 ? 1 : instance.getMode() + 1;
        } else {
            return instance.getMode() == 1 ? 2 : 1;
        }
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!(instance.getMode() == 2)) {
            if (entity.hasEffect((MobEffect) TensuraMobEffects.FUTURE_VISION.get())) {
                entity.removeEffect((MobEffect) TensuraMobEffects.FUTURE_VISION.get());
                entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                instance.setCoolDown(10);
            } else {
                int duration = instance.isMastered(entity) ? 400 : 200;
                instance.setCoolDown(instance.isMastered(entity) ? 30 : 20);
                entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.FUTURE_VISION.get(), duration, 0, false, false, false));
                entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        } else {
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
                lackedMana = SkillHelper.outOfMagiculeStillConsume(entity, (double) ((int) (healingHP * (float) cost)));
                if (lackedMana > 0.0) {
                    healingHP = (float) ((double) healingHP - lackedMana / (double) cost);
                }

                target.heal(healingHP);
                if (this.isMastered(instance, entity)) {
                    healingSpiritual = TensuraEPCapability.getSpiritualHealth(target);
                    lackedSpiritual = target.getAttributeValue((Attribute) TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get()) - healingSpiritual;
                    lackedMP = SkillHelper.outOfMagiculeStillConsume(entity, (double) ((int) (lackedSpiritual * 60.0)));
                    if (lackedMP > 0.0) {
                        lackedSpiritual -= lackedMP / 60.0;
                    }

                    TensuraEPCapability.setSpiritualHealth(target, healingSpiritual + lackedSpiritual);
                }

                TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.HEART, 1.0);
                level.playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }
}
