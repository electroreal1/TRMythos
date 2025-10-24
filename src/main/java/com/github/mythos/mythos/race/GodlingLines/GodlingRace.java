package com.github.mythos.mythos.race.GodlingLines;

import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.ability.skill.resist.ResistSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.CommonSkills;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GodlingRace extends Race {

    public GodlingRace() {
        super(Difficulty.INTERMEDIATE);
    }
    @Override
    public double getBaseHealth() {
        return 60;
    }


    @Override
    public double getSpiritualHealthMultiplier() {
        return 5.0;
    }

    @Override
    public float getPlayerSize() {
        return 1f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 3f;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 2f;
    }

    @Override
    public double getKnockbackResistance() {
        return 0.0f;
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
    public double getSprintSpeed() {
        return 0.21f;
    }

    private double auraMin = 10000;
    private double auraMax = 50000;
    private double startingMagiculeMin = 10000;
    private double startingMagiculeMax = 50000;

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
        list.add(ExtraSkills.HEAVENLY_EYE.get());
        list.add(ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.get());
        list.add(IntrinsicSkills.DIVINE_KI_RELEASE.get());
        return list;
    }
    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race)((IForgeRegistry)TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.BUDDING_DEMIGOD));
        return list;
    }

    public @Nullable Race getDefaultEvolution(Player player) {
        return ((Race)((IForgeRegistry)TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.BUDDING_DEMIGOD));
    }

    public @Nullable Race getAwakeningEvolution(Player player) {
        return ((Race)((IForgeRegistry)TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.BUDDING_DEMIGOD));
    }

    public @Nullable Race getHarvestFestivalEvolution(Player player) {
        return ((Race)((IForgeRegistry)TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.BUDDING_DEMIGOD));
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

