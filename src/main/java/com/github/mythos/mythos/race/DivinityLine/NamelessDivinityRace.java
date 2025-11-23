package com.github.mythos.mythos.race.DivinityLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.CommonSkills;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import io.github.Memoires.trmysticism.registry.race.MysticismRaces;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class NamelessDivinityRace extends Race {

    public NamelessDivinityRace() {super(Difficulty.EASY);}
    @Override
    public double getBaseHealth() {return 80;}

    @Override
    public double getSpiritualHealthMultiplier() {
        return 2.0;
    }

    @Override
    public float getPlayerSize() {
        return 2f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 3f;
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
        return 0.2;
    }
    @Override
    public double getSprintSpeed() {
        return 0.25f;
    }

    private double auraMin = 8000;
    private double auraMax = 10000;
    private double startingMagiculeMin = 12000;
    private double startingMagiculeMax = 20000;

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
        list.add(CommonSkills.SELF_REGENERATION.get());
        list.add(ExtraSkills.MAGIC_SENSE.get());
        list.add(ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.get());
        list.add(IntrinsicSkills.DIVINE_KI_RELEASE.get());
        return list;
    }
    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.LESSER_DIVINITY));
        return list;
    }

    public @Nullable Race getDefaultEvolution(Player player) {
        return ((Race)((IForgeRegistry)TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.LESSER_DIVINITY));
    }

    public @Nullable Race getAwakeningEvolution(Player player) {
        return ((Race)((IForgeRegistry)TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.LESSER_DIVINITY));
    }

    public @Nullable Race getHarvestFestivalEvolution(Player player) {
        return ((Race)((IForgeRegistry)TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.LESSER_DIVINITY));
    }

    @Override
    public List<Race> getPreviousEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MysticismRaces.DIVINE_ARMY_WASP));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MysticismRaces.DIVINE_BLACK_SPIDER));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MysticismRaces.DIVINE_BLUE_CENTIPEDE));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MysticismRaces.DIVINE_FOLIARIS));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MysticismRaces.DIVINE_WOLF));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MysticismRaces.DIVINE_TENGU));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MysticismRaces.DIVINE_DEATHSTALKER_SCORPION));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MysticismRaces.DIVINE_FIRE_ANT));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MysticismRaces.DIVINE_HARDSHELL_ANT));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MysticismRaces.DIVINE_STAG_BEETLE));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MysticismRaces.DIVINE_EMPEROR_SCORPION));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MysticismRaces.DIVINE_KNIGHT_SPIDER));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MysticismRaces.DIVINE_DRONE_BEETLE));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MysticismRaces.DIVINE_EMPRESS_WASP));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MysticismRaces.DIVINE_YELLOW_CENTIPEDE));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MysticismRaces.DIVINE_PURPLE_CENTIPEDE));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TensuraRaces.DIVINE_VAMPIRE.get().getRegistryName()));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TensuraRaces.DIVINE_SKELETON.get().getRegistryName()));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TensuraRaces.DIVINE_ONI.get().getRegistryName()));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TensuraRaces.DIVINE_HUMAN.get().getRegistryName()));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TensuraRaces.DIVINE_FISH.get().getRegistryName()));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TensuraRaces.DIVINE_FIGHTER.get().getRegistryName()));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TensuraRaces.DIVINE_ELF.get().getRegistryName()));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TensuraRaces.DIVINE_DWARF.get().getRegistryName()));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TensuraRaces.DIVINE_DRAGON.get().getRegistryName()));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TensuraRaces.DIVINE_BOAR.get().getRegistryName()));
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(TensuraRaces.DIVINE_BEAST.get().getRegistryName()));

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
