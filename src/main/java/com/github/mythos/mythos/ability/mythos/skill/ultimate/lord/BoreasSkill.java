package com.github.mythos.mythos.ability.mythos.skill.ultimate.lord;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.entity.BoreasBarrier;
import com.github.mythos.mythos.registry.MythosDimensions;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class BoreasSkill extends Skill {
    protected static final UUID ACCELERATION = UUID.fromString("753d7901-7c9c-49de-ad16-6b2b6e1e9342");

    public BoreasSkill() {
        super(SkillType.ULTIMATE);
    }

    @Override
    public double getObtainingEpCost() {
        return 2000000;
    }

    @Override
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/ultimate/boreas.png");
    }

    public boolean meetEPRequirement(@NotNull Player player, double newEP) {
        if (!EnableUltimateSkillObtainment()) return false;
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, Skills.INTROVERT.get()) && SkillUtils.isSkillMastered(player, UniqueSkills.SUPPRESSOR.get());
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
        Level level = entity.getLevel();
        BoreasBarrier barrier = new BoreasBarrier(level, entity);
        level.addFreshEntity(barrier);
    }

    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);
        Level var4 = entity.level;
        if (var4 instanceof ServerLevel serverLevel) {
            serverLevel.getEntitiesOfClass(BoreasBarrier.class, entity.getBoundingBox().inflate(50.0))
                    .stream().filter((barrier) -> barrier.getOwner() != null && barrier.getOwner()
                            .equals(entity)).forEach(Entity::discard);
        }
    }

    @Override
    public int getMaxMastery() {
        return 2000;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity living) {
        if (!instance.isToggled()) return;
        instance.addMasteryPoint(living);
    }

    @Override
    public void onProjectileHit(ManasSkillInstance instance, LivingEntity living, ProjectileImpactEvent event) {
        Entity projectile = event.getProjectile();

        event.setCanceled(true);

        Vec3 bounce = projectile.getDeltaMovement().scale(-0.5).add(0, 0.2, 0);
        projectile.setDeltaMovement(bounce);

        living.level.playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.MASTER, 1, 2);
    }

    public Component getModeName(int mode) {
        MutableComponent var10000;
        switch (mode) {
            case 1 -> var10000 = Component.translatable("trmythos.skill.boreas.sanctuary");
            case 2 -> var10000 = Component.translatable("trmythos.skill.boreas.spiritual");
            case 3 -> var10000 = Component.translatable("trmythos.skill.boreas.mind");
            case 4 -> var10000 = Component.translatable("trmythos.skill.boreas.beckon");
            default -> var10000 = Component.empty();
        }

        return var10000;
    }

    @Override
    public int nextMode(@NotNull LivingEntity entity, @NotNull TensuraSkillInstance instance, boolean reverse) {
        if (reverse) {
            return instance.getMode() <= 1 ? 4 : instance.getMode() - 1;
        } else {
            return instance.getMode() >= 4 ? 1 : instance.getMode() + 1;
        }
    }

    @Override
    public int modes() {
        return 4;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() == 1) {
            if (!(entity instanceof ServerPlayer player)) return;

            ServerLevel currentLevel = player.getLevel();
            ServerLevel sanctuary = player.getServer().getLevel(MythosDimensions.SANCTUARY_KEY);
            if (instance.onCoolDown()) return;

            if (sanctuary == null) return;

            if (currentLevel.dimension().equals(MythosDimensions.SANCTUARY_KEY)) {
                collapseSanctuary(player, instance);
            } else {
                enterSanctuary(player, instance);
            }

            instance.setCoolDown(100);
        } else if (instance.getMode() == 2) {
            onSpiritualLockdown(instance, entity);
        } else if (instance.getMode() == 3) {
            onMindCrush(instance, entity);
        } else if (instance.getMode() == 4) {
            onBeckon(entity);
        }
    }

    public void onBeckon(LivingEntity user) {
        if (!(user instanceof ServerPlayer player)) return;

        if (!player.level.dimension().equals(MythosDimensions.SANCTUARY_KEY)) {
            player.displayClientMessage(Component.literal("The Sanctuary does not hear your call from here.")
                    .withStyle(ChatFormatting.RED), true);
            return;
        }


        BlockHitResult hit = (BlockHitResult) player.pick(100.0D, 0.0F, false);
        Vec3 targetPos = hit.getLocation();

        if (hit.getType() == HitResult.Type.MISS) {
            targetPos = player.position().add(player.getLookAngle().scale(100));
        }

        ServerLevel sanctuary = player.getLevel();
        boolean isShifting = player.isShiftKeyDown();

        AABB searchArea = AABB.ofSize(new Vec3(0, 65, 0), 2000, 256, 2000);
        List<LivingEntity> entities = sanctuary.getEntitiesOfClass(LivingEntity.class, searchArea);

        int affectedCount = 0;
        for (LivingEntity entity : entities) {
            if (entity == player) continue;

            boolean ally = player.isAlliedTo(entity);

            if ((isShifting && !ally) || (!isShifting && ally)) {
                entity.teleportTo(targetPos.x, targetPos.y + 0.1, targetPos.z);

                sanctuary.sendParticles(ParticleTypes.REVERSE_PORTAL,
                        entity.getX(), entity.getY() + 1, entity.getZ(),
                        10, 0.2, 0.5, 0.2, 0.05);

                affectedCount++;
            }
        }

        if (affectedCount > 0) {
            float pitch = isShifting ? 0.5f : 1.5f;
            sanctuary.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0f, pitch);

            String type = isShifting ? "Intruders" : "Allies";
            player.displayClientMessage(Component.literal("Beckoning " + type + " to your gaze.")
                    .withStyle(ChatFormatting.AQUA), true);
        }
    }

    public void onMindCrush(ManasSkillInstance instance, LivingEntity user) {
        if (!(user instanceof ServerPlayer player)) return;

        if (!player.level.dimension().equals(MythosDimensions.SANCTUARY_KEY)) {
            player.displayClientMessage(Component.literal("The mind cannot be crushed outside of your Sanctuary.")
                    .withStyle(ChatFormatting.RED), true);
            return;
        }

        if (instance.getCoolDown() > 0) return;

        ServerLevel sanctuary = player.getLevel();

        player.getServer().tell(new net.minecraft.server.TickTask(player.getServer().getTickCount() + 5, () -> {

            float damageAmount = instance.isMastered(user) ? 1000.0f : 500.0f;

            AABB dimensionRange = AABB.ofSize(new Vec3(0, 65, 0), 10000, 256, 10000);
            List<LivingEntity> targets = sanctuary.getEntitiesOfClass(LivingEntity.class, dimensionRange);

            for (LivingEntity target : targets) {
                if (target != player && !user.isAlliedTo(target)) {
                    DamageSourceHelper.directSpiritualHurt(target, player, damageAmount);

                    target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 2));
                    target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                }
            }

            sanctuary.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.PLAYERS, 2.0f, 2.0f);
        }));

        instance.setCoolDown(20);
        instance.addMasteryPoint(player);
    }

    public void onSpiritualLockdown(ManasSkillInstance instance, LivingEntity user) {
        if (!(user instanceof ServerPlayer player)) return;

        if (!player.level.dimension().equals(MythosDimensions.SANCTUARY_KEY)) {
            player.displayClientMessage(Component.literal("Spiritual Lockdown can only be manifested within the Sanctuary.")
                    .withStyle(ChatFormatting.RED), true);
            return;
        }

        if (instance.getCoolDown() > 0) return;

        ServerLevel sanctuary = player.getLevel();

        sanctuary.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 5.0f, 0.5f);

        sanctuary.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BELL_RESONATE, SoundSource.PLAYERS, 5.0f, 0.5f);

        sanctuary.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 5.0f, 0.5f);

        sanctuary.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SHROOMLIGHT_PLACE, SoundSource.PLAYERS, 5.0f, 0.5f);

        AABB dimensionRange = AABB.ofSize(new Vec3(0, 65, 0), 10000, 256, 10000);
        List<LivingEntity> entities = sanctuary.getEntitiesOfClass(LivingEntity.class, dimensionRange);

        for (LivingEntity target : entities) {
            if (target != player && !user.isAlliedTo(target)) {
                applyAntiSkill(target);
            }
        }

        instance.setCoolDown(60);
    }

    private void applyAntiSkill(LivingEntity target) {
        target.addEffect(new MobEffectInstance(TensuraMobEffects.ANTI_SKILL.get(), 10, 1, false, false, false));

        if (target.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.WITCH,
                    target.getX(), target.getY() + 1, target.getZ(),
                    20, 0.5, 0.5, 0.5, 0.1);
        }

        if (target instanceof Player p) {
            p.displayClientMessage(Component.literal("Your spiritual path has been locked!")
                    .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD), true);
        }
    }

    private void enterSanctuary(ServerPlayer owner, ManasSkillInstance instance) {
        ServerLevel currentLevel = owner.getLevel();
        ServerLevel sanctuary = owner.getServer().getLevel(MythosDimensions.SANCTUARY_KEY);

        if (sanctuary == null) {
            owner.displayClientMessage(Component.literal("Error: Sanctuary dimension not found."), false);
            return;
        }

        this.writeLocationToTag(instance.getOrCreateTag(), owner);

        CompoundTag originalPositions = new CompoundTag();

        float radius = instance.isMastered(owner) ? 20.0F : 10.0F;
        List<LivingEntity> nearbyEntities = currentLevel.getEntitiesOfClass(
                LivingEntity.class,
                owner.getBoundingBox().inflate(radius),
                (entity) -> entity != owner
        );

        for (LivingEntity entity : nearbyEntities) {
            CompoundTag entityData = entity.getPersistentData();

            entityData.putBoolean("InSanctuary", true);
            entityData.putDouble("SanctuaryRetX", entity.getX());
            entityData.putDouble("SanctuaryRetY", entity.getY());
            entityData.putDouble("SanctuaryRetZ", entity.getZ());
            entityData.putString("SanctuaryRetDim", currentLevel.dimension().location().toString());
            entityData.putBoolean("IsSanctuaryOwner", false);

            originalPositions.put(entity.getUUID().toString(), entityData.copy());

            if (entity.hasEffect(TensuraMobEffects.SPATIAL_BLOCKADE.get())) {
                entity.removeEffect(TensuraMobEffects.SPATIAL_BLOCKADE.get());
            }

            SkillHelper.moveAcrossDimensionTo(entity, 0.5D, 65.0D, 0.5D, entity.getYRot(), entity.getXRot(), sanctuary);
        }

        owner.getPersistentData().putBoolean("IsSanctuaryOwner", true);
        if (owner.hasEffect(TensuraMobEffects.SPATIAL_BLOCKADE.get())) {
            owner.removeEffect(TensuraMobEffects.SPATIAL_BLOCKADE.get());
        }

        SkillHelper.moveAcrossDimensionTo(owner, 0.5D, 65.0D, 0.5D, owner.getYRot(), owner.getXRot(), sanctuary);

        CompoundTag skillTag = instance.getOrCreateTag();
        skillTag.put("OriginalPositions", originalPositions);
        skillTag.putBoolean("InSanctuary", true);

        instance.setCoolDown(100);

        sanctuary.playSound(null, 0.5, 65, 0.5, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    private void writeLocationToTag(CompoundTag tag, Player player) {
        tag.putDouble("PosX", player.getX());
        tag.putDouble("PosY", player.getY());
        tag.putDouble("PosZ", player.getZ());
        tag.putString("Level", player.level.dimension().location().toString());
    }

    private void collapseSanctuary(ServerPlayer owner, ManasSkillInstance instance) {
        ServerLevel sanctuary = owner.getLevel();
        CompoundTag skillTag = instance.getOrCreateTag();

        CompoundTag originalPositions = skillTag.getCompound("OriginalPositions");

        sanctuary.playSound(null, owner.getX(), owner.getY(), owner.getZ(),
                SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 2.0f, 0.5f);


        for (ServerPlayer p : sanctuary.players()) {
            String uuidKey = p.getUUID().toString();

            if (originalPositions.contains(uuidKey)) {
                CompoundTag data = originalPositions.getCompound(uuidKey);
                returnEntityToOrigin(p, data);
            } else {
                returnToOrigin(p);
            }

        }

        List<LivingEntity> mobs = sanctuary.getEntitiesOfClass(LivingEntity.class,
                owner.getBoundingBox().inflate(500),
                (e) -> !(e instanceof Player) && e.getPersistentData().getBoolean("InSanctuary"));

        for (LivingEntity mob : mobs) {
            String uuidKey = mob.getUUID().toString();
            if (originalPositions.contains(uuidKey)) {
                returnEntityToOrigin(mob, originalPositions.getCompound(uuidKey));
            } else {
                mob.discard();
            }
        }

        skillTag.putBoolean("InSanctuary", false);
        skillTag.remove("OriginalPositions");
    }

    private void returnEntityToOrigin(LivingEntity entity, CompoundTag data) {
        String dimStr = data.getString("SanctuaryRetDim");
        if (dimStr.isEmpty()) dimStr = data.getString("dimension");

        ResourceLocation dimLoc = new ResourceLocation(dimStr);
        ResourceKey<Level> destKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, dimLoc);
        ServerLevel dest = entity.getServer().getLevel(destKey);

        if (dest != null) {
            double x = data.contains("SanctuaryRetX") ? data.getDouble("SanctuaryRetX") : data.getDouble("x");
            double y = data.contains("SanctuaryRetY") ? data.getDouble("SanctuaryRetY") : data.getDouble("y");
            double z = data.contains("SanctuaryRetZ") ? data.getDouble("SanctuaryRetZ") : data.getDouble("z");

            SkillHelper.moveAcrossDimensionTo(entity, x, y, z, entity.getYRot(), entity.getXRot(), dest);
        }

        entity.getPersistentData().remove("InSanctuary");
        entity.getPersistentData().remove("IsSanctuaryOwner");
    }

    private void returnToOrigin(ServerPlayer player) {
        CompoundTag nbt = player.getPersistentData();
        if (nbt.contains("SanctuaryRetDim")) {
            ResourceLocation dimLoc = new ResourceLocation(nbt.getString("SanctuaryRetDim"));
            ResourceKey<Level> returnDimKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, dimLoc);
            ServerLevel returnWorld = Objects.requireNonNull(player.getServer()).getLevel(returnDimKey);

            if (returnWorld != null) {
                double x = nbt.getDouble("SanctuaryRetX");
                double y = nbt.getDouble("SanctuaryRetY");
                double z = nbt.getDouble("SanctuaryRetZ");

                player.teleportTo(returnWorld, x, y, z, player.getYRot(), player.getXRot());
            }
        }

        nbt.remove("IsSanctuaryOwner");
        nbt.remove("is_suppressed");
    }

}
