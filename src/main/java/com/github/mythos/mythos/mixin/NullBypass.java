package com.github.mythos.mythos.mixin;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.tensura.ability.skill.resist.ResistSkill;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ResistSkill.class})
public class NullBypass {
    public NullBypass() {
    }

    @Inject(
            method = {"isNullificationBypass"},
            at = {@At("HEAD")},
            remap = false,
            cancellable = true
    )
    private void isNullificationBypass(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        Entity var4 = damageSource.getEntity();
        if (var4 instanceof LivingEntity living) {
            if (TensuraSkillCapability.isSkillInSlot(living, (ManasSkill) Skills.CHILD_OF_THE_PLANE.get())) {
                cir.setReturnValue(true);
            }

            if (TensuraSkillCapability.isSkillInSlot(living, (ManasSkill) Skills.ORUNMILA.get())) {
                cir.setReturnValue(true);
            }
        }

    }
}
