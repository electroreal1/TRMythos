package com.github.mythos.mythos.ability.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class CursedBloodline extends Skill {

    public CursedBloodline(SkillType unique) {
        super(SkillType.UNIQUE);
    }

    private boolean isLanre = true;
    private boolean locked = false;
    private int lockTicks = 0;
    private int radianceTicks = 0;

    @Override
    public int modes() {
        return 2;
    }

    @Override
    public int getMaxMastery() {
        return 6000;
    }

    @Override
    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    @Override
    public Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.translatable("trmythos.skill.cursed_bloodline.soul_release");
            case 2 -> Component.translatable("trmythos.skill.cursed_bloodline.divided_radiance");
            default -> Component.empty();
        };
    }


    private void toggleSoul(ServerPlayer player) {
        if (locked) return;
        isLanre = !isLanre;
        locked = true;
        lockTicks = 100;
        sendSoulMessage(player);
    }

    private void lockSoul(int ticks) {
        locked = true;
        lockTicks = ticks;
    }

    private void sendSoulMessage(ServerPlayer player) {
        String name = isLanre ? "Lanre" : "Haliax";
        player.sendSystemMessage(Component.literal("ยง7Your soul shifts, ยงb" + name + "ยง7 takes control."));
    }

    private void spawnSoulParticles(ServerPlayer player) {
        if (!(player.level instanceof ServerLevel server)) return;

        for (int i = 0; i < 5; i++) {
            double dx = player.getX() + (player.getRandom().nextDouble() - 0.5);
            double dy = player.getY() + 1.0;
            double dz = player.getZ() + (player.getRandom().nextDouble() - 0.5);

            server.sendParticles(
                    isLanre ? ParticleTypes.SOUL : ParticleTypes.SCULK_SOUL,
                    dx, dy, dz, 1, 0, 0, 0, 0.01
            );
        }
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof ServerPlayer player)) return;

        if (lockTicks > 0) {
            lockTicks--;
            if (lockTicks <= 0) locked = false;
        }

        if (!locked && player.getHealth() < player.getMaxHealth() * 0.25f) {
            toggleSoul(player);
        }

        if (!locked && player.getRandom().nextInt(5000) == 0) {
            toggleSoul(player);
        }

        spawnSoulParticles(player);
    }


    private void onPressed(ServerPlayer player, TensuraSkillInstance instance) {
        Level level = player.level;
        double radius = 8.0D;

        if (instance.getMode() == 1) {
            if (isLanre) {
                List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius),
                        e -> e.isAlive() && !e.isSpectator() && !e.is(player) && !player.isAlliedTo(e));
                for (LivingEntity target : targets) {
                    Vec3 knockback = target.position().subtract(player.position()).normalize().scale(1.5);
                    target.push(knockback.x, 0.5, knockback.z);
                    target.hurt(DamageSource.playerAttack(player), 4.0F);
                }
                player.removeAllEffects();
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1, false, false, false));
                level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.4F);
            } else {
                List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius),
                        e -> e.isAlive() && !e.isSpectator());
                for (LivingEntity target : targets) {
                    target.hurt(DamageSource.MAGIC, 6.0F);
                }
                player.hurt(DamageSource.MAGIC, 2.0F);
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 1, false, false, false));
                level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0F, 0.6F);
            }
        } else if (instance.getMode() == 2) {
            AttributeModifier lanreRegen = new AttributeModifier(UUID.fromString("2a9435be-b33f-4bc3-9f40-32dfe50f771e"), "lanre_regen", 0.05, AttributeModifier.Operation.ADDITION);
            AttributeModifier haliaxDamage = new AttributeModifier(UUID.fromString("f884fb27-1786-476e-a1a2-4ef6db6e8d88"), "haliax_damage", 0.2, AttributeModifier.Operation.MULTIPLY_BASE);

            if (isLanre) {
                List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius),
                        e -> e.isAlive() && !e.isSpectator() && !e.is(player));
                for (LivingEntity target : nearby) {
                    if (player.isAlliedTo(target)) {
                        target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1, true, true));
                        target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0, true, true));
                    } else {
                        target.hurt(DamageSource.playerAttack(player), 3.0F);
                    }
                }
                if (!player.getAttribute(Attributes.LUCK).hasModifier(lanreRegen))
                    player.getAttribute(Attributes.LUCK).addTransientModifier(lanreRegen);
                player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1, false, false, false));
            } else {
                if (!player.getAttribute(Attributes.ATTACK_DAMAGE).hasModifier(haliaxDamage))
                    player.getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(haliaxDamage);

                List<LivingEntity> enemies = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius),
                        e -> e.isAlive() && !e.isSpectator() && !e.is(player) && !player.isAlliedTo(e));
                for (LivingEntity target : enemies) {
                    SkillHelper.checkThenAddEffectSource(target, player,
                            (MobEffect) TensuraMobEffects.CORROSION.get(), 100, 1, false, false, false, true);
                }

                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0, false, false, false));
            }

            instance.setCoolDown(1200);
        }
    }

}







