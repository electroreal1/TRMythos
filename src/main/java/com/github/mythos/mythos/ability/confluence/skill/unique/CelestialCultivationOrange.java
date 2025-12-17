package com.github.mythos.mythos.ability.confluence.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;

public class CelestialCultivationOrange extends Skill {
    public CelestialCultivationOrange(SkillType type) {
        super(type);
    }

    @Override
    public double getObtainingEpCost() {
        return 500000;
    }

    @Override
    public double learningCost() {
        return 10000;
    }

    @Override
    public int modes() {
        return 1;
    }

    @Override
    public Component getModeName(int mode) {
        return Component.translatable("trmythos.skill.celestial_cultivation_orange.aura");
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.isToggled()) {
            if (entity instanceof Player) {
                Player player = (Player)entity;
                TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                    double maxMP = player.getAttributeValue((Attribute) TensuraAttributeRegistry.MAX_MAGICULE.get());
                    double regen = instance.isMastered(entity) ? 280000.0 : 140000.0;
                    cap.setMagicule(Math.min(cap.getMagicule() + regen, maxMP));
                });
                TensuraPlayerCapability.sync(player);
            }

            entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.PRESENCE_SENSE.get(), 200, 3, false, false, false));
        }
    }

    public void onLearnSkill(ManasSkillInstance instance, LivingEntity living, UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            if (living instanceof Player) {
                Player player = (Player)living;
                TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                    cap.setBlessed(true);
                });
                TensuraPlayerCapability.sync(player);
            }

        }
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (entity instanceof Player player) {
            TensuraPlayerCapability.getFrom(player).ifPresent((cap -> {
                cap.setBaseAura(cap.getBaseAura() - 10000, player);
                cap.setBaseMagicule(cap.getBaseMagicule() + 10000, player);
            }));
            TensuraPlayerCapability.sync(player);
        }
    }
}
