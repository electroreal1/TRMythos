package com.github.mythos.mythos.race.DivinityLine;

import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.github.mythos.mythos.registry.skill.Skills;
import com.mojang.datafixers.util.Pair;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GreaterDivinityRace extends Race {
    public GreaterDivinityRace() {super(Difficulty.EASY);}

    @Override
    public double getBaseHealth() {return 600;}

    @Override
    public double getSpiritualHealthMultiplier() {
        return 2.0;
    }

    @Override
    public float getPlayerSize() {return 2.2f;}

    @Override
    public double getBaseAttackDamage() {
        return 6f;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 4f;
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
        return 0.25;
    }
    @Override
    public double getSprintSpeed() {
        return 0.4f;
    }

    private double auraMin = 500000;
    private double auraMax = 500000;
    private double startingMagiculeMin = 500000;
    private double startingMagiculeMax = 500000;

    @Override
    public double getAuraEvolutionReward() {
        return AuraEvolutionReward();
    }
    @Override
    public double getManaEvolutionReward() {
        return ManaEvolutionReward();
    }

    private double AuraEvolutionReward() {
        return 250000;
    }

    private double ManaEvolutionReward() {
        return 250000;
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
        list.add(ExtraSkills.INFINITE_REGENERATION.get());
        list.add(ExtraSkills.MULTILAYER_BARRIER.get());
        list.add(ExtraSkills.HEAVENLY_EYE.get());
        return list;
    }

    public List<Race> getNextEvolutions(Player player) {
        SkillStorage storage = SkillAPI.getSkillsFrom((Entity)player);
        List<Race> list = new ArrayList<>();
        if (SkillUtils.hasSkill(player, ExtraSkills.FLAME_MANIPULATION.get())) {
            list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.LESSER_GOD_OF_FLAME));
        }
        if (SkillUtils.hasSkill(player, ExtraSkills.WATER_MANIPULATION.get())) {
            list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.LESSER_GOD_OF_WATER));
        }
        if (SkillUtils.hasSkill(player, ExtraSkills.EARTH_MANIPULATION.get())) {
            list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.LESSER_GOD_OF_EARTH));
        }
        if (SkillUtils.hasSkill(player, ExtraSkills.WIND_MANIPULATION.get())) {
            list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.LESSER_GOD_OF_WIND));
        }
        if (SkillUtils.hasSkill(player, ExtraSkills.SPATIAL_MANIPULATION.get())) {
            list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.LESSER_GOD_OF_SPACE));
        }
        if (SkillUtils.hasSkill(player, io.github.Memoires.trmysticism.registry.skill.ExtraSkills.LIGHT_MANIPULATION.get())) {
            list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.LESSER_GOD_OF_LIGHT));
        }
        if (SkillUtils.hasSkill(player, io.github.Memoires.trmysticism.registry.skill.ExtraSkills.DARKNESS_MANIPULATION.get())) {
            list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.LESSER_GOD_OF_DARKNESS));
        }
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
