package com.github.mythos.mythos.ability.mythos.skill.ultimate.lord;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.registry.skill.CommonSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import io.github.Memoires.trmysticism.registry.effects.MysticismMobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class AsclepiusSkill extends Skill {
    public AsclepiusSkill(SkillType type) {
        super(SkillType.ULTIMATE);
    }

    public boolean meetEPRequirement(@NotNull Player player, double newEP) {
        if (!EnableUltimateSkillObtainment()) return false;
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, UniqueSkills.HEALER.get()) &&
                SkillUtils.isSkillMastered(player, CommonSkills.POISON.get()) &&
                SkillUtils.isSkillMastered(player, CommonSkills.CORROSION.get()) &&
                SkillUtils.isSkillMastered(player, CommonSkills.PARALYSIS.get());
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (entity instanceof Player player && !instance.isTemporarySkill()) {
            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            Skill greedSkill = UniqueSkills.HEALER.get();
            Skill greedSkill1 = CommonSkills.PARALYSIS.get();
            Skill greedSkill2 = CommonSkills.CORROSION.get();
            Skill greedSkill3 = CommonSkills.POISON.get();
            storage.getSkill(greedSkill).ifPresent(storage::forgetSkill);
            storage.getSkill(greedSkill1).ifPresent(storage::forgetSkill);
            storage.getSkill(greedSkill2).ifPresent(storage::forgetSkill);
            storage.getSkill(greedSkill3).ifPresent(storage::forgetSkill);
        }
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {

        entity.getActiveEffects().forEach(effectInstance -> {
            MobEffect effect = effectInstance.getEffect();
            if (effect.isBeneficial()) return;
            if (!effect.isBeneficial()) {
                entity.removeEffect(effect);
            }
        });

        if (this.isInSlot(entity)) {
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.STRENGTHEN.get(), 240, 0, false, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 240, 0, false, false, false));
        }
    }

    @Override
    public void onTouchEntity(ManasSkillInstance instance, LivingEntity attacker, LivingHurtEvent e) {
        if (this.isInSlot(attacker) && e.getSource().getDirectEntity() == attacker && DamageSourceHelper.isPhysicalAttack(e.getSource())) {
            Level level = attacker.getLevel();
            LivingEntity target = e.getEntity();
            int mastery = instance.getMastery();
            toggleSpecificResistances(target);
            level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 1.0F, 1.0F);
            if (level instanceof ServerLevel serverLevel) {
                Map effectsMap = target.getActiveEffectsMap();
                if (mastery >= 0) {
                    this.addEffect(target, effectsMap, TensuraMobEffects.FATAL_POISON.get(), 200, 9);
                    serverLevel.sendParticles(TensuraParticles.POISON_BUBBLE.get(), target.position().x, target.position().y +
                            (double)target.getBbHeight() / 2.0, target.position().z,
                            20, 0.08, 0.08, 0.08, 0.15);
                }

                if (mastery >= 334) {
                    this.addEffect(target, effectsMap, TensuraMobEffects.PARALYSIS.get(), 200, 5);
                    serverLevel.sendParticles(TensuraParticles.PARALYSING_BUBBLE.get(), target.position().x, target.position().y +
                            (double)target.getBbHeight() / 2.0, target.position().z,
                            20, 0.08, 0.08, 0.08, 0.15);
                }

                if (mastery >= 667) {
                    this.addEffect(target, effectsMap, TensuraMobEffects.CORROSION.get(), 200, 7);
                    serverLevel.sendParticles(TensuraParticles.ACID_BUBBLE.get(), target.position().x, target.position().y +
                            (double)target.getBbHeight() / 2.0, target.position().z,
                            20, 0.08, 0.08, 0.08, 0.15);
                }

                if (mastery >= 1000) {
                    this.addEffect(target, effectsMap, TensuraMobEffects.BURDEN.get(), 200, 4);
                    serverLevel.sendParticles(ParticleTypes.EFFECT, target.position().x, target.position().y +
                            (double)target.getBbHeight() / 2.0, target.position().z,
                            20, 0.08, 0.08, 0.08, 0.15);
                }

                if (mastery >= 1500) {
                    this.addEffect(target, effectsMap, TensuraMobEffects.FRAGILITY.get(), 200, 4);
                }

                if (instance.isMastered(attacker)) {
                    this.addEffect(target, effectsMap, MysticismMobEffects.MARKED_FOR_DEATH.get(), 200, 0);
                }

                CompoundTag tag = instance.getOrCreateTag();
                int time = tag.getInt("activatedTimes");
                if (time % 10 == 0) {
                    this.addMasteryPoint(instance, attacker);
                }

                tag.putInt("activatedTimes", time + 1);
            }
        }
    }

    private static int toggleSpecificResistances(LivingEntity player) {
        int toggledCount = 0;
        Iterator var2 = SkillAPI.getSkillsFrom(player).getLearnedSkills().iterator();

        while(true) {
            ManasSkillInstance instance;
            do {
                if (!var2.hasNext()) {
                    return toggledCount;
                }

                instance = (ManasSkillInstance)var2.next();
            } while(!instance.getSkill().equals(ResistanceSkills.CORROSION_RESISTANCE.get()) &&
                    !instance.getSkill().equals(ResistanceSkills.POISON_RESISTANCE.get()) &&
                    !instance.getSkill().equals(ResistanceSkills.PARALYSIS_RESISTANCE.get()) &&
                    !instance.getSkill().equals(ResistanceSkills.ABNORMAL_CONDITION_RESISTANCE.get()) &&
                    !instance.getSkill().equals(ResistanceSkills.CORROSION_NULLIFICATION.get()) &&
                    !instance.getSkill().equals(ResistanceSkills.POISON_NULLIFICATION.get()) &&
                    !instance.getSkill().equals(ResistanceSkills.PARALYSIS_NULLIFICATION.get()) &&
                    !instance.getSkill().equals(ResistanceSkills.ABNORMAL_CONDITION_NULLIFICATION.get()));

            if (instance.isToggled()) {
                instance.setToggled(false);
                instance.onToggleOff(player);
                ++toggledCount;
            }
        }
    }

    private void addEffect(LivingEntity target, Map<MobEffect, MobEffectInstance> activeEffects, MobEffect effect, int duration, int level) {
        MobEffectInstance instance = activeEffects.get(effect);
        MobEffectInstance newInstance = new MobEffectInstance(effect, duration, level);
        MinecraftForge.EVENT_BUS.post(new MobEffectEvent.Added(target, instance, newInstance, target));
        if (instance == null) {
            activeEffects.put(newInstance.getEffect(), newInstance);
            newInstance.getEffect().addAttributeModifiers(target, target.getAttributes(), newInstance.getAmplifier());
        } else if (instance.update(newInstance)) {
            effect.removeAttributeModifiers(target, target.getAttributes(), instance.getAmplifier());
            effect.addAttributeModifiers(target, target.getAttributes(), instance.getAmplifier());
        }

    }

    @Override
    public double getObtainingEpCost() {
        return 1000000;
    }
}
