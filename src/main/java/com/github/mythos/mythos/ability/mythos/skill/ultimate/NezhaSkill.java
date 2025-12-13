package com.github.mythos.mythos.ability.mythos.skill.ultimate;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class NezhaSkill extends Skill {
    public NezhaSkill(SkillType type) {super(SkillType.ULTIMATE);}

    //@Nullable
    //@Override
    //public ResourceLocation getSkillIcon() {
    //    return new ResourceLocation("trmythos", "textures/skill/ultimate/nezah.png");
    //}

    public double getObtainingEpCost() {return 2500000;}
    public double learningCost() {return 2500000;}

    public boolean meetEPRequirement(Player entity, double newEP) {
        if (!EnableUltimateSkillObtainment()) return false;
        return (SkillUtils.isSkillMastered(entity, (ManasSkill) Skills.ETERNAL.get()) && TensuraPlayerCapability.isTrueHero(entity));
    }

    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill() && entity instanceof Player player) {
            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            Skill previousSkill = (Skill) Skills.ETERNAL.get();
            Objects.requireNonNull(storage);
            storage.forgetSkill(previousSkill);
        }
    }

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {return true;}

    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        entity.addEffect(new MobEffectInstance((MobEffect) MythosMobEffects.COMPLETE_REGENERATION.get(), 1200, 2, false, false, false));
    }

    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        MobEffectInstance effectInstance = entity.getEffect((MobEffect)MythosMobEffects.COMPLETE_REGENERATION.get());
        if (effectInstance != null && effectInstance.getAmplifier() < 2)
            entity.removeEffect((MobEffect)MythosMobEffects.COMPLETE_REGENERATION.get());
    }

    public void onTakenDamage(ManasSkillInstance instance, LivingDamageEvent event) {
        CompoundTag tag = instance.getOrCreateTag();
        float resistance = tag.getFloat("resistance");

        // Only process if the event isn't canceled, skill is active, and not on cooldown
        if (!event.isCanceled() && instance.isToggled() && !instance.onCoolDown()) {

            // Apply resistance to the incoming damage
            float reducedDamage = event.getAmount() * (100.0F - resistance) / 100.0F;
            event.setAmount(reducedDamage);

            // Gradually increase resistance when taking damage
            if (resistance > 0.0F) {
                if (!instance.isMastered(event.getEntity())) {
                    // Normal mode: resistance caps at 50%
                    if (resistance < 50.0F) {
                        tag.putFloat("resistance", resistance + 0.1F);
                        instance.setCoolDown(3); // short cooldown between gains
                    }
                } else {
                    // Mastered mode: resistance can reach 75%
                    if (resistance < 75.0F) {
                        tag.putFloat("resistance", resistance + 0.1F);
                    }
                }
            } else {
                // First activation: start with a baseline resistance
                tag.putFloat("resistance", 25.0F);
            }
        }
    }

    public boolean canBeSlotted(ManasSkillInstance instance) {return true;}

    public void onSubordinateDamage(ManasSkillInstance instance, LivingEntity owner, LivingDamageEvent event) {
        LivingEntity target = event.getEntity();

        if (owner == null || owner.level.isClientSide)
            return;

        if (isInSlot(owner))
            return;

        if (target.distanceTo(owner) > 20.0D)
            return;

        if (SkillHelper.isSubordinate(owner, target))
            return;

        float reduced = event.getAmount() * 0.5F;
        event.setAmount(reduced);

        owner.level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 0.6F, 1.0F);
    }

    public void onSubordinateDeath(ManasSkillInstance instance, LivingEntity owner, LivingDeathEvent event) {
        if (isInSlot(owner)) {
            LivingEntity target = event.getEntity();
            if (target.getPersistentData().getBoolean("is_display")) {
                if (owner instanceof Player) {
                    Player player = (Player)owner;
                    player.displayClientMessage((Component)Component.translatable("trmysticism.skill.mode.mephisto.life_domination.message.is_display")
                            .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
                }
                return;
            }
            if (owner.distanceToSqr((Entity)target) > 400.0D &&
                    owner instanceof Player) {
                Player player = (Player)owner;
                player.displayClientMessage((Component)Component.translatable("trmysticism.skill.mode.asmodeus.angel_of_death.failure", new Object[] { target.getName().getString() }).withStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
            }
            TensuraEPCapability.getFrom(target).ifPresent(cap -> {
                double subordinateEP = cap.getEP();
                if (!SkillHelper.outOfMagicule(owner, subordinateEP * 0.05D)) {
                    target.revive();
                    CompoundTag tags = target.getPersistentData();
                    tags.putBoolean("NO_EP_PLUNDER", true);
                    tags.putBoolean("NO_SKILL_PLUNDER", true);
                    tags.putBoolean("no_loot", true);
                    event.setCanceled(true);
                    owner.level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                    target.setHealth((float)(target.getMaxHealth() * 0.5D));
                    if (owner instanceof Player) {
                        Player player = (Player)owner;
                        player.displayClientMessage((Component)Component.translatable("trmysticism.skill.mode.asmodeus.angel_of_death.success", new Object[] { target.getName().getString() }).withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)), false);
                    }
                }
            });
        }
    }

    public int modes() {return 1;}
    public Component getModeName(int mode) {
        MutableComponent name;
        switch (mode) {
            case 1:
                name = Component.translatable("trmythos.skill.mode.nezha.zombie_breath");
                break;
            default:
                name = Component.empty();
        }
        return name;
    }

    public boolean onHeld(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, int heldTicks) {
        CompoundTag tag = instance.getOrCreateTag();
        if (instance.getMode() == 1) {
            // Range
            tag = instance.getOrCreateTag();
            if (tag.getDouble("range") < 3.0D)
                tag.putDouble("range", 30.0D);

            // Add mastery points every three seconds held
            if (heldTicks % 60 == 0 && heldTicks > 0)
                addMasteryPoint(instance, entity);

            // Convert to subordinate if killed by this effect
            if (!entity.isAlive()) {
                SkillHelper.getSubordinateOwner(entity);
                // Optional visual cue
                entity.level.playSound(null, entity.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.PLAYERS, 1.0F, 0.8F);
            }

            float damage = instance.isMastered(entity) ? 1500.0F : 1000.0F;
           // BreathEntity.spawnBreathEntity((EntityType) MythosEntityTypes.ZOMBIE_BREATH.get(), entity, instance, magiculeCost(entity, instance));
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
            return true;
        }
        return false;
    }
}
