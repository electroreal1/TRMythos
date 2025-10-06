package com.github.mythos.mythos.race.JormungandrRaceLine;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.CommonSkills;
import com.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SerpentRace extends Race {

    private double auraMin = 5000.0;
    private double auraMax = 5000.0;
    private double startingMagiculeMin = 5000.0;
    private double startingMagiculeMax = 5000.0;

    public SerpentRace(Difficulty difficulty) {
        super(difficulty);
    }

    public SerpentRace() {
        super(Difficulty.INTERMEDIATE);
    }

    @Override
    public double getBaseHealth() {
        return 30;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 5.0;
    }

    @Override
    public float getPlayerSize() {
        return 0.9f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 2;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 3.25;
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

    @Override
    public Pair<Double, Double> getBaseAuraRange() {
        return Pair.of(auraMin, auraMax);
    }

    @Override
    public Pair<Double, Double> getBaseMagiculeRange() {
        return Pair.of(startingMagiculeMin, startingMagiculeMax);
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
        return 7000;
    }

    private double ManaEvolutionReward() {
        return 3000;
    }

    @Override
    public List<TensuraSkill> getIntrinsicSkills(Player player) {
        List<TensuraSkill> list = new ArrayList<>();
        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        list.add(IntrinsicSkills.DRAGON_SKIN.get());
        list.add(CommonSkills.SELF_REGENERATION.get());
        List<TensuraSkill> serpentSkills = List.of(
                (TensuraSkill) CommonSkills.PARALYSIS.get(),
                CommonSkills.CORROSION.get(),
                CommonSkills.POISON.get()
        );

        List<TensuraSkill> lesserSerpentSkills = List.of(
                (TensuraSkill) IntrinsicSkills.DRAGON_EAR.get(),
                IntrinsicSkills.DRAGON_EYE.get()
        );

        // Filter out skills the player already has
        List<TensuraSkill> availableSerpentSkills = serpentSkills.stream()
                .filter(skill -> !storage.getSkill((ManasSkill) skill).isPresent())
                .toList();

        List<TensuraSkill> availableLesserSerpentSkills = lesserSerpentSkills.stream()
                .filter(skill -> !storage.getSkill((ManasSkill) skill).isPresent())
                .toList();

        // Pick one random skill from each list (if any are left)
        TensuraSkill randomSerpentSkill = null;
        if (!availableSerpentSkills.isEmpty()) {
            randomSerpentSkill = availableSerpentSkills.get(
                    player.getRandom().nextInt(availableSerpentSkills.size())
            );
        }

        TensuraSkill randomLesserSerpentSkill = null;
        if (!availableLesserSerpentSkills.isEmpty()) {
            randomLesserSerpentSkill = availableLesserSerpentSkills.get(
                    player.getRandom().nextInt(availableLesserSerpentSkills.size())
            );
        }

        // Add guaranteed / chosen skills to the returned list
        if (randomSerpentSkill != null) list.add(randomSerpentSkill);
        if (randomLesserSerpentSkill != null) list.add(randomLesserSerpentSkill);

        // You can add other intrinsic skills here if desired
        return list;
    }

    @Override
    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.GREATER_SERPENT_RACE));
        return list;
    }

    @Override
    @Nullable
    public Race getDefaultEvolution(Player player) {
        return (Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.GREATER_SERPENT_RACE);
    }

    @Override
    @Nullable
    public Race getAwakeningEvolution(Player player) {
        return (Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.GREATER_SERPENT_RACE);
    }

    @Override
    @Nullable
    public Race getHarvestFestivalEvolution(Player player) {
        return (Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.GREATER_SERPENT_RACE);
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
