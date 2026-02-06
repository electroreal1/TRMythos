package com.github.mythos.mythos.ability.mythos.skill.unique.evolved;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
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

    @Override
    public boolean meetEPRequirement(Player player, double newEP) {
        return TensuraEPCapability.getCurrentEP(player) >= getObtainingEpCost()
                && SkillUtils.isSkillMastered(player, Skills.GAZE.get());
    }

    @Override
    public int modes() {
        return 1;
    }

    @Override
    public Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.literal("Dimensional Fold");
            default -> Component.empty();
        };
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Shadow of the Tesseract");
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity living, UnlockSkillEvent event) {
        if (living instanceof Player player) {
            player.displayClientMessage(Component.literal("§b« Notification »\n")
                    .append(Component.literal("§d« Evolution of the soul detected. The individual’s physical form is no longer bound by 3D constraints. »\n"))
                    .append(Component.literal("§9« Skill [Gaze] has evolved into [Shadow of the Tesseract]. »")), false);
        }
    }

    @Override
    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        LivingEntity user = event.getEntity();
        if (this.isInSlot(user)) {
            DamageSource source = event.getSource();
            if (!source.isBypassInvul() && !source.isMagic()) {
                Entity attacker = source.getDirectEntity();
                double dodgeChance = 0.3;

                if (attacker instanceof LivingEntity livingAttacker && SkillUtils.canNegateDodge(livingAttacker, source)) {
                    dodgeChance = 0.0;
                }

                if (user.getRandom().nextDouble() < dodgeChance) {
                    user.level.playSound(null, user.getX(), user.getY(), user.getZ(),
                            SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.5F, 1.2F);
                    event.setCanceled(true);
                }
            }
        }
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.isToggled()) {
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.PRESENCE_SENSE.get(), 200, 2, false, false, false));
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.HEAT_SENSE.get(), 200, 0, false, false, false));
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.AUDITORY_SENSE.get(), 200, 0, false, false, false));
            entity.addEffect(new MobEffectInstance(MythosMobEffects.NON_EUCLIDEAN_STEP.get(), 020, 0, false, false, false));
            entity.fallDistance = 0;
        }

        if (this.isInSlot(entity)) {
            LivingEntity target = MythosUtils.getLookedAtEntity(entity, 40);

            if (target != null) {
                target.addEffect(new MobEffectInstance(MythosMobEffects.SPATIAL_DYSPHORIA.get(), 200, 1, false,
                        false, false));

                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 19, false,
                        false));
            }
        }
    }

    @Override
    public List<MobEffect> getImmuneEffects(ManasSkillInstance instance, LivingEntity entity) {
        List<MobEffect> list = new ArrayList<>();
        list.add(TensuraMobEffects.FEAR.get());
        list.add(TensuraMobEffects.MIND_CONTROL.get());
        list.add(TensuraMobEffects.INSANITY.get());
        list.add(MobEffects.CONFUSION);
        return list;
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
        }
    }


}