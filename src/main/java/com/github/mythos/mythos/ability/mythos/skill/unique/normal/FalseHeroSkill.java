package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.unique.CookSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class FalseHeroSkill extends Skill {
    public FalseHeroSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    public void onLearnSkill(ManasSkillInstance instance, LivingEntity living, UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            if (living instanceof Player) {
                Player player = (Player)living;
                TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                    cap.setBlessed(true);
                });
                TensuraEPCapability.getFrom(player).ifPresent((cap -> {
                    cap.setChaos(true);
                }));
                TensuraEPCapability.sync(player);
                TensuraPlayerCapability.sync(player);
            }

        }
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        int masteryCount = instance.getMastery();
        Random random = new Random();
        if (entity instanceof Player player) {
            if (!instance.isMastered(entity)) {
            if (random.nextInt(100) < 5) {
                int losePoints = 10;
                instance.setMastery(masteryCount - losePoints);
            }
        } else {
            if (random.nextInt(100) < 5) {
                    TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {

                        double magicules = TensuraPlayerCapability.getBaseMagicule(player);
                        double aura = TensuraPlayerCapability.getBaseAura(player);

                        cap.setBaseAura(aura * 0.25, player);
                        cap.setBaseMagicule(magicules * 0.25, player);
                    });

                }
            }

            Level level = entity.level;
            for (LivingEntity entity1 : level.getEntitiesOfClass(LivingEntity.class,
                    new AABB(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY))) {

                if (entity1 instanceof Player player1) continue;
                if (entity1 instanceof Mob mob) {
                    mob.setTarget(player);
                }

            }
        }
    }

    @Override
    public int modes() {
        return 2;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 2 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 2) ? 1 : (instance.getMode() + 1);
    }

    private boolean activatedChaoticFate(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        if (tag.getInt("ChaoticFate") < 100) {
            return false;
        } else {
            return instance.isMastered(entity) && instance.isToggled() ? true : tag.getBoolean("ChaoticFateActivated");
        }
    }

    public void onTouchEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent event) {
        if (!TensuraSkillCapability.isSkillInSlot(entity, Skills.FALSE_HERO.get())) return;
        CompoundTag tag = instance.getOrCreateTag();

        if (this.activatedChaoticFate(instance, entity)) {
            if (!instance.onCoolDown()) {
                LivingEntity target = event.getEntity();
                AttributeInstance targetHealth = target.getAttribute(Attributes.MAX_HEALTH);
                if (targetHealth != null) {
                    double amount = (double) event.getAmount();

                    AttributeModifier chefModifier = targetHealth.getModifier(CookSkill.COOK);
                    if (chefModifier != null) {
                        amount -= chefModifier.getAmount();
                    }

                    AttributeModifier targetModifier = new AttributeModifier(
                            CookSkill.COOK, "Cook", amount * -1.0, AttributeModifier.Operation.ADDITION
                    );
                    targetHealth.removeModifier(targetModifier);
                    targetHealth.addPermanentModifier(targetModifier);

                    AttributeInstance casterHealth = entity.getAttribute(Attributes.MAX_HEALTH);
                    if (casterHealth != null) {
                        double casterMaxHealth = casterHealth.getBaseValue();
                        double reduction = casterMaxHealth * 0.03; // 3%
                        casterHealth.setBaseValue(casterMaxHealth - reduction);
                    }

                    if (!instance.isMastered(entity) || !instance.isToggled()) {
                        tag.putBoolean("ChaoticFateActivated", false);
                    }

                    this.addMasteryPoint(instance, entity);
                    instance.setCoolDown(1);
                    entity.getLevel().playSound((Player) null, entity.blockPosition(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.AMBIENT, 1.0F, 1.0F);
                }
            }
        }
    }


    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() == 1) {
            CompoundTag tag = instance.getOrCreateTag();
            int learnPoint = tag.getInt("ChaoticFate");
                tag.putInt("ChaoticFate", learnPoint + SkillUtils.getEarningLearnPoint(instance, entity, true));


                if (entity.isShiftKeyDown()) {
                    LivingEntity target = SkillHelper.getTargetingEntity(entity, 6.0, false);
                    if (target == null || !target.isAlive()) {
                        target = entity;
                    }

                    removeCookedHP(target, instance);
                } else {
                    boolean activated = tag.getBoolean("ChaoticFateActivated");
                    tag.putBoolean("ChaoticFateActivated", !activated);
                    entity.swing(InteractionHand.MAIN_HAND, true);
                    entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), activated ? SoundEvents.CONDUIT_DEACTIVATE : SoundEvents.CONDUIT_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
                }

        } else if (instance.getMode() == 2) {
            if (Math.random() < 0.05) {
                instance.setCoolDown(100);
                return;
            } else {
                if (entity instanceof Player caster) {
                    Level world = caster.level;
                    BlockPos pos = caster.blockPosition();

                    double radius = 5;

                    List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class,
                            new AABB(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius,
                                    pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius));

                    for (LivingEntity entity1 : entities) {
                        if (entity == caster) continue; // skip caster
                        double totalDamage = 100;
                        double perType = totalDamage / 3.0;

                        entity.hurt(DamageSource.IN_FIRE, (float) perType);
                        entity.hurt(DamageSource.DROWN, (float) perType);
                        entity.hurt(TensuraDamageSources.lightning(caster), (float) perType);

                        world.playSound(null, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0F, 1.0F);
                        world.explode(null, pos.getX(), pos.getY(), pos.getZ(), 3.0F, Explosion.BlockInteraction.NONE);
                        instance.setCoolDown(100);
                    }
                }
            }
        }
    }

    public static void removeCookedHP(LivingEntity entity, @Nullable ManasSkillInstance instance) {
        AttributeInstance health = entity.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) {
            if (health.getModifier(CookSkill.COOK) != null) {
                health.removePermanentModifier(CookSkill.COOK);
                if (instance != null) {
                    instance.setCoolDown(1);
                }

                entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.COMPOSTER);
            }

        }
    }



}
