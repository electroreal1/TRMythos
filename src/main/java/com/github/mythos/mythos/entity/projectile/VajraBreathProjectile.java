package com.github.mythos.mythos.entity.projectile;

import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.entity.magic.breath.BreathEntity;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.registry.MythosEntityTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class VajraBreathProjectile extends BreathEntity {

    public VajraBreathProjectile(EntityType<? extends VajraBreathProjectile> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public VajraBreathProjectile(Level level, LivingEntity entity) {
        this((EntityType) MythosEntityTypes.VAJRA_BREATH.get(), level);
        this.setOwner(entity);
    }

    public void tick() {
        super.tick();
    }

    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        entity.setSecondsOnFire(5);
        entity.hurt(DamageSourceHelper.addSkillAndCost(TensuraDamageSources.thunderBreath(this, this.getOwner()), this.getMpCost(), this.getSkill()), this.getDamage());
        if (entity instanceof LivingEntity living) {
            SkillHelper.checkThenAddEffectSource(living, this.getOwner(), (MobEffect) TensuraMobEffects.PARALYSIS.get(), 200, 0, false, false, false);
            SkillHelper.checkThenAddEffectSource(living, this.getOwner(), (MobEffect) TensuraMobEffects.TRUE_BLINDNESS.get(), 200, 0, false, false, false);
        }
    }

    public void spawnParticle() {
        Entity rotation = this.getOwner();
        if (rotation instanceof LivingEntity owner) {
            Vec3 var21 = owner.getLookAngle().normalize();
            Vec3 pos = owner.position().add(var21.scale(1.6));
            double x = pos.x;
            double y = pos.y + (double)(owner.getEyeHeight() * 0.9F);
            double z = pos.z;
            double speed = owner.getRandom().nextDouble() * 0.35 + 0.35;

            for(int i = 0; i < 20; ++i) {
                double ox = Math.random() * 0.3 - 0.15;
                double oy = Math.random() * 0.3 - 0.15;
                double oz = Math.random() * 0.3 - 0.15;
                Vec3 randomVec = (new Vec3(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5)).normalize();
                Vec3 result = var21.scale(3.0).add(randomVec).normalize().scale(speed);
                owner.getLevel().addParticle((ParticleOptions) TensuraParticles.LIGHTNING_EFFECT.get(), x + ox, y + oy, z + oz, result.x, result.y, result.z);
            }
        }
    }

}
