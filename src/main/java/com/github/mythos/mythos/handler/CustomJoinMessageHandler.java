package com.github.mythos.mythos.handler;

import com.github.mythos.mythos.networking.MythosNetwork;
import com.github.mythos.mythos.networking.play2server.ShaderPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(modid = "trmythos")
public class CustomJoinMessageHandler {

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer joiningPlayer) {
            String name = joiningPlayer.getGameProfile().getName();
            MinecraftServer server = joiningPlayer.getServer();
            if (server == null) return;

            if (name.equalsIgnoreCase("Dev")) { //FanatasyFan
                executeGlobalSimulationBreach(server);
            }

            if (name.equalsIgnoreCase("Dev")) { // PrimordialRose
                executeGlobalRoseBreach(server);
            }
        }
    }

    private static void executeGlobalRoseBreach(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            MythosNetwork.sendToPlayer(new ShaderPacket("trmythos:shaders/post/master_sky.json", 1.0f, 0.2f, 0.5f), player);

            player.playNotifySound(SoundEvents.END_PORTAL_SPAWN, SoundSource.AMBIENT, 1.2f, 0.5f);
            player.playNotifySound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.AMBIENT, 0.8f, 1.5f);
            player.playNotifySound(SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 1.0f, 0.1f);

            player.sendSystemMessage(Component.literal("§fThe first and the last, the beginning and the end. I am what encompasses all."));
        }

        scheduler.schedule(() -> server.execute(() -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                MythosNetwork.sendToPlayer(new ShaderPacket("none", 1.0f, 1.0f, 1.0f), player);
                player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.AMBIENT, 0.5f, 0.5f);
            }
        }), 4000, TimeUnit.MILLISECONDS);
    }

    private static void executeGlobalSimulationBreach(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            MythosNetwork.sendToPlayer(new ShaderPacket("trmythos:shaders/post/master_sky.json", 0.1f, 0.8f, 1.0f), player);

            player.sendSystemMessage(Component.literal("§c§l[!] ALERT: §fThe Multi-Dimensional Barrier Has Been Breached."));
            playSound(player, SoundEvents.END_PORTAL_SPAWN, 1.0f, 0.5f);
            playSound(player, SoundEvents.ZOMBIE_VILLAGER_CURE, 0.6f, 2.0f);
            playSound(player, SoundEvents.ANVIL_LAND, 0.4f, 0.1f);
        }

        scheduler.schedule(() -> server.execute(() -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.sendSystemMessage(Component.literal("§7Initiating §bMythos.exe§7... §8[██████████] 100%"));
                playSound(player, SoundEvents.EXPERIENCE_ORB_PICKUP, 0.8f, 1.0f);
                playSound(player, SoundEvents.IRON_TRAPDOOR_CLOSE, 0.5f, 2.0f);
            }
        }), 1500, TimeUnit.MILLISECONDS);

        scheduler.schedule(() -> server.execute(() -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                MythosNetwork.sendToPlayer(new ShaderPacket("none", 1.0f, 1.0f, 1.0f), player);
                player.sendSystemMessage(Component.literal("§d§oWelcome to this reality."));

                playSound(player, SoundEvents.BEACON_ACTIVATE, 1.0f, 1.0f);
                playSound(player, SoundEvents.PLAYER_LEVELUP, 0.5f, 0.8f);
                playSound(player, SoundEvents.ENDER_EYE_DEATH, 0.7f, 1.2f);
            }
        }), 3500, TimeUnit.MILLISECONDS);
    }

    private static void playSound(ServerPlayer player, net.minecraft.sounds.SoundEvent sound, float volume, float pitch) {
        player.playNotifySound(sound, net.minecraft.sounds.SoundSource.PLAYERS, volume, pitch);
    }
}