package com.github.mythos.mythos.mixin;

import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({CharmSkill.class})
public class CharmSkillMixin {
    @Inject(
            method = {"isMindControlFailed"},
            at = {@At("HEAD")},
            cancellable = true,
            remap = false
    )
    private static void addHaliCheck(LivingEntity user, LivingEntity target, Level level, CallbackInfoReturnable<Boolean> cir) {
        boolean failed = !CharmSkill.canMindControl(target, level);
        if (!failed && SkillUtils.isSkillToggled(target, Skills.HALI.get())) {
            failed = true;
        }

        if (SkillUtils.isSkillToggled(target, Skills.KHONSU.get())) {
            failed = true;
        }

        cir.setReturnValue(failed);
    }
}
