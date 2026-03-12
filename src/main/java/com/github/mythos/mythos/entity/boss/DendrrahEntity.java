package com.github.mythos.mythos.entity.boss;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.battlewill.melee.RoaringLionPunchArt;
import com.github.manasmods.tensura.ability.skill.extra.SpatialMotionSkill;
import com.github.manasmods.tensura.api.entity.subclass.IFollower;
import com.github.manasmods.tensura.api.entity.subclass.ITeleportation;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.entity.human.OtherworlderEntity;
import com.github.manasmods.tensura.entity.magic.projectile.AuraBulletProjectile;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.mythos.mythos.ability.mythos.skill.ultimate.god.DendrrahSkill;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;

public class DendrrahEntity extends OtherworlderEntity implements ITeleportation, FlyingAnimal, IFollower {
    public DendrrahEntity(EntityType<? extends OtherworlderEntity> type, Level level) {
        super(type, level);

        setupManualAttribute(Attributes.MAX_HEALTH, 36000.0D);
        this.setHealth(36000.0F);

        setupManualAttribute(Attributes.ATTACK_DAMAGE, 400.0D);
        setupManualAttribute(Attributes.ATTACK_SPEED, 2.5D);
        setupManualAttribute(Attributes.ARMOR, 60.0D);
        setupManualAttribute(Attributes.MOVEMENT_SPEED, 0.45D);
        setupManualAttribute(Attributes.FLYING_SPEED, 0.7D);
        setupManualAttribute(Attributes.FOLLOW_RANGE, 128.0D);
        setupManualAttribute(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
        setupManualAttribute(ForgeMod.ATTACK_RANGE.get(), 6.0D);

        TensuraEPCapability.setLivingEP(this, 200000000.0D);
        TensuraEPCapability.setCurrentLivingEP(this, 200000000.0D);
        TensuraEPCapability.setSpiritualHealth(this, 500000000.0D);
        TensuraEPCapability.sync(this);


        this.bossEvent = (ServerBossEvent) (new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED,
                BossEvent.BossBarOverlay.NOTCHED_20)).setPlayBossMusic(true);
        this.xpReward = 25000;
    }

    private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(DendrrahEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK = SynchedEntityData.defineId(DendrrahEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(DendrrahEntity.class, EntityDataSerializers.BOOLEAN);

    private final ServerBossEvent bossEvent;
    private int skillCooldown = 0;
    private int miscAnimationTicks = 0;

    private ManasSkillInstance dendrahhSkill;
    private ManasSkillInstance roaringLionPunchSkill;
    private ManasSkillInstance spatialMotionSkill;

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 36000.0D)
                .add(Attributes.ATTACK_DAMAGE, 400.0D)
                .add(Attributes.ATTACK_SPEED, 2.5D)
                .add(Attributes.ARMOR, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.45D)
                .add(Attributes.FLYING_SPEED, 0.7D)
                .add(Attributes.FOLLOW_RANGE, 128.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(ForgeMod.ATTACK_RANGE.get(), 6.0D)
                .add(TensuraAttributeRegistry.MAX_MAGICULE.get(), 100000000.0D)
                .add(TensuraAttributeRegistry.MAX_AURA.get(), 100000000.0D)
                .add(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get(), 500000000.0D);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance instance) {
        return instance.getEffect().isBeneficial();
    }

    private void setupManualAttribute(Attribute attribute, double value) {
        AttributeInstance instance = this.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }

    @Override
    public AttributeMap getAttributes() {
        return super.getAttributes();
    }

    @Override
    public AttributeInstance getAttribute(Attribute attribute) {
        return this.getAttributes().getInstance(attribute);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE, 0);
        this.entityData.define(ATTACK, 0);
        this.entityData.define(FLYING, false);
    }

    public int getPhase() {
        return this.entityData.get(PHASE);
    }

    public void setPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    public int getAttack() {
        return this.entityData.get(ATTACK);
    }

    public void setAttack(int attackId) {
        this.entityData.set(ATTACK, attackId);
        this.miscAnimationTicks = 0;

    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
    }

