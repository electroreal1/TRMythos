package com.github.mythos.mythos.entity;

import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.registry.MythosEntityTypes;
import io.github.Memoires.trmysticism.entity.projectile.skill.AntaeusBlueProjectile;
import io.github.Memoires.trmysticism.entity.projectile.skill.AntaeusPurpleProjectile;
import io.github.Memoires.trmysticism.entity.projectile.skill.AntaeusRedProjectile;
import io.github.Memoires.trmysticism.registry.skill.UltimateSkills;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.HashMap;
import java.util.Map;

public class BoreasBarrier extends Entity {
    private LivingEntity owner;
    private final Map<Projectile, Integer> projectilesToDespawn;

    private static final double ISOLATION_RADIUS = 10.0;

    public BoreasBarrier(EntityType<? extends BoreasBarrier> entityType, Level level) {
        super(entityType, level);
        this.projectilesToDespawn = new HashMap<>();
        this.noPhysics = true;
        this.setInvisible(true);
    }

    public BoreasBarrier(Level level, LivingEntity owner) {
        this(MythosEntityTypes.BOREAS_BARRIER.get(), level);
        this.setPos(owner.getX(), owner.getY(), owner.getZ());
        this.owner = owner;
    }

    public LivingEntity getOwner() {
        return owner;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.owner != null && this.owner.isAlive()) {
            this.teleportToOwner();
            this.applyIsolationField();
            this.handleDespawnTimers();
        } else {
            this.discard();
        }
    }

    private void applyIsolationField() {
        AABB area = this.getBoundingBox().inflate(ISOLATION_RADIUS);

        this.level.getEntitiesOfClass(Projectile.class, area, this::shouldAffectProjectile).forEach(projectile -> {
            if (projectile.getOwner() != this.owner) {
                this.stopProjectile(projectile);
            }
        });

        this.level.getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity != this.owner && !entity.isAlliedTo(this.owner)).forEach(entity -> {

            double distance = entity.distanceTo(this.owner);

            if (distance < ISOLATION_RADIUS) {
                applyBreachDamage(entity);

                if (!SkillUtils.hasSkill(entity, UltimateSkills.SUSANOO.get())) {
                    Vec3 pushDirection = entity.position().subtract(this.position()).normalize().scale(1.2);
                    entity.setDeltaMovement(pushDirection);
                    entity.hurtMarked = true;
                }
            }
        });
    }

    private void applyBreachDamage(LivingEntity target) {
        if (target.tickCount % 10 == 0) {
            target.hurt(TensuraDamageSources.elementalAttack(TensuraDamageSources.SPACE_ATTACK, target, false), 100.0F);
            target.hurt(DamageSource.mobAttack(target), 100.0F);
        }
    }

    private void teleportToOwner() {
        this.setPos(this.owner.getX(), this.owner.getY(), this.owner.getZ());
    }

    private void handleDespawnTimers() {
        this.projectilesToDespawn.entrySet().removeIf(entry -> {
            Projectile p = entry.getKey();
            int time = entry.getValue();
            if (time > 0 && p.isAlive()) {
                entry.setValue(time - 1);
                return false;
            } else {
                p.discard();
                return true;
            }
        });
    }

    private boolean shouldAffectProjectile(Projectile projectile) {
        return !(projectile instanceof AntaeusBlueProjectile) &&
                !(projectile instanceof AntaeusPurpleProjectile) &&
                !(projectile instanceof AntaeusRedProjectile);
    }

    private void stopProjectile(Projectile projectile) {
        projectile.setDeltaMovement(Vec3.ZERO);
        projectile.setNoGravity(true);
        this.projectilesToDespawn.putIfAbsent(projectile, 60 + this.level.random.nextInt(40));
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}