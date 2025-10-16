package com.github.trmythos.trmythos.race.ValkyrieRaceLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SoulCourierRace extends Race {
    public SoulCourierRace() {
        super(Difficulty.INTERMEDIATE);
    }

    @Override
    public double getBaseHealth() {
        return 300;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 4.0;
    }

    @Override
    public float getPlayerSize() {
        return 1;
    }

    @Override
    public double getBaseAttackDamage() {
        return 1.5;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 3;
    }

    @Override
    public double getKnockbackResistance() {
        return 0;
    }

    @Override
    public double getJumpHeight() {
        return 1;
    }

    @Override
    public double getMovementSpeed() {
        return 0.2f;
    }

    @Override
    public double getSprintSpeed() {
        return 0.22f;
    }

    private double auraMin = 60000.0;
    private double auraMax = 60000.0;
    private double startingMagiculeMin = 40000.0;
    private double startingMagiculeMax = 40000.0;

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
        list.add(ResistanceSkills.DARKNESS_ATTACK_RESISTANCE.get());
        list.add(ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get());
        list.add(ExtraSkills.ULTRASPEED_REGENERATION.get());
        return list;
    }
    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.VALKYRIE_RACE));
        return list;
    }

    public @Nullable Race getDefaultEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.VALKYRIE_RACE));
    }

    public @Nullable Race getAwakeningEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.VALKYRIE_RACE));
    }

    public @Nullable Race getHarvestFestivalEvolution(Player player) {
        return ((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.VALKYRIE_RACE));
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
    public void raceAbility(Player entity) {
    if (!entity.m5833() && !entity.m7500()) {
      Level level = entity.m9236();
      if ((entity.m150110()).f35936) {
        (entity.m150110()).f35936 = false;
        (entity.m150110()).f35935 = false;
      } else {
        (entity.m150110()).f35936 = true;
        (entity.m150110()).f35935 = true;
      } 
      entity.m150110().m35943(0.06F);
      entity.m6885();
      level.m6263((Player)null, entity.m20185(), entity.m20186(), entity.m20189(), SoundEvents.f11887, SoundSource.PLAYERS, 0.5F, 1.0F);
    } 
  }
}
}
