package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.entity.projectile.StarFallProjectile;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;


public class StargazerSkill extends Skill {
    private static final UUID MOONSPEAK_DAMAGE_UUID = UUID.fromString("dcd9fd31-3324-4d1c-8f7d-6bf3c4a2d001");
    private static final UUID MOONSPEAK_ARMOR_UUID = UUID.fromString("1a92cb86-1a3b-4fb6-9fbc-3d95f3b15ea8");
    private static final UUID MOONSPEAK_SPEED_UUID = UUID.fromString("7ad3e3fb-c7a9-4af3-bc8d-cb2b4eaadd10");

    public StargazerSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/stargazer.png");
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity living, LivingHurtEvent e) {
        if (instance.isToggled()) {
            if (DamageSourceHelper.isSpatialDamage(e.getSource())) {
                if (instance.isMastered(living)) {
                    e.setAmount(e.getAmount() * 3.0F);
                } else {
                    e.setAmount(e.getAmount() * 2.0F);
                }
            }
            if (DamageSourceHelper.isLightDamage(e.getSource())) {
                if (instance.isMastered(living)) {
                    e.setAmount(e.getAmount() * 3);
                } else {
                    e.setAmount(e.getAmount() * 2);
                }
            }
        }
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        Level level = entity.level;

        if (level.isClientSide) return;

        boolean isNight = !level.isDay();

        if (isNight) {
            applyModifiers(entity);
        } else {
            removeModifiers(entity);
        }
    }

    private void applyModifiers(LivingEntity living) {
        AttributeInstance dmg = living.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmg != null && dmg.getModifier(MOONSPEAK_DAMAGE_UUID) == null) {
            dmg.addPermanentModifier(new AttributeModifier(
                    MOONSPEAK_DAMAGE_UUID,
                    "moonspeak_damage",
                    20.0,
                    AttributeModifier.Operation.ADDITION
            ));
        }

        AttributeInstance armor = living.getAttribute(Attributes.ARMOR);
        if (armor != null && armor.getModifier(MOONSPEAK_ARMOR_UUID) == null) {
            armor.addPermanentModifier(new AttributeModifier(
                    MOONSPEAK_ARMOR_UUID,
                    "moonspeak_armor",
                    20.0,
                    AttributeModifier.Operation.ADDITION
            ));
        }

        AttributeInstance speed = living.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null && speed.getModifier(MOONSPEAK_SPEED_UUID) == null) {
            speed.addPermanentModifier(new AttributeModifier(
                    MOONSPEAK_SPEED_UUID,
                    "moonspeak_speed",
                    0.1,
                    AttributeModifier.Operation.ADDITION
            ));
        }
    }

    private void removeModifiers(LivingEntity living) {
        AttributeInstance dmg = living.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmg != null) dmg.removeModifier(MOONSPEAK_DAMAGE_UUID);

        AttributeInstance armor = living.getAttribute(Attributes.ARMOR);
        if (armor != null) armor.removeModifier(MOONSPEAK_ARMOR_UUID);

        AttributeInstance speed = living.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) speed.removeModifier(MOONSPEAK_SPEED_UUID);
    }

    @Override
    public int modes() {
        return 2;
    }

    @Override
    public int getMaxMastery() {
        return 3000;
    }

    @Override
    public double getObtainingEpCost() {
        return 100000;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() == 1) {
            if (!SkillHelper.outOfMagicule(entity, instance)) {
                this.addMasteryPoint(instance, entity);
                instance.setCoolDown(instance.isMastered(entity) ? 2 : 5);
                instance.markDirty();
                entity.swing(InteractionHand.MAIN_HAND, true);
                entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
                entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0F, 1.0F);
                TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions) TensuraParticles.SOLAR_FLASH.get());
                TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions) TensuraParticles.SOLAR_FLASH.get(), 2.0);
                TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions) TensuraParticles.SOLAR_FLASH.get(), 4.0);
                List<LivingEntity> list = entity.getLevel().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(5), (living) -> {
                    return !living.is(entity) && living.isAlive() && !living.isAlliedTo(entity);
                });
                if (!list.isEmpty()) {

                    int amount = instance.isMastered(entity) ? 250 : 100;
                    Iterator var6 = list.iterator();

                    while (true) {
                        Player player;
                        LivingEntity target;
                        do {
                            if (!var6.hasNext()) {
                                return;
                            }

                            target = (LivingEntity) var6.next();
                            if (!(target instanceof Player)) {
                                break;
                            }

                            player = (Player) target;
                        } while (player.getAbilities().invulnerable);

                        target.hurt(TensuraDamageSources.elementalAttack("tensura.light_attack", entity, true), amount);
                        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 10, 1, false, false, false));
                        target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 10, 1, false, false, false));
                        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 1, false, false, false));
                    }
                }
            }
        } if (instance.getMode() == 2) {
            if (!SkillHelper.outOfMagicule(entity, instance)) {
                entity.swing(InteractionHand.MAIN_HAND, true);
                this.addMasteryPoint(instance, entity);
                Level level = entity.getLevel();
                int distance = instance.isMastered(entity) ? 30 : 20;
                Entity target = SkillHelper.getTargetingEntity(entity, (double)distance, false, true);
                Vec3 pos;
                if (target != null) {
                    pos = target.getEyePosition();
                } else {
                    BlockHitResult result = SkillHelper.getPlayerPOVHitResult(entity.level, entity, ClipContext.Fluid.NONE, (double)distance);
                    pos = result.getLocation().add(0.0, 0.5, 0.0);
                }

                this.StarFall(instance, entity, pos, 10, 3.0);


                level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
                instance.setCoolDown(10);
            }
        }
    }

    private void StarFall(ManasSkillInstance instance, LivingEntity entity, Vec3 pos, int arrowAmount, double distance) {
        int arrowRot = 360 / arrowAmount;

        for(int i = 0; i < arrowAmount; ++i) {
            Vec3 arrowPos = entity.getEyePosition().add((new Vec3(0.0, distance, 0.0)).zRot(((float)(arrowRot * i) - (float)arrowRot / 2.0F) * 0.017453292F).xRot(-entity.getXRot() * 0.017453292F).yRot(-entity.getYRot() * 0.017453292F));
            StarFallProjectile arrow = new StarFallProjectile(entity.getLevel(), entity);
            arrow.setSpeed(2.0F);
            arrow.setPos(arrowPos);
            arrow.shootFromRot(pos.subtract(arrowPos).normalize());
            arrow.setLife(50);
            if (instance.isMastered(entity)) {
                arrow.setDamage(22.5f);
            } else arrow.setDamage(15);

            arrow.setMpCost(this.magiculeCost(entity, instance) / (double)arrowAmount);
            arrow.setSkill(instance);
            entity.getLevel().addFreshEntity(arrow);
        }

    }
}
