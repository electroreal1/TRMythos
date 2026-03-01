package com.github.mythos.mythos.race.PaperLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PaperRace extends Race {


        public PaperRace() { super(Difficulty.INTERMEDIATE); }
        @Override
        public double getBaseHealth() {
            return 7;
        }

        @Override
        public double getSpiritualHealthMultiplier() {
            return 28.0;
        }

        @Override
        public float getPlayerSize() {
            return 0.5f;
        }

        @Override
        public double getBaseAttackDamage() {
            return 3.0f;
        }

        @Override
        public double getBaseAttackSpeed() {
            return 9.0f;
        }

        @Override
        public double getKnockbackResistance() {
            return 0.0f;
        }

        @Override
        public double getJumpHeight() {
            return 2;
        }


        @Override
        public double getMovementSpeed() {
            return 0.5;
        }

        @Override
        public double getSprintSpeed() {
            return 0.8f;
        }

        private double auraMin = 400;
        private double auraMax = 600;
        private double startingMagiculeMin = 500;
        private double startingMagiculeMax = 600;

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
            return list;
        }

        public List<Race> getNextEvolutions(Player player) {
            List<Race> list = new ArrayList<>();
            list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.SOUL_COURIER_RACE));
            return list;
        }

        public @Nullable Race getDefaultEvolution(Player player) {
            return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.SOUL_COURIER_RACE));
        }

        public @Nullable Race getAwakeningEvolution(Player player) {
            return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.SOUL_COURIER_RACE));
        }

        public @Nullable Race getHarvestFestivalEvolution(Player player) {
            return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.SOUL_COURIER_RACE));
        }

        public boolean isMajin() {
            return false;
        }
        public boolean isSpiritual() {
            return false;
        }
        public boolean isDivine() {
            return true;
        }

    public void raceAbility(Player entity) {
        if (!entity.isSpectator() && !entity.isCreative()) {
            Level level = entity.level;
            if ((entity.getAbilities()).mayfly) {
                (entity.getAbilities()).mayfly = false;
                (entity.getAbilities()).flying = false;
            } else {
                (entity.getAbilities()).mayfly = true;
                (entity.getAbilities()).flying = true;
            }
            entity.getAbilities().setFlyingSpeed(0.3F);
            entity.onUpdateAbilities();
            level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, 0.5F, 1.0F);
        }
    }
    }

