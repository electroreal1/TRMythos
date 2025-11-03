package com.github.mythos.mythos.mob_effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.awt.*;

public class ExcaliburRegeneration extends MobEffect {
    public ExcaliburRegeneration(MobEffectCategory pCategory, Color pColor) {
        super(MobEffectCategory.BENEFICIAL, 990000 );
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        Level level = entity.level;
        if (level.isClientSide) return;

        float currentHealth = entity.getHealth();
        float maxHealth = entity.getMaxHealth();

        if (currentHealth >= maxHealth) return;

        float missingRatio = (maxHealth - currentHealth) / maxHealth;

        float baseHeal = 10f + missingRatio * 60f;

        float totalHeal = baseHeal * (1.0f + (amplifier * 0.5f));

        entity.heal(totalHeal);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}


