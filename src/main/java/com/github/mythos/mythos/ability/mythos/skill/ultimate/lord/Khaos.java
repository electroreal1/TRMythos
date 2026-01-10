package com.github.mythos.mythos.ability.mythos.skill.ultimate.lord;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
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
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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

    public boolean meetEPRequirement(Player player, double newEP) {
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, Skills.SHADOW_OF_THE_TESSERACT.get());
    }

    public Component getModeName(int mode) {
        MutableComponent var10000 = switch (mode) {
            case 1 -> Component.literal("Dimensional Fold");
            case 2 -> Component.literal("Boundary Erasure");
            default -> Component.empty();
        };

        return var10000;
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Khaos, Lord of Fractured Origin");
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity living, UnlockSkillEvent event) {
        if (living instanceof Player player) {
            player.displayClientMessage(Component.literal("§b« W-Warning »\n")
                    .append(Component.literal("§d« System logic error... Overwriting... »\n"))
                    .append(Component.literal("§9« The boundaries of reality are being forcibly erased. The User has become a localized anomaly. »\n"))
                    .append(Component.literal("§8« Ultimate Skill [Khaos] has been manifested. »\n")
                            .append(Component.literal("§9« Everything is fluid. Everything is wrong. The Voice is... fading. »"))), false);
        }
    }

    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        if (!instance.isToggled()) return;
        if (!event.isCanceled()) {
            DamageSource damageSource = event.getSource();
            if (!damageSource.isBypassInvul() && !damageSource.isMagic()) {
                Entity var5 = damageSource.getDirectEntity();
                if (var5 instanceof LivingEntity) {
                    LivingEntity entity = (LivingEntity) var5;
                    double dodgeChance = 0.3;
                    if (SkillUtils.canNegateDodge(entity, damageSource)) {
                        dodgeChance = 0.0;
                    }

                    if (!(entity.getRandom().nextDouble() >= dodgeChance)) {
                        entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 2.0F, 1.0F);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
        if (this.isInSlot(entity)) {
            entity.getPersistentData().putBoolean("AuraOfUnmadeActive", true);
        }
    }

    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);

        entity.getPersistentData().putBoolean("AuraOfUnmadeActive", false);

        BlockPos pos = entity.blockPosition();
        int r = 16;
        for (BlockPos p : BlockPos.betweenClosed(pos.offset(-r, -5, -r), pos.offset(r, 10, r))) {
            BlockState realState = entity.level.getBlockState(p);
            if (entity instanceof Player player) {
                sendFakeBlock(player, p, realState);
            }
        }
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.isToggled()) {
            entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.PRESENCE_SENSE.get(), 200, 3, false,
                    false, false));
            entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.HEAT_SENSE.get(), 200, 0, false,
                    false, false));
            entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.AUDITORY_SENSE.get(), 200, 0, false,
                    false, false));
            entity.addEffect(new MobEffectInstance(MythosMobEffects.NON_EUCLIDEAN_STEP.get(), 200, 0, false,
                    false, false));
        }

        if (TensuraSkillCapability.isSkillInSlot(entity, this)) {
            LivingEntity entity1 = MythosUtils.getLookedAtEntity(entity, 30);
            SkillHelper.checkThenAddEffectSource(entity1, entity, new MobEffectInstance(MythosMobEffects.SPATIAL_DYSPHORIA.get(), 10,
                    3, false, false, false));
            SkillHelper.checkThenAddEffectSource(entity1, entity, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10,
                    10, false, false, false));
        }
    }

    @Override
    public List<MobEffect> getImmuneEffects(ManasSkillInstance instance, LivingEntity entity) {
        List<MobEffect> list = new ArrayList<>();
        list.add(TensuraMobEffects.FEAR.get());
        list.add(TensuraMobEffects.MIND_CONTROL.get());
        list.add(TensuraMobEffects.INSANITY.get());
        return list;
    }

    @Override
    public int modes() {
        return 2;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        Level level = entity.level;
        if (entity instanceof Player player) {
            if (instance.getMode() == 1) {
                if (entity.isShiftKeyDown()) {
                    player.displayClientMessage(Component.literal("§8[§dOpening Spatial Gate Menu...§8]"), true);
                    if (entity instanceof ServerPlayer) {
                        ServerPlayer serverPlayer = (ServerPlayer) entity;
                        if (level.dimension() == TensuraDimensions.LABYRINTH) {
                            serverPlayer.playNotifySound(SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
                            serverPlayer.displayClientMessage(Component.translatable("tensura.ability.activation_failed").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
                        } else {
                            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(SpatialMenu::new, Component.empty()), (buf) -> {
                                buf.writeBoolean(false);
                            });
                            serverPlayer.playNotifySound(SoundEvents.ENDER_CHEST_OPEN, SoundSource.PLAYERS, 1.0F, 1.0F);
                        }
                    }

                    entity.removeEffect((MobEffect) TensuraMobEffects.WARPING.get());
                    instance.setCoolDown(instance.isMastered(entity) ? 10 : 20);
                } else {
                    HitResult hit = player.pick(32, 0, false);

                    EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(player,
                            player.getEyePosition(),
                            player.getEyePosition().add(player.getLookAngle().scale(32.0D)),
                            player.getBoundingBox().expandTowards(player.getLookAngle().scale(32.0D)).inflate(1.0D),
                            e -> !e.isSpectator() && e instanceof LivingEntity, 32.0D);

                    if (entityHit != null && entityHit.getEntity() instanceof LivingEntity target) {
                        target.hurt(TensuraDamageSources.elementalAttack("tensura.space_attack", entity, false), 1000);

                        level.addParticle(ParticleTypes.REVERSE_PORTAL, target.getX(), target.getY() + 1, target.getZ(), 0, 0, 0);
                        player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0f, 0.5f);
                    } else if (hit.getType() == HitResult.Type.BLOCK) {

                        BlockPos targetPos = ((BlockHitResult) hit).getBlockPos().relative(((BlockHitResult) hit).getDirection());
                        level.addParticle(ParticleTypes.PORTAL, player.getX(), player.getY(), player.getZ(), 0, 0, 0);

                        player.teleportTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);

                        player.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1.0f, 1.0f);
                        player.fallDistance = 0;
                    }
                }
            } else if (instance.getMode() == 2) {
                HitResult groundHit = player.pick(20.0D, 0.0F, false);
                BlockPos centerPos;

                if (groundHit.getType() == HitResult.Type.BLOCK) {
                    centerPos = ((BlockHitResult) groundHit).getBlockPos();
                } else {
                    centerPos = player.blockPosition();
                }

                executeBoundaryErasure(player, centerPos);
                instance.setCoolDown(200);
            }
        }
    }

    private void executeBoundaryErasure(Player player, BlockPos center) {
        Level level = player.level;
        if (!level.isClientSide) {
            int radius = 12;
            int duration = 200;

            ((ServerLevel) level).sendParticles(ParticleTypes.SQUID_INK, center.getX(), center.getY() + 1, center.getZ(), 50, 0.5, 0.5, 0.5, 0.1);
            level.playSound(null, center, SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.5F, 0.5F);

            AABB area = new AABB(center).inflate(radius);
            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area);

            for (LivingEntity target : targets) {
                if (target == player) {
                    target.getPersistentData().putInt("BoundaryErasureUser", duration);
                } else {
                    target.addEffect(new MobEffectInstance(MythosMobEffects.BOUNDARY_ERASURE_SINK.get(), duration, 0));
                }
            }
        }
    }



}
