package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = "trmythos")
public class ChildOfThePlaneHandler {


    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        checkPlayerRace(player);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (player.tickCount % 200 == 0) {
            checkPlayerRace(player);
        }
    }

    private static void checkPlayerRace(ServerPlayer player) {
        ServerLevel level = player.server.overworld();
        ChildOfThePlaneRaceHandler tracker = ChildOfThePlaneRaceHandler.get(level);
        if (tracker.isClaimed()) return;
        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        Skill ChildOfThePlane = Skills.CHILD_OF_THE_PLANE.get();

        String playerRace = String.valueOf(TensuraPlayerCapability.getRace(player));

        List<? extends String> eligible = MythosSkillsConfig.racesThatCanCompeteForChildOfThePlane.get();
        if (eligible.stream().map(String::toLowerCase).anyMatch(r -> r.equals(playerRace))) {
            if (!storage.getSkill(ChildOfThePlane).isPresent()) {
                tracker.claim(player.getUUID());
                storage.learnSkill(ChildOfThePlane);
                broadcastAchievement(player, playerRace);
            }
        }
    }

    private static void broadcastAchievement(ServerPlayer player, String race) {
        MutableComponent raceName = Component.literal("Child of the Plane").withStyle(ChatFormatting.GOLD);
        player.server.getPlayerList().broadcastSystemMessage(
                Component.literal("§c" + player.getName().getString() + " has become the first " + raceName),
                false
        );
    }

    private static String getPlayerRace(ServerPlayer player) {
        // TODO: Replace this with your actual getter from your race system
        // Example: return RaceHelper.getRace(player);
        return "unknown";
    }

    private static String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

}
