package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PretenderKingSkill extends Skill {
    public PretenderKingSkill(SkillType type) {
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
        if (!instance.isToggled()) return;
        if (!(living instanceof Player player)) return;

        var data = living.getPersistentData();

        if (!data.getBoolean("ars_universum_active")) return;

        int time = data.getInt("ars_universum_timer") - 1;
        data.putInt("ars_universum_timer", time);

        if (time <= 0) {
            data.remove("ars_universum_active");

            Objects.requireNonNull(living.getAttribute(Attributes.MOVEMENT_SPEED))
                        .removeModifier(UUID.fromString("b2f7a2c4-1e34-4f92-a812-arsmove0001"));
            Objects.requireNonNull(living.getAttribute(Attributes.ATTACK_SPEED))
                        .removeModifier(UUID.fromString("a1d8f9e1-4b71-42b4-arsatk0002"));
        }

        TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
            double maxMP = player.getAttributeValue((Attribute) TensuraAttributeRegistry.MAX_MAGICULE.get());
            double regenRate = instance.isMastered(living) ? maxMP / 10 : maxMP / 20;
            cap.setMagicule(Math.min(cap.getMagicule() + regenRate, maxMP));
        });
        TensuraPlayerCapability.sync(player);
        if (player.hasEffect(TensuraMobEffects.PRESENCE_SENSE.get())) {
            return;
        } else {
            player.addEffect(new MobEffectInstance(TensuraMobEffects.PRESENCE_SENSE.get(), 20, 2, false, false, false));
        }
    }

    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        if (!event.isCanceled()) {
            DamageSource damageSource = event.getSource();
            if (!damageSource.isBypassInvul() && !damageSource.isMagic()) {
                Entity var5 = damageSource.getDirectEntity();
                if (var5 instanceof LivingEntity) {
                    LivingEntity entity = (LivingEntity)var5;
                    double dodgeChance = 0.1;

                    if (!(entity.getRandom().nextDouble() >= dodgeChance)) {
                        entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 2.0F, 1.0F);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @Override
    public void onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;

        var data = attacker.getPersistentData();
        if (!data.getBoolean("ars_universum_active")) return;

        double mp = entity.getAttributeValue((Attribute) TensuraAttributeRegistry.MAX_MAGICULE.get());

        double bonus = mp * 0.01f;
        if (bonus > 200) {
            bonus = 200;
        }
        event.setAmount((float) (event.getAmount() + bonus));

        data.remove("ars_universum_active");

        attacker.getAttribute(Attributes.MOVEMENT_SPEED)
                .removeModifier(UUID.fromString("b5b4cc2b-f1cb-4711-ae6a-1dca224583b4"));
        attacker.getAttribute(Attributes.ATTACK_SPEED)
                .removeModifier(UUID.fromString("d946a0b4-d771-4134-972f-0e27ef929daa"));

        attacker.level.playSound(null, attacker.blockPosition(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.PLAYERS, 1.0F, 0.9F);
    }

    @Override
    public int modes() {
        return 3;
    }

    public Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.translatable("trmythos.skill.pretender_king.dictatum");
            case 2 -> Component.translatable("trmythos.skill.pretender_king.univeresum");
            case 3 -> Component.translatable("trmythos.skill.pretender_king.decree");
            default -> Component.empty();
        };
    }

    public double magiculeCost(LivingEntity entity, ManasSkillInstance instance) {
        double var10000 = switch (instance.getMode()) {
            case 1 -> 100;
            case 2 -> 10000;
            case 3 -> 100000;
            default -> 0.0;
        };

        return var10000;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 3 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 3) ? 1 : (instance.getMode() + 1);
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() == 1) {

            if (SkillHelper.outOfMagicule(entity, instance)) return;
            int radius = 10;
            int duration = 6;
            int maxStacks = 3;
            Level level = entity.level;

            level.playSound(null, entity.blockPosition(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.PLAYERS, 1.0F, 0.8F);

            if (level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 360; i += 10) {
                    double angle = Math.toRadians(i);
                    double x = entity.getX() + Math.cos(angle) * radius;
                    double z = entity.getZ() + Math.sin(angle) * radius;

                    serverLevel.sendParticles(ParticleTypes.ENCHANT, x, entity.getY() + 0.2, z, 2,
                            0.05, 0.1, 0.05, 0.0);
                }
            }


            List<LivingEntity> targets = entity.level.getEntitiesOfClass(
                    LivingEntity.class,
                    entity.getBoundingBox().inflate(radius),
                    e -> e != entity && e.isAlive()
            );

            for (LivingEntity target : targets) {
                MobEffectInstance slow = target.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
                int slowAmp = slow == null ? 0 : slow.getAmplifier() + 1;
                slowAmp = Math.min(slowAmp, maxStacks - 1);

                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, slowAmp, false, false, false));

                MobEffectInstance fatigue = target.getEffect(MobEffects.DIG_SLOWDOWN);
                int fatigueAmp = fatigue == null ? 0 : fatigue.getAmplifier() + 1;
                fatigueAmp = Math.min(fatigueAmp, maxStacks - 1);

                target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration, fatigueAmp, false, false, false));
                instance.setCoolDown(instance.isMastered(entity) ? 5 : 10);
            }
        } else if (instance.getMode() == 2) {
            if (SkillHelper.outOfMagicule(entity, instance)) return;

            int duration = 5;
            UUID MOVE_UUID = UUID.fromString("b5b4cc2b-f1cb-4711-ae6a-1dca224583b4");
            UUID ATK_UUID = UUID.fromString("d946a0b4-d771-4134-972f-0e27ef929daa");

            Objects.requireNonNull(entity.getAttribute(Attributes.MOVEMENT_SPEED))
                    .addTransientModifier(new AttributeModifier(MOVE_UUID, "Ars Universum Speed", 0.20,
                            AttributeModifier.Operation.MULTIPLY_TOTAL));

            Objects.requireNonNull(entity.getAttribute(Attributes.ATTACK_SPEED))
                    .addTransientModifier(new AttributeModifier(ATK_UUID, "Ars Universum Attack Speed", 0.15,
                            AttributeModifier.Operation.MULTIPLY_TOTAL));

            entity.getPersistentData().putBoolean("ars_universum_active", true);
            entity.getPersistentData().putInt("ars_universum_timer", duration);

            entity.level.playSound(null, entity.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.2F);
        }
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (!instance.isMastered(entity)) {
            entity.sendSystemMessage(Component.literal("You are too weak to use this move."));
        } else {
            Level level = entity.level;
            if (level.isClientSide) return true;
            int radius = 10;
            int damage = 400;

            if (heldTicks % 20 != 0) return true;
            if (SkillHelper.outOfMagicule(entity, instance)) return false;

            List<LivingEntity> targets = level.getEntitiesOfClass(
                    LivingEntity.class,
                    entity.getBoundingBox().inflate(radius),
                    e -> e != entity && e.isAlive()
            );

            DamageSource soulDamage = TensuraDamageSources.soulScatter(entity);

            for (LivingEntity target : targets) {
                target.hurt(soulDamage, damage);
            }

            level.playSound(null, entity.blockPosition(), SoundEvents.WITHER_AMBIENT, SoundSource.PLAYERS, 0.8F, 0.6F);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, entity.getX(), entity.getY() + 1.0, entity.getZ(), 60,
                        radius * 0.3, 0.5, radius * 0.3, 0.01);
            }
        }
        return true;
    }
}
