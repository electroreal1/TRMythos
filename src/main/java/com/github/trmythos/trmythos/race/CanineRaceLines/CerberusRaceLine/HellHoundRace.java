package com.github.trmythos.trmythos.race.CanineRaceLines.CerberusRaceLine;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.trmythos.trmythos.registry.race.TRMythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class HellHoundRace extends Race {
    public HellHoundRace() {
        super(Difficulty.INTERMEDIATE);
    }
    @Override
    public double getBaseHealth() {
        return 300;
    }
    public double getSpiritualHealthMultiplier() {
        return 2f;
    }

    @Override
    public float getPlayerSize() {
        return 1.5f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 3;
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
        return 0.22;
    }
    @Override
    public double getSprintSpeed() {
        return 0.25f;
    }

    private double auraMin = 250000;
    private double auraMax = 250000;
    private double startingMagiculeMin = 250000;
    private double startingMagiculeMax = 250000;
    private double baseMagicule = 250000;
    private double baseAura = 250000;

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
        return 200000;
    }

    private double ManaEvolutionReward() {
        return 300000;
    }

    @Override
    public Pair<Double, Double> getBaseMagiculeRange() {
        // The range of values that the Max Magicules could be. So between 80 and 120
        return Pair.of(startingMagiculeMin, startingMagiculeMax);
    }
    @Override
    public List<Race> getPreviousEvolutions(Player player) {
        List<Race> list = new ArrayList();
        list.add((Race)((IForgeRegistry) TRMythosRaces.CANINE_RACE));
        return list;
    }
    @Override
    // public double getEvolutionPercentage(Player player) {
    //  double minimalEP = this.getMinBaseAura() + this.getMaxBaseMagicule();
    //   return TensuraPlayerCapability.getBaseEP(player) * 50;
    //  }
    public double getEvolutionPercentage(Player player) {
        SkillStorage storage = SkillAPI.getSkillsFrom(player);

        // Base evolution percentage
        double evolutionPercentage = 0.1f;

        // Add 50% if the player has the Water Domination skill
        if (storage.getSkill((ManasSkill) ExtraSkills.FLAME_DOMINATION.get()).isPresent()) {
            evolutionPercentage += 49.9;
        }

        // Add evolution percentage based on EP ratio
        double minimalEP = this.getMinBaseAura() + this.getMinBaseMagicule();
        evolutionPercentage += TensuraPlayerCapability.getBaseEP(player) * 50 / minimalEP;

        // Return the final value
        return evolutionPercentage;
    }

    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TRMythosRaces.CERBERUS_RACE));
        return list;
    }

    public @Nullable Race getDefaultEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TRMythosRaces.CERBERUS_RACE));
    }

    public @Nullable Race getAwakeningEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TRMythosRaces.CERBERUS_RACE));
    }

    public @Nullable Race getHarvestFestivalEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TRMythosRaces.CERBERUS_RACE));
    }

    public List<Component> getRequirementsForRendering(Player player) {
        List<Component> list = new ArrayList();
        list.add(Component.translatable("tensura.evolution_menu.ep_requirement"));
        list.add(Component.translatable("trmythos.flame_domination.acquired"));
        return list;
    }

    public List<TensuraSkill> getIntrinsicSkills (Player player){
        List<TensuraSkill> list = new ArrayList<>();
        list.add(ResistanceSkills.FLAME_ATTACK_RESISTANCE.get());
        list.add(ResistanceSkills.HEAT_RESISTANCE.get());
        list.add(ResistanceSkills.MAGIC_RESISTANCE.get());
        list.add(IntrinsicSkills.FLAME_BREATH.get());
        list.add(IntrinsicSkills.FLAME_TRANSFORM.get());
        return list;
    }

    public boolean isMajin() {
        return true;
    }
    public boolean isSpiritual() {
        return true;
    }
}
