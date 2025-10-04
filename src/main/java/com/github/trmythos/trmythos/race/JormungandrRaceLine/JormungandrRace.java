package com.github.trmythos.trmythos.race.JormungandrRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class JormungandrRace extends Race {
    public JormungandrRace() {
        super(Difficulty.INTERMEDIATE);
    }

    @Override
    public double getBaseHealth() {
        return 10000;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 5.0;
    }

    @Override
    public float getPlayerSize() {
        return 4;
    }

    @Override
    public double getBaseAttackDamage() {
        return 20;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 5;
    }

    @Override
    public double getKnockbackResistance() {
        return 2;
    }

    @Override
    public double getJumpHeight() {
        return 2;
    }

    @Override
    public double getMovementSpeed() {
        return 0.25f;
    }

    @Override
    public double getSprintSpeed() {
        return 3f;
    }

    private double auraMin = 2500000.0;
    private double auraMax = 2500000.0;
    private double startingMagiculeMin = 2500000.0;
    private double startingMagiculeMax = 2500000.0;

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
        list.add(ExtraSkills.MULTILAYER_BARRIER.get());
        list.add(ExtraSkills.ULTRA_INSTINCT.get());
        list.add(ResistanceSkills.MAGIC_NULLIFICATION.get());
        list.add(ExtraSkills.MAJESTY.get());
        return list;
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

