package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.manascore.attribute.ManasCoreAttributes;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.race.RaceHelper;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.util.TensuraAdvancementsHelper;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CommonSenseSkill extends Skill {
    public CommonSenseSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    protected static final UUID ACCELERATION = UUID.fromString("46dc5eee-34e9-4a6c-ad3d-58048cb06c6f");

    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/common_sense.png");
    }

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public int getMaxMastery() {
        return 1000;
    }

    public double getObtainingEpCost() {
        return 100000;
    }

    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        onToggle(instance, entity, ACCELERATION, true);
    }

    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        onToggle(instance, entity, ACCELERATION, false);
    }

    public static void onToggle(ManasSkillInstance instance, LivingEntity entity, UUID uuid, boolean on) {
        if (on) {
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null) {
                AttributeModifier speedModifier = new AttributeModifier(uuid, "Thought Acceleration", instance.isMastered(entity) ? 0.02D : 0.01D, AttributeModifier.Operation.ADDITION);
                if (!speed.hasModifier(speedModifier))
                    speed.addTransientModifier(speedModifier);
            }
            AttributeInstance attackSpeed = entity.getAttribute(Attributes.ATTACK_SPEED);
            if (attackSpeed != null) {
                AttributeModifier speedModifier = new AttributeModifier(uuid, "Thought Acceleration", instance.isMastered(entity) ? 0.4D : 0.2D, AttributeModifier.Operation.ADDITION);
                if (!attackSpeed.hasModifier(speedModifier))
                    attackSpeed.addTransientModifier(speedModifier);
            }
        } else {
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5F, 0.5F);
            AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null)
                speed.removeModifier(uuid);
            AttributeInstance attackSpeed = entity.getAttribute(Attributes.ATTACK_SPEED);
            if (attackSpeed != null)
                attackSpeed.removeModifier(uuid);
        }
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.isToggled()) {
            CompoundTag tag = instance.getOrCreateTag();
            int time = tag.getInt("activatedTimes");
            if (time % 10 == 0)
                addMasteryPoint(instance, entity);
            tag.putInt("activatedTimes", time + 1);
        }
    }

    @Override
    public List<MobEffect> getImmuneEffects(ManasSkillInstance instance, LivingEntity entity) {
        List<MobEffect> list = new ArrayList<>();
        list.add((MobEffect) TensuraMobEffects.INSANITY.get());
        list.add((MobEffect) TensuraMobEffects.FEAR.get());
        list.add((MobEffect) TensuraMobEffects.MIND_CONTROL.get());
        return list;
    }

    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        if (event.isCanceled())
            return;
        LivingEntity entity = event.getEntity();
        if (!isInSlot(entity))
            return;
        DamageSource damageSource = event.getSource();
        if (damageSource.isBypassInvul() || damageSource.isBypassArmor())
            return;
        if (damageSource.getEntity() == null || damageSource.getEntity() != damageSource.getDirectEntity())
            return;
        if (Objects.requireNonNull(entity.getAttribute(Attributes.LUCK)).getValue() > 0.50F)
            return;
        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 2.0F, 1.0F);
        event.setCanceled(true);
        if (SkillUtils.canNegateDodge(entity, damageSource))
            event.setCanceled(false);
    }

    public void onProjectileHit(ManasSkillInstance instance, LivingEntity entity, ProjectileImpactEvent event) {
        if (!isInSlot(entity))
            return;
        if (SkillUtils.isProjectileAlwaysHit(event.getProjectile()))
            return;
        if (Objects.requireNonNull(entity.getAttribute(Attributes.LUCK)).getValue() > 0.50F)
            return;
        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 2.0F, 1.0F);
        event.setCanceled(true);
    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity attacker, LivingHurtEvent e) {
        if (!isInSlot(attacker))
            return;
        DamageSource source = e.getSource();
        if (source.getEntity() != attacker)
            return;
        if (!DamageSourceHelper.isPhysicalAttack(source))
            return;
        if (Objects.requireNonNull(attacker.getAttribute(Attributes.LUCK)).getValue() <= 0.50D)
            return;
        LivingEntity entity = e.getEntity();
        if (SkillUtils.canNegateCritChance((Entity)entity))
            return;
        double critical = Objects.requireNonNull(attacker.getAttribute(ManasCoreAttributes.CRIT_MULTIPLIER.get())).getValue();
        e.setAmount((float)(e.getAmount() * critical));
        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, attacker
                .getSoundSource(), 1.0F, 1.0F);
        Level level = attacker.getLevel();
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource()
                    .broadcast(attacker, new ClientboundAnimatePacket(entity, 4));
        }
    }

    public void onLearnSkill(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, @NotNull UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            if (entity instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) entity;
                TensuraAdvancementsHelper.grant(player, TensuraAdvancementsHelper.Advancements.MASTER_SMITH);
            }
        }
    }

    public int modes() {
        return 1;
    }

    public Component getModeName(int mode) {
        MutableComponent name;
        switch (mode) {
            case 1:
                name = Component.translatable("trmythos.skill.mode.common_sense.trivia");
                break;
            default:
                name = Component.empty();
        }
        return name;
    }

    public double magiculeCost(LivingEntity entity, ManasSkillInstance instance) {
        if (instance.getMode() == 1) {
            return 1000.0D;
        }
        return 0.0D;
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        Level level = entity.getLevel();
        useTrivia(instance, entity, level);
    }

    private void useTrivia(ManasSkillInstance instance, LivingEntity entity, Level level) {
        addMasteryPoint(instance, entity);
        instance.setCoolDown(1);

        Vec3 target = entity.getEyePosition().add(entity.getLookAngle().scale(15.0D));
        Vec3 source = entity.getEyePosition().add(0.0D, 1.6D, 0.0D);
        Vec3 delta = target.subtract(source);
        Vec3 direction = delta.normalize();

        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CHICKEN_AMBIENT, SoundSource.PLAYERS, 5.0F, 1.0F);
        int steps = Mth.floor(delta.length());

        for (int i = 1; i < steps; i++) {

            Vec3 particlePos = source.add(direction.scale(i));

            ((ServerLevel) level).sendParticles(TensuraParticles.SONIC_BLAST.get(), particlePos.x, particlePos.y, particlePos.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);

            AABB hitbox = new AABB(new BlockPos(particlePos)).inflate(3.0D);

            List<LivingEntity> targets = level.getEntitiesOfClass(
                    LivingEntity.class,
                    hitbox,
                    living -> !living.isAlliedTo(entity)
            );

            if (!targets.isEmpty()) {
                for (LivingEntity living : targets) {

                    if (RaceHelper.isSpiritualLifeForm(living))
                        continue;

                    DamageSource dmg = TensuraDamageSources.mindRequiem(entity);

                    float amount = instance.isMastered(entity) ? 150.0F : 75.0F;

                    living.hurt(sourceWithMP(dmg, entity, instance), amount);
                }
            }
        }
    }
}

