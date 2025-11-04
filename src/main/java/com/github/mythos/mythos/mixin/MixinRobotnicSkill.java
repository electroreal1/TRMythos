package com.github.mythos.mythos.mixin;

import com.github.lucifel.virtuoso.ability.skill.unique.RobotnicSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.mythos.mythos.registry.MythosWeapons;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(RobotnicSkill.class)
public class MixinRobotnicSkill {

    @Unique
    private static final Set<Item> CUSTOM_BLACKLIST = Set.of(
            MythosWeapons.CATHARSIS.get(),
            MythosWeapons.UNDECEMBER.get(),
            MythosWeapons.FRAGARACH.get()
    );


    @Inject(
            method = "onPressed(Lcom/github/manasmods/manascore/api/skills/ManasSkillInstance;Lnet/minecraft/world/entity/LivingEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;getTag()Lnet/minecraft/nbt/CompoundTag;"
            ),
            cancellable = true
    )
    private void preventDeconstruction(ManasSkillInstance instance, LivingEntity entity, CallbackInfo ci) {
        if (entity instanceof Player player) {
            ItemStack stack = player.getMainHandItem();
            CompoundTag tag = stack.getTag();

            if (tag != null && CUSTOM_BLACKLIST.contains(stack.getItem())) {
                ci.cancel();
            }
        }
    }
}
