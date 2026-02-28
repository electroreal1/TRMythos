package com.github.mythos.mythos.mob_effect;

import com.github.manasmods.tensura.effect.template.TensuraMobEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class SchrodingersLabyrinthEffect extends TensuraMobEffect {
    public SchrodingersLabyrinthEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap map, int amplifier) {
        super.addAttributeModifiers(entity, map, amplifier);
        CompoundTag tag = entity.getPersistentData();
        tag.putLong("LabyrinthAnchor", entity.blockPosition().asLong());
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap map, int amplifier) {
        super.removeAttributeModifiers(entity, map, amplifier);
        entity.getPersistentData().remove("LabyrinthAnchor");
    }
}
