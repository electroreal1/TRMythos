package com.github.mythos.mythos.ability.mythos.skill.unique.vassal_line;

import com.github.manasmods.manascore.api.attribute.AttributeModifierHelper;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.attribute.ManasCoreAttributes;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.config.TensuraConfig;
import com.github.manasmods.tensura.data.TensuraTags;
import com.github.manasmods.tensura.entity.human.CloneEntity;
import com.github.manasmods.tensura.event.PossessionEvent;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.race.RaceHelper;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.dimensions.TensuraDimensions;
import com.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.manasmods.tensura.util.attribute.TensuraAttributeModifierIds;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.util.MythosUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;

public class UnitySkill extends Skill {

    public UnitySkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public int modes() {
        return 2;
    }

    @Override
    public MutableComponent getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.translatable("trmythos.skill.mode.unity.as_one");
            case 2 -> Component.translatable("trmythos.skill.mode.unity.unity");
            default -> Component.empty();
        };
    }

    @Override
    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    private boolean canPossess(LivingEntity target, Player player) {
        if (target.getType().is(TensuraTags.EntityTypes.NO_POSSESSION)) {
            return false;
        } else if (RaceHelper.isSpiritualLifeForm(target)) {
            return false;
        } else if (player.isCreative()) {
            return true;
        } else {
            if (target instanceof CloneEntity clone) {
                if (clone.getSkill() != this) {
                    return false;
                }

                if (clone.getOwner() == player) {
                    return true;
                }
            }

            if (SkillUtils.isSkillToggled(target, ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get())) {
                return false;
            } else {
                double amplifier = 1.0;
                if (SkillUtils.isSkillToggled(target, ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())) {
                    amplifier = 0.5;
                }

                if (target instanceof Player targetPlayer) {
                    if (!targetPlayer.isCreative() && !target.isSpectator()) {
                        int requirement = 0;
                        if ((double) target.getHealth() < (double) (target.getMaxHealth() * 0.1F) * amplifier) {
                            ++requirement;
                        }

                        if (TensuraEPCapability.getSpiritualHealth(target) < target.getAttributeValue(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get()) * 0.10000000149011612 * amplifier) {
                            ++requirement;
                        }

                        if (TensuraEPCapability.getEP(target) < TensuraEPCapability.getEP(player) * 0.25 * amplifier) {
                            ++requirement;
                        }

                        return requirement >= 2;
                    }
                }

                if ((double) target.getHealth() < (double) target.getMaxHealth() * 0.1 * amplifier) {
                    return true;
                } else if (TensuraEPCapability.getSpiritualHealth(target) < target.getAttributeValue(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get()) * 0.10000000149011612 * amplifier) {
                    return true;
                } else {
                    return TensuraEPCapability.getEP(target) < TensuraEPCapability.getEP(player) * 0.25 * amplifier;
                }
            }
        }
    }

    private boolean canCopySkill(ManasSkillInstance instance, LivingEntity target, boolean clone) {
        if (clone) {
            return instance.isTemporarySkill();
        } else if (target instanceof Player player) {
            return instance.isTemporarySkill() || TensuraPlayerCapability.getIntrinsicList(player).contains(SkillUtils.getSkillId(instance.getSkill()));
        } else {
            return true;
        }
    }

    public void copyStatsAndSkills(LivingEntity target, Player owner) {
        this.applyBaseAttributeModifiers(owner, target);
        owner.setHealth(Math.max(target.getHealth(), 0.0F));

        for (MobEffectInstance instance : target.getActiveEffects()) {
            owner.addEffect(new MobEffectInstance(instance));
        }

        boolean clone = target instanceof CloneEntity;
        Iterator<ManasSkillInstance> var8 = List.copyOf(SkillAPI.getSkillsFrom(target).getLearnedSkills()).iterator();

        while (true) {
            ManasSkillInstance instance;
            do {
                do {
                    if (!var8.hasNext()) {
                        return;
                    }

                    instance = var8.next();
                } while (!this.canCopySkill(instance, target, clone));
            } while (instance.getMastery() < 0 && instance.getMastery() != -100);

            ManasSkillInstance copy = TensuraSkillInstance.fromNBT(instance.toNBT());
            if (!copy.isTemporarySkill()) {
                copy.setRemoveTime(-2);
            }

            if (SkillUtils.learnSkill(owner, copy)) {
                owner.displayClientMessage(Component.translatable("tensura.skill.temporary.success_drain", copy.getSkill().getName()).setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)), false);
            }
        }
    }

    public void applyBaseAttributeModifiers(LivingEntity owner, LivingEntity target) {
        Map.Entry map;
        double value;
        for (Iterator<Map.Entry<Attribute, Triple<UUID, String, Double>>> var3 = this.getStatList().entrySet().iterator(); var3.hasNext(); AttributeModifierHelper.setModifier(owner, (Attribute) map.getKey(), new AttributeModifier((UUID) ((Triple) map.getValue()).getLeft(), (String) ((Triple) map.getValue()).getMiddle(), value - owner.getAttributeBaseValue((Attribute) map.getKey()), AttributeModifier.Operation.ADDITION))) {
            map = var3.next();
            AttributeInstance attribute = target.getAttribute((Attribute) map.getKey());
            if (attribute == null) {
                value = (Double) ((Triple<?, ?, ?>) map.getValue()).getRight();
            } else {
                value = target.getAttributeBaseValue((Attribute) map.getKey());
                AttributeModifier raceStat = attribute.getModifier((UUID) ((Triple<?, ?, ?>) map.getValue()).getLeft());
                if (raceStat != null) {
                    value += raceStat.getAmount();
                }
            }

            if (map.getKey().equals(Attributes.MOVEMENT_SPEED) && !(target instanceof Player)) {
                value = value / 0.23 * 0.1;
            } else if (map.getKey().equals(Attributes.ATTACK_DAMAGE) && !this.isOwnClone(owner, target)) {
                value = Math.min(value, TensuraConfig.INSTANCE.racesConfig.maxAttackPossession.get());
            } else if (map.getKey().equals(Attributes.MAX_HEALTH) && !this.isOwnClone(owner, target)) {
                value = Math.min(value, TensuraConfig.INSTANCE.racesConfig.maxHeathPossession.get());
            }
        }

        AttributeInstance jumpStrength = target.getAttribute(Attributes.JUMP_STRENGTH);
        double jump;
        if (jumpStrength == null) {
            AttributeInstance jumpPower = target.getAttribute(ManasCoreAttributes.JUMP_POWER.get());
            if (jumpPower == null) {
                jump = 0.42;
            } else {
                jump = target.getAttributeBaseValue(ManasCoreAttributes.JUMP_POWER.get());
                AttributeModifier raceStat = jumpPower.getModifier(TensuraAttributeModifierIds.RACE_JUMP_HEIGHT_MODIFIER_ID);
                if (raceStat != null) {
                    jump += raceStat.getAmount();
                }
            }
        } else {
            jump = Math.max(target.getAttributeBaseValue(Attributes.JUMP_STRENGTH) / 0.7 * 0.42, 0.42);
        }

        AttributeModifierHelper.setModifier(owner, ManasCoreAttributes.JUMP_POWER.get(), new AttributeModifier(TensuraAttributeModifierIds.RACE_JUMP_HEIGHT_MODIFIER_ID, "tensura:race_jump_power", jump - owner.getAttributeBaseValue(ManasCoreAttributes.JUMP_POWER.get()), AttributeModifier.Operation.ADDITION));
    }

    public boolean isOwnClone(LivingEntity owner, LivingEntity target) {
        boolean var10000;
        if (target instanceof CloneEntity clone) {
            if (clone.isOwnedBy(owner)) {
                var10000 = true;
                return true;
            }
        }

        var10000 = false;
        return false;
    }

    private Map<Attribute, Triple<UUID, String, Double>> getStatList() {
        return Map.of(Attributes.MAX_HEALTH, Triple.of(TensuraAttributeModifierIds.RACE_BASE_HEALTH_MODIFIER_ID, "tensura:race_base_health", 1.0), Attributes.ATTACK_DAMAGE, Triple.of(TensuraAttributeModifierIds.RACE_ATTACK_DAMAGE_MODIFIER_ID, "tensura:race_attack_damage", 0.1), Attributes.KNOCKBACK_RESISTANCE, Triple.of(TensuraAttributeModifierIds.RACE_KNOCKBACK_RESISTANCE_MODIFIER_ID, "tensura:race_knockback_resistance", 0.0), Attributes.MOVEMENT_SPEED, Triple.of(TensuraAttributeModifierIds.RACE_MOVEMENT_SPEED_MODIFIER_ID, "tensura:race_movement_speed", 0.1));
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity owner) {
        switch (instance.getMode()) {
            case 1: // Possession mode
                if (owner instanceof Player player) {
                    Level level = owner.getLevel();
                    if (!level.dimension().equals(TensuraDimensions.HELL) && !level.dimension().equals(TensuraDimensions.LABYRINTH)) {
                        TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                            if (cap.isSpiritualForm()) {
                                LivingEntity target = SkillHelper.getTargetingEntity(owner, 5.0, false);
                                if (target == null || !target.isAlive()) {
                                    return;
                                }

                                if (!this.canPossess(target, player)) {
                                    player.displayClientMessage(Component.translatable("tensura.targeting.not_allowed").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                                    return;
                                }

                                PossessionEvent event = new PossessionEvent(target, player);
                                if (MinecraftForge.EVENT_BUS.post(event)) {
                                    return;
                                }

                                ((ServerPlayer) player).teleportTo((ServerLevel) level, target.position().x, target.position().y, target.position().z, target.getYRot(), target.getXRot());
                                player.hurtMarked = true;
                                level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
                                TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.SQUID_INK, 2.0, 20);
                                this.copyStatsAndSkills(target, player);
                                CloneEntity.copyEffects(target, player);
                                if (target instanceof CloneEntity clone) {
                                    if (clone.isOwnedBy(player)) {
                                        clone.copyEquipmentsOntoOwner(player, false);
                                        clone.resetOwner(null);
                                    }
                                }

                                if (target instanceof Player targetPlayer) {
                                    TensuraPlayerCapability.getFrom(targetPlayer).ifPresent((targetCap) -> {
                                        targetCap.setSpiritualForm(true);
                                        targetCap.applyBaseAttributeModifiers(targetPlayer);
                                        targetPlayer.getAbilities().mayfly = true;
                                        targetPlayer.getAbilities().flying = true;
                                        targetPlayer.onUpdateAbilities();
                                        SkillStorage storage = SkillAPI.getSkillsFrom(targetPlayer);

                                        for (ManasSkillInstance temp : List.copyOf(storage.getLearnedSkills())) {
                                            if (temp.isTemporarySkill()) {
                                                if (temp.getTag() != null) {
                                                    temp.getTag().remove("SpatialStorage");
                                                }

                                                storage.forgetSkill(temp);
                                            }
                                        }

                                    });
                                    TensuraPlayerCapability.sync(targetPlayer);
                                } else {
                                    target.skipDropExperience();
                                    if (!target.hurt(TensuraDamageSources.SOUL_SCATTER, target.getMaxHealth() * 10.0F)) {
                                        target.die(TensuraDamageSources.SOUL_SCATTER);
                                        target.discard();
                                    } else {
                                        target.deathTime = 19;
                                    }
                                }

                                cap.setSpiritualForm(false);
                                if (!player.isCreative() && !player.isSpectator()) {
                                    player.getAbilities().mayfly = false;
                                    player.getAbilities().flying = false;
                                    player.onUpdateAbilities();
                                }
                            } else {
                                double EP = TensuraEPCapability.getEP(owner);
                                EntityType<CloneEntity> type = TensuraEntityTypes.CLONE_DEFAULT.get();
                                CloneEntity clonex = new CloneEntity(type, level);
                                clonex.setLife(TensuraConfig.INSTANCE.skillsConfig.bodyDespawnTick.get() * 20);
                                clonex.tame(player);
                                clonex.setSkill(this);
                                clonex.setImmobile(true);
                                clonex.setHealth(owner.getHealth());
                                clonex.copyEquipments(owner);
                                EquipmentSlot[] var10 = EquipmentSlot.values();

                                for (EquipmentSlot slot : var10) {
                                    player.setItemSlot(slot, ItemStack.EMPTY);
                                }

                                TensuraEPCapability.setLivingEP(clonex, Math.max(EP / 100.0, 100.0));
                                clonex.copyStatsAndSkills(owner, CloneEntity.CopySkill.INTRINSIC, true);
                                clonex.setRemainingFireTicks(owner.getRemainingFireTicks());
                                CloneEntity.copyEffects(player, clonex);
                                AttributeInstance cloneHP = clonex.getAttribute(Attributes.MAX_HEALTH);
                                Race race = TensuraPlayerCapability.getRace(player);
                                if (cloneHP != null && race != null) {
                                    AttributeModifier cloneModifier = cloneHP.getModifier(TensuraAttributeModifierIds.RACE_BASE_HEALTH_MODIFIER_ID);
                                    if (cloneModifier != null) {
                                        double raceHP = race.getBaseHealth() - player.getAttributeBaseValue(Attributes.MAX_HEALTH);
                                        if (cloneModifier.getAmount() == raceHP) {
                                            clonex.setLife(-1);
                                            instance.getOrCreateTag().putUUID("OriginalBody", clonex.getUUID());
                                            instance.markDirty();
                                        }
                                    }
                                }

                                clonex.moveTo(owner.position().x, owner.position().y, owner.position().z, owner.getYRot(), owner.getXRot());
                                level.addFreshEntity(clonex);
                                cap.setSpiritualForm(true);
                                if (!player.isCreative() && !player.isSpectator()) {
                                    player.getAbilities().mayfly = true;
                                    player.getAbilities().flying = true;
                                    player.onUpdateAbilities();
                                }

                                cap.applyBaseAttributeModifiers(player);
                                level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                                SkillStorage storage = SkillAPI.getSkillsFrom(player);

                                for (ManasSkillInstance temp : List.copyOf(storage.getLearnedSkills())) {
                                    if (temp.isTemporarySkill()) {
                                        if (temp.getTag() != null) {
                                            temp.getTag().remove("SpatialStorage");
                                        }

                                        storage.forgetSkill(temp);
                                    }
                                }
                            }

                            TensuraPlayerCapability.sync(player);
                        });
                    } else {
                        player.displayClientMessage(Component.translatable("tensura.ability.activation_failed").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                    }
                }
                break;

            case 2:
                LivingEntity target = MythosUtils.getLookedAtEntity(owner, 10);
                SkillStorage userStorage = SkillAPI.getSkillsFrom(owner);
                SkillStorage targetStorage = SkillAPI.getSkillsFrom(target);
                Random random = new Random();

                if (owner.isShiftKeyDown()) {
                    List<ManasSkillInstance> targetSkills = targetStorage.getLearnedSkills().stream()
                            .filter(s -> !s.isTemporarySkill() && s.getSkill() instanceof Skill)
                            .filter(s -> ((Skill) s.getSkill()).getType() != SkillType.ULTIMATE)
                            .toList();

                    if (!targetSkills.isEmpty()) {
                        ManasSkillInstance toCopy = targetSkills.get(random.nextInt(targetSkills.size()));
                        TensuraSkillInstance tempCopy = new TensuraSkillInstance(toCopy.getSkill());

                        tempCopy.getOrCreateTag().putBoolean("IntertwinedTemp", true);
                        tempCopy.getOrCreateTag().putInt("Duration", 1200);

                        userStorage.learnSkill(tempCopy);
                        owner.sendSystemMessage(Component.literal("Existence Intertwined: Replicated " + Objects.requireNonNull(toCopy.getSkill().getName())
                                        .getString())
                                .withStyle(ChatFormatting.LIGHT_PURPLE));
                    }
                } else {
                    List<Skill> allSkills = SkillAPI.getSkillRegistry().getValues().stream()
                            .filter(s -> s instanceof Skill)
                            .map(s -> (Skill) s)
                            .filter(s -> s.getType() == Skill.SkillType.EXTRA || s.getType() == Skill.SkillType.UNIQUE)
                            .toList();

                    if (!allSkills.isEmpty()) {
                        Skill randomSkill = allSkills.get(random.nextInt(allSkills.size()));
                        TensuraSkillInstance grantedSkill = new TensuraSkillInstance(randomSkill);

                        grantedSkill.getOrCreateTag().putBoolean("IntertwinedTemp", true);

                        targetStorage.learnSkill(grantedSkill);
                        owner.sendSystemMessage(Component.literal("Existence Intertwined: Granted " + Objects.requireNonNull(randomSkill.getName())
                                        .getString() + " to " + target.getName().getString())
                                .withStyle(ChatFormatting.GOLD));
                    }
                }

                if (owner.level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, owner.getX(), owner.getY() + 1, owner.getZ(), 20, 0.5, 0.5, 0.5, 0.1);
                    serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, target.getX(), target.getY() + 1, target.getZ(), 20, 0.5, 0.5, 0.5, 0.1);
                }

                owner.level.playSound(null, owner.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 1.2f);
                instance.setCoolDown(30);
                break;


            case 3:
                LivingEntity target1 = MythosUtils.getLookedAtEntity(owner, 10);
                CharmSkill.charm(instance, target1);
                break;
        }
    }
}
