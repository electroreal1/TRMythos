package com.github.mythos.mythos.mixin;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({SkillHelper.class})
public abstract class SkillHelperMixin {
    public SkillHelperMixin() {
    }

    @ModifyVariable(
            method = {"outOfMagicule(Lnet/minecraft/world/entity/LivingEntity;D)Z"},
            at = @At("HEAD"),
            index = 1,
            remap = false,
            argsOnly = true
    )
    private static double mythosMagiculeCost(double cost, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (SkillUtils.isSkillToggled(player, (ManasSkill) Skills.DOMINATE.get())) {
                cost *= 0.5;
            }
        }
        return cost;
    }
}
