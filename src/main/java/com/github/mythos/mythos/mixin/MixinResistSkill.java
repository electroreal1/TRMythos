package com.github.mythos.mythos.mixin;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.resist.ResistSkill;
import com.github.mythos.mythos.registry.skill.Skills;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ResistSkill.class})
public class MixinResistSkill {

    @ModifyReturnValue(
            method = {"isNullificationBypass"},
            at = {@At("RETURN")},
            remap = false
    )
    private boolean trmythos$isNullificationBypass(boolean bypass, @NotNull DamageSource damageSource) {
        Entity var4 = damageSource.getEntity();
        if (var4 instanceof LivingEntity living) {
            if (SkillUtils.isSkillToggled(living, (ManasSkill) Skills.ORUNMILA.get())) {
                return true;
            }
        }

        return bypass;
    }
}
