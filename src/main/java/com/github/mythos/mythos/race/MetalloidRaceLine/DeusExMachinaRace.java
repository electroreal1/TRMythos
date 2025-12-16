package com.github.mythos.mythos.race.MetalloidRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class DeusExMachinaRace extends Race {
    public DeusExMachinaRace() {
        super(Difficulty.INTERMEDIATE);
    }
    @Override
    public double getBaseHealth() {
        return 7777;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 5.143371480005143;
    }

    @Override
    public float getPlayerSize() {
        return 2;
    }

    @Override
    public double getBaseAttackDamage() {
        return 40;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 6f;
    }

    @Override
    public double getKnockbackResistance() {
        return 1f;
    }

    @Override
    public double getJumpHeight() {
        return 4;
    }

    @Override
    public double getMovementSpeed() {
        return 0.3;
    }

    @Override
    public double getSprintSpeed() {
        return 0.4;
    }

    @Override
    public boolean isDivine() {
        return true;
    }

    @Override
    public boolean isSpiritual() {
        return true;
    }

    private double auraMin = 2500000;
    private double auraMax = 2500000;
    private double startingMagiculeMin = 2500000;
    private double startingMagiculeMax = 2500000;

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
        list.add(ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get());
        list.add(ResistanceSkills.HOLY_ATTACK_RESISTANCE.get());
        list.add(ResistanceSkills.DARKNESS_ATTACK_RESISTANCE.get());
        list.add(IntrinsicSkills.DIVINE_KI_RELEASE.get());
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
        return 250000;
    }

    private double ManaEvolutionReward() {
        return 250000;
    }
}
