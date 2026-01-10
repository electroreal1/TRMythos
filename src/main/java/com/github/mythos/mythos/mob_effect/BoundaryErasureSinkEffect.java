package com.github.mythos.mythos.mob_effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class BoundaryErasureSinkEffect extends MobEffect {
    public BoundaryErasureSinkEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.getPersistentData().contains("BoundaryErasureUser")) return;

        if (entity.isOnGround() || entity.isInWall()) {
            entity.noPhysics = true;

            Vec3 motion = entity.getDeltaMovement();
            entity.setDeltaMovement(motion.x * 0.5, -0.15, motion.z * 0.5);
        }

        entity.hurtMarked = true;
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, net.minecraft.world.entity.ai.attributes.AttributeMap pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(entity, pAttributeMap, pAmplifier);
        entity.noPhysics = false;
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }
}