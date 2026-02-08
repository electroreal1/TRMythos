package com.github.mythos.mythos.race.FableRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FableweaverRace extends FablebornRace {
    public FableweaverRace(Difficulty difficulty) {
        super(difficulty);
    }

    @Override
    public double getBaseHealth() {
        return 1500;
    }

    @Override
    public double getAdditionalSpiritualHealth() {
        return 500;
    }

    @Override
    public float getPlayerSize() {
        return 1.7f;
    }

    @Override
    public double getJumpHeight() {
        return 1.5;
    }

    @Override
    public double getBaseAttackDamage() {
        return 12;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 1.6;
    }

    @Override
    public double getMovementSpeed() {
        return 0.24;
    }


    private double auraMin = 35000;
    private double auraMax = 35000;
    private double startingMagiculeMin = 45000;
    private double startingMagiculeMax = 45000;

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
        list.add(ResistanceSkills.MAGIC_RESISTANCE.get());
        list.add(ResistanceSkills.ABNORMAL_CONDITION_RESISTANCE.get());
        return list;
    }

    @Override
    public boolean isSpiritual() {
        return true;
    }

    public List<Race> getPreviousEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add(TensuraRaces.RACE_REGISTRY.get().getValue(MythosRaces.FABLEWEAVER));
        return list;
    }

    @Nullable
    @Override
    public Race getDefaultEvolution(Player player) {
        return null;
    }

    @Nullable
    @Override
    public Race getAwakeningEvolution(Player player) {
        return TensuraRaces.RACE_REGISTRY.get().getValue(MythosRaces.LIVING_LEGEND);
    }

    @Override
    public @Nullable MutableComponent getName() {
        return Component.literal("Fableweaver");
    }

    public double getEvolutionPercentage(Player player) {
        double percentage = 0.0;
        if (TensuraEPCapability.getName(player) != null) {
            percentage += 50.0;
        }
        if (TensuraEPCapability.getEP(player) >= 80000.0) {
            percentage += 50;
        }

        return percentage;
    }

    @Override
    public List<Component> getRequirementsForRendering(Player player) {
        List<Component> list = new ArrayList();
        list.add(Component.translatable("tensura.evolution_menu.name_requirement"));
        list.add(Component.literal("Get more EP"));
        return list;
    }
}
