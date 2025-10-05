package com.github.trmythos.trmythos.race.ValkyrieRaceLine;

import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.CommonSkills;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.trmythos.trmythos.registry.race.TRMythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MaidenRace extends Race {

    public MaidenRace() { super(Difficulty.INTERMEDIATE); }
    @Override
    public double getBaseHealth() {
        return 50;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 4.0;
    }

    @Override
    public float getPlayerSize() {
        return 0.9f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 1.0f;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 3.0f;
    }

    @Override
    public double getKnockbackResistance() {
        return 0.0f;
    }

    @Override
    public double getJumpHeight() {
        return 1;
    }


    @Override
    public double getMovementSpeed() {
        return 0.15;
    }

    @Override
    public double getSprintSpeed() {
        return 0.2f;
    }

    private double auraMin = 700;
    private double auraMax = 1500;
    private double startingMagiculeMin = 500;
    private double startingMagiculeMax = 1300;

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
        list.add(IntrinsicSkills.DIVINE_KI_RELEASE.get());
        list.add(ResistanceSkills.HOLY_ATTACK_RESISTANCE.get());
        list.add(ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.get());
        return list;
    }

    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TRMythosRaces.SOUL_COURIER_RACE));
        return list;
    }

    public @Nullable Race getDefaultEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TRMythosRaces.SOUL_COURIER_RACE));
    }

    public @Nullable Race getAwakeningEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TRMythosRaces.SOUL_COURIER_RACE));
    }

    public @Nullable Race getHarvestFestivalEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TRMythosRaces.SOUL_COURIER_RACE));
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