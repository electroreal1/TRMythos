package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShadowAvengerSkill extends Skill {
    private static final UUID SHP_GROWTH_ID = UUID.fromString("7d3a529b-e065-4f76-8f24-6997097e937d");
    private static final String HATRED_KEY = "HatredStacks";
    private static final String DECAY_TIMER_KEY = "HatredDecayTimer";
    private static final String PERSISTENCE_KEY = "IsPersistent";

    public ShadowAvengerSkill(SkillType type) {
        super(type);
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public List<MobEffect> getImmuneEffects(ManasSkillInstance instance, LivingEntity entity) {
        List<MobEffect> list = new ArrayList<>();
        list.add(TensuraMobEffects.MIND_CONTROL.get());
        list.add(TensuraMobEffects.FEAR.get());
        list.add(TensuraMobEffects.INSANITY.get());
        return list;
    }

    @SubscribeEvent
    public void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance effectInstance = event.getEffectInstance();
        MobEffect effect = effectInstance.getEffect();

        if (effect.getCategory() == MobEffectCategory.HARMFUL) {
            AttributeInstance shpAttr = entity.getAttribute(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());

            if (shpAttr != null) {
                double currentAmount = 0;
                AttributeModifier currentMod = shpAttr.getModifier(SHP_GROWTH_ID);
                if (currentMod != null) {
                    currentAmount = currentMod.getAmount();
                    shpAttr.removeModifier(SHP_GROWTH_ID);
                }

                shpAttr.addPermanentModifier(new AttributeModifier(SHP_GROWTH_ID, "Shadow Avenger Growth", currentAmount + 10,
                        AttributeModifier.Operation.ADDITION));
            }

            applySynergyBuff(entity, effect);
        }
    }

    @SubscribeEvent
    public void onEffectRemove(MobEffectEvent.Remove event) {
        if (event.getEffect() != null && event.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
            event.setCanceled(true);
        }
    }

    private void applySynergyBuff(LivingEntity entity, MobEffect originalEffect) {
        if (originalEffect == MobEffects.POISON || originalEffect == MobEffects.WITHER) {
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 160, 2));
        } else if (originalEffect == MobEffects.WEAKNESS) {
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 160, 2));
        } else if (originalEffect == MobEffects.MOVEMENT_SLOWDOWN) {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 160, 2));
        } else if (originalEffect == TensuraMobEffects.MAGICULE_POISON.get()) {
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.MAGICULE_REGENERATION.get(), 160, 2));
        } else if (originalEffect == MobEffects.DIG_SLOWDOWN) {
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 160, 2));
        } else if (originalEffect == MobEffects.BLINDNESS) {
            entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 160, 2));
        }


        else {
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 160, 1));
        }
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level.isClientSide) return;

        CompoundTag tag = instance.getOrCreateTag();
        int stacks = tag.getInt(HATRED_KEY);

        if (stacks > 0) {
            int timer = tag.getInt(DECAY_TIMER_KEY);
            timer++;

            if (timer >= 100) {
                tag.putInt(HATRED_KEY, stacks - 1);
                tag.putInt(DECAY_TIMER_KEY, 0);
            } else {
                tag.putInt(DECAY_TIMER_KEY, timer);
            }
        }

        if (!this.isInSlot(entity)) return;
        if (tag.getBoolean(PERSISTENCE_KEY) && entity.tickCount % 20 == 0 && entity instanceof Player player) {
            double maxMP = TensuraPlayerCapability.getBaseMagicule(player);
            double drain = maxMP * 0.01f;

            if (SkillHelper.getMP(player, true) >= drain) {
                SkillHelper.drainMP(entity, null, drain, true);
                entity.setHealth(1);
            } else {
                tag.putBoolean(PERSISTENCE_KEY, false);
            }
        }
    }

    @Override
    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level.isClientSide) return;
        if (instance.isToggled()) {
            CompoundTag tag = instance.getOrCreateTag();
            int currentStacks = tag.getInt(HATRED_KEY);
            int cap = instance.isMastered(entity) ? 15 : 10;

            if (currentStacks < cap) {
                tag.putInt(HATRED_KEY, currentStacks + 1);
            }
            tag.putInt(DECAY_TIMER_KEY, 0);
        }
    }

    @Override
    public void onTakenDamage(ManasSkillInstance instance, LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        if (this.isInSlot(entity)) {
            if (DamageSourceHelper.isLightDamage(source) || DamageSourceHelper.isHoly(source)) {
                event.setAmount(event.getAmount() * 1.5f);
            }
        }
    }

    @Override
    public void onDeath(ManasSkillInstance instance, LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level.isClientSide) return;

        CompoundTag tag = instance.getOrCreateTag();
        double currentMP = SkillHelper.getMP(entity, false);

        if (!tag.getBoolean(PERSISTENCE_KEY) && currentMP > 10000) {
            event.setCanceled(true);
            tag.putBoolean(PERSISTENCE_KEY, true);
            entity.setHealth(1);

            entity.level.playSound(null, entity.blockPosition(), SoundEvents.WARDEN_HURT, SoundSource.AMBIENT, 1, 0.5f);
        }
    }

    @Override
    public void onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent event) {
        if (instance.isToggled()) {
            CompoundTag tag = instance.getOrCreateTag();
            int stacks = tag.getInt(HATRED_KEY);

            if (stacks > 0) {
                float multiplier = 1 + (stacks * 0.1f);
                event.setAmount(event.getAmount() * multiplier);

            }
        }
    }


}
