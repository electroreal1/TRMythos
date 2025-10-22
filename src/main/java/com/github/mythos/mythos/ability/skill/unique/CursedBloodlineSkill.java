package com.github.mythos.mythos.ability.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.magic.spiritual.darkness.DarknessCannonMagic;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class CursedBloodlineSkill extends Skill {

    public CursedBloodlineSkill() { super(SkillType.UNIQUE); }

    private boolean isLanre = true;
    private boolean locked = false;
    private int lockTicks = 0;

    @Override
    public int modes() {
        return 3;
    }

    @Override
    public int getMaxMastery() {
        return 6000;
    }

    @Override
    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 3 : 1;
    }

    @Override
    public Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.translatable("trmythos.skill.cursed_bloodline.soul_release");
            case 2 -> Component.translatable("trmythos.skill.cursed_bloodline.divided_radiance");
            case 3 -> Component.literal("swap soul");
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
        if (player.level instanceof ServerLevel server) {
            for (int i = 0; i < 5; i++) {
                double dx = player.getX() + (player.getRandom().nextDouble() - 0.5);
                double dy = player.getY() + 1.0;
                double dz = player.getZ() + (player.getRandom().nextDouble() - 0.5);
                server.sendParticles(
                        isLanre ? ParticleTypes.END_ROD : ParticleTypes.SMOKE,
                        dx, dy, dz, 1, 0, 0, 0, 0.01
                );
            }
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

        if (!locked && player.getRandom().nextInt(50) == 0) {
            toggleSoul(player);
        }

        spawnSoulParticles(player);
    }


    private void onPress(ServerPlayer player) {
        if (createDefaultInstance().getMode() == 1) {
        Level level = player.level;
        if (isLanre) {
            TensuraParticleHelper.addServerParticlesAroundSelf(player, (ParticleOptions) TensuraParticles.SOLAR_FLASH.get());
            TensuraParticleHelper.addServerParticlesAroundSelf(player, (ParticleOptions)TensuraParticles.SOLAR_FLASH.get(), 2.0);
            TensuraParticleHelper.addServerParticlesAroundSelf(player, (ParticleOptions)TensuraParticles.SOLAR_FLASH.get(), 4.0);
            TensuraParticleHelper.addServerParticlesAroundSelf(player, (ParticleOptions)TensuraParticles.SOLAR_FLASH.get(), 6.0);
            TensuraParticleHelper.addServerParticlesAroundSelf(player, (ParticleOptions)TensuraParticles.SOLAR_FLASH.get(), 8.0);
            TensuraParticleHelper.addServerParticlesAroundSelf(player, (ParticleOptions)TensuraParticles.SOLAR_FLASH.get(), 10.0);
            TensuraParticleHelper.addServerParticlesAroundSelf(player, (ParticleOptions)TensuraParticles.SOLAR_FLASH.get(), 12.0);
            player.getActiveEffects().stream()
                    .filter(e -> e.getEffect().getCategory() == MobEffectCategory.HARMFUL)
                    .map(MobEffectInstance::getEffect)
                    .toList()
                    .forEach(player::removeEffect);

            AABB area = new AABB(player.blockPosition()).inflate(4);
            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area)) {
                if (target != player) {
                    double dx = target.getX() - player.getX();
                    double dz = target.getZ() - player.getZ();
                    target.knockback(-1.2, dx, dz);
                    SkillHelper.checkThenAddEffectSource(target, player,
                            (MobEffect) TensuraMobEffects.BURDEN.get(), 200, 1, false, false, false, true);
                }
            }

            level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1f, 1.3f);
        } else {
            AABB area = new AABB(player.blockPosition()).inflate(5);
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area)) {
                if (entity != player) {
                    entity.hurt(DamageSource.playerAttack(player), 6f);
                    SkillHelper.checkThenAddEffectSource(entity, player,
                            (MobEffect) TensuraMobEffects.BURDEN.get(), 200, 1, false, false, false, true);
                }
            }

            player.hurt(TensuraDamageSources.elementalAttack("tensura.dark_attack", player, true), 2.0F);
            level.playSound(null, player.blockPosition(), SoundEvents.WITHER_BREAK_BLOCK, SoundSource.PLAYERS, 1f, 0.8f);
        }

        lockSoul(100);
        createDefaultInstance().setCoolDown(1);
        } else if (createDefaultInstance().getMode() == 3) {
            toggleSoul(player);
        }
    }
    private void onHold(ServerPlayer player) {
        if (createDefaultInstance().getMode() == 2) {
            Level level = player.level;
            if (isLanre) {
                AABB area = new AABB(player.blockPosition()).inflate(6);
                for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area)) {
                    if (entity.isAlliedTo(player)) {
                        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
                        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0));
                    } else {
                        entity.hurt(DamageSource.playerAttack(player), 4f);
                        SkillHelper.checkThenAddEffectSource(entity, player,
                                (MobEffect) TensuraMobEffects.BURDEN.get(), 160, 1, false, false, false, true);
                    }
                }

                level.playSound(null, player.blockPosition(), SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 1f, 1.1f);
            } else {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1));
                level.playSound(null, player.blockPosition(), SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 1f, 0.8f);

                SkillHelper.checkThenAddEffectSource(player, player,
                        (MobEffect) TensuraMobEffects.FATAL_POISON.get(), 200, 1, false, false, false, true);
            }

            lockSoul(200);
            createDefaultInstance().setCoolDown(2);
        }
    }
}
