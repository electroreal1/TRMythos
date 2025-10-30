package com.github.mythos.mythos.mob_effect;

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
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class CompleteRegenerationEffect extends SkillMobEffect {
    public CompleteRegenerationEffect(MobEffectCategory category, int color) {super(category, color);}

    // applyEffectTick
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        double maxHealth = entity.getMaxHealth() - TensuraEffectsCapability.getSeverance(entity);
        if (maxHealth <= 0.0D)
            return;

        boolean failed = false;
        float missingHealth = (float) maxHealth - entity.getHealth();

        if (missingHealth > 0.0F && !MobEffectHelper.shouldCancelHeal(entity)) {
            if (entity instanceof Player player) {
                float cost = (amplifier == 0 && SkillUtils.isSkillToggled(entity, Skills.ETERNAL.get()) && SkillUtils.isSkillToggled(entity, Skills.IMMORTAL.get())) ? 20.0F : 100.0F;
                Skill toggledSkill = (amplifier > 0)
                        ? ExtraSkills.INFINITE_REGENERATION.get()
                        : ExtraSkills.ULTRASPEED_REGENERATION.get();

                Optional<ManasSkillInstance> instance = SkillAPI.getSkillsFrom(entity).getSkill(toggledSkill);

                // Mastered version costs less
                if (instance.isPresent() && instance.get().isMastered(entity))
                    cost = 60.0F;

                double lackedMagicule = SkillHelper.outOfMagiculeStillConsume(player, (int) (missingHealth * cost));

                if (lackedMagicule > 0.0D) {
                    missingHealth -= lackedMagicule / cost;

                    // Turn off toggle if magicule insufficient
                    if (instance.isPresent() && instance.get().isToggled()) {
                        instance.get().setToggled(false);
                        SkillAPI.getSkillsFrom(entity).syncChanges();
                        failed = true;
                        player.removeEffect(this);

                        player.displayClientMessage(
                                Component.translatable("tensura.skill.lack_magicule.toggled_off", toggledSkill.getName())
                                        .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)),
                                false
                        );
                    }
                }
            }

            entity.heal(missingHealth);
        }

        if (!failed)
            healSpiritualHealth(entity, amplifier);
    }

    private void healSpiritualHealth(LivingEntity entity, int amplifier) {
        if (amplifier < 1)
            return;

        double currentSHP = TensuraEPCapability.getSpiritualHealth(entity);
        double maxSHP = entity.getAttributeValue(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());
        double missingSHP = maxSHP - currentSHP;

        if (missingSHP <= 0.0D)
            return;

        if (entity instanceof Player player) {
            float cost = 120.0F;
            Optional<ManasSkillInstance> instance = SkillAPI.getSkillsFrom(entity).getSkill(ExtraSkills.INFINITE_REGENERATION.get());

            if (instance.isPresent() && instance.get().isMastered(entity))
                cost = 80.0F;

            double lackedMagicule = SkillHelper.outOfMagiculeStillConsume(player, (int) (missingSHP * cost));

            if (lackedMagicule > 0.0D) {
                missingSHP -= lackedMagicule / cost;

                if (instance.isPresent() && instance.get().isToggled()) {
                    instance.get().setToggled(false);
                    SkillAPI.getSkillsFrom(entity).syncChanges();
                    player.removeEffect(this);

                    player.displayClientMessage(
                            Component.translatable("tensura.skill.lack_magicule.toggled_off",
                                            ((InfiniteRegenerationSkill) ExtraSkills.INFINITE_REGENERATION.get()).getName())
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)),
                            false
                    );
                }
            }
        }

        TensuraEPCapability.setSpiritualHealth(entity, Math.min(currentSHP + missingSHP, maxSHP));
    }

    // isDurationEffectTick
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 10 == 0;
    }

    public static boolean canStopDeath(DamageSource source, LivingEntity entity) {
        if (source.isBypassInvul())
            return false;

        if (source instanceof TensuraDamageSource tensuraSource) {
            if (tensuraSource.getIgnoreBarrier() >= 1.75D)
                return false;
        }

        return entity.getMaxHealth() > TensuraEffectsCapability.getSeverance(entity);
    }
}