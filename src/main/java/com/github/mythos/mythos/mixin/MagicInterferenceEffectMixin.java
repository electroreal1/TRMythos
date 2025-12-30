package com.github.mythos.mythos.mixin;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.effect.skill.debuff.MagicInterferenceEffect;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({MagicInterferenceEffect.class})
public class MagicInterferenceEffectMixin {

    public MagicInterferenceEffectMixin() {
    }

    @Inject(
            method = {"canStillFly"},
            at = {@At("HEAD")},
            cancellable = true,
            remap = false
    )
    private static void canStillFly(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (SkillUtils.hasSkill(player, (ManasSkill) Skills.PRETENDER_KING.get())) {
            cir.setReturnValue(true);
        }

    }
}
