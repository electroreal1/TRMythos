package com.github.mythos.mythos.ability.mythos.skill.unique.evolved;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ShadowOfTheTesseract extends Skill {
    public ShadowOfTheTesseract(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public double getObtainingEpCost() {
        return 99000;
    }

    public boolean meetEPRequirement(Player player, double newEP) {
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, Skills.GAZE.get());
    }

    public Component getModeName(int mode) {
        MutableComponent var10000;
        switch (mode) {
            case 1:
                var10000 = Component.literal("Dimensional Fold");
                break;
            case 2:
                var10000 = Component.literal("Boundary Erasure");
                break;
            case 3:
                var10000 = Component.literal("Dimensional Atrophy");
                break;
            case 4:
                var10000 = Component.literal("The Great Silence");
                break;

            default:
                var10000 = Component.empty();
        }

        return var10000;
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Shadow of the Tesseract");
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity living, UnlockSkillEvent event) {
        if (living instanceof Player player) {
            player.displayClientMessage(Component.literal("§b« Notification »\n")
                    .append(Component.literal("§d« Evolution of the soul detected. The individual’s physical form is no longer bound by 3D constraints. »\n"))
                    .append(Component.literal("§9« Skill [Gaze] is evolving into... »\n"))
                    .append(Component.literal("§8« Evolved Skill [Shadow of the Tesseract] acquired. »\n")
                            .append(Component.literal("§9« Caution: Movement through folded space may cause temporary loss of biological orientation. »"))), false);
        }
    }

    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        if (!event.isCanceled()) {
                DamageSource damageSource = event.getSource();
                if (!damageSource.isBypassInvul() && !damageSource.isMagic()) {
                    Entity var5 = damageSource.getDirectEntity();
                    if (var5 instanceof LivingEntity) {
                        LivingEntity entity = (LivingEntity)var5;
                        double dodgeChance = 0.3;
                        if (SkillUtils.canNegateDodge(entity, damageSource)) {
                            dodgeChance = 0.0;
                        }

                        if (!(entity.getRandom().nextDouble() >= dodgeChance)) {
                            entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 2.0F, 1.0F);
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

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.isToggled()) {
            entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.PRESENCE_SENSE.get(), 200, 2, false,
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
                    2, false, false, false));
            SkillHelper.checkThenAddEffectSource(entity1, entity, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10,
                    5, false, false, false));
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
        return 1;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        Level level = entity.level;
        if (entity instanceof Player player) {

            if (entity.isShiftKeyDown()) {
                player.displayClientMessage(Component.literal("§8[§dOpening Spatial Gate Menu...§8]"), true);
                if (entity instanceof ServerPlayer) {
                    ServerPlayer serverPlayer = (ServerPlayer)entity;
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

                entity.removeEffect((MobEffect)TensuraMobEffects.WARPING.get());
                instance.setCoolDown(instance.isMastered(entity) ? 10 : 20);
            } else {
                HitResult hit = player.pick(32, 0, false);

                EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(player,
                        player.getEyePosition(),
                        player.getEyePosition().add(player.getLookAngle().scale(32.0D)),
                        player.getBoundingBox().expandTowards(player.getLookAngle().scale(32.0D)).inflate(1.0D),
                        e -> !e.isSpectator() && e instanceof LivingEntity, 32.0D);

                if (entityHit != null && entityHit.getEntity() instanceof LivingEntity target) {
                        target.hurt(TensuraDamageSources.elementalAttack("tensura.space_attack", entity, false), 100);

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
            }
        }

}
