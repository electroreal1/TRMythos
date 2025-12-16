package com.github.mythos.mythos.race.MetalloidRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class MetalloidProtoRace extends Race {
    public MetalloidProtoRace() {
        super(Difficulty.INTERMEDIATE);
    }
    @Override
    public double getBaseHealth() {
        return 250;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 4;
    }

    @Override
    public float getPlayerSize() {
        return 2;
    }

    @Override
    public double getBaseAttackDamage() {
        return 5;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 3.2f;
    }

    @Override
    public double getKnockbackResistance() {
        return 0.2f;
    }

    @Override
    public double getJumpHeight() {
        return 1.5;
    }

    @Override
    public double getMovementSpeed() {
        return 0.22;
    }

    @Override
    public double getSprintSpeed() {
        return 0.25;
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
    public List<TensuraSkill> getIntrinsicSkills(Player player) {
        List<TensuraSkill> list = new ArrayList<>();
        list.add(ExtraSkills.ULTRASPEED_REGENERATION.get());
        list.add(ExtraSkills.MULTILAYER_BARRIER.get());
        list.add(ExtraSkills.ULTRA_INSTINCT.get());
        return list;
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
        return 20000;
    }

    private double ManaEvolutionReward() {
        return 20000;
    }

    @Override
    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race) TensuraRaces.RACE_REGISTRY.get().getValue(MythosRaces.METALLOID_EXPERIMENTER));
        return list;
    }
}
