package com.github.mythos.mythos.mob_effect;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.InfiniteRegenerationSkill;
import com.github.manasmods.tensura.capability.effects.TensuraEffectsCapability;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.effect.template.MobEffectHelper;
import com.github.manasmods.tensura.effect.template.SkillMobEffect;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.util.damage.TensuraDamageSource;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class CostlessRegenerationEffect extends SkillMobEffect {
    public CostlessRegenerationEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public void applyEffectTick(LivingEntity entity, int pAmplifier) {
        double maxHealth = entity.getMaxHealth() - TensuraEffectsCapability.getSeverance(entity);
        if (maxHealth > 0.0D) {
            boolean failed = false;
            float lackedHealth = (float)maxHealth - entity.getHealth();
            if (lackedHealth > 0.0F && !MobEffectHelper.shouldCancelHeal(entity)) {
                if (entity instanceof Player) {
                    Player player = (Player)entity;
                    float cost = (pAmplifier == 0 && SkillUtils.isSkillToggled((Entity)entity, (ManasSkill) Skills.DIKE.get())) ? 40.0F : 80.0F;
                    Skill toggledSkill = (Skill) Skills.DIKE.get();
                    Optional<ManasSkillInstance> instance = SkillAPI.getSkillsFrom((Entity)entity).getSkill((ManasSkill)toggledSkill);
                    if (instance.isPresent() && ((ManasSkillInstance)instance.get()).isMastered(entity))
                        cost = 40.0F;
                    double lackedMagicule = SkillHelper.outOfMagiculeStillConsume((LivingEntity)player, (int)(lackedHealth * cost));
                    if (lackedMagicule > 0.0D) {
                        lackedHealth = (float)(lackedHealth - lackedMagicule / cost);
                        if (instance.isPresent() && ((ManasSkillInstance)instance.get()).isToggled()) {
                            ((ManasSkillInstance)instance.get()).setToggled(false);
                            SkillAPI.getSkillsFrom((Entity)entity).syncChanges();
                            failed = true;
                            player.removeEffect((MobEffect)this);
                            player.displayClientMessage((Component) Component.translatable("tensura.skill.lack_magicule.toggled_off", new Object[] { toggledSkill.getName() }).withStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                        }
                    }
                }
                entity.heal(lackedHealth);
            }
            if (!failed)
                healSHP(entity, pAmplifier);
        }
    }

    private void healSHP(LivingEntity entity, int pAmplifier) {
        if (pAmplifier >= 1) {
            double SHP = TensuraEPCapability.getSpiritualHealth(entity);
            double maxSHP = entity.getAttributeValue((Attribute) TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());
            double lackedSHP = maxSHP - SHP;
            if (lackedSHP > 0.0D &&
                    lackedSHP > 0.0D) {
                if (entity instanceof Player) {
                    Player player = (Player)entity;
                    float cost = 240.0F;
                    Optional<ManasSkillInstance> instance = SkillAPI.getSkillsFrom((Entity)entity).getSkill((ManasSkill)Skills.DIKE.get());
                    if (instance.isPresent() && ((ManasSkillInstance)instance.get()).isMastered(entity))
                        cost = 50.0F;
                    double lackedMagicule = SkillHelper.outOfMagiculeStillConsume((LivingEntity)player, (int)(lackedSHP * cost));
                    if (lackedMagicule > 0.0D) {
                        lackedSHP -= lackedMagicule / cost;
                        if (instance.isPresent() && ((ManasSkillInstance)instance.get()).isToggled()) {
                            ((ManasSkillInstance)instance.get()).setToggled(false);
                            SkillAPI.getSkillsFrom((Entity)entity).syncChanges();
                            player.removeEffect((MobEffect)this);
                            player.displayClientMessage((Component)Component.translatable("tensura.skill.lack_magicule.toggled_off", new Object[] { ((InfiniteRegenerationSkill) ExtraSkills.INFINITE_REGENERATION.get()).getName() }).withStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                        }
                    }
                }
                TensuraEPCapability.setSpiritualHealth(entity, Math.min(SHP + lackedSHP, maxSHP));
            }
        }
    }

    public boolean m_6584_(int pDuration, int pAmplifier) {
        return (pDuration % 10 == 0);
    }

    public static boolean canStopDeath(DamageSource source, LivingEntity entity) {
        if (source.isBypassInvul())
            return false;
        if (source instanceof TensuraDamageSource) {
            TensuraDamageSource damageSource = (TensuraDamageSource)source;
            if (damageSource.getIgnoreBarrier() >= 1.75D)
                return false;
        }
        return (entity.getMaxHealth() > TensuraEffectsCapability.getSeverance(entity));
    }
}
