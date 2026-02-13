package com.github.mythos.mythos.entity.boss;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.battlewill.melee.RoaringLionPunchArt;
import com.github.manasmods.tensura.ability.battlewill.projectile.MagicBulletArt;
import com.github.manasmods.tensura.ability.skill.extra.SpatialMotionSkill;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.data.TensuraTags;
import com.github.manasmods.tensura.entity.human.OtherworlderEntity;
import com.github.manasmods.tensura.entity.magic.projectile.AuraBulletProjectile;
import com.github.manasmods.tensura.network.TensuraNetwork;
import com.github.manasmods.tensura.network.play2client.RequestFxSpawningPacket;
import com.github.manasmods.tensura.registry.battlewill.ProjectileArts;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.ability.mythos.skill.ultimate.god.DendrrahSkill;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class DendrrahEntity extends OtherworlderEntity {
    public DendrrahEntity(EntityType<? extends OtherworlderEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.xpReward = 10000;
    }

    private int skillCooldown = 0;
    private ManasSkillInstance dendrahhSkill;
    private ManasSkillInstance roaringLionPunchSkill;
    private ManasSkillInstance spatialMotionSkill;
    private boolean hasWarGodReleased = false;

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 36000.0F).add(Attributes.ATTACK_DAMAGE, 120.0F).add(Attributes.ATTACK_SPEED, 2.5F).add(Attributes.ARMOR, 60.0F).add(Attributes.MOVEMENT_SPEED, 0.45F).add(Attributes.KNOCKBACK_RESISTANCE, 12.0F).add(Attributes.FOLLOW_RANGE, 128.0F).add(ForgeMod.ATTACK_RANGE.get(), 6.0F).add(ForgeMod.SWIM_SPEED.get(), 3.2F).build();
    }

