package com.github.mythos.mythos.ability.skill.unique.fused_skills;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.effects.TensuraEffectsCapability;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.entity.magic.barrier.BlizzardEntity;
import com.github.manasmods.tensura.entity.magic.breath.BreathEntity;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.util.damage.TensuraDamageSource;
import com.github.mythos.mythos.registry.MythosEntityTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class ParanoiaSkill extends Skill {

    public ParanoiaSkill(SkillType type) {
        super(type);
    }


//    public void onLearnSkill(ManasSkillInstance instance, LivingEntity living, UnlockSkillEvent event, Player player) {
//         SkillStorage storage = SkillAPI.getSkillsFrom(player);
//         Skill profanitySkill = Skills.PROFANITY.get();
//         Skill dreamerSkill = UniqueSkills.DREAMER.get();
//         Skill paranoiaSkill = FusedSkills.PARANOIA.get();
//        if (storage.getSkill(profanitySkill).isPresent() && storage.getSkill(dreamerSkill).isPresent() && storage.getSkill(paranoiaSkill).isPresent()) {
//            storage.forgetSkill(profanitySkill);
//            storage.forgetSkill(dreamerSkill);
//            storage.learnSkill(paranoiaSkill);
//        }
//    }

    public double getObtainingEpCost() {
        return 250000;
    }

    @Override
    public int getMaxMastery() {
        return 10000;
    }

    public @Nullable ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/fused/paranoia");
    }

    @Override
    public boolean canBeToggled(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity) {
        return true;
    }

    public void onBeingDamaged(@NotNull ManasSkillInstance instance, LivingAttackEvent event) {
        if (this.isInSlot(event.getEntity())) {
            if (!event.isCanceled()) {
                LivingEntity entity = event.getEntity();
                if (this.isInSlot(entity)) {
                    DamageSource damageSource = event.getSource();
                    if (!damageSource.isBypassInvul() && !(entity.getRandom().nextFloat() > (0.1F))) {
                        entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 2.0F, 1.0F);
                        event.setCanceled(true);
                        if (SkillUtils.canNegateDodge(entity, damageSource)) {
                            event.setCanceled(false);
                        }
                    }
                }
            }
        }
    }

    public void onDeath(@NotNull ManasSkillInstance instance, LivingDeathEvent event) {
        if (!event.isCanceled()) {
            DamageSource source = event.getSource();
            if (source != DamageSource.OUT_OF_WORLD) {
                if (source instanceof TensuraDamageSource damageSource) {
                    if (damageSource.getIgnoreBarrier() >= 3.0F) {
                        return;
                    }
                }

                LivingEntity entity = event.getEntity();
                if (!entity.isAlive()) {
                    if (source.getEntity() != null) {
                        if (source.getEntity() == entity) {
                            return;
                        }

                        if (source.getEntity() == SkillHelper.getSubordinateOwner(entity)) {
                            return;
                        }
                    }

                    this.addMasteryPoint(instance, entity);
                    entity.setHealth(Math.max(entity.getHealth(), entity.getMaxHealth()));
                    entity.invulnerableTime = Math.max(60, entity.invulnerableTime);
                    Predicate<MobEffect> predicate = (effect) -> {
                        return effect.getCategory() == MobEffectCategory.HARMFUL;
                    };
                    SkillHelper.removePredicateEffect(entity, predicate);
                    TensuraEffectsCapability.resetEverything(entity, false, false);
                    TensuraEPCapability.getFrom(entity).ifPresent((cap) -> {
                        if (cap.getEP() <= 0.0) {
                            cap.setEP(entity, 100.0, false);
                        } else if (cap.getCurrentEP() <= 0.0) {
                            cap.setCurrentEP(entity, cap.getEP() * 0.5);
                        }

                        double SHP = entity.getAttributeValue((Attribute) TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());
                        if (cap.getSpiritualHealth() < SHP * 0.5) {
                            cap.setSpiritualHealth(SHP * 0.5);
                        }

                    });
                    TensuraEPCapability.sync(entity);
                    if (entity instanceof Player player) {
                        TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                            float multiplier = 1F;
                            cap.setMagicule(Math.max(cap.getMagicule(), player.getAttributeValue((Attribute) TensuraAttributeRegistry.MAX_MAGICULE.get()) * (double) multiplier));
                            cap.setAura(Math.max(cap.getAura(), player.getAttributeValue((Attribute) TensuraAttributeRegistry.MAX_AURA.get()) * (double) multiplier));
                            TensuraPlayerCapability.sync(player);
                        });
                    }

                    event.setCanceled(true);
                    if (!instance.onCoolDown()) {
                        instance.setCoolDown(600);
                    }

                    entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                    TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.TOTEM_OF_UNDYING, 1.0);
                    TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.TOTEM_OF_UNDYING, 2.0);
                    TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.FLASH, 1.0);
                }
            }
        }
    }

    public void onDamageEntity(ManasSkillInstance instance, @NotNull LivingEntity user, @NotNull LivingHurtEvent event) {
        if (!instance.isToggled()) return;

        DamageSource source = event.getSource();
        if (!(source instanceof TensuraDamageSource damageSource)) return;

        if (damageSource.getEntity() != user) return;

        boolean isDarkness = false;
        try {
            isDarkness = "tensura.dark_attack".equals(damageSource.getMsgId());
        } catch (Exception ignored) {
        }

        if (isDarkness) {
            float multiplier = instance.isMastered(user) ? 6.0F : 4.0F;
            event.setAmount(event.getAmount() * multiplier);
        }
    }

    public int modes() {
        return 2;
    }

    public void onTick(ManasSkillInstance instance, @NotNull LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        tag.remove("target");
        tag.putInt("heldSeconds", 0);
    }


    public @NotNull Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.translatable("trmythos.skill.paranoia.dragonfire");
            case 2 -> Component.translatable("trmythos.skill.paranoia.frozen");
            default -> Component.empty();
        };
    }

    public double magiculeCost(LivingEntity entity, ManasSkillInstance instance) {
        double var10000;
        switch (instance.getMode()) {
            case 2:
                var10000 = 10000;
                break;
            default:
                var10000 = 100000;
        }

        return var10000;
    }

    public int nextMode(@NotNull LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (instance.isMastered(entity)) {
            return instance.getMode() == 3 ? 1 : instance.getMode() + 1;
        } else {
            return instance.getMode() == 1 ? 2 : 1;
        }
    }



    public boolean onHeld(ManasSkillInstance instance, @NotNull LivingEntity entity, int heldTicks) {
        if (instance.getMode() == 1) {
            // Check for magicule every 20 ticks
            if (heldTicks % 20 == 0 && SkillHelper.outOfMagicule(entity, instance)) {
                return false;
            }

            // Add mastery points every 100 ticks
            if (heldTicks % 100 == 0 && heldTicks > 0) {
                this.addMasteryPoint(instance, entity);
            }

            // Damage
            float damage = instance.isMastered(entity) ? 1000.0F : 500.0F;
            BreathEntity.spawnBreathEntity(
                    MythosEntityTypes.DRAGONFIRE.get(),
                    entity,
                    instance,
                    damage,
                    this.magiculeCost(entity, instance)
            );

            // Play sound
            entity.getLevel().playSound(
                    null,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    SoundEvents.BLAZE_SHOOT,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );

            return true;
        } return true;
    }



    public void onPressed(ManasSkillInstance instance, @NotNull LivingEntity entity) {
        if (instance.getMode() != 2) return;

        boolean hasBlizzard = !entity.getLevel().getEntitiesOfClass(BlizzardEntity.class, entity.getBoundingBox(),
                blizzard -> blizzard.getOwner() == entity).isEmpty();

        if (entity.isShiftKeyDown()) {
            // Remove all existing blizzards
            for (BlizzardEntity blizzard : entity.getLevel().getEntitiesOfClass(BlizzardEntity.class, entity.getBoundingBox(),
                    b -> b.getOwner() == entity)) {
                blizzard.discard();
            }
        } else {
            // Only spawn if none exists and enough magicule
            if (!hasBlizzard && !SkillHelper.outOfMagicule(entity, 2000.0F)) {
                // Consume magicule
                SkillHelper.outOfMagicule(entity, 2000.0F);

                // Spawn the Blizzard entity
                BlizzardEntity blizzard = new BlizzardEntity(entity.getLevel(), entity);
                blizzard.setOwner(entity);
                float damage = instance.isMastered(entity) ? 500.0F : 250.0f;
                blizzard.setDamage(damage);
                entity.getLevel().addFreshEntity(blizzard);

                // Swing and play sound
                entity.swing(InteractionHand.MAIN_HAND, true);
                entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);

                // Mastery and cooldown
                this.addMasteryPoint(instance, entity);
                instance.setCoolDown(100);

                // Reset held ticks
                instance.getOrCreateTag().putInt("HeldTicks", 0);
                instance.markDirty();
            }
        }
        instance.markDirty();
    }

    private boolean hasBlizzard(LivingEntity owner) {
        return !owner.getLevel().getEntitiesOfClass(BlizzardEntity.class, owner.getBoundingBox(), (blizzard) -> {
            return blizzard.getOwner() == owner;
        }).isEmpty();
    }


}
