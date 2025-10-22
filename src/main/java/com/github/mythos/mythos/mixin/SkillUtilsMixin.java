package com.github.mythos.mythos.mixin;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.mythos.mythos.registry.skill.Skills;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.Memoires.trmysticism.registry.skill.UltimateSkills;
import io.github.Memoires.trmysticism.registry.skill.UniqueSkills;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

import static com.github.manasmods.tensura.ability.SkillUtils.isSkillToggled;
import static com.github.manasmods.tensura.capability.skill.TensuraSkillCapability.isSkillInSlot;

@Mixin(
        value = {SkillUtils.class},
        priority = 8
)
public class SkillUtilsMixin {

    public SkillUtilsMixin() {
    }

    @Shadow
    public static boolean hasSkill(Entity entity, ManasSkill manasSkill) {
        return false;
    }

    @ModifyReturnValue(
            method = {"getEarningLearnPoint"},
            at = {@At("RETURN")},
            remap = false
    )
    private static int trmythos$modifyEarningLearnPoint(int original, ManasSkillInstance instance, LivingEntity entity, boolean isMode) {
        int point = original;
            if (isSkillToggled(entity, (ManasSkill)Skills.ORUNMILA.get())) {
                point = original + 20;
            }

        return point;
    }

    @ModifyReturnValue(
            method = {"getBonusMasteryPoint"},
            at = {@At("RETURN")},
            remap = false
    )
    private static int trmythos$modifyBonusMasteryPoint(int original, ManasSkillInstance instance, LivingEntity entity) {
        int point = original;

        if (isSkillToggled(entity, (ManasSkill)Skills.ORUNMILA.get())) {
            point += 20;
        }


        return point;
    }


    @ModifyReturnValue(
            method = {"canNegateDodge"},
            at = {@At("RETURN")},
            remap = false
    )
    private static boolean trmythos$modifyCanNegateDodge(boolean original, LivingEntity entity, DamageSource source) {
        Entity var4 = source.getEntity();
        if (var4 instanceof LivingEntity attacker) {
            if (isSkillInSlot(attacker, (ManasSkill)Skills.ORUNMILA.get())) {
                return true;
            }
        }

        return original;
    }

    @ModifyReturnValue(
            method = {"reducingResistances"},
            at = {@At("RETURN")},
            remap = false
    )
    private static boolean customReduction(boolean original, LivingEntity entity) {
        return original ||
                isSkillInSlot(entity, (ManasSkill)Skills.ORUNMILA.get()) ||
                isSkillInSlot(entity, (ManasSkill)Skills.CHILD_OF_THE_PLANE.get());
    }


    @ModifyReturnValue(
            method = {"getMagiculeGain"},
            at = {@At("RETURN")},
            remap = false
    )
    private static float modifyMagiculeGain(float original, Player player, boolean majin) {
        float bonus = original;
        if (hasSkill(player, (ManasSkill)Skills.CHILD_OF_THE_PLANE.get())) {
            Optional<ManasSkillInstance> instance = SkillAPI.getSkillsFrom(player).getSkill((ManasSkill) Skills.CHILD_OF_THE_PLANE.get());
            if (instance.isPresent()) {
                bonus = ((ManasSkillInstance)instance.get()).isMastered(player) ? 0.075F : 0.05F;
                if (((ManasSkillInstance)instance.get()).getOrCreateTag().getBoolean("ChildOfThePlane")) {
                    bonus *= 3.0F;
                }
            }
        }
        return bonus;
    }

    @ModifyReturnValue(
            method = {"getAuraGain"},
            at = {@At("RETURN")},
            remap = false
    )
    private static float modifyAuraGain(float original, Player player, boolean majin) {
        float bonus = original;
        if (hasSkill(player, (ManasSkill)Skills.CHILD_OF_THE_PLANE.get())) {
            Optional<ManasSkillInstance> instance = SkillAPI.getSkillsFrom(player).getSkill((ManasSkill)Skills.CHILD_OF_THE_PLANE.get());
            if (instance.isPresent()) {
                bonus = ((ManasSkillInstance)instance.get()).isMastered(player) ? 0.075F : 0.05F;
                if (((ManasSkillInstance)instance.get()).getOrCreateTag().getBoolean("ChildOfThePlane")) {
                    bonus *= 3.0F;
                }
            }
        }
        return bonus;
    }



}