//    @Override
//    public ResourceLocation getTextureLocation() {
//        return new ResourceLocation("trmythos", "textures/entity/bosses/dendrahh.png");
//    }

    public ResourceLocation getTextureLocation() {
        return new ResourceLocation("tensura", "textures/entity/otherworlder/hinata_sakaguchi.png");
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.5, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 12));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, (entity) -> entity.getType().is(TensuraTags.EntityTypes.OTHERWORLDER_PREY)));
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (this.getLevel().isClientSide) return;
        if (dendrahhSkill == null) {
            this.giveDendrrahSkill();
        }
        if (spatialMotionSkill == null) {
            this.giveSpatialMotionSkill();
        }

        if (roaringLionPunchSkill == null) {
            this.giveRoaringLionPunchSkill();
        }
    }

    @Override
    public List<ManasSkill> getUniqueSkills() {
        return List.of(
                // Original Skills
                Skills.DENDRRAH.get(), ExtraSkills.SPATIAL_MOTION.get(), ResistanceSkills.ABNORMAL_CONDITION_NULLIFICATION.get(), ResistanceSkills.COLD_RESISTANCE.get(), ResistanceSkills.CORROSION_RESISTANCE.get(), ResistanceSkills.DARKNESS_ATTACK_RESISTANCE.get(), ResistanceSkills.EARTH_ATTACK_RESISTANCE.get(), ResistanceSkills.ELECTRICITY_RESISTANCE.get(), ResistanceSkills.FLAME_ATTACK_RESISTANCE.get(), ResistanceSkills.GRAVITY_ATTACK_RESISTANCE.get(), ResistanceSkills.HEAT_RESISTANCE.get(), ResistanceSkills.HOLY_ATTACK_RESISTANCE.get(), ResistanceSkills.LIGHT_ATTACK_RESISTANCE.get(), ResistanceSkills.MAGIC_RESISTANCE.get(), ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.get(), ResistanceSkills.PAIN_RESISTANCE.get(), ResistanceSkills.PARALYSIS_RESISTANCE.get(), ResistanceSkills.PIERCE_RESISTANCE.get(), ResistanceSkills.POISON_RESISTANCE.get(), ResistanceSkills.SPATIAL_ATTACK_RESISTANCE.get(), ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get(), ResistanceSkills.THERMAL_FLUCTUATION_RESISTANCE.get(), ResistanceSkills.WATER_ATTACK_RESISTANCE.get(), ResistanceSkills.WIND_ATTACK_RESISTANCE.get());
    }

    private void giveDendrrahSkill() {
        DendrrahSkill skill = new DendrrahSkill();
        ManasSkillInstance instance = skill.createDefaultInstance();
        instance.setToggled(true);
        instance.setMastery(3000);

        SkillStorage storage = SkillAPI.getSkillsFrom(this);
        storage.learnSkill(instance);
        this.dendrahhSkill = instance;
    }

    private void giveSpatialMotionSkill() {
        SpatialMotionSkill skill = new SpatialMotionSkill();
        ManasSkillInstance instance = skill.createDefaultInstance();
        instance.setToggled(true);
        instance.setMastery(1000);

        SkillStorage storage = SkillAPI.getSkillsFrom(this);
        storage.learnSkill(instance);
        this.spatialMotionSkill = instance;
    }

    private void giveRoaringLionPunchSkill() {
        RoaringLionPunchArt skill = new RoaringLionPunchArt();
        ManasSkillInstance instance = skill.createDefaultInstance();
        instance.setToggled(true);
        instance.setMastery(1000);

        SkillStorage storage = SkillAPI.getSkillsFrom(this);
        storage.learnSkill(instance);
        this.roaringLionPunchSkill = instance;
    }

    @Override
    public void tick() {
        super.tick();

        if (skillCooldown > 0) skillCooldown--;

        if (!getLevel().isClientSide) {
            MobEffectInstance rampageEffect = this.getEffect(MythosMobEffects.ARES_BERSERKER.get());
            if (rampageEffect != null && !hasWarGodReleased) {
                transformToWarGod();
            }

            if (rampageEffect != null) {
                handleDendrahhEffects();
            }
        }


        if (!getLevel().isClientSide && spatialMotionSkill != null) {
            LivingEntity target = this.getTarget();
            if (target != null && this.distanceToSqr(target) > 8.0 * 8.0) {

            }

            if (!getLevel().isClientSide && spatialMotionSkill != null) {
                LivingEntity target2 = this.getTarget();
                if (target2 != null && this.distanceToSqr(target2) > 8.0 * 8.0) {

                    MagicBulletArt bulletSkill = new MagicBulletArt();
                    ManasSkillInstance bulletInstance = new TensuraSkillInstance(ProjectileArts.MAGIC_BULLET.get());
                    bulletInstance.setToggled(true);

                    double hpPercent = this.getHealth() / this.getMaxHealth();
                    int power = Math.max(1, (int) (hpPercent * 10));

                    CompoundTag tag = bulletInstance.getOrCreateTag();
                    tag.putInt("PowerScale", power);
                    bulletInstance.markDirty();

                    AuraBulletProjectile bullet = new AuraBulletProjectile(this.getLevel(), this);
                    bullet.setSkill(bulletInstance);
                    bullet.setSize(0.25F + 0.1F * power);
                    bullet.setSpeed(1.5F);
                    bullet.setDamage(10.0F * power);
                    bullet.setApCost(bulletSkill.auraCost(this, bulletInstance) * power);
                    bullet.setColor(bullet.getColorBySize((float) power));
                    bullet.age = 0;

                    Vec3 direction = target2.position().subtract(this.position()).normalize();
                    bullet.setDeltaMovement(direction.x, direction.y, direction.z);
                    this.getLevel().addFreshEntity(bullet);

                    this.getLevel().playSound(null, this.blockPosition(), SoundEvents.ARROW_SHOOT, SoundSource.HOSTILE, 1.5F, 1.0F);
                    TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.END_ROD, 1.0);
                }
            }

            if (!getLevel().isClientSide && roaringLionPunchSkill != null) {
                if (target != null && this.distanceToSqr(target) < 4.0 * 4.0) {
                    getLevel().playSound(null, blockPosition(), SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.HOSTILE, 1.5F, 1.0F);

                    if (this.getMainHandItem().isEmpty()) {

                        double reach = 3.0 + this.getAttributeValue(ForgeMod.ATTACK_RANGE.get());
                        LivingEntity realTarget = SkillHelper.getTargetingEntity(this, reach, false);
                        if (realTarget != null) {
                            Level level = this.getLevel();
                            float damage = (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0);
                            DamageSource source = DamageSourceHelper.addSkillAndCost(DamageSource.mobAttack(this), 0.0);
                            realTarget.hurt(source, damage);
                            SkillHelper.knockBack(this, realTarget, 0.015F * damage);
                            this.swing(InteractionHand.MAIN_HAND, true);
                            level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 1.0F, 1.0F);
                        }
                    }
                }
            }
        }
    }

    private void handleDendrahhEffects() {
        if (this.tickCount % 40 == 0) {
            TensuraNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this),
                    new RequestFxSpawningPacket(new ResourceLocation("tensura:wrath_boost"),
                            this.getId(), this.getX(), this.getY(), this.getZ(), 0.0, 1.0, 0.0, true, false));

            this.addEffect(new MobEffectInstance(MythosMobEffects.ARES_BERSERKER.get(), 400, 2, false, false , false));
        }

        if (this.tickCount % 200 == 0) {
            Level level = this.getLevel();
            level.playSound(null, this.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 5.0F, 0.8F);

            level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(10), e -> e != this)
                    .forEach(target -> {
                        if (!(target instanceof Mob mob && mob.getTarget() == this)) {
                            target.hurt(DamageSource.mobAttack(this), 8.0F);
                        }
                    });
        }
    }

    private void transformToWarGod() {
        if (hasWarGodReleased) return;
        hasWarGodReleased = true;

        Level level = this.getLevel();
        this.setHealth(50000);

        AttributeInstance attackAttr = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr != null) {
            attackAttr.setBaseValue(180.0F);
        }

        this.addEffect(new MobEffectInstance(MythosMobEffects.ARES_BERSERKER.get(), 20 * 60 * 5, 12, false, false, false));

        level.playSound(null, this.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 5.0F, 1.0F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 1.0, this.getZ(), 60, 2.0, 2.0, 2.0, 0.2);
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean result = super.doHurtTarget(target);
        if (!this.getLevel().isClientSide && dendrahhSkill != null) {
            LivingHurtEvent event = new LivingHurtEvent(this, DamageSource.mobAttack(this), 10.0F);
            dendrahhSkill.getSkill().onDamageEntity(dendrahhSkill, this, event);
        }
        return result;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }
}
