package com.github.mythos.mythos.race.ValkyrieRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class EnvoyOfValhallaRace extends Race {

    public EnvoyOfValhallaRace() {
        super(Race.Difficulty.INTERMEDIATE);
    }

    @Override
    public double getBaseHealth() {
        return 3000;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 4.0;
    }

    @Override
    public float getPlayerSize() {
        return 1.3f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 4.0f;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 3.0f;
    }

    @Override
    public double getKnockbackResistance() {
        return 0.5f;
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
        return 0.3f;
    }

    private double auraMin = 600000;
    private double auraMax = 600000;
    private double startingMagiculeMin = 1400000;
    private double startingMagiculeMax = 1400000;

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
        list.add(UniqueSkills.HEALER.get());
        list.add(UniqueSkills.TUNER.get());
        list.add(ExtraSkills.INFINITE_REGENERATION.get());
        return list;
    }

    public double getEvolutionPercentage(Player player) {
        double minimalEP = this.getMinBaseAura() + this.getMinBaseMagicule();

        // Base EP contribution, capped at 50
        double percentage = TensuraPlayerCapability.getBaseEP(player) * 50 / minimalEP;
        percentage = Math.min(percentage, 50.0);

        // True Hero bonus adds 50
        if (TensuraPlayerCapability.isTrueHero(player)) {
            percentage += 50.0;
        } else if (TensuraPlayerCapability.isTrueDemonLord(player)) {
            percentage += 0f;
        }

        // Final cap at 100
        return Math.min(percentage, 100.0);
    }

    public List<Component> getRequirementsForRendering(Player player) {
        List<Component> list = new ArrayList();
        list.add(Component.translatable("tensura.evolution_menu.ep_requirement"));
        list.add(Component.translatable("tensura.evolution_menu.awaken_requirement",
                new Object[]{ Component.translatable("tensura.attribute.true_hero.name").withStyle(ChatFormatting.GOLD)}));
        return list;
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

    public boolean isMajin() {
        return false;
    }

    public boolean isSpiritual() {
        return true;
    }

    public boolean isDivine() {
        return true;
    }
}