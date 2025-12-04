package com.github.mythos.mythos.race.MetalloidRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import io.github.Memoires.trmysticism.registry.race.MysticismRaces;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class MetalloidSupremeRace extends Race {
    public MetalloidSupremeRace() {
        super(Difficulty.INTERMEDIATE);
    }
    @Override
    public double getBaseHealth() {
        return 1500;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 8;
    }

    @Override
    public float getPlayerSize() {
        return 2;
    }

    @Override
    public double getBaseAttackDamage() {
        return 18;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 4.5f;
    }

    @Override
    public double getKnockbackResistance() {
        return 0.4f;
    }

    @Override
    public double getJumpHeight() {
        return 0;
    }

    @Override
    public double getMovementSpeed() {
        return 0.26;
    }

    @Override
    public double getSprintSpeed() {
        return 0.3;
    }

    @Override
    public boolean isDivine() {
        return true;
    }

    private double auraMin = 1250000;
    private double auraMax = 1250000;
    private double startingMagiculeMin = 1250000;
    private double startingMagiculeMax = 1250000;

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
        list.add(ExtraSkills.INFINITE_REGENERATION.get());
        list.add(ResistanceSkills.ABNORMAL_CONDITION_RESISTANCE.get());
        list.add(ResistanceSkills.PARALYSIS_RESISTANCE.get());
        list.add(ResistanceSkills.POISON_RESISTANCE.get());
        list.add(ResistanceSkills.THERMAL_FLUCTUATION_RESISTANCE.get());
        list.add(ResistanceSkills.HEAT_RESISTANCE.get());
        list.add(ResistanceSkills.COLD_RESISTANCE.get());
        list.add(ResistanceSkills.CORROSION_RESISTANCE.get());
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
        return 150000;
    }

    private double ManaEvolutionReward() {
        return 150000;
    }

    @Override
    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race) TensuraRaces.RACE_REGISTRY.get().getValue(MythosRaces.DEUS_EX_MACHINA));
        if (TensuraEPCapability.isChaos(player)) {
            list.add((Race) TensuraRaces.RACE_REGISTRY.get().getValue(MysticismRaces.CHAOS_METALLOID));
        }
        return list;
    }
}
