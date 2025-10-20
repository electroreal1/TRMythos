package com.github.mythos.mythos.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.world.entity.player.Player;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

public class skillUtilsMixin {
    private static boolean hasSkill(Entity entity, ManasSkill skill) {
        return false;
    }
    @ModifyReturnValue(method = "getEarningLearnPoint", at = @At("RETURN"), remap = false)
    private static int modifyEarningLearnPoint(int basePoints, ManasSkillInstance instance, LivingEntity entity, boolean isMode) {
        // Ultimate & Unique Skill Bonuses
        if (hasSkill(entity, Skills.ELTNAM.get())) basePoints += 10;

        return basePoints;
    }
    @ModifyReturnValue(method = "getBonusMasteryPoint", at = @At("RETURN"), remap = false)
    private static int modifyBonusMasteryPoint(int basePoints, ManasSkillInstance instance, LivingEntity entity) {
        // Ultimate & Unique Skill Bonuses
        if (hasSkill(entity, Skills.ELTNAM.get())) basePoints += 10;
        return basePoints;
    }
}