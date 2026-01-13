package com.github.mythos.mythos.ability.mythos.skill.ultimate.lord;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
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
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import com.github.mythos.mythos.util.MythosUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import static com.github.mythos.mythos.handler.KhaosHandler.sendFakeBlock;

public class Khaos extends Skill {
    protected static final UUID ACCELERATION = UUID.fromString("48dc5eee-34e9-4a6c-ad3d-58048cb06c6f");

    public Khaos(SkillType type) {
        super(SkillType.ULTIMATE);
    }

    @Override
    public double getObtainingEpCost() {
        return 1500000;
    }

    @Override
    public boolean meetEPRequirement(Player player, double newEP) {
        return TensuraEPCapability.getCurrentEP(player) >= getObtainingEpCost() &&
                SkillUtils.isSkillMastered(player, Skills.SHADOW_OF_THE_TESSERACT.get());
    }

    @Override
    public Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.literal("Dimensional Fold");
            case 2 -> Component.literal("Boundary Erasure");
            default -> Component.empty();
        };
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse) return (instance.getMode() == 1) ? 2 : (instance.getMode() - 1);
        else return (instance.getMode() == 2) ? 1 : (instance.getMode() + 1);
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Khaos");
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.isToggled()) {
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.PRESENCE_SENSE.get(), 200, 3, false, false));
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.HEAT_SENSE.get(), 200, 0, false, false));
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.AUDITORY_SENSE.get(), 200, 0, false, false));

            entity.addEffect(new MobEffectInstance(MythosMobEffects.NON_EUCLIDEAN_STEP.get(), 200, 0, false, false));
            if (entity.isSprinting()) {
                entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 10, 0, false, false, false));
            }
        }

        if (this.isInSlot(entity)) {
            LivingEntity target = MythosUtils.getLookedAtEntity(entity, 40);
            if (target != null) {
                SkillHelper.checkThenAddEffectSource(target, entity, new MobEffectInstance(MythosMobEffects.SPATIAL_DYSPHORIA.get(), 200, 1, false, false));
                SkillHelper.checkThenAddEffectSource(target, entity, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 9, false, false)); // Slowness 10
            }

            if (entity.tickCount % 5 == 0 && entity instanceof Player player && !player.level.isClientSide) {
                handleAuraOfUnmade(player);
            }
        }
    }

    private void handleAuraOfUnmade(Player player) {
        BlockPos pos = player.blockPosition();
        int r = 15;
        for (int i = 0; i < 20; i++) {
            BlockPos randomPos = pos.offset(player.getRandom().nextInt(r * 2) - r,
                    player.getRandom().nextInt(10) - 5,
                    player.getRandom().nextInt(r * 2) - r);
            BlockState state = player.level.getBlockState(randomPos);

            if (state.is(BlockTags.LEAVES) || state.is(Blocks.AZALEA_LEAVES)) {
                sendFakeBlock(player, randomPos, Blocks.STONE.defaultBlockState());
            } else if (state.is(Blocks.WATER)) {
                sendFakeBlock(player, randomPos, Blocks.GLASS.defaultBlockState());
            } else if (state.is(Blocks.FIRE)) {
                sendFakeBlock(player, randomPos, Blocks.AIR.defaultBlockState());
            }
        }
    }

    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);
        if (entity instanceof Player player) {
            BlockPos p = player.blockPosition();
            int r = 20;
            for (BlockPos targetP : BlockPos.betweenClosed(p.offset(-r, -10, -r), p.offset(r, 10, r))) {
                sendFakeBlock(player, targetP, player.level.getBlockState(targetP));
            }
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
                EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(player,
                        player.getEyePosition(),
                        player.getEyePosition().add(player.getLookAngle().scale(32.0D)),
                        player.getBoundingBox().expandTowards(player.getLookAngle().scale(32.0D)).inflate(1.0D),
                        e -> e instanceof LivingEntity && !e.isSpectator(), 32.0D);

                if (entityHit != null && entityHit.getEntity() instanceof LivingEntity target) {
                    target.hurt(TensuraDamageSources.elementalAttack("tensura.space_attack", player, false), 150.0F);
                    level.addParticle(ParticleTypes.REVERSE_PORTAL, target.getX(), target.getY() + 1, target.getZ(), 0, 0, 0);
                    player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 0.5F);
                } else {
                    HitResult blockHit = player.pick(32, 0, false);
                    if (blockHit.getType() == HitResult.Type.BLOCK) {
                        BlockPos pos = ((BlockHitResult) blockHit).getBlockPos().relative(((BlockHitResult) blockHit).getDirection());
                        player.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                        player.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
                        player.fallDistance = 0;
                    }
                }
            }
            instance.setCoolDown(instance.isMastered(player) ? 10 : 20);
        } else if (instance.getMode() == 2) {
            HitResult hit = player.pick(20.0D, 0.0F, false);
            BlockPos center = hit.getType() == HitResult.Type.BLOCK ? ((BlockHitResult) hit).getBlockPos() : player.blockPosition();

            executeBoundaryErasure(player, center);
            instance.setCoolDown(600);
        }
    }

    private void executeBoundaryErasure(Player player, BlockPos center) {
        Level level = player.level;
        if (!level.isClientSide) {
            int radius = 12;
            int duration = 200;

            level.playSound(null, center, SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 2.0F, 0.5F);
            ((ServerLevel) level).sendParticles(ParticleTypes.WARPED_SPORE, center.getX(), center.getY(), center.getZ(), 100, 5, 1, 5, 0.1);

            AABB area = new AABB(center).inflate(radius);
            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area);

            for (LivingEntity target : targets) {
                if (target == player) {
                    target.addEffect(new MobEffectInstance(MythosMobEffects.BOUNDARY_ERASURE_USER.get(), duration, 0));
                } else {
                    target.addEffect(new MobEffectInstance(MythosMobEffects.BOUNDARY_ERASURE_SINK.get(), duration, 0));
                }
            }
        }
    }

}
