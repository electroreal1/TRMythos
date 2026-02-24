package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.effect.template.Transformation;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import com.mojang.math.Vector3f;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;


public class ChildOfThePlaneSkill extends Skill implements Transformation {
    public ChildOfThePlaneSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    public @Nullable ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/child_of_the_plane.png");
    }

    @Override
    public double getObtainingEpCost() {
        return 100000;
    }

    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return entity.hasEffect(MythosMobEffects.CHILD_OF_THE_PLANE.get());
    }

    @Override
    public int getMaxMastery() {
        return 3000;
    }

    public boolean canIgnoreCoolDown(ManasSkillInstance instance, LivingEntity entity) {
        return instance.getOrCreateTag().getBoolean("ChildOfThePlane");
    }

    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        if (this.canTick(instance, entity)) {
            entity.removeEffect(MythosMobEffects.CHILD_OF_THE_PLANE.get());
        }
    }

    private static final double rotation = 0;

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        Level level = entity.level;
        if (!(level instanceof ServerLevel server)) return;
        RandomSource rand = player.level.random;
        int portals = 10;
        double yBase = 1.5;
        for (int i = 0; i < portals; i++) {
            double angle = i * 2 * Math.PI / portals + rotation;
            double radius = 0.5 + rand.nextDouble() * 0.7;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yBase + Math.sin(rotation * 2 + i) * 0.2;
            double vx = (rand.nextDouble() - 0.5) * 0.05;
            double vy = (rand.nextDouble() - 0.5) * 0.05;
            double vz = (rand.nextDouble() - 0.5) * 0.05;
            server.sendParticles(net.minecraft.core.particles.ParticleTypes.PORTAL, px, py, pz, 1, vx, vy, vz, 0.0);
            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();
            float size = 0.6f + rand.nextFloat() * 0.3f;
            server.sendParticles(new DustParticleOptions(new Vector3f(r, g, b), size), px, py, pz, 1, 0, 0, 0, 0);
        }
    }

    public int modes() {
        return 1;
    }

    public Component getModeName(int mode) {
        return Component.translatable("trmythos.skill.child_of_the_plane.mode");
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!this.failedToActivate(entity, MythosMobEffects.CHILD_OF_THE_PLANE.get())) {
            if (!entity.hasEffect(MythosMobEffects.CHILD_OF_THE_PLANE.get())) {
                if (SkillHelper.outOfMagicule(entity, instance)) {
                    return;
                }

                this.addMasteryPoint(instance, entity);
                instance.setCoolDown(1200);
                entity.setHealth(entity.getHealth() * 2.0F);
                if (entity instanceof Player player) {
                    TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                        cap.setMagicule(cap.getMagicule() * 2.0);
                        cap.setAura(cap.getAura() * 2.0);
                        TensuraPlayerCapability.sync(player);
                    });
                }

                entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.0F, 1.0F);

                if (entity instanceof Player player) {
                    int amplifier = Objects.requireNonNull(player.getServer()).getPlayerList().getPlayerCount();

                    entity.addEffect(new MobEffectInstance(MythosMobEffects.CHILD_OF_THE_PLANE.get(), this.isMastered(instance, entity) ? 7200 : 3600, amplifier / 5, false, false, false));
                }

                TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.POOF, 3.0);
                TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.EXPLOSION, 3.0);
                TensuraParticleHelper.addServerParticlesAroundSelf(entity, TensuraParticles.SOLAR_FLASH.get(), 2.0);
                TensuraParticleHelper.spawnServerParticles(entity.level, TensuraParticles.YELLOW_LIGHTNING_SPARK.get(), entity.getX(), entity.getY(), entity.getZ(), 55, 0.08, 0.08, 0.08, 0.5, true);
            } else {
                entity.removeEffect(MythosMobEffects.CHILD_OF_THE_PLANE.get());
                entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
            }

        }
    }

    public static float getChildOfThePlaneBoost(Player player, boolean magicule, boolean majin) {
        TensuraSkill skill = Skills.CHILD_OF_THE_PLANE.get();

        Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(skill);
        if (optional.isEmpty()) {
            return 0.0F;
        } else if (majin) {
            return magicule ? 0.08F : 0.07F;
        } else {
            return magicule ? 0.07F : 0.08F;
        }

    }
}
