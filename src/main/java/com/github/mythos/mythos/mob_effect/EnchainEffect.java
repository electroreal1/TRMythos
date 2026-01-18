package com.github.mythos.mythos.mob_effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EnchainEffect extends MobEffect {
    public EnchainEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);

        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107408e-7c22-11ed-afa1-0242ac120002", -1.0, AttributeModifier.Operation.MULTIPLY_TOTAL);

        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "710743ae-7c22-11ed-afa1-0242ac120002", 1.0, AttributeModifier.Operation.ADDITION);
    }
}
