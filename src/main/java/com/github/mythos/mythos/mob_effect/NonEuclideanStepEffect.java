package com.github.mythos.mythos.mob_effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class NonEuclideanStepEffect extends MobEffect {
    public NonEuclideanStepEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.fallDistance > 0) {
            entity.fallDistance = 0;
        }

        if (entity.horizontalCollision) {
            Vec3 move = entity.getDeltaMovement();

            if (Math.abs(move.x) > 0.005 || Math.abs(move.z) > 0.005) {

                entity.setDeltaMovement(move.x, 0.25, move.z);

                entity.setOnGround(true);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}