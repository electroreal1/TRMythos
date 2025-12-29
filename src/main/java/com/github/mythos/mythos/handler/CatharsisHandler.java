package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.mythos.mythos.ability.confluence.skill.ConfluenceUniques;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CatharsisHandler {

    private static final String INCANTATION = "Exurge de deorsum, O Salvator mutationis!";
    private static boolean obtained = false;
    private static String ownerUUID = null;

    public static boolean isObtained() {
        return obtained;
    }

    public static boolean isOwner(String uuid) {
        return ownerUUID != null && ownerUUID.equals(uuid);
    }

    public static void markObtained(String uuid) {
        obtained = true;
        ownerUUID = uuid;
    }

    public static String getOwnerUUID() {
        return ownerUUID;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new CatharsisHandler());
    }

    @SubscribeEvent
    public void onPlayerChat(ServerChatEvent event) {
        if (event.isCanceled()) return;

        ServerPlayer player = event.getPlayer();
        String message = event.getMessage().getString(); // only the actual sent text

        if (!message.equalsIgnoreCase(INCANTATION)) return;

        if (!player.level.dimension().equals(Level.END)) {
            player.sendSystemMessage(Component.literal("You must be in the End to obtain Catharsis!").withStyle(ChatFormatting.RED), false);
            return;
        }

        if (obtained && !player.getUUID().toString().equals(ownerUUID)) {
            player.sendSystemMessage(Component.literal("Catharsis has already been claimed!").withStyle(ChatFormatting.RED), false);
            return;
        }

        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        ManasSkill skill = ConfluenceUniques.CATHARSIS.get();
        if (storage.getSkill(skill).isEmpty()) {
            storage.learnSkill(skill);
            obtained = true;
            ownerUUID = player.getUUID().toString();
            player.sendSystemMessage(Component.literal("You have fused with Catharsis.").withStyle(ChatFormatting.GOLD), false);
        } else {
            player.sendSystemMessage(Component.literal("You already possess Catharsis.").withStyle(ChatFormatting.RED), false);
        }
    }

}


