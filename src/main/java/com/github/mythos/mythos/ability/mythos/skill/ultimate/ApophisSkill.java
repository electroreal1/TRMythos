package com.github.mythos.mythos.ability.mythos.skill.ultimate;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.network.TensuraNetwork;
import com.github.manasmods.tensura.network.play2client.RequestFxSpawningPacket;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.github.mythos.mythos.config.MythosSkillsConfig.ApophisEmbodiment;
import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class ApophisSkill extends Skill {
    public ApophisSkill(SkillType type) {super(SkillType.ULTIMATE);}

    @Override
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/apophis.png");
    }

    @Override
    public double getObtainingEpCost() {return 5000000;}

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {return true;}

    public boolean canBeSlotted(ManasSkillInstance instance, LivingEntity entity) {return true;}

    public int getMaxMastery() {return 3000;}

    public boolean meetEPRequirement(@NotNull Player player, double newEP) {
        if (!EnableUltimateSkillObtainment()) return false;
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, (ManasSkill) Skills.PROFANITY.get());
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        TensuraEPCapability.getFrom(entity).ifPresent((cap) -> {
            if (!cap.isChaos() || !cap.isMajin()) {
                cap.setMajin(true);
            }
        });
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill() && entity instanceof Player player) {
            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            Skill previousSkill = (Skill) Skills.PROFANITY.get();
            Objects.requireNonNull(storage);
            storage.forgetSkill(previousSkill);
        }
    }

    public int modes() {
        return 4;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 4 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 4) ? 1 : (instance.getMode() + 1);
    }

    public Component getModeName(int mode) {
        MutableComponent name;
        switch (mode) {
            case 1:
                name = Component.translatable("trmythos.skill.mode.apophis.indoctrination");
                break;
            case 2:
                name = Component.translatable("trmythos.skill.mode.apophis.capitulate_to_heresy");
                break;
            case 3:
                name = Component.translatable("trmythos.skill.mode.apophis.envoy");
                break;
            case 4:
                name = Component.translatable("trmythos.skill.mode.apophis.embodiment_of_sin");
                break;
            default:
                name = Component.empty();
        }
        return name;
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        Player player;
        LivingEntity target;
        // Indoctrination
        if (instance.getMode() == 1) {
            CharmSkill.charm(instance, entity);
        }

        // Capitulate to Heresy
        if (instance.getMode() == 2) {
            if (entity instanceof Player) {
                player = (Player)entity;
            } else {
                return;
            }
            target = (LivingEntity)SkillHelper.getTargetingEntity(LivingEntity.class, (LivingEntity)player, 20.0D, 0.0D, false);
            if (TensuraEPCapability.getPermanentOwner(target) != entity.getUUID())
                return;

            harvestFestivalSubordinate(target, (Player)entity);
        }

        // Embodiment of Sin
        if (instance.getMode() == 4) {
            if (instance.isMastered(entity)) {
                if (ApophisEmbodiment()) {
                    SkillStorage storage = SkillAPI.getSkillsFrom(entity);
                    List<Skill> sinSkill = Arrays.asList(
                            UniqueSkills.WRATH.get(), UniqueSkills.ENVY.get(), UniqueSkills.GREED.get(),
                            UniqueSkills.PRIDE.get(), UniqueSkills.LUST.get(), UniqueSkills.GLUTTONY.get(),
                            UniqueSkills.SLOTH.get(), Skills.CARNAGE.get());

                    Skill chosenSkill = sinSkill.get(new java.util.Random().nextInt(sinSkill.size()));

                    TensuraSkillInstance newInstance = new TensuraSkillInstance(chosenSkill);

                    newInstance.setMastery(0);
                    if (storage.learnSkill(newInstance)) {
                        instance.setMastery(0);
                    }

                    entity.sendSystemMessage(Component.literal("You have acquired the Sin Series Skill: " + chosenSkill));

                    storage.syncAll();

                } else {
                    entity.sendSystemMessage(Component.literal("The World Is Suppressing Your Power."));
                }
            }
        }
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        Level level = entity.level;
        if (level.isClientSide) return true;
        if (instance.getMode() == 3) {

            // Every second
            if (heldTicks % 20 == 0) {
                if (SkillHelper.outOfMagicule(entity, instance)) return false;

                // Play aura sound & spawn FX
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 1.0F, 0.8F);

                TensuraNetwork.INSTANCE.send(
                        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                        new RequestFxSpawningPacket(
                                new ResourceLocation("tensura:strength_sap"),
                                entity.getId(), 0.0, 1.0, 0.0, true)
                );

                // Find targets in a 10-block radius
                List<LivingEntity> targets = level.getEntitiesOfClass(
                        LivingEntity.class,
                        entity.getBoundingBox().inflate(10.0),
                        e -> e.isAlive() && !e.is(entity) && !entity.isAlliedTo(e)
                );

                if (!targets.isEmpty()) {
                    double ep = TensuraEPCapability.getEP(entity);
                    if (ep <= 0) return true;

                    // Damage = 1 point per 5000 EP
                    float damagePerSecond = (float) (ep / 5000.0);

                    for (LivingEntity target : targets) {
                        if (target instanceof Player p && p.getAbilities().invulnerable) continue;

                        // Apply void damage
                        target.hurt(DamageSource.OUT_OF_WORLD, damagePerSecond);

                        TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.SMOKE, 1.0);
                    }
                }
            }

            // Add mastery every 3 seconds
            if (heldTicks % 60 == 0 && heldTicks > 0) {
                this.addMasteryPoint(instance, entity);
            }

            return true;
        }
        return false;
    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity living, LivingHurtEvent e) {
        if (instance.isToggled()) {
            if (DamageSourceHelper.isDarkDamage(e.getSource()) || DamageSourceHelper.isTensuraMagic(e.getSource()) || DamageSourceHelper.isSpiritual(e.getSource())) {
                if (instance.isMastered(living)) {
                    e.setAmount(e.getAmount() * 5.0F);
                } else {
                    e.setAmount(e.getAmount() * 4.0F);
                }
            }
        }
    }

    public void onEntityHurt(LivingHurtEvent event, ManasSkillInstance instance, Player player) {
        if (isInSlot(player)) {
            LivingEntity target = event.getEntity();
            DamageSource source = event.getSource();
            float amount = event.getAmount();

            if ((DamageSourceHelper.isDarkDamage(source)) || (DamageSourceHelper.isTensuraMagic(source))) {
                event.setCanceled(true);

                applyHealth(target, amount);
            }

            if ((DamageSourceHelper.isLightDamage(source)) || (DamageSourceHelper.isHoly(source))) {
                amount = event.getAmount() * 2.0F;
            }

            if (event.getSource().isMagic()) {
                event.setAmount(event.getAmount() * 0.25f);
            }
        }
    }

    private static void applyHealth(LivingEntity entity, float amount) {
        float currentAbsorption = entity.getAbsorptionAmount();
        entity.setAbsorptionAmount(currentAbsorption + amount);
    }

    private void harvestFestivalSubordinate(LivingEntity target, Player owner) {
        if (target instanceof Player) {
            if (TensuraPlayerCapability.isTrueDemonLord((Player)target)) {
                owner.displayClientMessage(Component.translatable("tensura.evolve.demon_lord.already").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                return;
            }

            if (TensuraPlayerCapability.isTrueHero(target)) {
                owner.displayClientMessage(Component.translatable("tensura.evolve.demon_lord.hero").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                return;
            }

            TensuraPlayerCapability.getFrom(owner).ifPresent((ownerCap) -> {
                int ownerSoulPoints = ownerCap.getSoulPoints();
                int harvestFestivalCost = 100000;
                if (ownerSoulPoints < harvestFestivalCost) {
                    owner.displayClientMessage(Component.translatable("trmythos.skill.mode.apophis.not_enough_souls", new Object[]{harvestFestivalCost / 1000}).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                } else {
                    TensuraPlayerCapability.getFrom((Player)target).ifPresent((cap) -> {
                        ownerCap.setSoulPoints(ownerCap.getSoulPoints() - harvestFestivalCost);
                        RaceHelper.awakening((Player)target, false);
                    });
                }
            });
        }
    }
}

