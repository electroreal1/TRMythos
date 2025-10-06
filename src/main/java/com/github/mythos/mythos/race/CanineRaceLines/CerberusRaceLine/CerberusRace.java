package com.github.mythos.mythos.race.CanineRaceLines.CerberusRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class CerberusRace extends Race {
    public CerberusRace() {
        super(Difficulty.INTERMEDIATE);
    }
    @Override
    public double getBaseHealth() {
        return 3000;
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
        return 10;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 4;
    }

    @Override
    public double getKnockbackResistance() {
        return 0.35f;
    }

    @Override
    public double getJumpHeight() {
        return 2;
    }

    @Override
    public double getMovementSpeed() {
        return 0.3;
    }
    @Override
    public double getSprintSpeed() {
        return 0.4f;
    }

    private double auraMin = 1000000;
    private double auraMax = 1000000;
    private double startingMagiculeMin = 1000000;
    private double startingMagiculeMax = 1000000;
    private double baseMagicule = 1000000;
    private double baseAura = 1000000;

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
        return 1200000;
    }

    private double ManaEvolutionReward() {
        return 800000;
    }

    @Override
    public Pair<Double, Double> getBaseMagiculeRange() {
        // The range of values that the Max Magicules could be. So between 80 and 120
        return Pair.of(startingMagiculeMin, startingMagiculeMax);
    }
    @Override
    public List<Race> getPreviousEvolutions(Player player) {
        List<Race> list = new ArrayList();
        list.add((Race)((IForgeRegistry) MythosRaces.HELL_HOUND_RACE));
        return list;
    }
    @Override
    // public double getEvolutionPercentage(Player player) {
    //  double minimalEP = this.getMinBaseAura() + this.getMaxBaseMagicule();
    //   return TensuraPlayerCapability.getBaseEP(player) * 50;
    //  }

    public List<Component> getRequirementsForRendering(Player player) {
        List<Component> list = new ArrayList();
        list.add(Component.translatable("tensura.evolution_menu.ep_requirement"));
        return list;
    }

    public List<TensuraSkill> getIntrinsicSkills (Player player){
        List<TensuraSkill> list = new ArrayList<>();
        list.add(ResistanceSkills.MAGIC_NULLIFICATION.get());
        list.add(ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get());
        list.add(UniqueSkills.ROYAL_BEAST.get());
        list.add(ExtraSkills.INFINITE_REGENERATION.get());
        list.add(ExtraSkills.BLACK_FLAME.get());
        return list;
    }

    public boolean isMajin() {
        return true;
    }
    public boolean isSpiritual() {
        return true;
    }
}
