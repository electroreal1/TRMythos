package com.github.mythos.mythos.mixin;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.skill.unique.InfinityPrisonSkill;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.mythos.mythos.ability.confluence.skill.ConfluenceUniques;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
        value = {InfinityPrisonSkill.class},
        priority = 12
)
public class FragarachInfinityPrisonPierceMixin {
    public FragarachInfinityPrisonPierceMixin() {
    }

    @Inject(
            method = {"onBeingDamaged"},
            at = {@At("HEAD")},
            cancellable = true,
            remap = false
    )
    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event, CallbackInfo ci) {
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof Player player) {
            if (this.isBypassSkill(player)) {
                ci.cancel();
            }

        }
    }

    private boolean isBypassSkill(Player player) {
        boolean original = false;
        if (TensuraSkillCapability.isSkillInSlot(player, ConfluenceUniques.FRAGARACH.get())) {
            original = true;
        }

        return original;
    }

}