    public ResourceLocation getTextureLocation() {
        return new ResourceLocation("tensura", "textures/entity/otherworlder/hinata_sakaguchi.png");
    } // TEMP

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 12.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
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
                Skills.DENDRRAH.get(),
                ExtraSkills.SPATIAL_MOTION.get(),
                ResistanceSkills.ABNORMAL_CONDITION_NULLIFICATION.get(),
                ResistanceSkills.COLD_RESISTANCE.get(),
                ResistanceSkills.CORROSION_RESISTANCE.get(),
                ResistanceSkills.DARKNESS_ATTACK_RESISTANCE.get(),
                ResistanceSkills.EARTH_ATTACK_RESISTANCE.get(),
                ResistanceSkills.ELECTRICITY_RESISTANCE.get(),
                ResistanceSkills.FLAME_ATTACK_RESISTANCE.get(),
                ResistanceSkills.GRAVITY_ATTACK_RESISTANCE.get(),
                ResistanceSkills.HEAT_RESISTANCE.get(),
                ResistanceSkills.HOLY_ATTACK_RESISTANCE.get(),
                ResistanceSkills.LIGHT_ATTACK_RESISTANCE.get(),
                ResistanceSkills.MAGIC_RESISTANCE.get(),
                ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.get(),
                ResistanceSkills.PAIN_RESISTANCE.get(),
                ResistanceSkills.PARALYSIS_RESISTANCE.get(),
                ResistanceSkills.PIERCE_RESISTANCE.get(),
                ResistanceSkills.POISON_RESISTANCE.get(),
                ResistanceSkills.SPATIAL_ATTACK_RESISTANCE.get(),
                ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get(),
                ResistanceSkills.THERMAL_FLUCTUATION_RESISTANCE.get(),
                ResistanceSkills.WATER_ATTACK_RESISTANCE.get(),
                ResistanceSkills.WIND_ATTACK_RESISTANCE.get());
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
        if (this.level.isClientSide) return;

        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        if (skillCooldown > 0) skillCooldown--;
        if (this.getAttack() != 0) {
            this.miscAnimationTicks++;
            if (this.miscAnimationTicks > 40) this.setAttack(0);
        }

        this.setNoGravity(this.isFlying());

        if (getPhase() == 0 && this.getHealth() < this.getMaxHealth() * 0.5) {
            enterSecondPhase();
        }

        if (!this.hasEffect(MythosMobEffects.BLOOD_COAT.get())) {
            this.addEffect(new MobEffectInstance(MythosMobEffects.BLOOD_COAT.get(), 600, 3, false, false, true));
        }

        if (!this.hasEffect(MythosMobEffects.COMPLETE_REGENERATION.get())) {
            this.addEffect(new MobEffectInstance(MythosMobEffects.COMPLETE_REGENERATION.get(), 600, 3, false, false, true));
        }

        if (!this.hasEffect(TensuraMobEffects.PRESENCE_SENSE.get())) {
            this.addEffect(new MobEffectInstance(TensuraMobEffects.PRESENCE_SENSE.get(), 600, 32, false, false, true));
        }

        if (!this.hasEffect(TensuraMobEffects.STRENGTHEN.get())) {
            this.addEffect(new MobEffectInstance(TensuraMobEffects.STRENGTHEN.get(), 600, 10, false, false, true));
        }

        handleCombatAbilities();
    }

    private void enterSecondPhase() {
        this.setPhase(1);
        this.setFlying(true);
        this.bossEvent.setColor(BossEvent.BossBarColor.RED);
        this.addEffect(new MobEffectInstance(MythosMobEffects.ARES_BERSERKER.get(), 99999, 30, false, false));
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(900.0D);
        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 2.0F, 1.0F);
    }

    private void handleCombatAbilities() {
        LivingEntity target = this.getTarget();
        if (target == null || skillCooldown > 0) return;

        double distSq = this.distanceToSqr(target);

        if (this.isFlying()) {
            this.moveControl.setWantedPosition(target.getX(), target.getY() + 6.0D, target.getZ(), 1.0D);
            if (distSq > 100.0) {
                performDashStrike(target);
                this.skillCooldown = 50;
            } else {
                fireMagicBullet(target);
                this.skillCooldown = 30;
            }
            return;
        }

        if (distSq > 256.0) {
            this.teleportTowards(this, target, 10.0D);
            this.skillCooldown = 60;
        } else if (distSq < 16.0) {
            performLionPunch(target);
            this.skillCooldown = 40;
        }
    }

    private void fireMagicBullet(LivingEntity target) {
        if (this.level.isClientSide) return;

        this.setAttack(4);

        AuraBulletProjectile bullet = new AuraBulletProjectile(this.level, this);

        Vec3 spawnPos = this.getEyePosition().add(this.getLookAngle().scale(1.2));
        bullet.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

        bullet.setDamage(150.0F);
        bullet.setSpeed(1.5F);
        bullet.noPhysics = true;

        Vec3 dir = target.getEyePosition().subtract(spawnPos).normalize();
        bullet.shoot(dir.x, dir.y, dir.z, 1.5F, 0F);

        this.level.addFreshEntity(bullet);


        this.level.playSound(null, this.blockPosition(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.HOSTILE, 1.0F, 1.0F);
    }

    private void performLionPunch(LivingEntity target) {
        this.setAttack(1);
        target.hurt(DamageSource.mobAttack(this), 600.0F);
        SkillHelper.knockBack(this, target, 2.0F);
        this.swing(InteractionHand.MAIN_HAND);
        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.0F, 1.2F);
    }

    private void performDashStrike(LivingEntity target) {
        this.setAttack(2);
        Vec3 dash = target.position().subtract(this.position()).normalize().scale(2.0D);
        this.setDeltaMovement(dash.x, 0.1D, dash.z);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public boolean removeWhenFarAway(double dist) {
        return false;
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(Component.literal("D'endrrah"));
        this.bossEvent.setName(this.getDisplayName());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        float maxAllowedDamage = this.getMaxHealth() * 0.05F;

        if (amount > maxAllowedDamage && !source.isBypassInvul()) {
            amount = maxAllowedDamage;

            if (this.level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, this.getX(), this.getY() + 1, this.getZ(), 10, 0.5, 0.5, 0.5, 0.1);
            }
        }

        if (shouldDodge(source)) {
            return false;
        }

        return super.hurt(source, amount);
    }

    private boolean shouldDodge(DamageSource source) {
        if (this.level.isClientSide || this.getPhase() == 0) return false;

        if (this.random.nextFloat() < 0.15F && source.getEntity() instanceof LivingEntity attacker) {
            this.teleportTowards(this, attacker, 5.0D);
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 1.0F);
            this.invulnerableTime = 10;
            return true;
        }
        return false;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean result = super.doHurtTarget(target);
        if (!this.getLevel().isClientSide && dendrahhSkill != null) {
            LivingHurtEvent event = new LivingHurtEvent(this, DamageSource.mobAttack(this), 600.0F);
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
    public boolean shouldFollow() {
        return true;
    }

    @Override
    public Component getName() {
        return Component.literal("D'endrrah");
    }
}
