package com.github.mythos.mythos.race.DivinityLine.LightGod;

import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class GreaterGodOfLightRace extends Race {
    public GreaterGodOfLightRace() {super(Difficulty.EASY);}

    @Override
    public double getBaseHealth() {return 4800;}

    @Override
    public double getSpiritualHealthMultiplier() {
        return 2.0;
    }

    @Override
    public float getPlayerSize() {return 2.3f;}

    @Override
    public double getBaseAttackDamage() {
        return 9f;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 4f;
    }

    @Override
    public double getKnockbackResistance() {
        return 1.8f;
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
        return 0.55f;
    }

    private double auraMin = 4000000;
    private double auraMax = 4000000;
    private double startingMagiculeMin = 4000000;
    private double startingMagiculeMax = 4000000;

    @Override
    public double getAuraEvolutionReward() {
        return AuraEvolutionReward();
    }
    @Override
    public double getManaEvolutionReward() {
        return ManaEvolutionReward();
    }

    private double AuraEvolutionReward() {
        return 2000000;
    }

    private double ManaEvolutionReward() {
        return 2000000;
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

    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.LESSER_TITAN));
        return list;
    }

    public double getEvolutionPercentage(Player player) {
        double minimalEP = this.getMinBaseAura() + this.getMinBaseMagicule();
        double percentage = TensuraPlayerCapability.getBaseEP(player) * 50 / minimalEP;
        percentage = Math.min(percentage, 50.0);
        if (TensuraPlayerCapability.isDemonLordSeed(player) || TensuraPlayerCapability.isHeroEgg(player)) {
            percentage += 50.0D;
        }
        return Math.min(percentage, 100.0);
    }

    public List<Component> getRequirementsForRendering(Player player) {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("tensura.evolution_menu.ep_requirement"));
        list.add(Component.translatable("trmythos.race.greater_god.egg_or_seed"));
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
