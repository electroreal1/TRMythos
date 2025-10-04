package com.github.trmythos.trmythos.race.JormungandrRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.trmythos.trmythos.registry.race.AllRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MidgardianSpiritRace extends Race {
    public MidgardianSpiritRace() {
        super(Difficulty.INTERMEDIATE);
    }

    @Override
    public double getBaseHealth() {
        return 5000;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 5.0;
    }

    @Override
    public float getPlayerSize() {
        return 2;
    }

    @Override
    public double getBaseAttackDamage() {
        return 10;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 4;
    }

    @Override
    public double getKnockbackResistance() {
        return 1.5f;
    }

    @Override
    public double getJumpHeight() {
        return 1.5;
    }

    @Override
    public double getMovementSpeed() {
        return 0.2f;
    }

    @Override
    public double getSprintSpeed() {
        return 2.5f;
    }

    private double auraMin = 500000.0;
    private double auraMax = 500000.0;
    private double startingMagiculeMin = 500000.0;
    private double startingMagiculeMax = 500000.0;

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
    public List<TensuraSkill> getIntrinsicSkills(Player player) {
        List<TensuraSkill> list = new ArrayList<>();
        list.add(ExtraSkills.INFINITE_REGENERATION.get());
        list.add(ExtraSkills.MAGIC_JAMMING.get());
        list.add(ResistanceSkills.EARTH_ATTACK_RESISTANCE.get());
        list.add(ResistanceSkills.WATER_ATTACK_RESISTANCE.get());
        list.add(ResistanceSkills.ABNORMAL_CONDITION_RESISTANCE.get());
        return list;
    }

    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(AllRaces.JORMUNGANDR_RACE));
        return list;
    }
    public @Nullable Race getDefaultEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(AllRaces.JORMUNGANDR_RACE));
    }

    public @Nullable Race getAwakeningEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(AllRaces.JORMUNGANDR_RACE));
    }

    public @Nullable Race getHarvestFestivalEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(AllRaces.JORMUNGANDR_RACE));
    }

    public boolean isMajin() {
        return true;
    }
    public boolean isSpiritual() {
        return false;
    }

    public boolean isDivine() {
        return true;
    }

}

