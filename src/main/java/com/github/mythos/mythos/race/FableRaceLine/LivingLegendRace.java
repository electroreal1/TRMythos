package com.github.mythos.mythos.race.FableRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class LivingLegendRace extends FableweaverRace{
    public LivingLegendRace(Difficulty difficulty) {
        super(difficulty);
    }

    @Override
    public double getBaseHealth() {
        return 8000;
    }

    @Override
    public double getAdditionalSpiritualHealth() {
        return 7000;
    }

    @Override
    public float getPlayerSize() {
        return 1.8f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 45;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 2.2;
    }

    @Override
    public double getKnockbackResistance() {
        return 0;
    }

    @Override
    public double getJumpHeight() {
        return 2;
    }

    @Override
    public double getMovementSpeed() {
        return 0.35;
    }

    private double auraMin = 500000;
    private double auraMax = 500000;
    private double startingMagiculeMin = 800000;
    private double startingMagiculeMax = 800000;

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
        list.add(ResistanceSkills.PHYSICAL_ATTACK_NULLIFICATION.get());
        list.add(ExtraSkills.UNIVERSAL_PERCEPTION.get());
        list.add(ExtraSkills.INFINITE_REGENERATION.get());
        list.add(ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get());
        return list;
    }

    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.AKASHIC_LEGEND));
        return list;
    }

    public @Nullable Race getDefaultEvolution(Player player) {
        return (TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.AKASHIC_LEGEND);
    }

    public @Nullable Race getAwakeningEvolution(Player player) {
        return (TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.AKASHIC_LEGEND);
    }

    @Override
    public @org.jetbrains.annotations.Nullable MutableComponent getName() {
        return Component.literal("Living Legend");
    }


    @Override
    public boolean isSpiritual() {
        return true;
    }

    @Override
    public boolean isDivine() {
        return true;
    }
}
