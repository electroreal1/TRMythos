package com.github.mythos.mythos.mixin;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.mythos.mythos.ability.confluence.skill.unique.ConfluenceUniques;
import com.github.mythos.mythos.ability.mythos.skill.unique.BibliomaniaSkill;
import com.github.mythos.mythos.ability.mythos.skill.unique.ChildOfThePlaneSkill;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

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

        if (hasSkill(entity, (ManasSkill)Skills.ORUNMILA.get())) {
            point = original + 20;
        }
        if (hasSkill(entity, (ManasSkill)Skills.ELTNAM.get())) {
            point += 6;
        }
        if (hasSkill(entity, (ManasSkill)Skills.ZEPIA.get())) {
            point += 9;
        }
        if (hasSkill(entity, (ManasSkill)Skills.OMNISCIENT_EYE.get())) {
            point += 10;
        }
        if (hasSkill(entity, (ManasSkill)Skills.TRUE_DAO.get())) {
            point += 5;
        }
        if (hasSkill(entity, (ManasSkill)Skills.ORIGIN_DAO.get())) {
            point += 10;
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
        if (hasSkill(entity, (ManasSkill)Skills.ELTNAM.get())) {
            point += 6;
        }
        if (hasSkill(entity, (ManasSkill)Skills.ZEPIA.get())) {
            point += 9;
        }
        if (hasSkill(entity, (ManasSkill)Skills.OMNISCIENT_EYE.get())) {
            point += 10;
        }
        if (hasSkill(entity, (ManasSkill)Skills.OMNISCIENT_EYE.get()) && instance.getSkill() instanceof com.github.manasmods.tensura.ability.magic.Magic) {
            point += 999;
        }
        if (hasSkill(entity, (ManasSkill)Skills.TRUE_DAO.get())) {
            point += 5;
        }
        if (hasSkill(entity, (ManasSkill)Skills.ORIGIN_DAO.get())) {
            point += 10;
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
            if (isSkillInSlot(attacker, (ManasSkill) ConfluenceUniques.FRAGARACH.get())) {
                return Math.random() < 0.5;
            }
        }

        return original;
    }

    @ModifyReturnValue(
            method = {"reducingResistances"},
            at = {@At("RETURN")},
            remap = false
    )
    private static boolean NullToResistAndResistToNothing(boolean original, LivingEntity entity) {
        return original ||
                isSkillInSlot(entity, (ManasSkill)Skills.ORUNMILA.get()) ||
                isSkillInSlot(entity, (ManasSkill)Skills.CHILD_OF_THE_PLANE.get()) ||
                isSkillInSlot(entity, (ManasSkill)Skills.TRUE_DAO.get()) ||
                isSkillInSlot(entity, (ManasSkill)Skills.ORIGIN_DAO.get()) ||
                entity.hasEffect(MythosMobEffects.BLOOD_COAT.get());
    }

    @ModifyReturnValue(
            method = {"getMagiculeGain"},
            at = {@At("RETURN")},
            remap = false
    )
    private static float MythosMagiculeGain(float original, Player player, boolean majin) {

        original += ChildOfThePlaneSkill.getChildOfThePlaneBoost(player, true, majin);
        original += BibliomaniaSkill.getBibliomaniaBoost(player, true, majin);
        return original;
    }

    @ModifyReturnValue(
            method = {"getAuraGain"},
            at = {@At("RETURN")},
            remap = false
    )
    private static float MythosAuraGain(float original, Player player, boolean majin) {
        if (hasSkill(player, (ManasSkill) Skills.NASCENT_DAO.get())) {
            original = 0;
        }
        if (hasSkill(player, (ManasSkill) Skills.AWAKENED_DAO.get())) {
            original = 0;
        }
        if (hasSkill(player, (ManasSkill) Skills.TRUE_DAO.get())) {
            original = 0;
        }
        if (hasSkill(player, (ManasSkill) Skills.ORIGIN_DAO.get())) {
            original = 0;
        }


        original += ChildOfThePlaneSkill.getChildOfThePlaneBoost(player, false, majin);
        original += BibliomaniaSkill.getBibliomaniaBoost(player, true, majin);
        return original;
    }

}

