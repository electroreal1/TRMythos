package com.github.trmythos.trmythos.race.CanineRaceLines.FenrirRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.trmythos.trmythos.registry.race.TRMythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class FenrisWolfRace extends Race {
    public FenrisWolfRace() {
        super(Difficulty.INTERMEDIATE);
    }
    @Override
    public double getBaseHealth() {
        return 6000;
    }
    public double getSpiritualHealthMultiplier() {
        return 0.5f;
    }

    @Override
    public float getPlayerSize() {
        return 2f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 15;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 4;
    }

    @Override
    public double getKnockbackResistance() {
        return 0.2f;
    }

    @Override
    public double getJumpHeight() {
        return 1;
    }

    @Override
    public double getMovementSpeed() {
        return 0.3;
    }
    @Override
    public double getSprintSpeed() {
        return 0.35f;
    }

    private double auraMin = 1000000;
    private double auraMax = 1000000;
    private double startingMagiculeMin = 1000000;
    private double startingMagiculeMax = 1000000;

    @Override
    public Pair<Double, Double> getBaseAuraRange() {
        // The range of values that the Aura Range could be. So between 800 and 1211
        return Pair.of(auraMin, auraMax);
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
        return 800000;
    }

    private double ManaEvolutionReward() {
        return 1200000;
    }

    @Override
    public Pair<Double, Double> getBaseMagiculeRange() {
        // The range of values that the Max Magicules could be. So between 80 and 120
        return Pair.of(startingMagiculeMin, startingMagiculeMax);
    }
    @Override
    public List<Race> getPreviousEvolutions(Player player) {
        List<Race> list = new ArrayList();
        list.add((Race)((IForgeRegistry) TRMythosRaces.DREAD_BEAST_RACE));
        return list;
    }
    @Override
    // public double getEvolutionPercentage(Player player) {
    //  double minimalEP = this.getMinBaseAura() + this.getMaxBaseMagicule();
    //   return TensuraPlayerCapability.getBaseEP(player) * 50;
    //  }

    public List<TensuraSkill> getIntrinsicSkills (Player player){
        List<TensuraSkill> list = new ArrayList<>();
        list.add(ExtraSkills.INFINITE_REGENERATION.get());
        list.add(ResistanceSkills.HOLY_ATTACK_NULLIFICATION.get());
        list.add(ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get());
        list.add(UniqueSkills.ROYAL_BEAST.get());
        list.add(IntrinsicSkills.DIVINE_KI_RELEASE.get());
        return list;
    }

    public boolean isMajin() {
        return true;
    }
    public boolean isDivine() {
        return true;
    }
}
