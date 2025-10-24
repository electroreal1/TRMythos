package com.github.mythos.mythos.race.GodlingLines;

import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.CommonSkills;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BuddingDemigodRace extends Race {

    public BuddingDemigodRace() {
        super(Difficulty.INTERMEDIATE);
    }
    @Override
    public double getBaseHealth() {
        return 300;
    }
    @Override
    public double getSpiritualHealthMultiplier() {
        return 3.333333333333333f;
    }

    @Override
    public float getPlayerSize() {
        return 1f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 5f;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 3.5f;
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
        return 0.21;
    }
    @Override
    public double getSprintSpeed() {
        return 0.24f;
    }

    private double auraMin = 250000;
    private double auraMax = 250000;
    private double startingMagiculeMin = 250000;
    private double startingMagiculeMax = 250000;

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
        list.add(io.github.Memoires.trmysticism.registry.skill.ExtraSkills.MANA_MANIPULATION.get());
        list.add(ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get());
        list.add(ExtraSkills.ULTRASPEED_REGENERATION.get());
        list.add(ExtraSkills.MULTILAYER_BARRIER.get());
        return list;
    }
    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        list.add((Race)((IForgeRegistry)TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.SEMIDEUS));
        list.add((Race)((IForgeRegistry)TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.UUL_BORN));
        list.add((Race)((IForgeRegistry)TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.DIVINE_HOST));
        return list;
    }

    public @Nullable Race getDefaultEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.SEMIDEUS));
    }

    public @Nullable Race getAwakeningEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.UUL_BORN));
    }

    public @Nullable Race getHarvestFestivalEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.SEMIDEUS));
    }
    public boolean isDivine() {
        return true;
    }

    public boolean isMajin() {
        return false;
    }

    public boolean isSpiritual() {
        return false;
    }

}

