package com.github.mythos.mythos.ability.mythos.skill.ultimate.lord;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.registry.dimensions.TensuraDimensions;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.manasmods.tensura.world.TensuraGameRules;
import com.mojang.math.Vector3f;
import io.github.Memoires.trmysticism.registry.effects.MysticismMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SatanSkill extends Skill {
    public SatanSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    private final Map<BlockPos, BlockState> originalBlocks = new HashMap<>();
    private final Map<BlockPos, Integer> restorationTimers = new HashMap<>();

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (instance.isTemporarySkill()) return;

        SkillUtils.learnSkill(entity, UniqueSkills.GREAT_SAGE.get());
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        Level level = entity.level;
        SkillStorage storage = SkillAPI.getSkillsFrom(entity);
        if (level.dimension() == Level.OVERWORLD && entity.tickCount % 5 == 0) {
            if (storage.getSkill(ResistanceSkills.PHYSICAL_ATTACK_NULLIFICATION.get()).isEmpty()) {
                SkillUtils.learnSkill(entity, ResistanceSkills.PHYSICAL_ATTACK_NULLIFICATION.get(), 500);
            }
            if (storage.getSkill(ResistanceSkills.PIERCE_NULLIFICATION.get()).isEmpty()) {
                SkillUtils.learnSkill(entity, ResistanceSkills.PIERCE_NULLIFICATION.get(), 500);
            }
            if (storage.getSkill(ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get()).isEmpty()) {
                SkillUtils.learnSkill(entity, ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get(), 500);
            }
            if (storage.getSkill(ResistanceSkills.ABNORMAL_CONDITION_NULLIFICATION.get()).isEmpty()) {
                SkillUtils.learnSkill(entity, ResistanceSkills.ABNORMAL_CONDITION_NULLIFICATION.get(), 500);
            }
            if (TensuraGameRules.canSkillGrief(level)) {
                BlockPos center = entity.blockPosition();
                int radius = 7;

                for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -1, -radius), center.offset(radius, -1, radius))) {
                    BlockState currentState = level.getBlockState(pos);

                    if (currentState.isSolidRender(level, pos) && !currentState.is(Blocks.MAGMA_BLOCK) && !originalBlocks.containsKey(pos.immutable())) {

                        originalBlocks.put(pos.immutable(), currentState);
                        restorationTimers.put(pos.immutable(), 200);

                        level.setBlockAndUpdate(pos, Blocks.MAGMA_BLOCK.defaultBlockState());
                    }
                }
            }

            Iterator<Map.Entry<BlockPos, Integer>> iterator = restorationTimers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<BlockPos, Integer> entry = iterator.next();
                BlockPos pos = entry.getKey();
                int remainingTime = entry.getValue();

                if (entity.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) > 225) {
                    remainingTime -= 5;
                } else {
                    remainingTime--;
                }

                if (remainingTime <= 0) {
                    BlockState oldState = originalBlocks.get(pos);
                    if (oldState != null) {
                        level.setBlockAndUpdate(pos, oldState);
                    }

                    originalBlocks.remove(pos);
                    iterator.remove();
                } else {
                    entry.setValue(remainingTime);
                }
            }

        } else if (level.dimension().equals(TensuraDimensions.HELL) || level.dimension() == Level.NETHER) {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 2, false, false, false));
        }

    }

    @Override
    public void onTakenDamage(ManasSkillInstance instance, LivingDamageEvent event) {
        Entity attacker = event.getSource().getEntity();
        DamageSource source = event.getSource();
        Level level = event.getEntity().level;

        if (TensuraEPCapability.isMajin((LivingEntity) attacker)) {
            double reflected = event.getAmount() * 0.75;
            event.setCanceled(true);
            attacker.hurt(source, (float) reflected);
        }

        if (level.dimension() == Level.NETHER || level.dimension().equals(TensuraDimensions.HELL)) {
            double amount = event.getAmount() * 0.40;
            event.setAmount((float) amount);
        }
    }

    @Override
    public void onTouchEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent event) {
        LivingEntity attacked = event.getEntity();
        Level level = entity.level;

        if (level.dimension() == Level.NETHER || level.dimension().equals(TensuraDimensions.HELL)) {
            attacked.addEffect(new MobEffectInstance(MysticismMobEffects.MARKED_FOR_DEATH.get()));
        }
    }

    @Override
    public int modes() {
        return 3;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse) return (instance.getMode() == 1) ? 3 : (instance.getMode() - 1);
        else return (instance.getMode() == 3) ? 1 : (instance.getMode() + 1);
    }

    public Component getModeName(int mode) {
        MutableComponent name;
        switch (mode) {
            case 1 -> name = Component.literal("Lord of Hell's Fury");
            case 2 -> name = Component.literal("Lord of Hell's Call");
            case 3 -> name = Component.literal("Pact with the Devil");
            default -> name = Component.empty();
        }
        return name;
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity owner, int heldTicks) {
        if (instance.getMode() == 2) {
            if (!(owner instanceof Player)) return false;
            Level level = owner.level;
            if (!(level instanceof ServerLevel server)) return false;

            double radius = 100.0;
            double pullStrength = 5.0;
            double hellDistance = 20.0;
            double rotation = heldTicks * 0.01;

            drawRotatingPentagram(server, owner.position(), 20, rotation);

            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, owner.getBoundingBox().inflate(radius), e -> e != owner);

            RandomSource rand = level.random;

            for (LivingEntity target : entities) {
                Vec3 dir = new Vec3(owner.getX() - target.getX(), owner.getY() - target.getY(), owner.getZ() - target.getZ());

                double distance = dir.length();
                if (distance < 0.001) continue;


                double angle = Math.atan2(dir.z, dir.x) + Math.PI / 2;
                double spiralX = Math.cos(angle) * 0.2;
                double spiralZ = Math.sin(angle) * 0.2;

                Vec3 motion = dir.normalize().scale(pullStrength).add(spiralX, 0, spiralZ);
                target.setDeltaMovement(motion.x, motion.y * 0.1, motion.z);


                for (int i = 0; i < 3; i++) {
                    double px = target.getX() + (rand.nextDouble() - 0.5) * 0.5;
                    double py = target.getY() + rand.nextDouble();
                    double pz = target.getZ() + (rand.nextDouble() - 0.5) * 0.5;
                    level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, px, py, pz, 0, 0.05, 0);
                }


                if (distance <= hellDistance) {
                    ResourceKey<Level> hellDimension = TensuraDimensions.HELL;
                    ServerLevel hellLevel = server.getServer().getLevel(hellDimension);
                    if (hellLevel == null) continue;


                    for (int i = 0; i < 20; i++) {
                        double ox = (rand.nextDouble() - 0.5);
                        double oy = rand.nextDouble() * 1.5;
                        double oz = (rand.nextDouble() - 0.5);
                        level.addParticle(ParticleTypes.PORTAL, target.getX() + ox, target.getY() + oy, target.getZ() + oz, 0, 0.05, 0);
                    }

                    if (target instanceof ServerPlayer serverPlayer) {
                        serverPlayer.teleportTo(hellLevel, target.getX(), target.getY() + 1, target.getZ(), target.getYRot(), target.getXRot());
                    } else if (target instanceof Mob mob) {
                        Mob newMob = (Mob) mob.getType().create(hellLevel);
                        if (newMob != null) {
                            newMob.moveTo(target.getX(), target.getY() + 1, target.getZ(), target.getYRot(), target.getXRot());
                            hellLevel.addFreshEntity(newMob);
                            target.remove(Entity.RemovalReason.DISCARDED);
                        }
                    }
                }
            }


            for (int i = 0; i < 15; i++) {
                double ox = (rand.nextDouble() - 0.5) * radius;
                double oz = (rand.nextDouble() - 0.5) * radius;
                level.addParticle(ParticleTypes.SOUL, owner.getX() + ox, owner.getY() + 0.5, owner.getZ() + oz, 0, -0.05, 0);
            }
            return true;
        }
        return true;
    }

    private void drawRotatingPentagram(ServerLevel level, Vec3 center, double radius, double rotation) {
        DustParticleOptions redDust = new DustParticleOptions(new Vector3f(1.0f, 0.0f, 0.0f), 1.5f);

        int points = 5;
        Vec3[] vertices = new Vec3[points];

        for (int i = 0; i < points; i++) {
            double angle = i * 2.0 * Math.PI / points - (Math.PI / 2.0) + rotation;
            vertices[i] = new Vec3(center.x + Math.cos(angle) * radius, center.y + 0.3, center.z + Math.sin(angle) * radius);
        }

        for (int i = 0; i < points; i++) {
            int next = (i + 2) % points;
            spawnParticleLine(level, redDust, vertices[i], vertices[next]);
        }

        int circleParticles = 300;
        for (int i = 0; i < circleParticles; i++) {
            double angle = i * 2.0 * Math.PI / circleParticles + rotation;
            double cx = center.x + Math.cos(angle) * radius;
            double cz = center.z + Math.sin(angle) * radius;

            level.sendParticles(redDust, cx, center.y + 0.3, cz, 1, 0, 0, 0, 0);
        }
    }

    private void spawnParticleLine(ServerLevel level, DustParticleOptions particle, Vec3 start, Vec3 end) {
        double distance = start.distanceTo(end);
        int count = (int) (distance * 2);

        for (int i = 0; i <= count; i++) {
            double pct = (double) i / count;
            double px = start.x + (end.x - start.x) * pct;
            double py = start.y + (end.y - start.y) * pct;
            double pz = start.z + (end.z - start.z) * pct;

            level.sendParticles(particle, px, py, pz, 1, 0, 0, 0, 0);
        }
    }

}
