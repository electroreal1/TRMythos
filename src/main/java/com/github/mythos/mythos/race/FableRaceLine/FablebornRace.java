package com.github.mythos.mythos.race.FableRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.CommonSkills;
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

public class FablebornRace extends Race {
    public FablebornRace(Difficulty difficulty) {
        super(difficulty);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Fableborn");
    }

    @Override
    public double getBaseHealth() {
        return 80;
    }

    @Override
    public double getAdditionalSpiritualHealth() {
        return -30;
    }

    @Override
    public float getPlayerSize() {
        return 1.5f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 2;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 1.2;
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
        return 0.2;
    }

    private double auraMin = 300;
    private double auraMax = 800;
    private double startingMagiculeMin = 500;
    private double startingMagiculeMax = 1200;

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
        list.add(CommonSkills.SELF_REGENERATION.get());
        list.add(ExtraSkills.MAGIC_SENSE.get());
        list.add(ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.get());
        return list;
    }

    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.FABLEWEAVER));
        return list;
    }

    public @Nullable Race getDefaultEvolution(Player player) {
        return (TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.FABLEWEAVER);
    }

    public @Nullable Race getAwakeningEvolution(Player player) {
        return (TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.FABLEWEAVER);
    }

    public @Nullable Race getHarvestFestivalEvolution(Player player) {
        return (TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.FABLEWEAVER);
    }

    @Override
    public boolean isSpiritual() {
        return true;
    }
}
