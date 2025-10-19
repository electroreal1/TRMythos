package com.github.mythos.mythos.race.JormungandrRaceLine;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
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

public class GreaterSerpentRace extends Race {

    public GreaterSerpentRace(Difficulty difficulty) {
        super(difficulty);
    }

    @Override
    public double getBaseHealth() {
        return 150;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 5.0;
    }

    @Override
    public float getPlayerSize() {
        return 1;
    }

    @Override
    public double getBaseAttackDamage() {
        return 4;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 3.25;
    }

    @Override
    public double getKnockbackResistance() {
        return 0.5;
    }

    @Override
    public double getJumpHeight() {
        return 1.25;
    }

    @Override
    public double getMovementSpeed() {
        return 0.15;
    }

    @Override
    public double getSprintSpeed() {
        return 0.175f;
    }

    private double auraMin = 50000.0;
    private double auraMax = 50000.0;
    private double startingMagiculeMin = 50000.0;
    private double startingMagiculeMax = 50000.0;

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

    public GreaterSerpentRace() {
        super(Difficulty.INTERMEDIATE);
    }

    @Override
    public List<TensuraSkill> getIntrinsicSkills(Player player) {
        List<TensuraSkill> list = new ArrayList<>();
        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        list.add(IntrinsicSkills.DRAGON_MODE.get());
        list.add(ExtraSkills.SNAKE_EYE.get());
        list.add(ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.get());
        List<TensuraSkill> serpentSkills = List.of(
                (TensuraSkill) CommonSkills.PARALYSIS.get(),
                CommonSkills.CORROSION.get(),
                CommonSkills.POISON.get()
        );
        // Filter out skills the player already has
        List<TensuraSkill> availableSerpentSkills = serpentSkills.stream()
                .filter(skill -> !storage.getSkill((ManasSkill) skill).isPresent())
                .toList();
        // Pick one random skill from each list (if any are left)
        TensuraSkill randomSerpentSkill = null;
        if (!availableSerpentSkills.isEmpty()) {
            randomSerpentSkill = availableSerpentSkills.get(
                    player.getRandom().nextInt(availableSerpentSkills.size())
            );
        }
        // Add guaranteed / chosen skills to the returned list
        if (randomSerpentSkill != null) list.add(randomSerpentSkill);
        // You can add other intrinsic skills here if desired
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
        return 30000;
    }

    private double ManaEvolutionReward() {
        return 70000;
    }

    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.SON_OF_LOKI_RACE));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TensuraRaces.DRAGONEWT.getId()));
        return list;
    }
    public @Nullable Race getDefaultEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.SON_OF_LOKI_RACE));
    }

    public @Nullable Race getAwakeningEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.SON_OF_LOKI_RACE));
    }

    public @Nullable Race getHarvestFestivalEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.SON_OF_LOKI_RACE));
    }

    public boolean isMajin() {
        return true;
    }
    public boolean isSpiritual() {
        return false;
    }

    public boolean isDivine() {
        return false;
    }

}
