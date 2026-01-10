package com.github.mythos.mythos.mob_effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SpatialDysphoriaEffect extends MobEffect {
    public SpatialDysphoriaEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int pAmplifier) {
        if (entity.level.isClientSide) return;

        double intensityMultiplier = 1.0 + (pAmplifier * 0.5);
        int warpFrequency = Math.max(5, 30 - (pAmplifier * 5));

        if (entity.tickCount % warpFrequency == 0) {
            double warpX = (entity.getRandom().nextDouble() - 0.5) * intensityMultiplier;
            double warpZ = (entity.getRandom().nextDouble() - 0.5) * intensityMultiplier;

            double warpY = pAmplifier >= 2 ? 0.4 : 0.1;

            entity.setDeltaMovement(warpX, warpY, warpZ);

            entity.hurtMarked = true;
        }

        if (entity.tickCount % 80 == 0) {
            if (entity.getRandom().nextBoolean()) {
                float crushForce = -0.8f - (pAmplifier * 0.2f);
                entity.setDeltaMovement(entity.getDeltaMovement().add(0, crushForce, 0));
                entity.hurtMarked = true;
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }
}