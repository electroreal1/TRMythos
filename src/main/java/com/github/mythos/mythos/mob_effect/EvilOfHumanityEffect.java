package com.github.mythos.mythos.mob_effect;

import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EvilOfHumanityEffect extends MobEffect {
    private static final UUID ABSOLUTE_CORRUPTION_ID = UUID.fromString("a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d");
    private static List<MobEffect> harmfulCache = null;

    public EvilOfHumanityEffect() {
        super(MobEffectCategory.HARMFUL, 0x000000);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level.isClientSide) return;

        entity.hurt(TensuraDamageSources.bloodDrain(entity), 3.0F * amplifier);
        DamageSourceHelper.directSpiritualHurt(entity, entity, 15.0F * amplifier);


        double totalDrain = 500.0 * (amplifier + 1);
        SkillHelper.drainMP(entity, entity, totalDrain, true);


        if (entity.tickCount % 10 == 0) {
            applyRandomRegistryMalice(entity, amplifier);
        }

        if (entity.getRandom().nextFloat() < 0.15f) {
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.MIND_CONTROL.get(), 20, 0, false, false));
        }
    }

    private void applyRandomRegistryMalice(LivingEntity entity, int amplifier) {
        if (harmfulCache == null) {
            harmfulCache = ForgeRegistries.MOB_EFFECTS.getValues().stream()
                    .filter(effect -> effect.getCategory() == MobEffectCategory.HARMFUL)
                    .filter(effect -> effect != this)
                    .collect(Collectors.toList());
        }

        if (!harmfulCache.isEmpty()) {
            for (int i = 0; i < 5; i++) {
                MobEffect randomEffect = harmfulCache.get(entity.getRandom().nextInt(harmfulCache.size()));
                entity.addEffect(new MobEffectInstance(randomEffect, 100, amplifier));
            }
        }
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        var armor = attributeMap.getInstance(Attributes.ARMOR);
        var toughness = attributeMap.getInstance(Attributes.ARMOR_TOUGHNESS);
        var shp = attributeMap.getInstance(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());
        var move = attributeMap.getInstance(Attributes.MOVEMENT_SPEED);

        double meltFactor = -0.2 * (amplifier + 1);

        if (armor != null) {
            armor.addTransientModifier(new AttributeModifier(ABSOLUTE_CORRUPTION_ID, "Humanity Armor Melt", meltFactor, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        if (toughness != null) {
            toughness.addTransientModifier(new AttributeModifier(ABSOLUTE_CORRUPTION_ID, "Humanity Toughness Melt", meltFactor, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        if (move != null) {
            move.addTransientModifier(new AttributeModifier(ABSOLUTE_CORRUPTION_ID, "Humanity Root", -0.5, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }

        if (shp != null) {
            shp.addTransientModifier(new AttributeModifier(ABSOLUTE_CORRUPTION_ID, "Humanity Soul Collapse", -100.0 * (amplifier + 1), AttributeModifier.Operation.ADDITION));
        }

        super.addAttributeModifiers(entity, attributeMap, amplifier);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        var armor = attributeMap.getInstance(Attributes.ARMOR);
        var toughness = attributeMap.getInstance(Attributes.ARMOR_TOUGHNESS);
        var shp = attributeMap.getInstance(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());
        var move = attributeMap.getInstance(Attributes.MOVEMENT_SPEED);

        if (armor != null) armor.removeModifier(ABSOLUTE_CORRUPTION_ID);
        if (toughness != null) toughness.removeModifier(ABSOLUTE_CORRUPTION_ID);
        if (shp != null) shp.removeModifier(ABSOLUTE_CORRUPTION_ID);
        if (move != null) move.removeModifier(ABSOLUTE_CORRUPTION_ID);

        super.removeAttributeModifiers(entity, attributeMap, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}