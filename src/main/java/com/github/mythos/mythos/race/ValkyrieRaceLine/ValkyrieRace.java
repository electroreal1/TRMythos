package com.github.mythos.mythos.race.ValkyrieRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.config.TensuraConfig;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.TensuraStats;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ValkyrieRace extends Race {

    public ValkyrieRace() { super(Difficulty.INTERMEDIATE); }
    @Override
    public double getBaseHealth() {
        return 700;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 4.0;
    }

    @Override
    public float getPlayerSize() {
        return 1.1f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 2.5f;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 3.0f;
    }

    @Override
    public double getKnockbackResistance() {
        return 0.2f;
    }

    @Override
    public double getJumpHeight() {
        return 2;
    }


    @Override
    public double getMovementSpeed() {
        return 0.22;
    }

    @Override
    public double getSprintSpeed() {
        return 0.25f;
    }

    private double auraMin = 300000;
    private double auraMax = 300000;
    private double startingMagiculeMin = 200000;
    private double startingMagiculeMax = 200000;

    @Override
    public Pair<Double, Double> getBaseAuraRange() {
        return Pair.of(auraMin, auraMax);
    }
    public double getEvolutionPercentage(Player player) {
        double minimalEP = this.getMinBaseAura() + this.getMinBaseMagicule();

        // Base EP contribution, capped at 50
        double percentage = TensuraPlayerCapability.getBaseEP(player) * 50 / minimalEP;
        percentage = Math.min(percentage, 50.0);
        double boss = (double)Math.min(TensuraStats.getBossKilled(player) * 50 / (Integer)TensuraConfig.INSTANCE.racesConfig.bossForSaint.get(), 50);
        return percentage + boss;
    }
    public List<Component> getRequirementsForRendering(Player player) {
        List<Component> list = new ArrayList();
        list.add(Component.translatable("tensura.evolution_menu.ep_requirement"));
        list.add(Component.translatable("trmythos.evolution_menu.boss_kill_requirement"));
        return list;
    }

    @Override
    public Pair<Double, Double> getBaseMagiculeRange() {
        return Pair.of(startingMagiculeMin, startingMagiculeMax);
    }

    @Override
    public List<TensuraSkill> getIntrinsicSkills(Player player) {
        List<TensuraSkill> list = new ArrayList<>();
        list.add(ExtraSkills.MAGIC_LIGHT_TRANSFORM.get());
        list.add(ResistanceSkills.LIGHT_ATTACK_RESISTANCE.get());
        list.add(ExtraSkills.HEAVENLY_EYE.get());
        list.add(ExtraSkills.ULTRA_INSTINCT.get());
        return list;
    }

    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.ENVOY_OF_VALHALLA));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.NAMELESS_DIVINITY_V));
        return list;
    }

    public @Nullable Race getDefaultEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.ENVOY_OF_VALHALLA));
    }

    public @Nullable Race getAwakeningEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.ENVOY_OF_VALHALLA));
    }

    public @Nullable Race getHarvestFestivalEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.ENVOY_OF_VALHALLA));
    }

    public void raceAbility(Player entity) {
        if (!entity.isSpectator() && !entity.isCreative()) {
            Level level = entity.level;
            if ((entity.getAbilities()).mayfly) {
                (entity.getAbilities()).mayfly = false;
                (entity.getAbilities()).flying = false;
            } else {
                (entity.getAbilities()).mayfly = true;
                (entity.getAbilities()).flying = true;
            }
            entity.getAbilities().setFlyingSpeed(0.1F);
            entity.onUpdateAbilities();
            level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, 0.5F, 1.0F);
        }
    }

    public boolean isMajin() {
        return false;
    }
    public boolean isSpiritual() {
        return true;
    }
    public boolean isDivine() {
        return true;
    }

}