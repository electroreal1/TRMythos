package com.github.trmythos.trmythos.race.JormungandrRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
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

public class SonOfLokiRace extends Race {
    public SonOfLokiRace() {
        super(Difficulty.INTERMEDIATE);
    }

        @Override
        public double getBaseHealth() {
            return 1000;
        }

        @Override
        public double getSpiritualHealthMultiplier() {
            return 5.0;
        }

        @Override
        public float getPlayerSize() {
            return 1.5f;
        }

        @Override
        public double getBaseAttackDamage() {
            return 6;
        }

        @Override
        public double getBaseAttackSpeed() {
            return 3.5;
        }

        @Override
        public double getKnockbackResistance() {
            return 1;
        }

        @Override
        public double getJumpHeight() {
            return 1.5;
        }

        @Override
        public double getMovementSpeed() {
            return 0.175f;
        }

        @Override
        public double getSprintSpeed() {
            return 2;
        }

        private double auraMin = 250000.0;
        private double auraMax = 250000.0;
        private double startingMagiculeMin = 250000.0;
        private double startingMagiculeMax = 250000.0;

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
        public double getAuraEvolutionReward() {
            return AuraEvolutionReward();
        }
        @Override
        public double getManaEvolutionReward() {
            return ManaEvolutionReward();
        }

        private double AuraEvolutionReward() {
            return 300000;
        }

        private double ManaEvolutionReward() {
            return 400000;
        }

        @Override
        public List<TensuraSkill> getIntrinsicSkills(Player player) {
            List<TensuraSkill> list = new ArrayList<>();
            list.add(IntrinsicSkills.FLAME_BREATH.get());
            list.add(ExtraSkills.ULTRASPEED_REGENERATION.get());
            list.add(IntrinsicSkills.DIVINE_KI_RELEASE.get());
            list.add(ResistanceSkills.MAGIC_RESISTANCE.get());
            list.add(ResistanceSkills.PIERCE_RESISTANCE.get());
            return list;
        }

    public List<Race> getNextEvolutions(Player player) {
       List<Race> list = new ArrayList<>();
       list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TRMythosRaces.MIDGARDIAN_SPIRIT_RACE));
       return list;
    }
     public @Nullable Race getDefaultEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TRMythosRaces.MIDGARDIAN_SPIRIT_RACE));
    }

    public @Nullable Race getAwakeningEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TRMythosRaces.MIDGARDIAN_SPIRIT_RACE));
    }

    public @Nullable Race getHarvestFestivalEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TRMythosRaces.MIDGARDIAN_SPIRIT_RACE));
    }

        public boolean isMajin() {
            return true;
        }
        public boolean isSpiritual() {
            return false;
        }

        public boolean isDivine() {
            return true;
        }

    }

