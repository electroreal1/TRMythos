package com.github.mythos.mythos.mixin;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.mythos.mythos.registry.skill.Skills;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.Memoires.trmysticism.ability.skill.ultimate.UrielSkill;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
        value = {UrielSkill.class},
        priority = 12
)
public class UrielBypassMixinMythos {
    public UrielBypassMixinMythos() {
    }

    @ModifyReturnValue(
            method = {"onBeingDamaged"},
            at = {@At("HEAD")},
            remap = false
    )
    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event, CallbackInfo ci) {
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof Player player) {
            if (TensuraSkillCapability.isSkillInSlot(player, Skills.ZERO.get())) {
                ci.cancel();
            }
        }
    }
}
