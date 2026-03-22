package com.github.mythos.mythos.handler;

import com.github.mythos.mythos.networking.MythosNetwork;
import com.github.mythos.mythos.networking.play2server.ShaderPacket;
import com.github.mythos.mythos.networking.play2server.WhiteoutPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = "trmythos")
public class CustomJoinMessageHandler {

    private static final Map<UUID, Consumer<ServerPlayer>> PROFILES = new HashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    public static final UUID ELECTRO = UUID.fromString("e313811f-6b1c-4aea-8211-0aaa4f9adb11");
    public static final UUID PRIMORDIAL_ROSE = UUID.fromString("7ee73300-fb30-4ed6-8cac-5d2ee3be2046");
    public static final UUID TERRACHARM = UUID.fromString("3c930a59-4d3d-4e4f-b62b-2f71073e1bbb");
    public static final UUID ARGON = UUID.fromString("0f8fc498-6cc0-4e1f-8769-4ae33cbb4a1f");
    public static final UUID HALLOW = UUID.fromString("7bd51cab-cb84-4ecf-a14b-38862fcdad21");
    public static final UUID SMOKE = UUID.fromString("bdd89345-7679-45ed-a040-27c14d67c504");

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Consumer<ServerPlayer> action = PROFILES.get(player.getUUID());
            if (action != null) {
                action.accept(player);
            }
        }
    }

    private static void registerProfile(UUID uuid, Consumer<ServerPlayer> action) {
        PROFILES.put(uuid, action);
    }

    private static void broadcastMessage(ServerPlayer trigger, Component message) {
        trigger.getServer().getPlayerList().getPlayers().forEach(p -> p.sendSystemMessage(message));
    }

    private static void broadcastSound(ServerPlayer trigger, SoundEvent sound, float vol, float pitch) {
        trigger.getServer().getPlayerList().getPlayers().forEach(p ->
                p.playNotifySound(sound, SoundSource.AMBIENT, vol, pitch));
    }

    private static void broadcastShader(ServerPlayer trigger, String shader, float r, float g, float b, int durationMs) {
        MinecraftServer server = trigger.getServer();
        server.getPlayerList().getPlayers().forEach(p ->
                MythosNetwork.sendToPlayer(new ShaderPacket(shader, r, g, b), p));

        scheduler.schedule(() -> server.execute(() ->
                server.getPlayerList().getPlayers().forEach(p ->
                        MythosNetwork.sendToPlayer(new ShaderPacket("none", 1.0f, 1.0f, 1.0f), p))
        ), durationMs, TimeUnit.MILLISECONDS);
    }

    private static void broadcastWhiteout(ServerPlayer trigger, float intensity, int durationMs) {
        MinecraftServer server = trigger.getServer();
        server.getPlayerList().getPlayers().forEach(p ->
                MythosNetwork.sendToPlayer(new WhiteoutPacket(intensity), p));

        scheduler.schedule(() -> server.execute(() ->
                server.getPlayerList().getPlayers().forEach(p ->
                        MythosNetwork.sendToPlayer(new WhiteoutPacket(0.0f), p))
        ), durationMs, TimeUnit.MILLISECONDS);
    }

    public static float[] hexToFloats(String hex) {
        if (hex.startsWith("#")) hex = hex.substring(1);
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return new float[]{ r / 255f, g / 255f, b / 255f };
    }

    public static float[] rbgToFloats(int r, int g, int b) {
        return new float[]{r / 255f, g / 255f, b / 255f};
    }

    static {
        registerProfile(ELECTRO, (p) -> {
            broadcastShader(p, "trmythos:shaders/post/master_sky.json", 0.1f, 0.8f, 1.0f, 3500);
            broadcastMessage(p, Component.literal("§c§l[!] ALERT: §fThe Multi-Dimensional Barrier Has Been Breached."));
            broadcastSound(p, SoundEvents.END_PORTAL_SPAWN, 1.0f, 0.5f);
            broadcastSound(p, SoundEvents.ZOMBIE_VILLAGER_CURE, 0.6f, 2.0f);
            broadcastSound(p, SoundEvents.ANVIL_LAND, 0.4f, 0.1f);

            scheduler.schedule(() -> p.getServer().execute(() -> {
                broadcastMessage(p, Component.literal("§7Initiating §bMythos.exe§7... §8[██████████] 100%"));
                broadcastSound(p, SoundEvents.EXPERIENCE_ORB_PICKUP, 0.8f, 1.0f);
                broadcastSound(p, SoundEvents.IRON_TRAPDOOR_CLOSE, 0.5f, 2.0f);
            }), 1500, TimeUnit.MILLISECONDS);

            scheduler.schedule(() -> p.getServer().execute(() -> {
                broadcastMessage(p, Component.literal("§d§oWelcome back to reality."));
                broadcastSound(p, SoundEvents.BEACON_ACTIVATE, 1.0f, 1.0f);
                broadcastSound(p, SoundEvents.PLAYER_LEVELUP, 0.5f, 0.8f);
                broadcastSound(p, SoundEvents.ENDER_EYE_DEATH, 0.7f, 1.2f);
            }), 3500, TimeUnit.MILLISECONDS);
        });

        registerProfile(HALLOW, (p) -> {
            broadcastShader(p, "trmythos:shaders/post/master_sky.json", 0.5f, 0.3f, 2.0f, 3500);
            broadcastMessage(p, Component.literal("§9No recompense,§b no salvation to be had.\n§5At the world's end, §0a bird sings of tomorrow..."));
            broadcastSound(p, SoundEvents.GHAST_WARN, 1.0f, 0.5f);
            broadcastSound(p, SoundEvents.BLAZE_BURN, 0.6f, 2.0f);
            broadcastSound(p, SoundEvents.CONDUIT_ACTIVATE, 0.4f, 0.1f);

            scheduler.schedule(() -> p.getServer().execute(() -> {
                broadcastMessage(p, Component.literal("§6This is an act of clemency."));
                broadcastSound(p, SoundEvents.BEACON_ACTIVATE, 1.0f, 1.0f);
                broadcastSound(p, SoundEvents.PLAYER_LEVELUP, 0.5f, 0.8f);
                broadcastSound(p, SoundEvents.ENDER_EYE_DEATH, 0.7f, 1.2f);
            }), 3500, TimeUnit.MILLISECONDS);
        });

        registerProfile(TERRACHARM, (p) ->
                broadcastShader(p, "trmythos:shaders/post/master_sky.json", 0.682f, 0.0f, 1.0f, 3500)
        );

        registerProfile(ARGON, (p) -> {
            broadcastWhiteout(p, 1.0f, 3500);
            broadcastMessage(p, Component.literal("Oh Vained white sand, heed me.").withStyle(ChatFormatting.WHITE));
            broadcastSound(p, SoundEvents.CREEPER_PRIMED, 1.0f, 1.0f);

            scheduler.schedule(() -> p.getServer().execute(() ->
                    broadcastMessage(p, Component.literal("The Serpent in white coils around sin again.").withStyle(ChatFormatting.DARK_RED))
            ), 3500, TimeUnit.MILLISECONDS);
        });

        registerProfile(SMOKE, (p) -> {
            broadcastMessage(p, Component.literal("I am the temptation of Truth").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
            float[] c = hexToFloats("#3C0061");
            broadcastShader(p, "trmythos:shaders/post/master_sky.json", c[0], c[1], c[2], 3500);
        });

        registerProfile(PRIMORDIAL_ROSE, (p) -> {
            broadcastShader(p, "trmythos:shaders/post/master_sky.json", 1.0f, 0.2f, 0.5f, 4000);
            broadcastSound(p, SoundEvents.END_PORTAL_SPAWN, 1.2f, 0.5f);
            broadcastSound(p, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, 0.8f, 1.5f);
            broadcastSound(p, SoundEvents.AMETHYST_BLOCK_CHIME, 1.0f, 0.1f);
            broadcastMessage(p, Component.literal("§fThe first and the last, the beginning and the end. I am what encompasses all."));

            scheduler.schedule(() -> p.getServer().execute(() ->
                    broadcastSound(p, SoundEvents.EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f)
            ), 4000, TimeUnit.MILLISECONDS);
        });
    }
}