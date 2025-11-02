package com.github.mythos.mythos.ability.skill.unique;

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
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
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
        return entity.hasEffect((MobEffect) MythosMobEffects.CHILD_OF_THE_PLANE.get());
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
            entity.removeEffect((MobEffect) MythosMobEffects.CHILD_OF_THE_PLANE.get());
        }
    }

    public int modes() {
        return 1;
    }

    public Component getModeName(int mode) {
        return Component.translatable("trmythos.skill.child_of_the_plane.mode");
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!this.failedToActivate(entity, (MobEffect) MythosMobEffects.CHILD_OF_THE_PLANE.get())) {
            if (!entity.hasEffect((MobEffect)MythosMobEffects.CHILD_OF_THE_PLANE.get())) {
                if (SkillHelper.outOfMagicule(entity, instance)) {
                    return;
                }

                this.addMasteryPoint(instance, entity);
                instance.setCoolDown(1200);
                entity.setHealth(entity.getHealth() * 2.0F);
                if (entity instanceof Player) {
                    Player player = (Player)entity;
                    TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                        cap.setMagicule(cap.getMagicule() * 2.0);
                        cap.setAura(cap.getAura() * 2.0);
                        TensuraPlayerCapability.sync(player);
                    });
                }

                entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.0F, 1.0F);

                if (entity instanceof Player player) {
                    int amplifier = player.getServer().getPlayerList().getPlayerCount();

                    entity.addEffect(new MobEffectInstance(
                            (MobEffect) MythosMobEffects.CHILD_OF_THE_PLANE.get(),
                            this.isMastered(instance, entity) ? 7200 : 3600,
                            amplifier,
                            false,
                            false,
                            false
                    ));
                }

                TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.POOF, 3.0);
                TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.EXPLOSION, 3.0);
                TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions) TensuraParticles.SOLAR_FLASH.get(), 2.0);
                TensuraParticleHelper.spawnServerParticles(entity.level, (ParticleOptions)TensuraParticles.YELLOW_LIGHTNING_SPARK.get(), entity.getX(), entity.getY(), entity.getZ(), 55, 0.08, 0.08, 0.08, 0.5, true);
            } else {
                entity.removeEffect((MobEffect)MythosMobEffects.CHILD_OF_THE_PLANE.get());
                entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
            }

        }
    }

    public static float getChildOfThePlaneBoost(Player player, boolean magicule, boolean majin) {
        TensuraSkill skill = (TensuraSkill) Skills.CHILD_OF_THE_PLANE.get();

        Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(skill);
        if (optional.isEmpty()) {
            return 0.0F;
        }

        else if (majin) {
            return magicule ? 0.08F : 0.07F;
        } else {
            return magicule ? 0.07F : 0.08F;
        }

    }
}
