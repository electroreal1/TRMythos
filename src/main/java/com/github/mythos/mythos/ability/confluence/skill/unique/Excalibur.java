package com.github.mythos.mythos.ability.confluence.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.skill.CommonSkills;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.mojang.math.Vector3f;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class Excalibur extends Skill {
    public Excalibur(SkillType type) {
        super(type);
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public double getObtainingEpCost() {
        return 250000;
    }

    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        if (entity instanceof LivingEntity) {
            if (entity.hasEffect(MythosMobEffects.EXCALIBUR_REGENERATION.get())) {
                return;
            } else {
                entity.addEffect(new MobEffectInstance((MobEffect) MythosMobEffects.EXCALIBUR_REGENERATION.get(), 1200, 1, false, false, false));
            }

            if (entity.hasEffect(TensuraMobEffects.INSPIRATION.get())) {
                return;
            } else {
                entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.INSPIRATION.get(), 1200, 1, false, false, false));
            }
        }
    }

    public void onLearnSkill(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, @NotNull UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            SkillUtils.learnSkill(entity, ExtraSkills.STEEL_STRENGTH.get());
            SkillUtils.learnSkill(entity, ExtraSkills.STRENGTHEN_BODY.get());
            SkillUtils.learnSkill(entity, CommonSkills.SELF_REGENERATION.get());
            SkillUtils.learnSkill(entity, ExtraSkills.MAJESTY.get());
        }
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }
    private static double rotation = 0;
    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        Level level = entity.level;
        if (!(level instanceof ServerLevel server)) return;
        RandomSource rand = player.level.random;
        double yOffset = 1.5;

        int crownPoints = 12;
        double crownRadius = 0.6;
        for (int i = 0; i < crownPoints; i++) {
            double angle = i * 2 * Math.PI / crownPoints + rotation;
            double px = player.getX() + Math.cos(angle) * crownRadius;
            double pz = player.getZ() + Math.sin(angle) * crownRadius;
            double py = player.getY() + yOffset + 0.6; // crown height
            float size = 0.8f + rand.nextFloat() * 0.2f;
            server.sendParticles(new DustParticleOptions(new Vector3f(1f, 0.95f, 0.6f), size),
                    px, py, pz, 1, 0, 0, 0, 0);
        }

        int groundPoints = 10;
        double groundRadius = 1.0;
        for (int i = 0; i < groundPoints; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double radius = 0.3 + rand.nextDouble() * 0.7;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + (rand.nextDouble() * 0.5);
            float size = 0.6f + rand.nextFloat() * 0.2f;
            server.sendParticles(new DustParticleOptions(new Vector3f(1f, 1f, 0.8f), size),
                    px, py, pz, 1, 0, 0, 0, 0);
        }
    }

    private void gainMastery(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        int time = tag.getInt("activatedTimes");
        if (time % 12 == 0) {
            this.addMasteryPoint(instance, entity);
        }

        tag.putInt("activatedTimes", time + 1);
    }

    @Override
    public int getMaxMastery() {
        return 3000;
    }


}
