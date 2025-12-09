package com.github.mythos.mythos.ability.mythos.skill.unique.fused_skills;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
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
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSource;
import com.github.mythos.mythos.registry.MythosEntityTypes;
import com.github.mythos.mythos.registry.skill.FusedSkills;
import com.github.mythos.mythos.registry.skill.Skills;
import com.mojang.math.Vector3f;
import io.github.Memoires.trmysticism.registry.skill.UniqueSkills;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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


    public void onLearnSkill(ManasSkillInstance instance, LivingEntity living, UnlockSkillEvent event, Player player) {
         SkillStorage storage = SkillAPI.getSkillsFrom(player);
         Skill profanitySkill = Skills.PROFANITY.get();
         Skill dreamerSkill = UniqueSkills.DREAMER.get();
         Skill paranoiaSkill = FusedSkills.PARANOIA.get();
        if (storage.getSkill(profanitySkill).isPresent() && storage.getSkill(dreamerSkill).isPresent() && storage.getSkill(paranoiaSkill).isPresent()) {
            storage.forgetSkill(profanitySkill);
            storage.forgetSkill(dreamerSkill);
            storage.learnSkill(paranoiaSkill);
        }
    }

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

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
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

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity living, LivingHurtEvent e) {
        if (instance.isToggled()) {
            if (DamageSourceHelper.isDarkDamage(e.getSource())) {
                if (instance.isMastered(living)) {
                    e.setAmount(e.getAmount() * 6.0F);
                } else {
                    e.setAmount(e.getAmount() * 4.0F);
                }
            }
        }
    }

    public int modes() {
        return 2;
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        Level level = entity.level;
        if (!(level instanceof ServerLevel server)) return;
        RandomSource rand = player.level.random;
        int particles = 20;
        double yOffset = 1.2;

        for (int i = 0; i < particles; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double radius = 0.3 + rand.nextDouble() * 1.2;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + (rand.nextDouble() - 0.5) * 0.6;

            float size = 0.5f + rand.nextFloat() * 0.4f;
            Vector3f color = rand.nextDouble() < 0.5 ? new Vector3f(0.4f, 0f, 0.5f) : new Vector3f(0.3f, 0f, 0.3f);

            double motionX = (rand.nextDouble() - 0.5) * 0.02;
            double motionY = (rand.nextDouble() - 0.5) * 0.02;
            double motionZ = (rand.nextDouble() - 0.5) * 0.02;

            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, motionX, motionY, motionZ, 0);
        }
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
            if (heldTicks % 20 == 0 && SkillHelper.outOfMagicule(entity, instance)) {
                return false;
            }

            if (heldTicks % 100 == 0 && heldTicks > 0) {
                this.addMasteryPoint(instance, entity);
            }

            float damage = instance.isMastered(entity) ? 1000.0F : 500.0F;
            BreathEntity.spawnBreathEntity(MythosEntityTypes.DRAGONFIRE.get(), entity, instance, damage, this.magiculeCost(entity, instance));

            entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);

            return true;
        } return true;
    }



    public void onPressed(ManasSkillInstance instance, @NotNull LivingEntity entity) {
        if (instance.getMode() == 1) return;

        boolean hasBlizzard = !entity.getLevel().getEntitiesOfClass(BlizzardEntity.class, entity.getBoundingBox(),
                blizzard -> blizzard.getOwner() == entity).isEmpty();

        if (entity.isShiftKeyDown()) {
            for (BlizzardEntity blizzard : entity.getLevel().getEntitiesOfClass(BlizzardEntity.class, entity.getBoundingBox(),
                    b -> b.getOwner() == entity)) {
                blizzard.discard();
            }
        } else {
            if (!hasBlizzard && !SkillHelper.outOfMagicule(entity, 2000.0F)) {
                SkillHelper.outOfMagicule(entity, 2000.0F);

                BlizzardEntity blizzard = new BlizzardEntity(entity.getLevel(), entity);
                blizzard.setOwner(entity);
                float damage = instance.isMastered(entity) ? 500.0F : 250.0f;
                blizzard.setDamage(damage);
                entity.getLevel().addFreshEntity(blizzard);
                blizzard.applyEffect(entity);
                entity.swing(InteractionHand.MAIN_HAND, true);
                entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);

                this.addMasteryPoint(instance, entity);
                instance.setCoolDown(100);

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
