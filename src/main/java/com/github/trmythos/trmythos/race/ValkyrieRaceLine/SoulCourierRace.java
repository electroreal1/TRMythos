package com.github.trmythos.trmythos.race.ValkyrieRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class SoulCourierRace extends Race {
    public SoulCourierRace() {
        super(Difficulty.INTERMEDIATE);
    }

    @Override
    public double getBaseHealth() {
        return 300;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 4.0;
    }

    @Override
    public float getPlayerSize() {
        return 1;
    }

    @Override
    public double getBaseAttackDamage() {
        return 1.5;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 3;
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
        return 0.2f;
    }

    @Override
    public double getSprintSpeed() {
        return 0.22f;
    }

    private double auraMin = 60000.0;
    private double auraMax = 60000.0;
    private double startingMagiculeMin = 40000.0;
    private double startingMagiculeMax = 40000.0;

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
        list.add(ResistanceSkills.DARKNESS_ATTACK_RESISTANCE.get());
        list.add(ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get());
        list.add(ExtraSkills.ULTRASPEED_REGENERATION.get());
        return list;
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