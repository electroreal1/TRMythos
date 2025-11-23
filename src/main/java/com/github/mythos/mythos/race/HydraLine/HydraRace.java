package com.github.mythos.mythos.race.HydraLine;

import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.CommonSkills;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class HydraRace extends Race {
    public HydraRace() {super(Difficulty.INTERMEDIATE);}

    @Override
    public double getBaseHealth() {
        return 5000;
    }

    @Override
    public double getSpiritualHealthMultiplier() {
        return 10.0;
    }

    @Override
    public float getPlayerSize() {
        return 4.0f;
    }

    @Override
    public double getBaseAttackDamage() {
        return 15;
    }

    @Override
    public double getBaseAttackSpeed() {
        return 5.0;
    }

    @Override
    public double getKnockbackResistance() {
        return 3;
    }

    @Override
    public double getJumpHeight() {
        return 1;
    }

    @Override
    public double getMovementSpeed() {
        return 0.15;
    }

    @Override
    public double getSprintSpeed(){return 0.8;}

    @Override
    public Pair<Double, Double> getBaseAuraRange() {
        return Pair.of(auraMin, auraMax);
    }

    @Override
    public Pair<Double, Double> getBaseMagiculeRange() {
        return Pair.of(startingMagiculeMin, startingMagiculeMax);
    }

    private double auraMin = 4000000.0;
    private double auraMax = 4000000.0;
    private double startingMagiculeMin = 4000000.0;
    private double startingMagiculeMax = 4000000.0;

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

    @Override
    public List<TensuraSkill> getIntrinsicSkills(Player player) {
        List<TensuraSkill> list = new ArrayList<>();
        list.add(IntrinsicSkills.DRAGON_SKIN.get());
        list.add(CommonSkills.SELF_REGENERATION.get());
        list.add(CommonSkills.HYDRAULIC_PROPULSION.get());
        list.add(ResistanceSkills.DARKNESS_ATTACK_RESISTANCE.get());
        list.add(ResistanceSkills.WATER_ATTACK_RESISTANCE.get());
        list.add(ExtraSkills.MAGIC_SENSE.get());
        list.add(ExtraSkills.SNAKE_EYE.get());
        list.add(ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.get());
        list.add(ResistanceSkills.ABNORMAL_CONDITION_RESISTANCE.get());
        list.add(ExtraSkills.ULTRASPEED_REGENERATION.get());
        list.add(ExtraSkills.UNIVERSAL_PERCEPTION.get());
        list.add(ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get());
        list.add(ExtraSkills.MAJESTY.get());
        list.add(ExtraSkills.ULTRA_INSTINCT.get());
        list.add(ExtraSkills.INFINITE_REGENERATION.get());
        list.add(IntrinsicSkills.DIVINE_KI_RELEASE.get());
        list.add(ResistanceSkills.MAGIC_RESISTANCE.get());
        return list;
    }

    @Override
    public boolean isMajin() {
        return true;
    }

    @Override
    public boolean isSpiritual() {
        return true;
    }

    @Override
    public boolean isDivine() {
        return true;
    }
}
