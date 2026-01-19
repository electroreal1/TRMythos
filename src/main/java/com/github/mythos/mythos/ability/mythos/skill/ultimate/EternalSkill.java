package com.github.mythos.mythos.ability.mythos.skill.ultimate;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.Objects;
import java.util.UUID;

import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class EternalSkill extends Skill {
    public EternalSkill(SkillType type) {super(SkillType.ULTIMATE);}
    protected static final UUID ACCELERATION = UUID.fromString("46dc5eee-34e9-4a6c-ad3d-58048cb06c6f");

    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/ultimate/eternal.png");
    }

    public double getObtainingEpCost() {return 1000000;}

    public double learningCost() {return 1000000;}

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
        return true;
    }

    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
        entity.addEffect(new MobEffectInstance((MobEffect) MythosMobEffects.COMPLETE_REGENERATION.get(), 1200, 2, false, false, false));
    }

    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);
        MobEffectInstance effectInstance = entity.getEffect((MobEffect)MythosMobEffects.COMPLETE_REGENERATION.get());
        if (effectInstance != null && effectInstance.getAmplifier() < 1)
            entity.removeEffect((MobEffect)MythosMobEffects.COMPLETE_REGENERATION.get());
    }

    public boolean meetEPRequirement(Player entity, double newEP) {
        if (!EnableUltimateSkillObtainment()) return false;
        return (SkillUtils.isSkillMastered(entity, UniqueSkills.SURVIVOR.get()) && TensuraPlayerCapability.isTrueDemonLord(entity));
    }

    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill() && entity instanceof Player player) {
            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            Skill previousSkill = UniqueSkills.SURVIVOR.get();
            Objects.requireNonNull(storage);
            storage.forgetSkill(previousSkill);
        }
    }

    public boolean canBeSlotted(ManasSkillInstance instance) {
        return true;
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
                if (!SkillHelper.outOfMagicule(owner, subordinateEP * 0.1D)) {
                    target.revive();
                    CompoundTag tags = target.getPersistentData();
                    tags.putBoolean("NO_EP_PLUNDER", true);
                    tags.putBoolean("NO_SKILL_PLUNDER", true);
                    tags.putBoolean("no_loot", true);
                    event.setCanceled(true);
                    owner.level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                    target.setHealth((float)(target.getMaxHealth() * 0.2D));
                    if (owner instanceof Player) {
                        Player player = (Player)owner;
                        player.displayClientMessage((Component)Component.translatable("trmysticism.skill.mode.asmodeus.angel_of_death.success", new Object[] { target.getName().getString() }).withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)), false);
                    }
                }
            });
        }
    }
}
