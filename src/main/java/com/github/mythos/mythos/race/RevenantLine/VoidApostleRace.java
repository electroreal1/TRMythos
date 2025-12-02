package com.github.mythos.mythos.race.RevenantLine;

import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.TensuraSkill;
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

public class VoidApostleRace extends Race {
    public VoidApostleRace(Difficulty difficulty) {super(difficulty);}
    public VoidApostleRace() {super(Difficulty.HARD);}

    @Override
    public double getBaseHealth() {
        return 2222;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 10.0009;
    }

    @Override
    public float getPlayerSize() {
        return 1.75f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 6.0f;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 4.0f;
    }

    @Override
    public double getKnockbackResistance() {
        return 1.0f;
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
        return 1.25f;
    }

    private double auraMin = 500000;
    private double auraMax = 500000;
    private double startingMagiculeMin = 1500000;
    private double startingMagiculeMax = 1500000;

    @Override
    public double getAuraEvolutionReward() {
        return AuraEvolutionReward();
    }
    @Override
    public double getManaEvolutionReward() {
        return ManaEvolutionReward();
    }

    private double AuraEvolutionReward() {
        return 50000;
    }

    private double ManaEvolutionReward() {
        return 50000;
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
        return list;
    }

    public List<Race> getNextEvolutions(Player player) {
        List<Race> list = new ArrayList<>();
        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        list.add((Race)((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.PRIMAL_CHAOS));
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
            entity.getAbilities().setFlyingSpeed(0.4F);
            entity.onUpdateAbilities();
            level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, 0.5F, 1.0F);
        }
    }

    public double getEvolutionPercentage(Player player) {
        double cryptidessence = 0.0D;
        if (player instanceof LocalPlayer) {
            LocalPlayer localPlayer = (LocalPlayer)player;
            cryptidessence = localPlayer.getStats().getValue(Stats.ITEM_USED.get(TensuraMobDropItems.DEMON_ESSENCE.get()));
        } else if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            cryptidessence = serverPlayer.getStats().getValue(Stats.ITEM_USED.get(TensuraMobDropItems.DEMON_ESSENCE.get()));
        }

        return cryptidessence * 100.0D / ((Integer) MythosConfig.INSTANCE.racesConfig.essenceForApostle.get()).intValue();
    }

    public List<Component> getRequirementsForRendering(Player player) {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("tensura.evolution_menu.consume_requirement", new Object[] { ((Item) TensuraMobDropItems.DEMON_ESSENCE
                .get()).getDescription().getString() }));
        return list;
    }
}
