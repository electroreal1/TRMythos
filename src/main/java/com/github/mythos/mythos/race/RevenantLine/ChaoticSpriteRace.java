package com.github.mythos.mythos.race.RevenantLine;

import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.*;
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

public class ChaoticSpriteRace extends Race {
    public ChaoticSpriteRace(Difficulty difficulty) {super(difficulty);}
    public ChaoticSpriteRace() {super(Difficulty.HARD);}

    @Override
    public double getBaseHealth() {
        return 1111;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 10.0009;
    }

    @Override
    public float getPlayerSize() {
        return 1.5f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 4.0f;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 4.0f;
    }

    @Override
    public double getKnockbackResistance() {
        return 0.5f;
    }

    @Override
    public double getJumpHeight() {
        return 1;
    }

    @Override
    public double getMovementSpeed() {
        return 0.25;
    }

    @Override
    public double getSprintSpeed() {
        return 1.2f;
    }

    private double auraMin = 250000;
    private double auraMax = 250000;
    private double startingMagiculeMin = 750000;
    private double startingMagiculeMax = 750000;

    @Override
    public double getAuraEvolutionReward() {
        return AuraEvolutionReward();
    }
    @Override
    public double getManaEvolutionReward() {
        return ManaEvolutionReward();
    }

    private double AuraEvolutionReward() {
        return 25000;
    }

    private double ManaEvolutionReward() {
        return 25000;
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
        list.add(ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.get());
        list.add(ResistanceSkills.PIERCE_RESISTANCE.get());
        list.add(ResistanceSkills.GRAVITY_ATTACK_RESISTANCE.get());
        list.add(CommonSkills.COERCION.get());
        list.add(IntrinsicSkills.POSSESSION.get());
        list.add(CommonSkills.SELF_REGENERATION.get());
        list.add(ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get());
        list.add(ExtraSkills.SOUND_MANIPULATION.get());
        list.add(UniqueSkills.REVERSER.get());
        list.add(ResistanceSkills.MAGIC_RESISTANCE.get());
        list.add(ExtraSkills.MAGIC_JAMMING.get());
        return list;
    }

    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.VOID_APOSTLE));
        return list;
    }

    public @Nullable Race getDefaultEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.VOID_APOSTLE));
    }

    public @Nullable Race getHarvestFestivalEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.VOID_APOSTLE));
    }

    public boolean isMajin() {
        return true;
    }
    public boolean isSpiritual() {return true;}

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
            entity.getAbilities().setFlyingSpeed(0.35F);
            entity.onUpdateAbilities();
            level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, 0.5F, 1.0F);
        }
    }
}
