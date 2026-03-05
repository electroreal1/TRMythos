package com.github.mythos.mythos.race.VoidVesselLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.util.JumpPowerHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EmptyBodyRace extends Race {
    public EmptyBodyRace() {
        super(Difficulty.EXTREME);
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Empty Body");
    }


    @Override
    public double getBaseHealth() {
        return 1200;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 4.16666666667f;
    }

    @Override
    public float getPlayerSize() {
        return 2;
    }

    @Override
    public double getBaseAttackDamage() {
        return 5;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 5;
    }

    @Override
    public double getKnockbackResistance() {
        return 0;
    }
    @Override
    public double getJumpHeight() {
        return JumpPowerHelper.defaultPlayer(2);
    }

    @Override
    public double getMovementSpeed() {
        return 1;
    }

    private final double auraMin = 400000;
    private final double auraMax = 400000;
    private final double startingMagiculeMin = 400000;
    private final double startingMagiculeMax = 400000;

    @Override
    public Pair<Double, Double> getBaseAuraRange() {
        return Pair.of(auraMin, auraMax);
    }

    @Override
    public Pair<Double, Double> getBaseMagiculeRange() {
        return Pair.of(startingMagiculeMin, startingMagiculeMax);
    }

    @Override
    public boolean isMajin() {
        return true;
    }

    @Override
    public List<TensuraSkill> getIntrinsicSkills(Player player) {
        List<TensuraSkill> list = new ArrayList<>();
        list.add(ExtraSkills.MORTAL_FEAR.get());
        list.add(ExtraSkills.SHADOW_MOTION.get());
        list.add(ExtraSkills.ULTRASPEED_REGENERATION.get());
        return list;
    }
}
