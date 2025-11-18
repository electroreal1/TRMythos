package com.github.mythos.mythos.race.DivinityLine.EarthGod;

import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class LesserGodOfEarthRace extends Race {
    public LesserGodOfEarthRace() {super(Difficulty.EASY);}

    @Override
    public double getBaseHealth() {return 1200;}

    @Override
    public double getSpiritualHealthMultiplier() {
        return 2.0;
    }

    @Override
    public float getPlayerSize() {return 2.2f;}

    @Override
    public double getBaseAttackDamage() {
        return 7f;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 4f;
    }

    @Override
    public double getKnockbackResistance() {
        return 1.5f;
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
        return 0.45f;
    }

    private double auraMin = 1000000;
    private double auraMax = 1000000;
    private double startingMagiculeMin = 1000000;
    private double startingMagiculeMax = 1000000;

    @Override
    public double getAuraEvolutionReward() {
        return AuraEvolutionReward();
    }
    @Override
    public double getManaEvolutionReward() {
        return ManaEvolutionReward();
    }

    private double AuraEvolutionReward() {
        return 500000;
    }

    private double ManaEvolutionReward() {
        return 500000;
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
        list.add(ResistanceSkills.EARTH_ATTACK_RESISTANCE.get());
        list.add(IntrinsicSkills.EARTH_TRANSFORM.get());
        list.add(io.github.Memoires.trmysticism.registry.skill.ExtraSkills.MANA_MANIPULATION.get());
        list.add(com.github.lucifel.virtuoso.registry.skill.ExtraSkills.CONCENTRATOR.get());
        return list;
    }

    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.GOD_OF_EARTH));
        return list;
    }

    public @Nullable Race getDefaultEvolution(Player player) {
        return ((Race)((IForgeRegistry)TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.GOD_OF_EARTH));
    }

    public @Nullable Race getAwakeningEvolution(Player player) {
        return ((Race)((IForgeRegistry)TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.GOD_OF_EARTH));
    }

    public @Nullable Race getHarvestFestivalEvolution(Player player) {
        return ((Race)((IForgeRegistry)TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.GOD_OF_EARTH));
    }

    public double getEvolutionPercentage(Player player) {
        double minimalEP = this.getMinBaseAura() + this.getMinBaseMagicule();
        double percentage = TensuraPlayerCapability.getBaseEP(player) * 50 / minimalEP;
        percentage = Math.min(percentage, 50.0);
        boolean hasRequiredSkill = SkillUtils.isSkillMastered(player, com.github.manasmods.tensura.registry.skill.ExtraSkills.EARTH_MANIPULATION.get());
        if (!hasRequiredSkill)
            return 0.0D;
        if (hasRequiredSkill)
            percentage += 25.0D;
        if (TensuraEPCapability.getName((LivingEntity)player) != null)
            percentage += 25.0D;
        return Math.min(percentage, 100.0);
    }

    public List<Component> getRequirementsForRendering(Player player) {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("trmythos.earth_manipulation.mastered"));
        list.add(Component.translatable("tensura.evolution_menu.name_requirement"));
        list.add(Component.translatable("tensura.evolution_menu.ep_requirement"));
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
