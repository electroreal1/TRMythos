package com.github.mythos.mythos.entity;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.mythos.mythos.registry.MythosEntityTypes;
import io.github.Memoires.trmysticism.entity.projectile.skill.AntaeusBlueProjectile;
import io.github.Memoires.trmysticism.entity.projectile.skill.AntaeusPurpleProjectile;
import io.github.Memoires.trmysticism.entity.projectile.skill.AntaeusRedProjectile;
import io.github.Memoires.trmysticism.registry.skill.UltimateSkills;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class IntrovertBarrier extends Entity {
    private LivingEntity owner;
    private final Map<Projectile, Integer> projectilesToDespawn;

    public IntrovertBarrier(EntityType<? extends IntrovertBarrier> entityType, Level level) {
        super(entityType, level);
        this.projectilesToDespawn = new HashMap();
        this.noPhysics = true;
        this.setInvisible(true);
    }

    public IntrovertBarrier(Level level, LivingEntity owner) {
        this((EntityType) MythosEntityTypes.INTROVERT_BARRIER.get(), level);
        this.setPos(owner.getX(), owner.getY(), owner.getZ());
        this.owner = owner;
    }

    public void tick() {
        super.tick();
        if (this.owner != null && this.owner.isAlive()) {
            this.teleportToOwner();
            this.applyBarrierEffects();
            this.haltProjectiles();
            this.handleDespawnTimers();
        } else {
            this.discard();
        }
    }

    private void applyBarrierEffects() {
        double radius = 2.0 + (double)this.owner.getBbHeight() / 2.0;
        AABB area = this.getBoundingBox().inflate(radius);
        this.level.getEntities(this, area, (entity) -> {
            return entity instanceof Projectile;
        }).forEach((projectile) -> {
            Vec3 motion = projectile.getDeltaMovement().scale(0.9);
            projectile.setDeltaMovement(motion);
            if (motion.lengthSqr() < 0.01) {
                projectile.setDeltaMovement(Vec3.ZERO);
            }

        });
        this.level.getEntities(this, area, (entity) -> {
            return entity instanceof LivingEntity && entity != this.owner && !entity.isAlliedTo(this.owner);
        }).forEach((entity) -> {
            if (!SkillUtils.hasSkill(entity, (ManasSkill) UltimateSkills.SUSANOO.get())) {
                Vec3 pushDirection = entity.position().subtract(this.position()).normalize().scale(1);
                entity.setDeltaMovement(pushDirection);
            }
        });
    }

    private void teleportToOwner() {
        if (this.owner != null && this.owner.isAlive()) {
            this.teleportTo(this.owner.getX(), this.owner.getY(), this.owner.getZ());
        }

    }

    private void haltProjectiles() {
        double radius = 2.0;
        this.level.getEntitiesOfClass(Projectile.class, this.getBoundingBox().inflate(radius)).stream().filter(this::shouldAffectProjectile).filter((projectile) -> {
            return projectile.getOwner() != this.owner;
        }).forEach(this::stopProjectile);
    }

    private void handleDespawnTimers() {
        Iterator<Map.Entry<Projectile, Integer>> iterator = this.projectilesToDespawn.entrySet().iterator();

        while(true) {
            while(iterator.hasNext()) {
                Map.Entry<Projectile, Integer> entry = (Map.Entry)iterator.next();
                Projectile projectile = (Projectile)entry.getKey();
                int remainingTime = (Integer)entry.getValue();
                if (remainingTime > 0 && projectile.isAlive()) {
                    entry.setValue(remainingTime - 4);
                } else {
                    projectile.discard();
                    iterator.remove();
                }
            }

            return;
        }
    }

    private boolean shouldAffectProjectile(Projectile projectile) {
        return !(projectile instanceof AntaeusBlueProjectile) && !(projectile instanceof AntaeusPurpleProjectile) && !(projectile instanceof AntaeusRedProjectile);
    }

    private void stopProjectile(Projectile projectile) {
        if (!(projectile instanceof AntaeusBlueProjectile) && !(projectile instanceof AntaeusPurpleProjectile) && !(projectile instanceof AntaeusRedProjectile)) {
            projectile.setDeltaMovement(0.0, 0.0, 0.0);
            projectile.setNoGravity(true);
            if (!this.projectilesToDespawn.containsKey(projectile)) {
                int despawnTime = 60 + this.level.random.nextInt(41);
                this.projectilesToDespawn.put(projectile, despawnTime);
            }

        }
    }

    protected void defineSynchedData() {
    }

    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
    }

    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public LivingEntity getOwner() {
        return this.owner;
    }
}
