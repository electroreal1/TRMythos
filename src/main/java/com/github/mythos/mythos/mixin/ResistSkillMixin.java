package com.github.mythos.mythos.mixin;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.tensura.ability.skill.resist.ResistSkill;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.mythos.mythos.registry.skill.Skills;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(
        value = {ResistSkill.class},
        priority = 919
)
public class ResistSkillMixin {
    public ResistSkillMixin() {
    }

    @ModifyReturnValue(
            method = {"isNullificationBypass"},
            at = {@At("RETURN")},
            remap = false
    )
    public boolean mythos$NullificationBypass(boolean original, @NotNull DamageSource damageSource) {
        Entity source = damageSource.getEntity();
        if (source instanceof LivingEntity living) {
            if (TensuraSkillCapability.isSkillInSlot(living, (ManasSkill) Skills.DENDRRAH.get())) {
                original = true;
            }
        }

        return original;
    }
}
