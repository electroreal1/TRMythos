package com.github.mythos.mythos.mob_effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class NonEuclideanStepEffect extends MobEffect {
    public NonEuclideanStepEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            if (player.fallDistance > 0) {
                player.fallDistance = 0;
            }

            if (player.horizontalCollision) {
                Vec3 move = player.getDeltaMovement();

                if (Math.abs(move.x) > 0.01 || Math.abs(move.z) > 0.01) {
                    player.setDeltaMovement(move.x, 0.2, move.z);

                    player.setOnGround(true);
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}