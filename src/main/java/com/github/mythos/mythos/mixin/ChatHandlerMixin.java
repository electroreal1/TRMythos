package com.github.mythos.mythos.mixin;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.mythos.mythos.ability.confluence.skill.unique.ConfluenceUniques;
import com.github.mythos.mythos.handler.CatharsisHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ChatHandlerMixin {

    @Inject(method = "handleChat", at = @At("HEAD"))
    private void onHandleChat(ServerboundChatPacket packet, CallbackInfo ci) {
        ServerPlayer player = ((ServerGamePacketListenerImpl)(Object)this).player;
        String msg = packet.message();

        if (!msg.equalsIgnoreCase("Exurge de deorsum, O Salvator mutationis!")) return;
        if (!player.level.dimension().equals(Level.END)) return;

        // Single-owner check
        if (CatharsisHandler.isObtained() && !CatharsisHandler.isOwner(player.getUUID().toString())) {
            player.sendSystemMessage(Component.literal("Catharsis has already been claimed!").withStyle(ChatFormatting.RED), false);
            return;
        }

        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        ManasSkill skill = ConfluenceUniques.CATHARSIS.get();
        if (!storage.getSkill(skill).isPresent()) {
            storage.learnSkill(skill);
            CatharsisHandler.markObtained(player.getUUID().toString());
            player.sendSystemMessage(Component.literal("You have fused with Catharsis.").withStyle(ChatFormatting.GOLD), false);
        } else {
            player.sendSystemMessage(Component.literal("You already possess Catharsis.").withStyle(ChatFormatting.RED), false);
        }
    }

}
