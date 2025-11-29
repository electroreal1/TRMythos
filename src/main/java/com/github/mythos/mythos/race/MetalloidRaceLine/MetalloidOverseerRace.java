package com.github.mythos.mythos.race.MetalloidRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class MetalloidOverseerRace extends Race {
    public MetalloidOverseerRace() {
        super(Difficulty.INTERMEDIATE);
    }

    @Override
    public double getBaseHealth() {
        return 600;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 6.666666666666667f;
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
        return 3.8;
    }

    @Override
    public double getKnockbackResistance() {
        return 0.3;
    }

    @Override
    public double getJumpHeight() {
        return 0;
    }

    @Override
    public double getMovementSpeed() {
        return 0.24;
    }

    @Override
    public double getSprintSpeed() {
        return 0.27;
    }

    private double auraMin = 500000;
    private double auraMax = 500000;
    private double startingMagiculeMin = 500000;
    private double startingMagiculeMax = 500000;

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
        return 80000;
    }

    private double ManaEvolutionReward() {
        return 80000;
    }

    @Override
    public List<TensuraSkill> getIntrinsicSkills(Player player) {
        List<TensuraSkill> list = new ArrayList<>();
        list.add(ExtraSkills.MORTAL_FEAR.get());
        list.add(ExtraSkills.MAJESTY.get());
        list.add(ExtraSkills.UNIVERSAL_PERCEPTION.get());
        return list;
    }

    @Override
    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race)(IForgeRegistry) TensuraRaces.RACE_REGISTRY.get().getValue(MythosRaces.METALLOID_SUPREME));
        return list;
    }
}
