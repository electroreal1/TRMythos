package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class StargazerHandler {
    private static long lastTime = -1;
    private static final long MESSAGE_COOLDOWN = 100;

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.level.isClientSide() || event.phase != TickEvent.Phase.END) return;

        ServerLevel world = (ServerLevel) event.level;
        long time = world.getDayTime() % 24000;

        if (lastTime == -1) {
            lastTime = time;
            return;
        }

        for (ServerPlayer player : world.players()) {
            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            if (storage == null || storage.getSkill(Skills.STARGAZER.get()).isEmpty()) continue;

            long lastMsgTime = player.getPersistentData().getLong("MoonMsgCooldown");
            if (world.getGameTime() - lastMsgTime < MESSAGE_COOLDOWN) continue;

            // Start of night
            if (lastTime < 12000 && time >= 12000) {
                player.displayClientMessage(
                        Component.literal("Night has arrived, embrace its serenity...")
                                .withStyle(ChatFormatting.AQUA),
                        false
                );
                player.getPersistentData().putLong("MoonMsgCooldown", world.getGameTime());
            }

            // Start of day
            if (lastTime > time) {
                player.displayClientMessage(
                        Component.literal("A new day dawns, but I will watch over you until tonight..")
                                .withStyle(ChatFormatting.AQUA),
                        false
                );
                player.getPersistentData().putLong("MoonMsgCooldown", world.getGameTime());
            }
        }

        lastTime = time;
    }
}

