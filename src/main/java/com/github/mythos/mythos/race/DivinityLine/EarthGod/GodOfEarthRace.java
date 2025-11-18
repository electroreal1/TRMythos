package com.github.mythos.mythos.race.DivinityLine.EarthGod;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
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

public class GodOfEarthRace extends Race {
    public GodOfEarthRace() {super(Difficulty.EASY);}

    @Override
    public double getBaseHealth() {return 2400;}

    @Override
    public double getSpiritualHealthMultiplier() {
        return 2.0;
    }

    @Override
    public float getPlayerSize() {return 2.2f;}

    @Override
    public double getBaseAttackDamage() {
        return 8f;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 4f;
    }

    @Override
    public double getKnockbackResistance() {
        return 1.75f;
    }

    @Override
    public double getJumpHeight() {
        return 2;
    }

    @Override
    public double getMovementSpeed() {
        return 0.25;
    }
    @Override
    public double getSprintSpeed() {
        return 0.5f;
    }

    private double auraMin = 2000000;
    private double auraMax = 2000000;
    private double startingMagiculeMin = 2000000;
    private double startingMagiculeMax = 2000000;

    @Override
    public double getAuraEvolutionReward() {
        return AuraEvolutionReward();
    }
    @Override
    public double getManaEvolutionReward() {
        return ManaEvolutionReward();
    }

    private double AuraEvolutionReward() {
        return 1000000;
    }

    private double ManaEvolutionReward() {
        return 1000000;
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
        list.add(ResistanceSkills.EARTH_ATTACK_NULLIFICATION.get());
        list.add(ExtraSkills.MAGIC_EARTH_TRANSFORM.get());
        return list;
    }

    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.GREATER_GOD_OF_EARTH));
        return list;
    }

    public @Nullable Race getDefaultEvolution(Player player) {
        return ((Race)((IForgeRegistry)TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.GREATER_GOD_OF_EARTH));
    }

    public @Nullable Race getAwakeningEvolution(Player player) {
        return ((Race)((IForgeRegistry)TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.GREATER_GOD_OF_EARTH));
    }

    public @Nullable Race getHarvestFestivalEvolution(Player player) {
        return ((Race)((IForgeRegistry)TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.GREATER_GOD_OF_EARTH));
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
            entity.getAbilities().setFlyingSpeed(0.12F);
            entity.onUpdateAbilities();
            level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, 0.5F, 1.0F);
        }
    }

    public boolean isDivine() {
        return true;
    }

    public boolean isMajin() {
        return false;
    }

    public boolean isSpiritual() {
        return true;
    }
}
