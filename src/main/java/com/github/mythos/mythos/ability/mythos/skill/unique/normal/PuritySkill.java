package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.entity.projectile.LightArrowProjectile;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;

public class PuritySkill extends Skill {
    public PuritySkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/purity.png");
    }

    @Override
    public int getMaxMastery() {
        return 1500;
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        TensuraEPCapability.getFrom(entity).ifPresent((cap) -> {
            if (!cap.isChaos() || cap.isMajin()) {
                cap.setMajin(false);
            }
        });
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    private static final double rotation = 0;

    @Override
    public List<MobEffect> getImmuneEffects(ManasSkillInstance instance, LivingEntity entity) {
        return MythosSkillsConfig.getPurityImmuneEffects();
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        TensuraEPCapability.getFrom(entity).ifPresent((cap) -> {
            if (!cap.isChaos() || cap.isMajin()) {
                cap.setMajin(false);
            }
        });

        if (!MythosSkillsConfig.EnableSkillAuras()) return;
        if (!(entity instanceof Player player)) return;
        Level level = entity.level;
        if (!(level instanceof ServerLevel server)) return;
        RandomSource rand = player.level.random;
        int points = 30;
        double radius = 1;
        double yOffset = 1.8;
        for (int i = 0; i < points; i++) {
            if (rand.nextDouble() > 0.4) continue;
            double angle = i * 2 * Math.PI / points + rotation;
            double px = player.getX() - Math.cos(angle) * radius;
            double pz = player.getZ() - Math.sin(angle) * radius;
            double py = player.getY() + yOffset + (rand.nextDouble() - 0.5) * 0.2;
            float size = 1f;
            Vector3f color;
            double r = rand.nextDouble();
            if (r < 0.5) color = new Vector3f(1f, 0.9f, 0f);
            else if (r < 0.8) color = new Vector3f(1f, 0.6f, 0f);
            else if (r < 0.95) color = new Vector3f(1f, 0.3f, 0f);
            else color = new Vector3f(1f, 1f, 1f);
            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 10, 0, 0, 0, 0);
        }
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
        return true;
    }

    private void spawnLightArrows(ManasSkillInstance instance, LivingEntity entity, Vec3 pos, int arrowAmount, double distance) {
        int arrowRot = 360 / arrowAmount;
//        int souls = TensuraPlayerCapability.getSoulPoints((Player) entity) / 1000;

        for(int i = 0; i < arrowAmount; ++i) {
            Vec3 arrowPos = entity.getEyePosition().add((new Vec3(0.0, distance, 0.0)).zRot(((float)(arrowRot * i) - (float)arrowRot / 2.0F) * 0.017453292F).xRot(-entity.getXRot() * 0.017453292F).yRot(-entity.getYRot() * 0.017453292F));
            LightArrowProjectile arrow = new LightArrowProjectile(entity.getLevel(), entity);
            arrow.setSpeed(2.0F);
            arrow.setPos(arrowPos);
            arrow.shootFromRot(pos.subtract(arrowPos).normalize());
            arrow.setLife(50);
            if (instance.isMastered(entity)) {
                arrow.setDamage(12.5f);
            } else arrow.setDamage(6.25f);

//            final double cap = MythosSkillsConfig.purityDamageCap.get();
//
//            if (arrow.getDamage() >= cap) {
//                arrow.setDamage((float) cap);
//            }

            arrow.setMpCost(this.magiculeCost(entity, instance) / (double)arrowAmount);
            arrow.setSpiritAttack(false);
            arrow.setSkill(instance);
            entity.getLevel().addFreshEntity(arrow);
        }

    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity living, LivingHurtEvent e) {
        if (instance.isToggled()) {
            if (DamageSourceHelper.isLightDamage(e.getSource())) {
                if (instance.isMastered(living)) {
                    e.setAmount(e.getAmount() * 4.0F);
                } else {
                    e.setAmount(e.getAmount() * 3.0F);
                }
            }
            if (DamageSourceHelper.isHoly(e.getSource())) {
                if (instance.isMastered(living)) {
                    e.setAmount(e.getAmount() * 4.0F);
                } else {
                    e.setAmount(e.getAmount() * 3.0F);
                }
            }
            if (e.getSource().isMagic()) {
                e.setAmount(e.getAmount() * 0.5f);
            }
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
                                    null,
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
            Entity target = SkillHelper.getTargetingEntity(entity, distance, false, true);
            Vec3 pos;
            if (target != null) {
                pos = target.getEyePosition();
            } else {
                BlockHitResult result = SkillHelper.getPlayerPOVHitResult(entity.level, entity, ClipContext.Fluid.NONE, distance);
                pos = result.getLocation().add(0.0, 0.5, 0.0);
            }

            if (instance.isMastered(entity)) {
                this.spawnLightArrows(instance, entity, pos, 10, 2.0);
                this.spawnLightArrows(instance, entity, pos, 10, 4.0);
            } else {
                this.spawnLightArrows(instance, entity, pos, 10, 3.0);
            }

            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
            instance.setCoolDown(10);
        }
    }
}

