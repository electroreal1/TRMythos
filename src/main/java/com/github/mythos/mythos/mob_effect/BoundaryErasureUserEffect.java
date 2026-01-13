package com.github.mythos.mythos.mob_effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BoundaryErasureUserEffect extends MobEffect {
    public BoundaryErasureUserEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level.isClientSide) {
            if (entity.tickCount % 2 == 0) {
                entity.level.addParticle(ParticleTypes.REVERSE_PORTAL,
                        entity.getX(), entity.getY(), entity.getZ(),
                        0, 0, 0);
            }
        } else {
            if (!entity.getPersistentData().contains("BoundaryErasureUser")) {
                entity.getPersistentData().putInt("BoundaryErasureUser", 200);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}