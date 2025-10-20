package com.github.mythos.mythos.ability.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.entity.magic.beam.BeamProjectile;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import org.jetbrains.annotations.NotNull;

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
        entity.addEffect(new MobEffectInstance((MobEffect) MythosMobEffects.RAPID_REGENERATION.get(), 1200, 1, false, false, false));
        if (!entity.hasEffect((MobEffect) TensuraMobEffects.HEAT_SENSE.get()) && !entity.hasEffect((MobEffect) TensuraMobEffects.AUDITORY_SENSE.get()) && !entity.hasEffect((MobEffect) TensuraMobEffects.PRESENCE_SENSE.get())) {
            entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.HEAT_SENSE.get(), 200, 0, false, false, false));
            entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.AUDITORY_SENSE.get(), 200, 0, false, false, false));
            entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.PRESENCE_SENSE.get(), 200, 0, false, false, false));
        }
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

    public boolean onHeld(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, int heldTicks) {
        if (instance.getMode() == 1) {
            if (heldTicks % 60 == 0 && heldTicks > 0)
                addMasteryPoint(instance, entity);
            double cost = magiculeCost(entity, instance);
            BeamProjectile.spawnLastingBeam((EntityType) TensuraEntityTypes.SPATIAL_RAY.get(),
                    instance.isMastered(entity) ? 100.0F : 50.0F, 0.5F, entity, instance, cost, cost, heldTicks);
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BEACON_AMBIENT, SoundSource.PLAYERS, 0.8F, 0.5F);
            if (heldTicks > 100) {
                instance.setCoolDown(instance.isMastered(entity) ? 10 : 20);
                removeHeldAttributeModifiers(instance, entity);
                return false;
            }
            return true;
        }
        return true;
    }

    public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        super.onRelease(instance, entity, heldTicks);
        if (instance.getMode() == 1 && !instance.onCoolDown()) {
            instance.setCoolDown(instance.isMastered(entity) ? 10 : 20);
        }
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        LivingEntity livingEntity2;
        int embrace;
        switch (instance.getMode()) {

            case 1:
                CompoundTag tag = instance.getOrCreateTag();

                instance.getOrCreateTag().putInt("BeamID", 0);

                instance.markDirty();
                break;

            case 2:
                LivingEntity target = (LivingEntity) SkillHelper.getTargetingEntity(LivingEntity.class, entity, 3.0D, 0.2D, false, true);
                if (target == null) {
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        player.displayClientMessage((Component) Component.translatable("tensura.targeting.not_targeted").withStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                    }
                    instance.setCoolDown(instance.isMastered(entity) ? 10 : 20);
                    return;
                }
                if (target.hasEffect((MobEffect) MythosMobEffects.VAPORIZATION_FREEZE.get())) {
                    target.removeEffect((MobEffect) MythosMobEffects.VAPORIZATION_FREEZE.get());
                    entity.swing(InteractionHand.MAIN_HAND, true);
                    entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_HURT_FREEZE, SoundSource.PLAYERS, 1.0F, 1.0F);
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
                    int duration = isMastered(instance, entity) ? 200 : 100;
                    SkillHelper.checkThenAddEffectSource(target, (Entity) entity, (MobEffect) MythosMobEffects.VAPORIZATION_FREEZE.get(), duration, 0, false, false, false, true);
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
                CompoundTag tag2 = instance.getOrCreateTag();
                livingEntity2 = SkillHelper.getTargetingEntity(entity, 3.0D, false);
                if (livingEntity2 == null)
                    return;
                if (livingEntity2 instanceof Player) {
                    Player player = (Player) livingEntity2;
                    if ((player.getAbilities()).instabuild)
                        return;
                }
                if (SkillHelper.outOfMagicule(entity, (ManasSkillInstance) instance))
                    return;
                this.addMasteryPoint(instance, entity);
                embrace = instance.isMastered(entity) ? 1 : 0;
                SkillHelper.checkThenAddEffectSource(livingEntity2, entity, (MobEffect) MythosMobEffects.BLOOD_DRAIN.get(), 100, embrace);
                SkillHelper.checkThenAddEffectSource(entity, entity, (MobEffect) MythosMobEffects.BLOOD_DRAIN.get(), 40, 0);
                entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
                break;

            case 4:
                CharmSkill.charm(instance, entity);
                break;
        }
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.isToggled()) {
            entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.HEAT_SENSE.get(), 200, 0, false, false, false));
            entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.AUDITORY_SENSE.get(), 200, 0, false, false, false));
            entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.PRESENCE_SENSE.get(), 200, 0, false, false, false));
        }
        if (!(entity instanceof Player player))
            return;
        CurseOfLightEffect.tick(instance, player);
    }
}