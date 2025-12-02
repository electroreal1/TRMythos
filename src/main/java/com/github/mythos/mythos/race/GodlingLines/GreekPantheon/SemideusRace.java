package com.github.mythos.mythos.race.GodlingLines.GreekPantheon;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SemideusRace extends Race {
    public SemideusRace() {
        super(Difficulty.INTERMEDIATE);
    }

    @Override
    public double getBaseHealth() {
        return 1000;
    }
    public double getSpiritualHealthMultiplier() {
        return 5;
    }

    @Override
    public float getPlayerSize() {
        return 2.2f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 10;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 5.0f;
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
        return 0.24;
    }
    @Override
    public double getSprintSpeed() {
        return 0.26f;
    }

    private double auraMin = 500000;
    private double auraMax = 500000;
    private double startingMagiculeMin = 500000;
    private double startingMagiculeMax = 500000;
    private double baseMagicule = 500000;
    private double baseAura = 500000;

    @Override
    public Pair<Double, Double> getBaseAuraRange() {
        // The range of values that the Aura Range could be. So between 800 and 1211
        return Pair.of(auraMin, auraMax);
    }

    @Override
    public double getAuraEvolutionReward() {
        return AuraEvolutionReward();
    }
    @Override
    public double getManaEvolutionReward() {
        return ManaEvolutionReward();
    }

    private double AuraEvolutionReward() {
        return 750000;
    }

    private double ManaEvolutionReward() {
        return 250000;
    }

    @Override
    public Pair<Double, Double> getBaseMagiculeRange() {
        // The range of values that the Max Magicules could be. So between 80 and 120
        return Pair.of(startingMagiculeMin, startingMagiculeMax);
    }
    @Override
    public List<Race> getPreviousEvolutions(Player player) {
        List<Race> list = new ArrayList();
        list.add((Race)((IForgeRegistry) MythosRaces.BUDDING_DEMIGOD));
        return list;
    }
    @Override
    public double getEvolutionPercentage(Player player) {
        SkillStorage storage = SkillAPI.getSkillsFrom(player);

        // Base evolution percentage
        double evolutionPercentage = 0.1f;

        if (TensuraPlayerCapability.isHeroEgg(player)) {
            evolutionPercentage += 49.9;
        } else  {
            evolutionPercentage = 0;
        }
        if (!TensuraPlayerCapability.isHeroEgg(player) ||
                TensuraPlayerCapability.isDemonLordSeed(player)){
            evolutionPercentage += 0f;
        }

        // Add evolution percentage based on EP ratio
        double minimalEP = this.getMinBaseAura() + this.getMinBaseMagicule();
        evolutionPercentage += TensuraPlayerCapability.getBaseEP(player) * 50 / minimalEP;

        // Return the final value
        return evolutionPercentage;
    }

   public List<Race> getNextEvolutions(Player player) {
       List<Race> list = new ArrayList<>();
       SkillStorage storage = SkillAPI.getSkillsFrom(player);
       list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.EUSEBIA));
       return list;
   }

    public List<Component> getRequirementsForRendering(Player player) {
        List<Component> list = new ArrayList();
        list.add(Component.translatable("tensura.evolution_menu.ep_requirement"));
        list.add(Component.translatable("trmythos.evolution_menu.pre_awakening",
                new Object[]{ Component.translatable("trmythos.race.require.hero_egg").withStyle(ChatFormatting.GOLD)}));
        return list;
    }

    public List<TensuraSkill> getIntrinsicSkills (Player player){
        List<TensuraSkill> list = new ArrayList<>();
        list.add(ExtraSkills.MAJESTY.get());
        return list;
    }

    @Override
    public boolean isSpiritual() {
        return false;
    }

    @Override
    public boolean isMajin() {
        return false;
    }

    @Override
    public boolean isDivine() {
        return true;
    }
}
