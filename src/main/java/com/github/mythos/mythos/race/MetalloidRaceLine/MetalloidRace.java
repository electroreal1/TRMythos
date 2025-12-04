package com.github.mythos.mythos.race.MetalloidRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class MetalloidRace extends Race {
    public MetalloidRace() {super(Difficulty.INTERMEDIATE);}


    @Override
    public double getBaseHealth() {
        return 55;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 4.545454545454545f;
    }

    @Override
    public float getPlayerSize() {
        return 2;
    }

    @Override
    public double getBaseAttackDamage() {
        return 3;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 2;
    }

    @Override
    public double getKnockbackResistance() {
        return 0.1f;
    }

    @Override
    public double getJumpHeight() {
        return 0;
    }

    @Override
    public double getMovementSpeed() {
        return 0.21;
    }

    @Override
    public double getSprintSpeed() {
        return 0.23;
    }

    private double auraMin = 8000;
    private double auraMax = 8000;
    private double startingMagiculeMin = 45000;
    private double startingMagiculeMax = 45000;

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
        list.add(ExtraSkills.CHANT_ANNULMENT.get());
        list.add(ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.get());
        return list;
    }

    @Override
    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race) TensuraRaces.RACE_REGISTRY.get().getValue(MythosRaces.METALLOID_PROTO));
        return list;
    }

}
