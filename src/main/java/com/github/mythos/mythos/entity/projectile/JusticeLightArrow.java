package com.github.mythos.mythos.entity.projectile;

import com.github.manasmods.tensura.entity.projectile.LightArrowProjectile;
import com.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import com.github.mythos.mythos.registry.MythosEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class JusticeLightArrow extends LightArrowProjectile {

    public JusticeLightArrow(Level world, LivingEntity shooter) {
        super(TensuraEntityTypes.LIGHT_ARROW.get(), world);
        // super(MythosEntity.JUSTICE_LIGHT_ARROW.get(), world);
        this.setOwner(shooter);
        this.setNoGravity(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide()) {
            for (int i = 0; i < 2; i++) {
                this.level.addParticle(
                        ParticleTypes.END_ROD,
                        this.getX() + (this.random.nextDouble() - 0.5) * 0.2,
                        this.getY() + (this.random.nextDouble() - 0.5) * 0.2,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 0.2,
                        0, 0, 0
                );
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        this.level.playSound(
                null,
                this.blockPosition(),
                SoundEvents.TRIDENT_HIT,
                SoundSource.PLAYERS,
                0.8F,
                1.2F
        );
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.level.playSound(
                null,
                this.blockPosition(),
                SoundEvents.LIGHTNING_BOLT_IMPACT,
                SoundSource.PLAYERS,
                0.5F,
                1.5F
        );
        this.discard();
    }
}
