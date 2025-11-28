package com.github.mythos.mythos.ability.mythos.skill.unique.evolved;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.manascore.attribute.ManasCoreAttributes;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.effects.TensuraEffectsCapability;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSource;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

public class PerseveranceSkill extends Skill {
    public PerseveranceSkill(SkillType type) {super(SkillType.UNIQUE);}
    public static final UUID PERSISTENT = UUID.fromString("8d2fe5e0-4bb1-3f7e-8a4a-2d1d889f4241");
    public static final UUID WILLPOWER = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    //@Override
    //public ResourceLocation getSkillIcon() {
    //    return new ResourceLocation("trmythos", "textures/skill/unique/perseverance.png");
    //}

    public int getMaxMastery() {return 5000;}

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
        return true;
    }

    public boolean canBeSlotted(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return instance.isToggled();
    }

    public boolean meetEPRequirement(Player entity, double newEP) {
        SkillStorage storage = SkillAPI.getSkillsFrom((Entity)entity);
        ManasSkill PrideSkill = (ManasSkill)SkillAPI.getSkillRegistry().getValue(Skills.TENACIOUS.getId());
        return ((Boolean)storage.getSkill(PrideSkill)
                .map(instance -> Boolean.valueOf(instance.isMastered((LivingEntity)entity)))
                .orElse(Boolean.valueOf(false))).booleanValue();
    }

    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill() && entity instanceof Player player) {
            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            Skill previousSkill = (Skill) Skills.PERSEVERANCE.get();
            Objects.requireNonNull(storage);
            storage.forgetSkill(previousSkill);
        }
    }

    public int modes() {return 2;}

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 2 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 2) ? 1 : (instance.getMode() + 1);
    }

    public Component getModeName(int mode) {
        MutableComponent name;
        switch (mode) {
            case 1:
                name = Component.translatable("trmythos.skill.mode.perseverance.willpower");
                break;
            case 2:
                name = Component.translatable("trmythos.skill.mode.perseverance.persistent");
                break;
            default:
                name = Component.empty();
        }
        return name;
    }

    public double magiculeCost(LivingEntity entity, ManasSkillInstance instance) {
        if (instance.getMode() == 2) {
            return 5000.0D;
        }
        return
                0.0D;
    }

    public void onToggleOn(ManasSkillInstance skillInstance, LivingEntity entity) {
        AttributeInstance instance = entity.getAttribute((Attribute) ManasCoreAttributes.CRIT_CHANCE.get());
        AttributeModifier attributemodifier = new AttributeModifier(PERSISTENT, "Persistent", 100.0D, AttributeModifier.Operation.ADDITION);
        if (instance != null && !instance.hasModifier(attributemodifier))
            instance.addTransientModifier(attributemodifier);
    }

    public void onToggleOff(ManasSkillInstance skillInstance, LivingEntity entity) {
        AttributeInstance instance = entity.getAttribute((Attribute)ManasCoreAttributes.CRIT_CHANCE.get());
        if (instance != null)
            instance.removeModifier(PERSISTENT);
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.isToggled()) {
            entity.addEffect(new MobEffectInstance((MobEffect) MythosMobEffects.EMPOWERMENT_REGENERATION.get(), 1200, 1, false, false, false));
            if (entity instanceof Player) {
                Player player = (Player)entity;
                empowermentHandler(player);
            }
        }
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        // Willpower
        if (instance.getMode() == 1) {
            double EP = TensuraEPCapability.getEP(entity);
            AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
            if (armor != null)
                if (armor.getModifier(WILLPOWER) != null) {
                    armor.removeModifier(WILLPOWER);
                    entity.level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.PLAYERS, 1.0F, 1.0F);
                } else {
                    AttributeModifier armorModifier = new AttributeModifier(WILLPOWER, "Willpower", getArmor(EP), AttributeModifier.Operation.ADDITION);
                    armor.addTransientModifier(armorModifier);
                    entity.level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ARMOR_EQUIP_NETHERITE, SoundSource.PLAYERS, 1.0F, 1.0F);
                    entity.level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0F, 1.0F);
                    TensuraParticleHelper.addServerParticlesAroundSelf((Entity)entity, (ParticleOptions)TensuraParticles.DARK_RED_LIGHTNING_SPARK.get(), 2.0D);
                    TensuraParticleHelper.addServerParticlesAroundSelf((Entity)entity, (ParticleOptions)TensuraParticles.DARK_PURPLE_LIGHTNING_SPARK.get(), 2.0D);
                    addMasteryPoint(instance, entity);
                    instance.setCoolDown(10);
                }
            AttributeInstance damage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
            if (damage != null)
                if (damage.getModifier(WILLPOWER) != null) {
                    damage.removeModifier(WILLPOWER);
                } else {
                    damage.addTransientModifier(new AttributeModifier(WILLPOWER, "Willpower", instance.isMastered(entity) ? (getAttack(EP) * 2.0D) : getAttack(EP), AttributeModifier.Operation.ADDITION));
                }
            AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null)
                if (speed.getModifier(WILLPOWER) != null) {
                    speed.removeModifier(WILLPOWER);
                } else {
                    speed.addTransientModifier(new AttributeModifier(WILLPOWER, "Willpower", getSpeed(EP) / 100.0D, AttributeModifier.Operation.ADDITION));
                }
        }
        // Persistent
        if (instance.getMode() == 2) {
            if (entity.hasEffect((MobEffect)TensuraMobEffects.SEVERANCE_BLADE.get()))
                return;
            entity.swing(InteractionHand.MAIN_HAND, true);
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            severance = instance.isMastered(entity) ? 19 : 4;
            entity.addEffect(new MobEffectInstance((MobEffect)TensuraMobEffects.SEVERANCE_BLADE.get(), 2400, severance, false, false, false));
        }
    }

    public void onTouchEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent event) {
        if (!isInSlot(entity))
            return;
        if (event.getSource().getEntity() == entity && DamageSourceHelper.isPhysicalAttack(event.getSource())) {
            LivingEntity t = event.getEntity();
            int durabilityBreak = (int)Math.max(1.0F, event.getAmount() / 4.0F);
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (!slot.getType().equals(EquipmentSlot.Type.HAND) ||
                        t.getItemBySlot(slot).canPerformAction(ToolActions.SHIELD_BLOCK)) {
                    ItemStack slotStack = t.getItemBySlot(slot);
                    slotStack.hurtAndBreak(durabilityBreak, t, living -> living.broadcastBreakEvent(slot));
                }
            }
        }
    }

    public void onTakenDamage(ManasSkillInstance instance, LivingDamageEvent e) {
        if (!isInSlot(e.getEntity()))
            return;
        Entity entity = e.getSource().getDirectEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity source = (LivingEntity)entity;
            source.getItemInHand(InteractionHand.MAIN_HAND).hurtAndBreak(5, source, attacker -> attacker.swing(InteractionHand.MAIN_HAND));
        }
    }

    public static double getArmor(double EP) {
        return (EP >= 1000000.0D) ? 500.0D : (EP / 5000.0D);
    }

    public static double getAttack(double EP) {
        if (EP <= 50000.0D)
            return EP / 5000.0D;
        if (EP >= 1000000.0D)
            return 100.0D;
        return EP / 5000.0D;
    }

    public static double getSpeed(double EP) {
        return (EP >= 1000000.0D) ? 80.0D : (EP / 25000.0D);
    }

    public void empowermentHandler(Player player) {
        TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
            double maxMP = player.getAttributeValue(TensuraAttributeRegistry.MAX_MAGICULE.get());
            double mpGain = maxMP * 0.01D;
            if (cap.getMagicule() + mpGain < cap.getBaseMagicule()) {
                cap.setMagicule(cap.getMagicule() + mpGain);
            } else {
                cap.setMagicule(cap.getBaseMagicule());
            }

            double maxAP = player.getAttributeValue(TensuraAttributeRegistry.MAX_AURA.get());
            double apGain = maxAP * 0.01D;
            if (cap.getAura() + apGain < cap.getBaseAura()) {
                cap.setAura(cap.getAura() + apGain);
            } else {
                cap.setAura(cap.getBaseAura());
            }
        });
    }

    public void onDeath(ManasSkillInstance instance, LivingDeathEvent event) {
        if (!event.isCanceled()) {
            DamageSource source = event.getSource();
            if (source != DamageSource.OUT_OF_WORLD &&
                    !instance.onCoolDown()) {
                if (source instanceof TensuraDamageSource) {
                    TensuraDamageSource damageSource = (TensuraDamageSource) source;
                    if (damageSource.getIgnoreBarrier() >= 3.0F)
                        return;
                }
                LivingEntity entity = event.getEntity();
                if (!entity.isAlive()) {
                    if (source.getEntity() != null) {
                        if (source.getEntity() == entity)
                            return;
                        if (source.getEntity() == SkillHelper.getSubordinateOwner(entity))
                            return;
                    }
                    addMasteryPoint(instance, entity);
                    entity.setHealth(Math.max(entity.getMaxHealth(), entity.getMaxHealth() * 0.25F));
                    entity.invulnerableTime = Math.max(60, entity.invulnerableTime);
                    Predicate<MobEffect> predicate = effect -> (effect.getCategory() == MobEffectCategory.HARMFUL);
                    SkillHelper.removePredicateEffect(entity, predicate);
                    TensuraEffectsCapability.resetEverything(entity, false, false);
                    TensuraEPCapability.getFrom(entity).ifPresent(cap -> {
                        double SHP = entity.getAttributeValue((Attribute) TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());
                        if (cap.getSpiritualHealth() < SHP * 0.25D)
                            cap.setSpiritualHealth(SHP * 0.25D);
                    });
                    event.setCanceled(true);
                    if (!instance.onCoolDown())
                        instance.setCoolDown(120);
                    entity.level.playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                    TensuraParticleHelper.addServerParticlesAroundSelf((Entity) entity, (ParticleOptions) ParticleTypes.FIREWORK, 1.0D);
                    TensuraParticleHelper.addServerParticlesAroundSelf((Entity) entity, (ParticleOptions) ParticleTypes.FIREWORK, 2.0D);
                    TensuraParticleHelper.addServerParticlesAroundSelf((Entity) entity, (ParticleOptions) ParticleTypes.FIREWORK, 1.0D);
                }
            }
        }
    }
}

