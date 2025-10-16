package com.github.mythos.mythos.ability.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;

import javax.annotation.Nullable;

public class BloodsuckerSkill extends Skill {
    public BloodsuckerSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    // No image yet :p
    // @Nullable
    // @Override
    // public ResourceLocation getSkillIcon() {
    //     return new ResourceLocation("trmythos", "textures/skill/unique/bloodsucker.png");
    // }

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
        return true;
    }

    public double getObtainingEpCost() {
        return 50000.0;
    }

    public double learningCost() {
        return 10000.0;
    }

    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.hasEffect((MobEffect) TensuraMobEffects.HEAT_SENSE.get()) && !entity.hasEffect((MobEffect) TensuraMobEffects.AUDITORY_SENSE.get())) {
            entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.HEAT_SENSE.get(), 200, 0, false, false, false));
            entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.AUDITORY_SENSE.get(), 200, 0, false, false, false));
        }
        entity.addEffect(new MobEffectInstance((MobEffect) MythosMobEffects.RAPID_REGENERATION.get(), 1200, 1, false, false, false));
    }

    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.hasEffect((MobEffect) TensuraMobEffects.HEAT_SENSE.get()) && !entity.hasEffect((MobEffect) TensuraMobEffects.AUDITORY_SENSE.get())) {
            entity.removeEffect((MobEffect) TensuraMobEffects.HEAT_SENSE.get());
            entity.removeEffect((MobEffect) TensuraMobEffects.AUDITORY_SENSE.get());
        }
        MobEffectInstance effectInstance = entity.getEffect((MobEffect) MythosMobEffects.RAPID_REGENERATION.get());
        if (effectInstance != null && effectInstance.getAmplifier() < 1)
            entity.removeEffect((MobEffect) MythosMobEffects.RAPID_REGENERATION.get());
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
                name = Component.translatable("trmythos.skill.mode.bloodsucker.space_ripper_stingy_eyes");
                break;
            case 2:
                name = Component.translatable("trmythos.skill.mode.bloodsucker.vaporization_freezing_technique");
                break;
            case 3:
                name = Component.translatable("trmythos.skill.mode.bloodsucker.blood_drain");
                break;
            case 4:
                name = Component.translatable("trmythos.skill.mode.bloodsucker.flesh_bud");
                break;
            default:
                name = Component.empty();
        }
        return name;
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        switch (instance.getMode()) {
            case 1:
                CompoundTag tag = instance.getOrCreateTag();
                int learnPoint = tag.getInt("SpaceRipperStingyEyes");
                if (learnPoint < 100) {
                    if (SkillHelper.outOfMagicule(entity, instance))
                        return;
                    tag.putInt("SpaceRipperStingyEyes", learnPoint + SkillUtils.getEarningLearnPoint(instance, entity, true));
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        if (tag.getInt("SpaceRipperStingyEyes") >= 100) {
                            player.displayClientMessage((Component) Component.translatable("tensura.skill.acquire_learning", new Object[]{getModeName(1)}).withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)), false);
                        } else {
                            instance.setCoolDown(10);
                            SkillUtils.learningFailPenalty(entity);
                            player.displayClientMessage((Component) Component.translatable("tensura.skill.learn_points_added", new Object[]{getModeName(1)}).withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)), true);
                        }
                        player.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                } else {
                    instance.getOrCreateTag().putInt("BeamID", 0);
                }
                instance.markDirty();
                break;
            case 2:
                LivingEntity target = (LivingEntity) SkillHelper.getTargetingEntity(LivingEntity.class, entity, 30.0D, 0.2D, false, true);
                if (target == null) {
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        player.displayClientMessage((Component) Component.translatable("tensura.targeting.not_targeted")
                                .withStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                    }
                    instance.setCoolDown(instance.isMastered(entity) ? 15 : 30);
                    return;
                }
                if (target.hasEffect((MobEffect) MythosMobEffects.VAPORIZATION_FREEZE.get())) {
                    target.removeEffect((MobEffect) MythosMobEffects.VAPORIZATION_FREEZE.get());
                    entity.swing(InteractionHand.MAIN_HAND, true);
                    entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0F, 1.0F);
                    TensuraParticleHelper.addServerParticlesAroundSelf((Entity) target, (ParticleOptions) ParticleTypes.SNOWFLAKE, 1.0D);
                    TensuraParticleHelper.addServerParticlesAroundSelf((Entity) target, (ParticleOptions) ParticleTypes.SNOWFLAKE, 1.0D);
                } else {
                    if (target instanceof Player) {
                        Player player = (Player) target;
                        if ((player.getAbilities()).instabuild)
                            return;
                    }
                    if (SkillHelper.outOfMagicule(entity, instance))
                        return;
                    instance.addMasteryPoint(entity);
                    instance.setCoolDown(instance.isMastered(entity) ? 15 : 30);
                    int duration = isMastered(instance, entity) ? 12000 : 6000;
                    SkillHelper.checkThenAddEffectSource(target, (Entity) entity, (MobEffect) TensuraMobEffects.INFINITE_IMPRISONMENT.get(), duration, 0, false, false, false, true);
                    DamageSourceHelper.markHurt(target, (Entity) entity);
                    entity.swing(InteractionHand.MAIN_HAND, true);
                    TensuraParticleHelper.addServerParticlesAroundSelf((Entity) target, (ParticleOptions) ParticleTypes.SNOWFLAKE, 1.0D);
                    TensuraParticleHelper.addServerParticlesAroundSelf((Entity) target, (ParticleOptions) ParticleTypes.SNOWFLAKE, 1.0D);
                    entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
                break;
                case 3:
                    if (SkillHelper.outOfMagicule(entity, instance))
                        return;
                    CompoundTag tag = instance.getOrCreateTag();
                    learnPoint = tag.getInt("BloodDrain");
                    if (learnPoint < 100) {
                        tag.putInt("BloodDrain", learnPoint + SkillUtils.getEarningLearnPoint(instance, entity, true));
                        if (entity instanceof Player) {
                            Player player = (Player) entity;
                            if (tag.getInt("BloodDrain") >= 100) {
                                player.displayClientMessage((Component) Component.translatable("tensura.skill.acquire_learning", new Object[]{getModeName(3)}).withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)), false);
                            } else {
                                instance.setCoolDown(10);
                                SkillUtils.learningFailPenalty(entity);
                                player.displayClientMessage((Component) Component.translatable("tensura.skill.learn_points_added", new Object[]{getModeName(3)}).withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)), true);
                            }
                            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                        }
                        instance.markDirty();
                    }
                    if (SkillHelper.outOfMagicule(entity, (ManasSkillInstance) instance))
                        return;
                    this.addMasteryPoint(instance, entity);
                    int embrace = instance.isMastered(entity) ? 1 : 0;
                    SkillHelper.checkThenAddEffectSource(entity, (Entity) entity, (MobEffect) MythosMobEffects.BLOOD_DRAIN.get(), 100, embrace);
                    SkillHelper.checkThenAddEffectSource(entity, (Entity) entity, (MobEffect) MythosMobEffects.BLOOD_DRAIN.get(), 100, embrace);
                    entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                    break;
                    case 4:
                        CharmSkill.charm(instance, entity);
                        break;
        }
    }

    public void onTick(Player player) {
        Level level = player.level;

        if (level.isClientSide) return;
        if (player.isCreative() || player.isSpectator()) return;

        boolean inDaylight = level.isDay() && level.canSeeSky(player.blockPosition());

        int skyLight = level.getBrightness(LightLayer.SKY, player.blockPosition());
        if (inDaylight && skyLight > 8 && !level.isRainingAt(player.blockPosition())) {

            // Set the player on fire for 2 seconds (40 ticks)
            player.setSecondsOnFire(2);

            // Deal 1% of max health as damage
            float maxHealth = player.getMaxHealth();
            float damage = maxHealth * 0.01f;

            player.hurt(level.damageSource().onFire(), damage);
        }
    }

    public boolean isDarkEnough(Player player) {
        if (player == null || player.level == null)
            return false;
        Level level = player.level;
        BlockPos pos = player.blockPosition();
        int lightlevel = level.getMaxLocalRawBrightness(pos);
        return lightlevel <= 7;
    }

    public static void applyBuff(LivingEntity entity, int strength, int resistance, int duration) {
        SkillHelper.checkThenAddEffectSource(entity, entity, TensuraMobEffects.STRENGTHEN.get(), duration, strength, false, false, false, true);
        SkillHelper.checkThenAddEffectSource(entity, entity, MobEffects.DAMAGE_RESISTANCE, duration, resistance, false, false, false, true);
    }

    public static void applyAttributeBuffs(Player player) {
        AttributeInstance attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr != null) {
            attackAttr.removeModifier(ATTACK_DAMAGE_BOOST);
            attackAttr.addTransientModifier(new AttributeModifier(ATTACK_DAMAGE_BOOST, "Curse of Light Attack Boost", 2.0D, AttributeModifier.Operation.ADDITION));
        }
    }

    private static void removeAttributeBuffs(Player player) {
        AttributeInstance attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr != null)
            attackAttr.removeModifier(ATTACK_DAMAGE_BOOST);
    }

    public static void handleCurseOfLight(Player player) {
        if (player.level.isClientSide()) return;

        if (isDarkEnough(player)) {
            applyBuff(player, 5, 1, 100);
            applyAttributeBuffs(player);
        } else {
            removeAttributeBuffs(player);
        }
    }

}