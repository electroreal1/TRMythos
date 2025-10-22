package com.github.mythos.mythos.ability.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.magic.Magic;
import com.github.manasmods.tensura.ability.magic.spiritual.light.SolarFlareMagic;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.entity.projectile.LightArrowProjectile;
import com.github.manasmods.tensura.util.damage.TensuraDamageSource;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class PuritySkill extends Skill {

    private boolean active = false;

    public PuritySkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/purity.png");
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        TensuraEPCapability.getFrom(entity).ifPresent((cap) -> {
            if (!cap.isChaos() || cap.isMajin()) {
                cap.setMajin(false);
            }
        });
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        TensuraEPCapability.getFrom(entity).ifPresent((cap) -> {
            if (!cap.isChaos() || cap.isMajin()) {
                cap.setMajin(false);
            }
        });
    }


    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
        return true;
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        active = true;
        MinecraftForge.EVENT_BUS.register(this);
        List<MobEffect> immune = MythosSkillsConfig.getPurityImmuneEffects();

        entity.getActiveEffects().forEach(effectInstance -> {
            MobEffect effect = effectInstance.getEffect();
            if (immune.contains(effect) && effect.isBeneficial()) return;
            if (!immune.contains(effect) && !effect.isBeneficial()) {
                entity.removeEffect(effect);
            }
        });
    }

    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        active = false;
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onEntityHurt(LivingHurtEvent event) {
        if (!active) return;
        if (!(event.getSource() instanceof TensuraDamageSource source)) return;

        ManasSkillInstance causingSkill = source.getSkill();
        boolean isMagic = causingSkill != null && causingSkill.getSkill() instanceof Magic;
        if (!isMagic) return;

        event.setAmount(event.getAmount() / 2f);
    }

    private void spawnLightArrows(ManasSkillInstance instance, LivingEntity entity, Vec3 pos, int arrowAmount, double distance) {
        int arrowRot = 360 / arrowAmount;
        int souls = TensuraPlayerCapability.getSoulPoints((Player) entity) / 1000;

        for(int i = 0; i < arrowAmount; ++i) {
            Vec3 arrowPos = entity.getEyePosition().add((new Vec3(0.0, distance, 0.0)).zRot(((float)(arrowRot * i) - (float)arrowRot / 2.0F) * 0.017453292F).xRot(-entity.getXRot() * 0.017453292F).yRot(-entity.getYRot() * 0.017453292F));
            LightArrowProjectile arrow = new LightArrowProjectile(entity.getLevel(), entity);
            arrow.setSpeed(2.0F);
            arrow.setPos(arrowPos);
            arrow.shootFromRot(pos.subtract(arrowPos).normalize());
            arrow.setLife(50);
            if (instance.isMastered(entity)) {
                arrow.setDamage(12.5f + souls);
            } else arrow.setDamage(6.25f + souls);
            arrow.setMpCost(this.magiculeCost(entity, instance) / (double)arrowAmount);
            arrow.setSpiritAttack(true);
            arrow.setSkill(instance);
            entity.getLevel().addFreshEntity(arrow);
        }

    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity user, LivingHurtEvent e) {
        if (!instance.isToggled()) return;

        DamageSource source = e.getSource();
        if (!(source instanceof TensuraDamageSource damageSource)) return;

        if (damageSource.getEntity() != user) return;

        boolean isLight = false;
        try {
            isLight = "tensura.light_attack".equals(damageSource.getMsgId());
        } catch (Exception ignored) {}

        if (isLight || damageSource.isHoly()) {
            float multiplier = instance.isMastered(user) ? 4.0F : 3.0F;
            e.setAmount(e.getAmount() * multiplier);
        }
    }

    @Override
    public int modes() {
        return 2;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    public Component getModeName(int mode) {
        MutableComponent var10000;
        switch (mode) {
            case 1:
                var10000 = Component.translatable("trmythos.skill.purity.purification");
                break;
            case 2:
                var10000 = Component.translatable("trmythos.skill.purity.justice");
                break;
            default:
                var10000 = Component.empty();
        }
        return var10000;
    }

    private boolean canTurnNormal(LivingEntity living) {
        if (TensuraEPCapability.isChaos(living)) {
            return true;
        } else {
            return TensuraEPCapability.isMajin(living);
        }
    }
    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (SkillHelper.outOfMagicule(entity, instance)) return;

        Level level = entity.level;
        if (level.isClientSide) return;

        if (instance.getMode() == 1) {
            LivingEntity target = SkillHelper.getTargetingEntity(entity, 10.0, false);
            if (target != null) {
                if (target instanceof Player p && p.getAbilities().invulnerable) return;

                if (this.canTurnNormal(target)) {
                    TensuraEPCapability.getFrom(target).ifPresent(cap -> {
                        boolean changed = false;
                        if (cap.isChaos() || cap.isMajin()) {
                            cap.setChaos(false);
                            cap.setMajin(false);
                            changed = true;
                        }
                        if (changed) {
                            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.SCULK_SOUL, 2.0);
                            entity.level.playSound(
                                    null, // player who hears it, null = all
                                    entity.getX(), entity.getY(), entity.getZ(),
                                    SoundEvents.BLAZE_SHOOT,
                                    SoundSource.PLAYERS,
                                    1.0F,
                                    1.2F
                            );
                        }
                    });

                    target.getActiveEffects().forEach(effect -> {
                        if (!effect.getEffect().isBeneficial()) {
                            target.removeEffect(effect.getEffect());
                        }
                    });

                    TensuraEPCapability.sync(target);
                    entity.swing(InteractionHand.MAIN_HAND, true);
                } else if (entity instanceof Player p) {
                    p.displayClientMessage(
                            Component.translatable("tensura.targeting.not_allowed")
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)),
                            false
                    );
                }
                instance.setCoolDown(30);
                this.addMasteryPoint(instance, entity);
            }
        } else  if (!SkillHelper.outOfMagicule(entity, instance) && instance.getMode() == 2) {
            entity.swing(InteractionHand.MAIN_HAND, true);
            this.addMasteryPoint(instance, entity);
            level = entity.getLevel();
            int distance = instance.isMastered(entity) ? 30 : 20;
            Entity target = SkillHelper.getTargetingEntity(entity, (double)distance, false, true);
            Vec3 pos;
            if (target != null) {
                pos = target.getEyePosition();
            } else {
                BlockHitResult result = SkillHelper.getPlayerPOVHitResult(entity.level, entity, ClipContext.Fluid.NONE, (double)distance);
                pos = result.getLocation().add(0.0, 0.5, 0.0);
            }

            if (instance.isMastered(entity)) {
                this.spawnLightArrows(instance, entity, pos, 10, 2.0);
                this.spawnLightArrows(instance, entity, pos, 10, 4.0);
            } else {
                this.spawnLightArrows(instance, entity, pos, 10, 3.0);
            }

            level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
            instance.setCoolDown(10);
        }
    }
}
