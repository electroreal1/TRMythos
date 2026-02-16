package com.github.mythos.mythos.mixin;

import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.intrinsic.PossessionSkill;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PossessionSkill.class})
public class PossessionSkillMixin {

    @Inject(
            method = {"canPossess"},
            at = {@At("HEAD")},
            cancellable = true,
            remap = false
    )
    private void addHaliPossessionCheck(LivingEntity target, Player player, CallbackInfoReturnable<Boolean> cir) {
        if (SkillUtils.isSkillToggled(target, Skills.HALI.get())) {
            cir.setReturnValue(false);
        }

        if (SkillUtils.isSkillToggled(target, Skills.KHONSU.get())) {
            cir.setReturnValue(false);
        }

    }
}
