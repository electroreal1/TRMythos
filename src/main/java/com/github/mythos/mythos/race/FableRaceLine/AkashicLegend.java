package com.github.mythos.mythos.race.FableRaceLine;

import com.github.lucifel.virtuoso.registry.skill.VExtraSkills;
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

public class AkashicLegend extends LivingLegendRace{
    public AkashicLegend(Difficulty difficulty) {
        super(difficulty);
    }

    @Override
    public double getBaseHealth() {
        return 10000;
    }

    @Override
    public double getAdditionalSpiritualHealth() {
        return getBaseHealth() * 10;
    }

    @Override
    public float getPlayerSize() {
        return 1.9f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 150;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 3;
    }

    @Override
    public double getKnockbackResistance() {
        return 10;
    }

    @Override
    public double getJumpHeight() {
        return 2;
    }

    @Override
    public double getMovementSpeed() {
        return 0.45;
    }

    private double auraMin = 10000000;
    private double auraMax = 10000000;
    private double startingMagiculeMin = 10000000;
    private double startingMagiculeMax = 10000000;

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
        list.add(ExtraSkills.HEAVENLY_EYE.get());
        list.add(VExtraSkills.CONCENTRATOR.get());
        list.add(VExtraSkills.MYSTIC_AURA.get());
        return list;
    }

    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.GENESIS_MYTH));
        return list;
    }

    public @Nullable Race getDefaultEvolution(Player player) {
        return ((TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.GENESIS_MYTH));
    }

    public @Nullable Race getAwakeningEvolution(Player player) {
        return ((TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.GENESIS_MYTH));
    }

    @Override
    public @org.jetbrains.annotations.Nullable MutableComponent getName() {
        return Component.literal("Akashic Legend");
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
