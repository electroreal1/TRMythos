package com.github.mythos.mythos.race.CanineRaceLines.CerberusRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class HoundOfHadesRace extends Race {
    public HoundOfHadesRace() { super(Difficulty.INTERMEDIATE); }

    @Override
    public double getBaseHealth() { return 10000; }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 2.0;
    }

    @Override
    public float getPlayerSize() { return 4.0f; }

    @Override
    public double getBaseAttackDamage() {
        return 20.0f;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 4.0f;
    }

    @Override
    public double getKnockbackResistance() {
        return 0.45f;
    }

    @Override
    public double getJumpHeight() {
        return 2;
    }

    @Override
    public double getMovementSpeed() {
        return 0.4;
    }

    @Override
    public double getSprintSpeed() {
        return 1.0f;
    }

    private double auraMin = 2500000;
    private double auraMax = 2500000;
    private double startingMagiculeMin = 2500000;
    private double startingMagiculeMax = 2500000;

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
        list.add(UniqueSkills.DIVINE_BERSERKER.get());
        list.add(IntrinsicSkills.POSSESSION.get());
        return list;
    }

    public double getEvolutionPercentage(Player player) {
        double minimalEP = this.getMinBaseAura() + this.getMinBaseMagicule();

        // Base EP contribution, capped at 50
        double percentage = TensuraPlayerCapability.getBaseEP(player) * 50 / minimalEP;
        percentage = Math.min(percentage, 50.0);

        // True Demon Lord bonus adds 50
        if (TensuraPlayerCapability.isTrueDemonLord(player)) {
            percentage += 50.0;
        } else if (TensuraPlayerCapability.isTrueHero(player)) {
            percentage += 0f;
        }

        // Final cap at 100
        return Math.min(percentage, 100.0);
    }

    public List<Component> getRequirementsForRendering(Player player) {
        List<Component> list = new ArrayList();
        list.add(Component.translatable("tensura.evolution_menu.ep_requirement"));
        list.add(Component.translatable("True Demon Lord Awakening"));
        return list;
    }

    public boolean isMajin() {
        return true;
    }
    public boolean isSpiritual() {
        return true;
    }
    public boolean isDivine() {
        return false;
    }
}