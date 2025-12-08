package com.github.mythos.mythos.ability.mythos.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.effects.TensuraEffectsCapability;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.util.damage.TensuraDamageSource;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.function.Predicate;

public class TenaciousSkill extends Skill {
    public TenaciousSkill(SkillType type) {super(SkillType.UNIQUE);}

    @Override
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/tenacious.png");
    }

    public int getMaxMastery() {return 500;}

    public double getObtainingEpCost() {
        return 50000;
    }

    public boolean canBeSlotted(ManasSkillInstance instance) {
        return (instance.getMastery() < 0);
    }

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return instance.isToggled();
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.isToggled()) {
            entity.addEffect(new MobEffectInstance((MobEffect) MythosMobEffects.ENERGIZED_REGENERATION.get(), 1200, 1, false, false, false));
        }
        if (entity instanceof Player) {
            Player player = (Player)entity;
            energizedHandler(player);
        }
        CompoundTag tag = instance.getOrCreateTag();
        int time = tag.getInt("activatedTimes");
        if (time % 12 == 0)
            addMasteryPoint(instance, entity);
        tag.putInt("activatedTimes", time + 1);
    }

    public void energizedHandler(Player player) {
        TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
            double maxMP = player.getAttributeValue(TensuraAttributeRegistry.MAX_MAGICULE.get());
            double mpGain = maxMP * 0.001D;
            if (cap.getMagicule() + mpGain < cap.getBaseMagicule()) {
                cap.setMagicule(cap.getMagicule() + mpGain);
            } else {
                cap.setMagicule(cap.getBaseMagicule());
            }

            double maxAP = player.getAttributeValue(TensuraAttributeRegistry.MAX_AURA.get());
            double apGain = maxAP * 0.001D;
            if (cap.getAura() + apGain < cap.getBaseAura()) {
                cap.setAura(cap.getAura() + apGain);
            } else {
                cap.setAura(cap.getBaseAura());
            }
        });
    }

    public void onDeath(ManasSkillInstance instance, LivingDeathEvent event) {
        if (!event.isCanceled()) {
            DamageSource source = event.getSource();
            if (source != DamageSource.OUT_OF_WORLD &&
                    !instance.onCoolDown()) {
                if (source instanceof TensuraDamageSource) {
                    TensuraDamageSource damageSource = (TensuraDamageSource)source;
                    if (damageSource.getIgnoreBarrier() >= 3.0F)
                        return;
                }
                LivingEntity entity = event.getEntity();
                if (!entity.isAlive()) {
                    if (source.getEntity() != null) {
                        if (source.getEntity() == entity)
                            return;
                        if (source.getEntity() == SkillHelper.getSubordinateOwner(entity))
                            return;
                    }
                    addMasteryPoint(instance, entity);
                    entity.setHealth(Math.max(entity.getHealth(), entity.getMaxHealth() * 0.1F));
                    entity.invulnerableTime = Math.max(60, entity.invulnerableTime);
                    Predicate<MobEffect> predicate = effect -> (effect.getCategory() == MobEffectCategory.HARMFUL);
                    SkillHelper.removePredicateEffect(entity, predicate);
                    TensuraEffectsCapability.resetEverything(entity, false, false);
                    event.setCanceled(true);
                    if (!instance.onCoolDown())
                        instance.setCoolDown(60);
                    entity.level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                    TensuraParticleHelper.addServerParticlesAroundSelf((Entity)entity, (ParticleOptions)ParticleTypes.FIREWORK, 1.0D);
                    TensuraParticleHelper.addServerParticlesAroundSelf((Entity)entity, (ParticleOptions) ParticleTypes.FIREWORK, 2.0D);
                    TensuraParticleHelper.addServerParticlesAroundSelf((Entity)entity, (ParticleOptions)ParticleTypes.FIREWORK, 1.0D);
                }
            }
        }
    }
}

