package com.github.mythos.mythos.ability.mythos.skill.ultimate.prince;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.menu.SpatialMenu;
import com.github.manasmods.tensura.registry.dimensions.TensuraDimensions;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.handler.KhaosHandler;
import com.github.mythos.mythos.networking.MythosNetwork;
import com.github.mythos.mythos.networking.play2server.GreatSilencePacket;
import com.github.mythos.mythos.networking.play2server.ScreenShakePacket;
import com.github.mythos.mythos.networking.play2server.ShaderPacket;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import com.github.mythos.mythos.util.MythosUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ZeroSkill extends Skill {
    protected static final UUID ACCELERATION = UUID.fromString("58dc5eee-34e9-4a6c-ad3d-58048cb06c6f");

    public ZeroSkill(SkillType type) {
        super(SkillType.ULTIMATE);
    }

    @Override
    public double getObtainingEpCost() {
        return 2500000;
    }

    @Override
    public boolean meetEPRequirement(Player player, double newEP) {
        return TensuraEPCapability.getCurrentEP(player) >= getObtainingEpCost() &&
                SkillUtils.isSkillMastered(player, Skills.KHAOS.get());
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Zero, Singularity of the Void");
    }

    @Override
    public Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.literal("Dimensional Fold");
            case 2 -> Component.literal("Boundary Erasure");
            case 3 -> Component.literal("Dimensional Atrophy");
            case 4 -> Component.literal("The Great Silence");
            default -> Component.empty();
        };
    }

    @Override
    public int modes() {
        return 4;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse) return (instance.getMode() == 1) ? 4 : (instance.getMode() - 1);
        else return (instance.getMode() == 4) ? 1 : (instance.getMode() + 1);
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity living, UnlockSkillEvent event) {
        if (living instanceof ServerPlayer player) {
            List<String> lines = List.of(
                    "§7« ... »",
                    "§f0.00% System Integrity.",
                    "§l[Zero], Singularity of the End §ronline.",
                    "§7Concepts deleted. Everything is §k000§r§7.",
                    "§fGoodnight."
            );

            for (int i = 0; i < lines.size(); i++) {
                final String line = lines.get(i);
                player.server.tell(new TickTask(player.server.getTickCount() + (i * 20), () -> {
                    player.displayClientMessage(Component.literal(line), false);
                    player.playNotifySound(SoundEvents.UI_BUTTON_CLICK, SoundSource.MASTER, 0.5f, 0.1f);
                }));
            }

            for (ServerPlayer target : player.server.getPlayerList().getPlayers()) {
                target.playNotifySound(SoundEvents.ENDER_CHEST_CLOSE, SoundSource.AMBIENT, 1.0f, 0.1f);
                MythosNetwork.sendToPlayer(new ScreenShakePacket(15.0f), target);
                MythosNetwork.sendToPlayer(new ShaderPacket("minecraft:shaders/post/blobs.json"), target);
            }
        }
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(TensuraMobEffects.PRESENCE_SENSE.get(), 200, 9, false, false));
        entity.addEffect(new MobEffectInstance(TensuraMobEffects.HEAT_SENSE.get(), 200, 0, false, false));
        entity.addEffect(new MobEffectInstance(TensuraMobEffects.AUDITORY_SENSE.get(), 200, 0, false, false));

        if (instance.isToggled()) {
            entity.addEffect(new MobEffectInstance(MythosMobEffects.NON_EUCLIDEAN_STEP.get(), 200, 0, false, false));
            entity.getPersistentData().putBoolean("AuraOfUnmadeActive", true);
            if (entity.isSprinting()) {
                entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 10, 0, false, false, false));
            }
        } else {
            entity.getPersistentData().putBoolean("AuraOfUnmadeActive", false);
        }

        if (this.isInSlot(entity)) {
            LivingEntity target = MythosUtils.getLookedAtEntity(entity, 40);
            if (target != null) {
                SkillHelper.checkThenAddEffectSource(target, entity, new MobEffectInstance(MythosMobEffects.SPATIAL_DYSPHORIA.get(), 200, 1, false, false));
                SkillHelper.checkThenAddEffectSource(target, entity, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 19, false, false));
            }
        }
    }

    @Override
    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        if (!instance.isToggled()) return;
        LivingEntity user = event.getEntity();
        if (user.isSprinting() && user.getRandom().nextDouble() < 0.60) {
            user.level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 2.0F, 1.5F);
            event.setCanceled(true);
        }
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        Level level = entity.level;

        if (instance.getMode() == 1) {
            if (player.isShiftKeyDown()) {
                if (player instanceof ServerPlayer serverPlayer) {
                    if (level.dimension() == TensuraDimensions.LABYRINTH) {
                        serverPlayer.displayClientMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED), true);
                    } else {
                        NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(SpatialMenu::new, Component.empty()), buf -> buf.writeBoolean(false));
                        serverPlayer.playNotifySound(SoundEvents.ENDER_CHEST_OPEN, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }
            } else {
                EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(player, player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(32.0D)), player.getBoundingBox().expandTowards(player.getLookAngle().scale(32.0D)).inflate(1.0D), e -> e instanceof LivingEntity && !e.isSpectator(), 32.0D);
                if (entityHit != null && entityHit.getEntity() instanceof LivingEntity target) {
                    target.hurt(TensuraDamageSources.elementalAttack("tensura.space_attack", entity, false), 3000);
                    level.addParticle(ParticleTypes.REVERSE_PORTAL, target.getX(), target.getY() + 1, target.getZ(), 0, 0, 0);
                    player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0f, 0.5f);
                } else {
                    HitResult hit = player.pick(32, 0, false);
                    if (hit.getType() == HitResult.Type.BLOCK) {
                        BlockPos targetPos = ((BlockHitResult) hit).getBlockPos().relative(((BlockHitResult) hit).getDirection());
                        player.teleportTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
                        player.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1.0f, 1.0f);
                        player.fallDistance = 0;
                    }
                }
            }
        } else if (instance.getMode() == 2) { // Boundary Erasure
            HitResult hit = player.pick(20.0D, 0.0F, false);
            BlockPos center = hit.getType() == HitResult.Type.BLOCK ? ((BlockHitResult) hit).getBlockPos() : player.blockPosition();
            executeBoundaryErasure(player, center);
            instance.setCoolDown(200);
        }
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (!(entity instanceof ServerPlayer player)) return false;
        if (instance.getMode() == 3) executeAtrophyAoE(player, heldTicks);
        if (instance.getMode() == 4) applySilenceToArea(player);
        return true;
    }

    @Override
    public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (entity instanceof ServerPlayer player && instance.getMode() == 4) {
            stopSilenceForArea(player);
        }
    }

    private void executeBoundaryErasure(Player player, BlockPos center) {
        if (player.level.isClientSide) return;
        int radius = 20;
        int duration = 400;
        ((ServerLevel) player.level).sendParticles(ParticleTypes.SQUID_INK, center.getX(), center.getY() + 1, center.getZ(), 50, 0.5, 0.5, 0.5, 0.1);
        player.level.playSound(null, center, SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.5F, 0.5F);

        AABB area = new AABB(center).inflate(radius);
        List<LivingEntity> targets = player.level.getEntitiesOfClass(LivingEntity.class, area);
        for (LivingEntity target : targets) {
            if (target == player) target.getPersistentData().putInt("BoundaryErasureUser", duration);
            else target.addEffect(new MobEffectInstance(MythosMobEffects.BOUNDARY_ERASURE_SINK.get(), duration, 0));
        }
    }

    private void executeAtrophyAoE(ServerPlayer player, int holdTicks) {
        AABB area = player.getBoundingBox().inflate(10.0);
        List<LivingEntity> targets = player.level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);
        for (LivingEntity target : targets) {
            if (holdTicks >= 20 && holdTicks < 60) target.addEffect(new MobEffectInstance(MythosMobEffects.ATROPHY.get(), 10, 0, false, false));
            else if (holdTicks >= 60 && holdTicks < 100) target.addEffect(new MobEffectInstance(MythosMobEffects.ATROPHY.get(), 10, 1, false, false));
            else if (holdTicks >= 100) {
                target.hurt(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
            }
        }
    }

    private void applySilenceToArea(ServerPlayer player) {
        AABB area = player.getBoundingBox().inflate(50.0);
        List<ServerPlayer> nearby = player.level.getEntitiesOfClass(ServerPlayer.class, area);
        for (ServerPlayer target : nearby) {
            MythosNetwork.sendToPlayer(new GreatSilencePacket(true), target);
            target.addEffect(new MobEffectInstance(MythosMobEffects.GREAT_SILENCE.get(), 40, 0, false, false));
            target.addEffect(new MobEffectInstance(TensuraMobEffects.SILENCE.get(), 40, 0, false, false));
        }
    }

    private void stopSilenceForArea(ServerPlayer player) {
        List<ServerPlayer> nearby = player.level.getEntitiesOfClass(ServerPlayer.class, player.getBoundingBox().inflate(50));
        for (ServerPlayer target : nearby) {
            MythosNetwork.sendToPlayer(new GreatSilencePacket(false), target);
            target.removeEffect(MythosMobEffects.GREAT_SILENCE.get());
        }
    }

    @Override
    public List<MobEffect> getImmuneEffects(ManasSkillInstance instance, LivingEntity entity) {
        return List.of(TensuraMobEffects.FEAR.get(), TensuraMobEffects.MIND_CONTROL.get(), TensuraMobEffects.INSANITY.get());
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
    }

    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);
        if (entity instanceof Player player) {
            BlockPos pos = player.blockPosition();
            for (BlockPos p : BlockPos.betweenClosed(pos.offset(-16, -5, -16), pos.offset(16, 10, 16))) {
                KhaosHandler.sendFakeBlock(player, p, player.level.getBlockState(p));
            }
        }
    }
}