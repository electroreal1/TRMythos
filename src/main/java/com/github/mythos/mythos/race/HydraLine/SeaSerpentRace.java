package com.github.mythos.mythos.race.HydraLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.CommonSkills;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SeaSerpentRace extends Race {
    public SeaSerpentRace() {super(Difficulty.INTERMEDIATE);}

    @Override
    public double getBaseHealth() {
        return 1000;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 4.0;
    }

    @Override
    public float getPlayerSize() {
        return 2.5f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 5;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 4.5;
    }

    @Override
    public double getKnockbackResistance() {
        return 1.5;
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
    public double getSprintSpeed(){return 0.55;}

    public Pair<Double, Double> getBaseAuraRange() {
        return Pair.of(Double.valueOf(100000.0D), Double.valueOf(200000.0D));
    }

    public Pair<Double, Double> getBaseMagiculeRange() {
        return Pair.of(Double.valueOf(300000.0D), Double.valueOf(400000.0D));
    }

    @Override
    public List<TensuraSkill> getIntrinsicSkills(Player player) {
        List<TensuraSkill> list = new ArrayList<>();
        list.add(IntrinsicSkills.DRAGON_SKIN.get());
        list.add(CommonSkills.SELF_REGENERATION.get());
        list.add(CommonSkills.HYDRAULIC_PROPULSION.get());
        list.add(ResistanceSkills.DARKNESS_ATTACK_RESISTANCE.get());
        list.add(ResistanceSkills.WATER_ATTACK_RESISTANCE.get());
        list.add(ExtraSkills.MAGIC_SENSE.get());
        list.add(ExtraSkills.SNAKE_EYE.get());
        list.add(ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.get());
        list.add(ResistanceSkills.ABNORMAL_CONDITION_RESISTANCE.get());
        return list;
    }

    @Override
    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.DARK_SEA_STALKER));
        return list;
    }

    @Override
    @Nullable
    public Race getDefaultEvolution(Player player) {
        return (Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.DARK_SEA_STALKER);
    }

    @Override
    @Nullable
    public Race getAwakeningEvolution(Player player) {
        return (Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.DARK_SEA_STALKER);
    }

    @Override
    @Nullable
    public Race getHarvestFestivalEvolution(Player player) {
        return (Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.DARK_SEA_STALKER);
    }

    public List<Race> getPreviousEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race)((IForgeRegistry)TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.SEA_BEAST));
        list.add((Race)TensuraRaces.ENLIGHTENED_MERFOLK.get());
        return list;
    }

    @Override
    public boolean isMajin() {
        return true;
    }

    @Override
    public boolean isSpiritual() {
        return false;
    }

    @Override
    public boolean isDivine() {
        return false;
    }
}
