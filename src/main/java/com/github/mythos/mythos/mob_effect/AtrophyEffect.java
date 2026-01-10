package com.github.mythos.mythos.mob_effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class AtrophyEffect extends MobEffect {
    public AtrophyEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int pAmplifier) {
        if (pAmplifier == 0) {
            Vec3 delta = entity.getDeltaMovement();
            entity.setDeltaMovement(delta.x * 0.05, delta.y, delta.z * 0.05);
            entity.hurtMarked = true;
        }
        else if (pAmplifier >= 1) {
            entity.setDeltaMovement(0, 0, 0);
            entity.setNoGravity(true);

            entity.setYRot(entity.yRotO);
            entity.setXRot(entity.xRotO);
            entity.setYHeadRot(entity.yHeadRotO);

            entity.hurtMarked = true;
        }
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }
}