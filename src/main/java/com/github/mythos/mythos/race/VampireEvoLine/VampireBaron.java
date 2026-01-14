package com.github.mythos.mythos.race.VampireEvoLine;

import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.effect.template.Transformation;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.race.RaceHelper;
import com.github.manasmods.tensura.race.vampire.VampireRace;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.CommonSkills;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.manasmods.tensura.world.TensuraGameRules;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VampireBaron extends VampireRace implements Transformation {


    @Override
    public double getBaseHealth() {
        return 800;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 1.5;
    }

    @Override
    public float getPlayerSize() {
        return 1.8f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 3;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 4;
    }

    @Override
    public double getKnockbackResistance() {
        return 0;
    }

    @Override
    public double getJumpHeight() {
        return 1.2;
    }

    @Override
    public double getMovementSpeed() {
        return 0.22;
    }

    private double auraMin = 100000;
    private double auraMax = 100000;
    private double startingMagiculeMin = 100000;
    private double startingMagiculeMax = 100000;

    @Override
    public Pair<Double, Double> getBaseAuraRange() {
        return Pair.of(auraMin, auraMax);
    }

    @Override
    public Pair<Double, Double> getBaseMagiculeRange() {
        return Pair.of(startingMagiculeMin, startingMagiculeMax);
    }

    @Override
    public double getAuraEvolutionReward() {
        return AuraEvolutionReward();
    }
    @Override
    public double getManaEvolutionReward() {
        return ManaEvolutionReward();
    }

    private double AuraEvolutionReward() {
        return 3000;
    }

    private double ManaEvolutionReward() {
        return 7000;
    }

    @Override
    public List<TensuraSkill> getIntrinsicSkills(Player player) {
        List<TensuraSkill> list = new ArrayList<>();
        list.add(IntrinsicSkills.DRAIN.get());
        list.add(IntrinsicSkills.BLOOD_MIST.get());
        list.add(ExtraSkills.SHADOW_MOTION.get());
        list.add(CommonSkills.COERCION.get());
        list.add(CommonSkills.SELF_REGENERATION.get());
        return list;
    }

    public @Nullable Race getDefaultEvolution(Player player) {
        return (Race) TensuraRaces.RACE_REGISTRY.get().getValue(MythosRaces.VAMPIRE_VISCOUNT);
    }

    public @Nullable Race getHarvestFestivalEvolution(Player player) {
        return (Race) TensuraRaces.RACE_REGISTRY.get().getValue(MythosRaces.VAMPIRE_VISCOUNT);
    }

    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.VAMPIRE_VISCOUNT));
        return list;
    }

    public double getEvolutionPercentage(Player player) {
        double minimalEP = this.getMinBaseAura() + this.getMinBaseMagicule();
        // Base EP contribution, capped at 50
        double percentage = TensuraPlayerCapability.getBaseEP(player) * 100 / minimalEP;
        percentage = Math.min(percentage, 100.0);


        return Math.min(percentage, 100.0);
    }

    public List<Component> getRequirementsForRendering(Player player) {
        List<Component> list = new ArrayList();
        list.add(Component.translatable("tensura.evolution_menu.ep_requirement"));
        return list;
    }

    public void raceAbility(Player player) {
        if (player.hasEffect((MobEffect)TensuraMobEffects.BATS_MODE.get())) {
            player.removeEffect((MobEffect)TensuraMobEffects.BATS_MODE.get());
            if (player.isSpectator() || player.isCreative()) {
                return;
            }

            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        } else {
            LivingEntity target = SkillHelper.getTargetingEntity(player, 5.0, false);
            if (target != null && RaceHelper.hasNoBlood(target) && (player.getHealth() < player.getMaxHealth() || player.getFoodData().needsFood() || player.isCreative())) {
                if (target.hurt(TensuraDamageSources.bloodDrain(target), 2.0F)) {
                    player.heal(2.0F);
                    player.getFoodData().eat(2, 0.0F);
                    player.getLevel().playSound((Player)null, target.getX(), target.getY(), target.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
                }

                return;
            }

            if (this.failedToActivate(player, (MobEffect)TensuraMobEffects.BATS_MODE.get())) {
                return;
            }

            player.addEffect(new MobEffectInstance((MobEffect)TensuraMobEffects.BATS_MODE.get(), 1728000, 0, false, false, false));
            if (player.isSpectator() || player.isCreative()) {
                return;
            }

            player.getAbilities().mayfly = true;
            player.getAbilities().flying = true;
            player.onUpdateAbilities();
        }

    }

    public void raceTick(Player player) {
        if (isUnderSun(player)) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 3, false, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 3, false, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 3, false, false, false));
            if (shouldBurn(player)) {
                player.setSecondsOnFire(2);
            }
        } else if (player.level.isNight() && player.level.getMoonPhase() == 4) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 3, false, false, false));
            player.addEffect(new MobEffectInstance((MobEffect)TensuraMobEffects.FRAGILITY.get(), 40, 3, false, false, false));
        }

    }

    public static boolean isUnderSun(LivingEntity entity) {
        if (!entity.isAlive()) {
            return false;
        } else {
            if (entity instanceof Player) {
                Player player = (Player)entity;
                if (player.isCreative()) {
                    return false;
                }
            }

            if (!entity.isSpectator()) {
                if (!entity.level.isDay()) {
                    return false;
                } else if (SkillUtils.noInteractiveMode(entity)) {
                    return false;
                } else {
                    float f = entity.getLightLevelDependentMagicValue();
                    boolean inWater = entity.isInFluidType((fluidType, height) -> {
                        return height > (double)entity.getBbHeight();
                    }) && (entity.isInWaterOrBubble() || entity.isInPowderSnow || entity.wasInPowderSnow);
                    boolean flag = inWater || entity.isInWaterOrRain();
                    return f > 0.5F && !flag && entity.level.canSeeSky(new BlockPos(entity.getEyePosition()));
                }
            } else {
                return false;
            }
        }
    }

    public static boolean shouldBurn(Player player) {
        if (player.isInWaterOrBubble()) {
            return false;
        } else {
            return player.level.getGameRules().getBoolean(TensuraGameRules.HARDCORE_RACE) || player.getItemBySlot(EquipmentSlot.HEAD).isEmpty();
        }
    }

    @Override
    public boolean isDivine() {
        return false;
    }

    @Override
    public boolean isMajin() {
        return true;
    }

    @Override
    public boolean isSpiritual() {
        return false;
    }
}
