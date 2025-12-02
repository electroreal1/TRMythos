package com.github.mythos.mythos.race.GodlingLines.GreekPantheon;

import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class EusebiaRace extends Race {
    public EusebiaRace(Difficulty difficulty) {super(difficulty);}
    public EusebiaRace() {super(Difficulty.INTERMEDIATE);}

    @Override
    public double getBaseHealth() {
        return 10000;
    }
    public double getSpiritualHealthMultiplier() {
        return 10;
    }

    @Override
    public float getPlayerSize() {
        return 2.2f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 20;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 5.0f;
    }

    @Override
    public double getKnockbackResistance() {
        return 2.0f;
    }

    @Override
    public double getJumpHeight() {
        return 2;
    }

    @Override
    public double getMovementSpeed() {
        return 0.26;
    }
    @Override
    public double getSprintSpeed() {
        return 0.55f;
    }

    private double auraMin = 5000000;
    private double auraMax = 5000000;
    private double startingMagiculeMin = 5000000;
    private double startingMagiculeMax = 5000000;

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
    public double getAuraEvolutionReward() {
        return AuraEvolutionReward();
    }
    @Override
    public double getManaEvolutionReward() {
        return ManaEvolutionReward();
    }

    private double AuraEvolutionReward() {
        return 7500000;
    }

    private double ManaEvolutionReward() {
        return 2500000;
    }

    @Override
    public List<Race> getPreviousEvolutions(Player player) {
        List<Race> list = new ArrayList();
        list.add((Race)((IForgeRegistry) MythosRaces.SEMIDEUS));
        return list;
    }

    @Override
    public double getEvolutionPercentage(Player player) {
        double minimalEP = this.getMinBaseAura() + this.getMinBaseMagicule();

        double percentage = TensuraPlayerCapability.getBaseEP(player) * 50 / minimalEP;
        percentage = Math.min(percentage, 50.0);

        if (TensuraPlayerCapability.isTrueHero(player)) {
            percentage += 50.0;
        }
        return Math.min(percentage, 100.0);
    }

    public List<Component> getRequirementsForRendering(Player player) {
        List<Component> list = new ArrayList();
        list.add(Component.translatable("tensura.evolution_menu.ep_requirement"));
        list.add(Component.translatable("trmythos.evolution_menu.true_hero"));
        return list;
    }

    // public List<TensuraSkill> getIntrinsicSkills (Player player){
    //     List<TensuraSkill> list = new ArrayList<>();
    //     list.add(ExtraSkills.MAJESTY.get());
    //     return list;
    // }

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
            entity.getAbilities().setFlyingSpeed(0.5F);
            entity.onUpdateAbilities();
            level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, 0.5F, 1.0F);
        }
    }

    public boolean isSpiritual() {return true;}
    public boolean isDivine() {return true;}
}
