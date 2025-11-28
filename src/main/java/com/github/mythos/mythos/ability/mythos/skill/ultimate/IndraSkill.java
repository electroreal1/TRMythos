package com.github.mythos.mythos.ability.mythos.skill.ultimate;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.effect.template.Transformation;
import com.github.manasmods.tensura.entity.magic.lightning.LightningBolt;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.entity.ThunderStorm;
import com.github.mythos.mythos.entity.projectile.VajraBreathProjectile;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import com.github.mythos.mythos.util.damage.MythosDamageSources;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IndraSkill extends Skill implements Transformation {
    public IndraSkill(SkillType type) {
        super(SkillType.ULTIMATE);
    }

    public double getObtainingEpCost() {
        return 3000000.0;
    }

    public boolean meetEPRequirement(@NotNull Player player, double newEP) {
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, (ManasSkill) Skills.HEAVENS_WRATH.get());
    }


    @Override
    public boolean canBeToggled(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity) {
        return true;
    }


    public void onDamageEntity(ManasSkillInstance instance, @NotNull LivingEntity living, @NotNull LivingHurtEvent event, Player player) {
        if (instance.isToggled()) {
            if (DamageSourceHelper.isLightningDamage(event.getSource())) {
                if (instance.isMastered(living)) {
                    event.setAmount(event.getAmount() * 15.0F);
                } else {
                    event.setAmount(event.getAmount() * 10.0F);
                }
            }
            Entity sourceEntity = event.getSource().getEntity();
            Entity targetEntity = event.getEntity();
            if (!(sourceEntity instanceof LivingEntity attacker)) return;
            if (!(targetEntity instanceof LivingEntity target)) return;
            SkillStorage storage = SkillAPI.getSkillsFrom(attacker);
            Skill blackLightning = ExtraSkills.BLACK_LIGHTNING.get();
            if (storage.getSkill(blackLightning).isPresent()) {
                target.hurt(TensuraDamageSources.blackLightning(attacker), 8.0F);
                if (attacker.level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            TensuraParticles.BLACK_LIGHTNING_SPARK.get(),
                            target.getX(), target.getY() + 1.0, target.getZ(),
                            15, 0.3, 0.5, 0.3, 0.05
                    );
                }
            }
        }
    }

    @Override
    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        if (instance.isToggled()) {
            LivingEntity target = event.getEntity();
            DamageSource source = event.getSource();
            float amount = event.getAmount();

            if (DamageSourceHelper.isLightningDamage(source)) {
                event.setCanceled(true);

                applyHealth(target, amount);
            }
        }
    }

    private static void applyHealth(LivingEntity entity, float amount) {
        float currentAbsorption = entity.getAbsorptionAmount();
        entity.setAbsorptionAmount(currentAbsorption + amount);

    }

    @Override
    public boolean canTick(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, @NotNull LivingEntity entity) {
        if (instance.isToggled()) {
            if (entity instanceof Player player) {
                TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                    double maxMP = player.getAttributeValue((Attribute) TensuraAttributeRegistry.MAX_MAGICULE.get());
                    double regen = instance.isMastered(entity) ? 280000.0 : 140000.0;
                    cap.setMagicule(Math.min(cap.getMagicule() + regen, maxMP));
                });
                TensuraPlayerCapability.sync(player);
            }
        }
    }

    public int modes() {
        return 4;
    }

    public @NotNull Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.translatable("trmythos.skill.indra.breath");
            case 2 -> Component.translatable("trmythos.skill.indra.divine");
            case 3 -> Component.translatable("trmythos.skill.indra.thunder");
            case 4 -> Component.translatable("trmythos.skill.indra.spear");
            default -> Component.empty();
        };
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        int var10000;
        if (reverse) {
            switch (instance.getMode()) {
                case 1:
                    var10000 = this.isMastered(instance, entity) ? 4 : 3;
                    break;
                case 2:
                    var10000 = 1;
                    break;
                case 3:
                    var10000 = 2;
                    break;
                case 4:
                    var10000 = 3;
                    break;

                default:
                    var10000 = 0;
            }

            return var10000;
        } else {
            switch (instance.getMode()) {
                case 1:
                    var10000 = 2;
                    break;
                case 2:
                    var10000 = 3;
                    break;
                case 3:
                    var10000 = 4;
                    break;
                case 4:
                    var10000 = this.isMastered(instance, entity) ? 4 : 1;
                    break;
                default:
                    var10000 = 1;
            }

            return var10000;
        }
    }
    public String modeLearningId(int mode) {
        String var10000;
        switch (mode) {
            case 4:
                var10000 = "Thunder";
                break;
            default:
                var10000 = "None";
        }
        return var10000;
    }

    @Override
    public int getMaxMastery() {
        return 5000;
    }

    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() == 1) {
            if (heldTicks % 20 == 0 && SkillHelper.outOfMagicule(entity, instance)) {
                return false;
            } else {
                if (heldTicks % 100 == 0 && heldTicks > 0) {
                    this.addMasteryPoint(instance, entity);
                }
                VajraBreathProjectile breath = new VajraBreathProjectile(entity.getLevel(), entity);
                entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 1.0F, 1.0F);
                entity.getLevel().addFreshEntity(breath);
                breath.setDamage(instance.isMastered(entity) ? 300 : 150);

            }
        }     return true;
    }

    public void onPressed(ManasSkillInstance instance, @NotNull LivingEntity entity) {
        if (instance.getMode() == 2) {

            boolean hasStorm = !entity.getLevel().getEntitiesOfClass(ThunderStorm.class, entity.getBoundingBox(),
                    thunderStorm -> thunderStorm.getOwner() == entity).isEmpty();

            if (entity.isShiftKeyDown()) {

                for (ThunderStorm thunderStorm : entity.getLevel().getEntitiesOfClass(ThunderStorm.class, entity.getBoundingBox(),
                        b -> b.getOwner() == entity)) {
                    thunderStorm.discard();
                }
            } else {

                if (!hasStorm && !SkillHelper.outOfMagicule(entity, 2000.0F)) {

                    SkillHelper.outOfMagicule(entity, 2000.0F);

                    ThunderStorm thunderStorm = new ThunderStorm(entity.getLevel(), entity);
                    thunderStorm.setOwner(entity);
                    float damage = instance.isMastered(entity) ? 1000.0F : 500.0f;
                    thunderStorm.setDamage(damage);
                    entity.getLevel().addFreshEntity(thunderStorm);
                    thunderStorm.applyEffect(entity);

                    entity.swing(InteractionHand.MAIN_HAND, true);
                    entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                            SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);

                    this.addMasteryPoint(instance, entity);
                    instance.markDirty();
                    instance.setCoolDown(50);
                }
            }
            instance.markDirty();
        } else if (instance.getMode() == 4) {
            if (!instance.isMastered(entity)) return;

            Level level = entity.getLevel();
            if (SkillHelper.outOfMagicule(entity, instance)) return;

            double reach = 100.0;
            Vec3 start = entity.position().add(0, entity.getEyeHeight(), 0);
            Vec3 lookVec = entity.getLookAngle();
            Vec3 end = start.add(lookVec.scale(reach));

            HitResult hitResult = entity.pick(reach, 0, false);

            Vec3 targetPos;

            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) hitResult;
                targetPos = blockHit.getLocation();
            } else if (hitResult.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHit = (EntityHitResult) hitResult;
                targetPos = entityHit.getLocation();
            } else {
                targetPos = end;
            }


            int numBolts = 20;
            float totalDamage = instance.isMastered(entity) ? 6000.0F : 3000.0F;
            float boltDamage = totalDamage / numBolts;

            for (int i = 0; i < numBolts; i++) {
                LightningBolt bolt = new LightningBolt(level, entity);
                bolt.setCause(entity instanceof ServerPlayer sp ? sp : null);
                bolt.setMpCost(this.magiculeCost(entity, instance));
                bolt.setTensuraDamage(boltDamage);
                bolt.setAdditionalVisual(3);
                bolt.setRadius(3.0F);
                bolt.setSkill(instance);
                bolt.setPos(targetPos.x, targetPos.y, targetPos.z);
                level.addFreshEntity(bolt);

                List<LivingEntity> entities = level.getEntitiesOfClass(
                        LivingEntity.class,
                        bolt.getBoundingBox(),
                        e -> e != entity
                );
                for (LivingEntity e : entities) {
                    e.hurt(MythosDamageSources.VAJRA_SPEAR, boltDamage);
                }
            }

            entity.swing(InteractionHand.MAIN_HAND, true);
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 1.0F, 1.0F);

            instance.setCoolDown(500);
            instance.addMasteryPoint(entity);
        } else if (instance.getMode() == 3) {
            if (!this.failedToActivate(entity, (MobEffect) MythosMobEffects.THUNDER_GOD.get())) {
                if (!entity.hasEffect((MobEffect)MythosMobEffects.THUNDER_GOD.get())) {
                    if (SkillHelper.outOfMagicule(entity, instance)) {
                        return;
                    }

                    this.addMasteryPoint(instance, entity);
                    instance.setCoolDown(1200);
                    entity.setHealth(entity.getMaxHealth());
                    entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WARDEN_ROAR, SoundSource.PLAYERS, 1.0F, 1.0F);
                    entity.addEffect(new MobEffectInstance((MobEffect)MythosMobEffects.THUNDER_GOD.get(), this.isMastered(instance, entity) ? 7200 : 3600, 0, false, false, false));
                    TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.EXPLOSION_EMITTER);
                    TensuraParticleHelper.spawnServerParticles(entity.level, (ParticleOptions)TensuraParticles.LIGHTNING_SPARK.get(), entity.getX(), entity.getY(), entity.getZ(), 55, 0.08, 0.08, 0.08, 0.5, true);
                } else {
                    entity.removeEffect((MobEffect)MythosMobEffects.THUNDER_GOD.get());
                    entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                }

            }
        }
    }

    private boolean hasStorm(LivingEntity owner) {
        return !owner.getLevel().getEntitiesOfClass(ThunderStorm.class, owner.getBoundingBox(), (thunderStorm) -> {
            return thunderStorm.getOwner() == owner;
        }).isEmpty();
    }
}
