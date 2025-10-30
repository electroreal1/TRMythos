package com.github.mythos.mythos.ability.skill.unique.evolved;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.entity.magic.projectile.SpatialArrowProjectile;
import com.github.manasmods.tensura.entity.projectile.KunaiProjectile;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.registry.skill.Skills;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.mojang.math.Vector3f;
import io.github.Memoires.trmysticism.data.repeater.PlayerActionFrame;
import io.github.Memoires.trmysticism.registry.effects.MysticismMobEffects;
import io.github.Memoires.trmysticism.registry.skill.UltimateSkills;
import io.github.Memoires.trmysticism.registry.skill.UniqueSkills;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class TheWorldSkill extends Skill {
    public TheWorldSkill(SkillType type) {super(SkillType.UNIQUE);}

    private static final ThreadLocal<Boolean> isRepeating = ThreadLocal.withInitial(() -> Boolean.valueOf(false));
    private static boolean recursionDepth;
    public static final Map<UUID, List<PlayerActionFrame>> PLAYBACK_FRAMES = new HashMap<>();

    // @Nullable
    // @Override
    // public ResourceLocation getSkillIcon() {
    //     return new ResourceLocation("trmythos", "textures/skill/unique/theworld.png
    // }

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
        return true;
    }

    public double getObtainingEpCost() {
        return 250000.0;
    }

    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {

    }

    public boolean meetEPRequirement(Player entity, double newEP) {
        SkillStorage storage = SkillAPI.getSkillsFrom((Entity)entity);
        ManasSkill PrideSkill = (ManasSkill)SkillAPI.getSkillRegistry().getValue(Skills.BLOODSUCKER.getId());
        return ((Boolean)storage.getSkill(PrideSkill)
                .map(instance -> Boolean.valueOf(instance.isMastered((LivingEntity)entity)))
                .orElse(Boolean.valueOf(false))).booleanValue();
    }

    public int modes() {
        return 4;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 4 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 4) ? 1 : (instance.getMode() + 1);
    }

    public Component getModeName(int mode) {
        MutableComponent name;
        switch (mode) {
            case 1:
                name = Component.translatable("trmythos.skill.mode.the_world.the_world");
                break;
            case 2:
                name = Component.translatable("trmythos.skill.mode.the_world.knife_throw");
                break;
            case 3:
                name = Component.translatable("trmythos.skill.mode.the_world.the_passion");
                break;
            case 4:
                name = Component.translatable("trmythos.skill.mode.the_world.menacing_pose");
                break;
            default:
                name = Component.empty();
        }
        return name;
    }

    public double magiculeCost(LivingEntity entity, ManasSkillInstance instance) {
        if (instance.getMode() == 1) {
            return 10000.0D;
        }

        if (instance.getMode() == 3) {
            return 5000.0D;
        }

        return
                0.0D;
    }

    public double auraCost(LivingEntity entity, ManasSkillInstance instance) {
        if (instance.getMode() == 2) {
            return 500.0D;
        }

        return
                0.0D;
    }

    // Stand Defense
    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        if (!event.isCanceled()) {
            LivingEntity entity = event.getEntity();
            if (instance.isToggled()) {
                DamageSource damageSource = event.getSource();
                if (!damageSource.isProjectile() && !damageSource.isMagic() &&
                        damageSource.getEntity() != null && damageSource.getEntity() == damageSource.getDirectEntity() &&
                        entity.getRandom().nextFloat() <= 0.50F) {
                    entity.level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundSource.PLAYERS, 2.0F, 1.0F);
                    event.setCanceled(true);
                    if (SkillUtils.canNegateDodge(entity, damageSource))
                        event.setCanceled(false);
                }
            }
        }
    }

    // Stand Offense
    public void onDamageEntity(ManasSkillInstance instance, LivingEntity attacker, LivingHurtEvent event) {
        CompoundTag tag = instance.getOrCreateTag();

        // Only trigger for physical attacks (ignore magic/projectile/etc.)
        if (!DamageSourceHelper.isPhysicalAttack(event.getSource())) {
            return;
        }

        // Stop if attacker is dead or invalid
        if (attacker.isRemoved() || !attacker.isAlive()) {
            return;
        }

        // Create a DamageSource for the repeating (reflected or chained) attack
        DamageSource repeater = null;
        if (attacker instanceof Player player) {
            repeater = DamageSource.playerAttack(player);
        } else if (attacker instanceof Mob mob) {
            repeater = DamageSource.mobAttack(mob);
        }

        // Handle recursive amplification (recursionDepth flag checked externally)
        if (recursionDepth && !isRepeating.get()) {
            int depth = tag.getInt("RecursionDepth");

            // Damage doubles each recursion level (exponential)
            float multiplier = (float) Math.pow(2.0, depth);
            float amplifiedDamage = event.getAmount() * multiplier;

            // Update recursion counter and modify the event damage
            tag.putInt("RecursionDepth", depth + 1);
            event.setAmount(amplifiedDamage);
        }

        // If the skill is toggled on, repeat the attack automatically
        if (instance.isToggled() && !isRepeating.get() && repeater != null) {
            isRepeating.set(true);
            try {
                // Reset entity's invulnerability timer to 0 (to allow immediate re-hit)
                event.getEntity().invulnerableTime = 0;

                // Apply the same damage again from this "repeater" source
                event.getEntity().hurt(repeater, event.getAmount());
            } finally {
                isRepeating.set(false);
            }
        }
    }

    // Menacing Pose
    public boolean onHeld(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, int heldTicks) {
        Level level = entity.level;
        if (level.isClientSide) return true;
        if (instance.getMode() == 4) {
            if (entity instanceof Player player && player.isSleeping())
                return false;

            // Apply rest effect
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.REST.get(), 5, 0, false, false, false));

            if (heldTicks % 20 == 0) {
                // Heal HP
                float healHP = instance.isMastered(entity)
                        ? (10.0F + entity.getMaxHealth() * 0.01F)
                        : 10.0F;
                entity.heal(healHP);

                // Heal spiritual HP
                float healSHP = instance.isMastered(entity)
                        ? (10.0F + (float) (entity.getAttributeValue(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get()) * 0.01D))
                        : 10.0F;
                TensuraEPCapability.healSpiritualHealth(entity, healSHP);

                // Regenerate magicules (mana) if full MP
                if (entity instanceof Player player) {
                    TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
                        double maxMP = player.getAttributeValue(TensuraAttributeRegistry.MAX_MAGICULE.get());
                        if (cap.getMagicule() >= maxMP) {
                            double regen = SkillHelper.mpRegen(player, maxMP, 2.0D);
                            CompoundTag tag = instance.getOrCreateTag();
                            tag.putDouble("storedMagicule", tag.getDouble("storedMagicule") + regen);
                            instance.markDirty();
                        }
                    });
                }

                List<LivingEntity> list = entity.level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(10.0D), entityData -> (entityData.isAlive()));
                if (!list.isEmpty())
                    for (LivingEntity target : list) {
                        if (target instanceof Player) {
                            Player player = (Player)target;
                            if ((player.isCreative()) && (player.isSpectator()))
                                continue;
                        }
                        SkillHelper.checkThenAddEffectSource(target, (Entity)entity, (MobEffect)MobEffects.MOVEMENT_SLOWDOWN, 200, 3);
                        SkillHelper.checkThenAddEffectSource(target, (Entity)entity, (MobEffect)TensuraMobEffects.FRAGILITY.get(), 200, 3);
                        SkillHelper.checkThenAddEffectSource(target, (Entity)entity, (MobEffect)MobEffects.WEAKNESS, 200, 3);
                    }
                return true;
            }
            return true;
        }
        return false;
    }

    public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() == 4 && !instance.onCoolDown()) {
            instance.setCoolDown(instance.isMastered(entity) ? 30 : 60);
        }
    }

    // Warp Shot
    public void onProjectileHit(ManasSkillInstance instance, LivingEntity entity, ProjectileImpactEvent event) {
        // Only activate if the skill is equipped in the current slot
        if (!instance.isToggled())
            return;

        // Ignore projectiles that are guaranteed to hit
        if (SkillUtils.isProjectileAlwaysHit(event.getProjectile()))
            return;

        // 25% chance to trigger (random float > 0.25F means skip)
        if (entity.getRandom().nextFloat() > 0.25F)
            return;

        // Play a deflect sound at the player's position
        Level level = entity.level;
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 2.0F, 1.0F);

        // Cancel the projectile impact (deflect/negate it)
        event.setCanceled(true);
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        BlockHitResult result;
        CompoundTag tag;
        BlockPos pos;
        SpatialArrowProjectile arrow;
        LivingEntity target;
        Vec3 vec3;
        int arrowAmount, i;
        Level level = entity.level;
        // The World
        if (instance.getMode() == 1) {
            // Check if the player has enough "magicules" (mana) to use the skill
            if (!SkillHelper.outOfMagicule(entity, instance)) {

                // Apply the "Time Stop Core" effect to the caster
                int duration = 100 + (instance.getMastery() / 100) * 20;
                entity.addEffect(new MobEffectInstance(
                        MysticismMobEffects.TIMESTOP_CORE.get(),
                        duration,
                        1,  // amplifier level
                        false, false, false
                ));

                // Find all nearby entities in a 160-block radius
                List<Entity> nearbyEntities = getEntitiesInRadius(entity, 160, true);

                // Apply "Time Stop" to all other living entities in the area
                for (Entity e : nearbyEntities) {
                    if (e instanceof LivingEntity living && living != entity) {
                        living.addEffect(new MobEffectInstance(
                                MysticismMobEffects.TIMESTOP.get(),
                                duration,
                                1,
                                false, false, false
                        ));
                    }
                }

                // Apply cooldown (shorter if mastered)
                instance.setCoolDown(instance.isMastered(entity) ? 60 : 120);

                // Add mastery points for using the skill
                instance.addMasteryPoint(entity);
            }
        }

        // Knife Throw
        if (instance.getMode() == 2) {
            // Make sure this only runs server-side
            if (level.isClientSide()) return;

            // If user is a player, check for shift (sneak)
            boolean isSneaking = (entity instanceof Player player) && player.isShiftKeyDown();

            // If sneaking → perform the spiral burst
            if (isSneaking) {
                target = SkillHelper.getTargetingEntity(entity, 20.0D, false);
                if (target == null) return;

                Vec3 targetPos = target.position().add(0.0D, target.getEyeHeight(), 0.0D);
                arrowAmount = 12;

                for (i = 0; i < arrowAmount; i++) {
                    // Compute rotated spawn position around the target
                    Vec3 offset = new Vec3(0.0D, Math.random() - 0.5D, 0.6D)
                            .normalize()
                            .scale(target.getBbHeight() + 6.0F)
                            .yRot((float) Math.toRadians(360.0F * i / arrowAmount));
                    Vec3 arrowPos = targetPos.add(offset);

                    arrow = new SpatialArrowProjectile(level, entity);
                    arrow.setSkill(instance);
                    arrow.setSpeed(1.0F);
                    arrow.setLife(50);
                    arrow.setDamage(100.0F);
                    arrow.setMpCost(5000.0F / arrowAmount);
                    arrow.setPos(arrowPos.x, arrowPos.y, arrowPos.z);

                    // Aim each arrow toward the target
                    Vec3 direction = targetPos.subtract(arrowPos).normalize();
                    arrow.shoot(direction.x, direction.y, direction.z, 1.0F, 0.0F);

                    level.addFreshEntity(arrow);
                    level.playSound(null, arrow.getX(), arrow.getY(), arrow.getZ(),
                            SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
                }

                // Optional mastery gain or cooldown for burst form
                instance.setCoolDown(10);
                instance.addMasteryPoint(entity);
                return;
            }

            // Otherwise → normal single-arrow shot
            arrow = new SpatialArrowProjectile(level, entity);
            arrow.setSkill(instance);
            arrow.setDamage(100.0F);
            arrow.setMpCost(magiculeCost(entity, instance));

            // Try warp shot, fallback to normal trajectory
            if (!warpShotArrow(entity, arrow)) {
                arrow.setPos(entity.getX(), entity.getEyeY() - 0.2D, entity.getZ());
                Vector3f facing = new Vector3f(entity.getLookAngle());
                arrow.shoot(facing.x(), facing.y(), facing.z(), 2.0F, 0.0F);
            }

            level.addFreshEntity(arrow);
            level.playSound(null, arrow.getX(), arrow.getY(), arrow.getZ(),
                    SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);

            instance.setCoolDown(1);
            instance.addMasteryPoint(entity);
        }

        // The Passion
        if (instance.getMode() == 3) {
            // If Future Vision is already active, deactivate it
            if (entity.hasEffect(TensuraMobEffects.FUTURE_VISION.get())) {
                entity.removeEffect(TensuraMobEffects.FUTURE_VISION.get());
                entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                instance.setCoolDown(10); // short cooldown
                return;
            }

            // Otherwise, activate Future Vision
            int duration = instance.isMastered(entity) ? 400 : 200; // 20s vs 10s
            int cooldown = instance.isMastered(entity) ? 30 : 20;   // 1.5s vs 1s
            instance.setCoolDown(cooldown);

            entity.addEffect(new MobEffectInstance(
                    TensuraMobEffects.FUTURE_VISION.get(),
                    duration, 0, false, false, false
            ));

            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.ENDERMAN_STARE, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    private boolean warpShotArrow(LivingEntity owner, SpatialArrowProjectile arrow) {
        // Check that the owner is alive and valid
        if (!owner.isAlive()) {
            return false;
        }

        // Try to find a target within 30 blocks in front of the owner
        Entity target = SkillHelper.getTargetingEntity(owner, 30.0D, 0.0D, false, false);
        if (target == null) {
            return false;
        }

        // Teleport and shoot the arrow from behind the target
        arrow.shootFromBehind(target, 2.0F, 0.0F);

        return true;
    }

    // Target entities in a given radius
    public static List<Entity> getEntitiesInRadius(LivingEntity entity, int radius, boolean allyCheck) {
        // Define a bounding box around the entity
        AABB aabb = new AABB(
                entity.getX() - radius, entity.getY() - radius, entity.getZ() - radius,
                entity.getX() + radius, entity.getY() + radius, entity.getZ() + radius
        );

        // Get all valid entities inside that bounding box
        List<Entity> entities = entity.level.getEntities((Entity)null, aabb, Entity::isAlive);
        List<Entity> result = new ArrayList<>();

        for (Entity e : entities) {
            double dx = e.getX() - entity.getX();
            double dy = e.getY() - entity.getY();
            double dz = e.getZ() - entity.getZ();

            // Check if the entity is within the circular radius
            double distanceSq = dx * dx + dy * dy + dz * dz;
            if (distanceSq <= radius * radius) {
                if (allyCheck) {
                    // If ally check is on, exclude certain allies (e.g., Susanoo)
                    if (!SkillUtils.hasSkill(entity, UltimateSkills.SUSANOO.get())) {
                        result.add(e);
                    }
                } else {
                    result.add(e);
                }
            }
        }
        return result;
    }

    // Time Stop duration calculation
    public int getMaxHeldTime(ManasSkillInstance instance, LivingEntity living) {
        return 100 + (instance.getMastery() / 100) * 20;
    }
}