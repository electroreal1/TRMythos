package com.github.mythos.mythos.race.FableRaceLine;

import com.github.lucifel.virtuoso.registry.skill.VExtraSkills;
import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class GenesisMyth extends Race{
    public GenesisMyth(Difficulty difficulty) {
        super(difficulty);
    }

    @Override
    public double getBaseHealth() {
        return 15000;
    }

    @Override
    public double getAdditionalSpiritualHealth() {
        return getBaseHealth() * 10;
    }

    @Override
    public float getPlayerSize() {
        return 1.95f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 300;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 3.5;
    }

    @Override
    public double getKnockbackResistance() {
        return 10;
    }

    @Override
    public double getJumpHeight() {
        return 2;
    }

    @Override
    public double getMovementSpeed() {
        return 0.6;
    }

    private double auraMin = 20000000;
    private double auraMax = 20000000;
    private double startingMagiculeMin = 20000000;
    private double startingMagiculeMax = 20000000;

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
        list.add(ExtraSkills.HEAVENLY_EYE.get());
        list.add(VExtraSkills.CONCENTRATOR.get());
        list.add(VExtraSkills.MYSTIC_AURA.get());
        return list;
    }

    @Override
    public @org.jetbrains.annotations.Nullable MutableComponent getName() {
        return Component.literal("Genesis Myth");
    }

    @Override
    public boolean isSpiritual() {
        return true;
    }

    @Override
    public boolean isDivine() {
        return true;
    }
}
