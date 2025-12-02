package com.github.mythos.mythos.race.RevenantLine;

import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.items.TensuraMobDropItems;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.*;
import com.github.mythos.mythos.config.MythosConfig;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class PrimalChaosRace extends Race {
    public PrimalChaosRace(Difficulty difficulty) {super(difficulty);}
    public PrimalChaosRace() {super(Difficulty.HARD);}

    @Override
    public double getBaseHealth() {
        return 3333;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 10.0009;
    }

    @Override
    public float getPlayerSize() {
        return 2.0f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 10.0f;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 4.0f;
    }

    @Override
    public double getKnockbackResistance() {
        return 1.5f;
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
        return 1.5f;
    }

    private double auraMin = 750000;
    private double auraMax = 750000;
    private double startingMagiculeMin = 2250000;
    private double startingMagiculeMax = 2250000;

    @Override
    public double getAuraEvolutionReward() {
        return AuraEvolutionReward();
    }
    @Override
    public double getManaEvolutionReward() {
        return ManaEvolutionReward();
    }

    private double AuraEvolutionReward() {
        return 100000;
    }

    private double ManaEvolutionReward() {
        return 100000;
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
        list.add(ExtraSkills.MAGIC_AURA.get());
        list.add(ResistanceSkills.DARKNESS_ATTACK_RESISTANCE.get());
        list.add(ExtraSkills.INFINITE_REGENERATION.get());
        list.add(IntrinsicSkills.DIVINE_KI_RELEASE.get());
        // list.add(Skills.PANDEMONIUM.get());
        return list;
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
            entity.getAbilities().setFlyingSpeed(0.5F);
            entity.onUpdateAbilities();
            level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, 0.5F, 1.0F);
        }
    }

    public double getEvolutionPercentage(Player player) {
        double minimalEP = this.getMinBaseAura() + this.getMinBaseMagicule();
        double percentage = TensuraPlayerCapability.getBaseEP(player) * 50 / minimalEP;
        percentage = Math.min(percentage, 50.0);
        if (!TensuraPlayerCapability.isTrueHero(player) && TensuraPlayerCapability.isTrueDemonLord(player)) {
            percentage += 50.0;
        }
        return Math.min(percentage, 100.0);
    }

    public List<Component> getRequirementsForRendering(Player player) {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("tensura.evolution_menu.ep_requirement"));
        list.add(Component.translatable("trmythos.evolution_menu.true_demon_lord"));
        return list;
    }
}
