package com.github.trmythos.trmythos.race.JormungandrRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.CommonSkills;
import com.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import com.github.trmythos.trmythos.registry.race.TRMythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SerpentRace extends Race {

    public SerpentRace(Difficulty difficulty) {
        super(difficulty);
    }

    @Override
    public double getBaseHealth() {
        return 30;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 5.0;
    }

    @Override
    public float getPlayerSize() {
        return 0.9f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 2;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 3.25;
    }

    @Override
    public double getKnockbackResistance() {
        return 0;
    }

    @Override
    public double getJumpHeight() {
        return 1;
    }

    @Override
    public double getMovementSpeed() {
        return 0.2;
    }

    private double auraMin = 5000.0;
    private double auraMax = 5000.0;
    private double startingMagiculeMin = 5000.0;
    private double startingMagiculeMax = 5000.0;

    @Override
    public Pair<Double, Double> getBaseAuraRange() {
        // The range of values that the Aura Range could be. So between 800 and 1211
        return Pair.of(auraMin, auraMax);
    }

    @Override
    public Pair<Double, Double> getBaseMagiculeRange() {
        // The range of values that the Max Magicules could be. So between 80 and 120
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
        return 7000;
    }

    private double ManaEvolutionReward() {
        return 3000;
    }

    public SerpentRace() {
        super(Difficulty.INTERMEDIATE);
    }

    @Override
    public List<TensuraSkill> getIntrinsicSkills(Player player) {
        List<TensuraSkill> list = new ArrayList<>();
        list.add(CommonSkills.CORROSION.get());
        list.add(CommonSkills.SELF_REGENERATION.get());
        list.add(IntrinsicSkills.DRAGON_EAR.get());
        list.add(IntrinsicSkills.DRAGON_EYE.get());
        list.add(IntrinsicSkills.DRAGON_SKIN.get());
        return list;
    }

    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TRMythosRaces.GREATER_SERPENT_RACE));
        return list;
    }
    public @Nullable Race getDefaultEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TRMythosRaces.GREATER_SERPENT_RACE));
    }

    public @Nullable Race getAwakeningEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TRMythosRaces.GREATER_SERPENT_RACE));
    }

    public @Nullable Race getHarvestFestivalEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TRMythosRaces.GREATER_SERPENT_RACE));
    }

    public boolean isMajin() {
        return true;
    }
    public boolean isSpiritual() {
        return false;
    }

    public boolean isDivine() {
        return false;
    }

}
