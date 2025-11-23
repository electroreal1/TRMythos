package com.github.mythos.mythos.mixin;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.mythos.mythos.registry.skill.Skills;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.Memoires.trmysticism.api.ICapabilityHasOwner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(
        value = {TensuraPlayerCapability.class},
        priority = 917
)
public abstract class TensuraPlayerCapabilityMixin implements ICapabilityHasOwner {
    public TensuraPlayerCapabilityMixin() {
    }

    @ModifyReturnValue(
            method = {"isDemonLordSeed()Z"},
            remap = false,
            at = {@At("RETURN")}
    )
    public boolean mythosisDemonLordSeed(boolean original) {
        if (SkillUtils.hasSkill(this.getOwner(), (ManasSkill) Skills.ARES.get())) {
            original = true;
        }
        if (SkillUtils.hasSkill(this.getOwner(), (ManasSkill) Skills.UNDERWORLD_PRINCE.get())) {
            original = true;
        }

        return original;
    }


}
