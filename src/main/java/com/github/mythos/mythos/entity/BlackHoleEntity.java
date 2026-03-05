package com.github.mythos.mythos.entity;

import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;

public class BlackHoleEntity extends Entity implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public BlackHoleEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
    }


    @Override
    public void tick() {
        super.tick();

        if (!this.level.isClientSide) {
            float pullRadius = 100.0f;
            float pullStrength = 0.25f;
            float spiralStrength = 0.15f;
            float captureRadius = 2.5f;
            float killRadius = 20.0f;

            List<Entity> targets = this.level.getEntities(this, this.getBoundingBox().inflate(pullRadius));

            for (Entity target : targets) {
                if (target == this) continue;

                Vec3 pullDir = this.position().subtract(target.position());
                double distance = pullDir.length();

                if (distance < pullRadius) {
                    if (distance < captureRadius) {
                        target.setDeltaMovement(target.getDeltaMovement().scale(0.1));

                        Vec3 lockPos = pullDir.scale(0.5);
                        target.setDeltaMovement(target.getDeltaMovement().add(lockPos));
                    } else {
                        double force = (1.0 - (distance / pullRadius)) * pullStrength;
                        Vec3 pullVec = pullDir.normalize().scale(force);
                        Vec3 tangentVec = new Vec3(-pullDir.z, 0, pullDir.x).normalize().scale(spiralStrength);

                        target.setDeltaMovement(target.getDeltaMovement().add(pullVec.add(tangentVec)));
                        if (target instanceof LivingEntity entity) {
                            entity.addEffect(new MobEffectInstance(TensuraMobEffects.SPATIAL_BLOCKADE.get(), 300, 20, false, false, false));
                        }
                    }

                    target.hurtMarked = true;

                    if (distance < killRadius && target instanceof LivingEntity living) {
                        DamageSource damageSource = new DamageSource(TensuraDamageSources.SPACE_ATTACK);
                        living.hurt(damageSource, 400.0f);
                        living.setDeltaMovement(living.getDeltaMovement().add(0, 0.02, 0));
                    }
                }
            }
        }
    }


    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag nbt) {
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag nbt) {
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.blackhole.base2", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}