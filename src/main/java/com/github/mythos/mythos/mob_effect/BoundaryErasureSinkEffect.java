package com.github.mythos.mythos.mob_effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class BoundaryErasureSinkEffect extends MobEffect {
    public BoundaryErasureSinkEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        entity.noPhysics = true;

        Vec3 move = entity.getDeltaMovement();
        entity.setDeltaMovement(move.x * 0.5, -0.3, move.z * 0.5);

        if (entity.getDeltaMovement().y > 0 && entity.isOnGround()) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0, -(1 + entity.getJumpBoostPower()), 0));
        }

        if (entity instanceof Player player) {
            if (player.zza > 0) {
                player.setDeltaMovement(entity.getDeltaMovement().add(0,  -(1 + player.getJumpBoostPower()), 0));
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}