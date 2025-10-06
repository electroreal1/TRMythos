package com.github.mythos.mythos.race.JormungandrRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.CommonSkills;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import com.github.manasmods.tensura.util.JumpPowerHelper;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a class that shows a short example of creating a custom race. For more information, check out the existing Race files in the mod.
 */
public class LesserSerpentRace extends Race {

    // Here are listed all the main characteristics of the race for easy changing
    private double baseHealth = 10.0;
   private double baseSpiritualHealth = 50;
    private double baseAttackDamage = 1.0;
    private double baseAttackSpeed = 3.0;
    private double knockbackResistance = 0.0;
    private double jumpHeight = 1.0;
    private double movementSpeed = 0.1;
    private double sprintSpeed = 0.12;
    private double auraMin = 300.0;
    private double auraMax = 500.0;
    private double startingMagiculeMin = 500.0;
    private double startingMagiculeMax = 700.0;

    private float playerSize = 0.75f;


    public LesserSerpentRace() {
        // Here is where the difficulty for each of the
        super(Difficulty.INTERMEDIATE);
    }

    @Override
    public double getBaseHealth() {
        return baseHealth;
    }

    public double getBaseSpiritualHealth() {
        return  baseSpiritualHealth;
    }

    @Override
    public float getPlayerSize() {
        return playerSize;
    }

    @Override
    public double getBaseAttackDamage() {
        return baseAttackDamage;
    }

    @Override
    public double getBaseAttackSpeed() {
        return baseAttackSpeed;
    }

    @Override
    public double getKnockbackResistance() {
        return knockbackResistance;
    }

    @Override
    public double getJumpHeight() {
        // Jump height is a little strange, so use this helper to hide the rest of the calculations
        return JumpPowerHelper.defaultPlayer(jumpHeight);
    }

    @Override
    public double getMovementSpeed() {
        return movementSpeed;
    }

    @Override
    public double getSprintSpeed() {
        return sprintSpeed;
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

    /**
     * This method adds the intrinsic skills, from either the addon or the main mod to the class.
     * To add more, simply add to the list with .add()
     * @param player
     * @return
     */

    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.SERPENT_RACE));
        return list;
    }
    public @Nullable Race getDefaultEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.SERPENT_RACE));
    }

    public @Nullable Race getAwakeningEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.SERPENT_RACE));
    }

    public @Nullable Race getHarvestFestivalEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.SERPENT_RACE));
    }

    @Override
    public List<TensuraSkill> getIntrinsicSkills(Player player) {
        List<TensuraSkill> list = new ArrayList<>();
        List<TensuraSkill> LesserSerpentSkills = List.of(
                (TensuraSkill) CommonSkills.PARALYSIS.get(),
                CommonSkills.CORROSION.get(),
                CommonSkills.POISON.get()
        );
        TensuraSkill randomLesserSerpentSkill = LesserSerpentSkills.get(
                player.getRandom().nextInt(LesserSerpentSkills.size())
        );
        list.add(IntrinsicSkills.SCALE_ARMOR.get());
        list.add(ExtraSkills.SENSE_HEAT_SOURCE.get());


        return list;
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
