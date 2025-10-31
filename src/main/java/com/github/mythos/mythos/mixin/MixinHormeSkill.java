package com.github.mythos.mythos.mixin;


import com.github.lucifel.virtuoso.ability.skill.ultimate.HormeSkill;
import com.github.lucifel.virtuoso.menu.TrueOptimizeMenu;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.mythos.mythos.registry.skill.FusedSkills;
import com.github.mythos.mythos.util.MythosUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(HormeSkill.class)
public abstract class MixinHormeSkill {

//    @ModifyReturnValue(
//            method = "onPressed()V",
//            at = {@At("RETURN")},
//            remap = false
//    )

    @Shadow protected abstract boolean canCreateSkill(ResourceLocation location, ServerPlayer serverPlayer, ManasSkillInstance instance);

    @Inject(method = "onPressed", at = @At("RETURN"), remap = false)
    private void addExtraSkills(ManasSkillInstance instance, LivingEntity entity, CallbackInfo ci) {
        if (instance.getMode() == 2) {
            if (!(entity instanceof ServerPlayer player)) return;
            ServerPlayer serverPlayer = (ServerPlayer) entity;

            // Add your extra skills
            List<ResourceLocation> extraSkills = new ArrayList<>();
            ResourceLocation optimalMove;

            if (MythosUtils.hasProfanity(player) && MythosUtils.hasDreamer(player)) {
                optimalMove = FusedSkills.PARANOIA.getId();
                if (this.canCreateSkill(optimalMove, player, instance)) {
                    extraSkills.add(optimalMove);
                }
            }

            if (MythosUtils.hasGravityDominationAndSpatialDomination(player)) {
                optimalMove = UniqueSkills.OPPRESSOR.getId();
                if (this.canCreateSkill(optimalMove, player, instance)) {
                    extraSkills.add(optimalMove);
                }
            }

            NetworkHooks.openScreen(player, new SimpleMenuProvider(TrueOptimizeMenu::new, Component.empty()), (buf) -> {
                buf.writeUUID(entity.getUUID());
                buf.writeCollection(extraSkills, FriendlyByteBuf::writeResourceLocation);
            });
        }
    }


    @Mixin(HormeSkill.class)
    public interface MixinHormeSkillInvoker {
        @Invoker("canCreateSkill")
        boolean callCanCreateSkill(ResourceLocation skill, ServerPlayer player, ManasSkillInstance instance);
    }


}

