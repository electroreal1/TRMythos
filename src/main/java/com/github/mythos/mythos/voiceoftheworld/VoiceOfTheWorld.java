package com.github.mythos.mythos.voiceoftheworld;

import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.mythos.mythos.networking.MythosNetwork;
import com.github.mythos.mythos.networking.play2server.ScreenShakePacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

import java.util.Objects;

public class VoiceOfTheWorld {

    private static final String PREFIX = "§8<< §bVoice of the World §8>> §f";

    private static final String TDL_COLOR = "§4";    // Dark Red
    private static final String HERO_COLOR = "§6";   // Golden Yellow
    private static final String EGG_COLOR = "§e";    // Pale Yellow
    private static final String SEED_COLOR = "§c";   // Pale Red
    private static final String HARVEST_COLOR = "§5"; // Dark Purple

    public static void broadcast(MinecraftServer server, String message) {
        Component text = Component.literal(PREFIX + message);
        server.getPlayerList().getPlayers().forEach(player -> {
            player.sendSystemMessage(text);
            player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER, 1.0f, 0.5f);
        });
    }

    public static void announceToPlayer(Player player, String message) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.literal(PREFIX + message));
            serverPlayer.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.MASTER, 1.0f, 0.5f);
        }
    }

    public static void delayedAnnouncement(ServerPlayer player, String... messages) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        for (int i = 0; i < messages.length; i++) {
            int delay = i * 30;
            String msg = messages[i];

            server.tell(new TickTask(server.getTickCount() + delay, () -> {
                if (player.isAlive()) {
                    announceToPlayer(player, msg);
                }
            }));
        }
    }

    public static void screenShake(ServerPlayer player, float intensity, int durationTicks) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        MythosNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                new ScreenShakePacket(intensity));

        server.tell(new TickTask(server.getTickCount() + durationTicks, () -> {
            if (player.isAlive()) {
                MythosNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                        new ScreenShakePacket(0));
            }
        }));
    }

    public static void checkHeroEggOrDemonLordSeed(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();

        if (TensuraPlayerCapability.isDemonLordSeed(player) && !tag.getBoolean("Mythos_SeedNotified")) {
            triggerSeedAcquisition(player);
            tag.putBoolean("Mythos_SeedNotified", true);
        }

        if (TensuraPlayerCapability.isHeroEgg(player) && !tag.getBoolean("Mythos_EggNotified")) {
            triggerEggAcquisition(player);
            tag.putBoolean("Mythos_EggNotified", true);
        }
    }

    private static void triggerSeedAcquisition(ServerPlayer player) {
        VoiceOfTheWorld.delayedAnnouncement(player,
                "Confirmed.",
                "Condition met. Individual: " + player.getName().getString() + " has acquired the " + SEED_COLOR + "[Demon Lord Seed].",
                "The path to the " + HARVEST_COLOR + "[Harvest Festival] " + "§fis now accessible."
        );
        player.playNotifySound(SoundEvents.WITHER_SPAWN, SoundSource.MASTER, 0.4f, 0.5f);
    }

    private static void triggerEggAcquisition(ServerPlayer player) {
        VoiceOfTheWorld.delayedAnnouncement(player,
                "Confirmed.",
                "The " + EGG_COLOR + "[Hero's Egg] " + "§fhas been manifested within the soul.",
                "Fulfill your duties to hatch the potential of a " + HERO_COLOR + "[True Hero]."
        );
        player.playNotifySound(SoundEvents.BEACON_ACTIVATE, SoundSource.MASTER, 0.6f, 1.2f);
    }

    public static void checkAwakeningStatus(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();

        boolean isTDL = TensuraPlayerCapability.isTrueDemonLord(player);
        if (isTDL && !tag.getBoolean("Mythos_AcknowledgedTDL")) {
            triggerHarvestFestival(player);
            tag.putBoolean("Mythos_AcknowledgedTDL", true);
        }

        boolean isHero = TensuraPlayerCapability.isTrueHero(player);
        if (isHero && !tag.getBoolean("Mythos_AcknowledgedHero")) {
            triggerHeroAwakening(player);
            tag.putBoolean("Mythos_AcknowledgedHero", true);
        }
    }

    private static void triggerHarvestFestival(ServerPlayer player) {
        VoiceOfTheWorld.broadcast(Objects.requireNonNull(player.getServer()),
                "Notice. Individual: " + player.getName().getString() + " has begun the " + HARVEST_COLOR + "[Harvest Festival].");

        VoiceOfTheWorld.delayedAnnouncement(player,
                "Requirement fulfilled. Soul transition initiated.",
                "Beginning gift distribution to subordinates...",
                "Successful. Evolution to " + TDL_COLOR + "[True Demon Lord] " + "§fis complete."
        );

        VoiceOfTheWorld.screenShake(player, 1.5f, 100); // Intensity reduced from 100 (which is extreme) to 1.5
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400, 0, false, false));
        player.playNotifySound(SoundEvents.END_PORTAL_SPAWN, SoundSource.MASTER, 1.0f, 0.8f);
    }

    private static void triggerHeroAwakening(ServerPlayer player) {
        VoiceOfTheWorld.broadcast(Objects.requireNonNull(player.getServer()),
                "Announcement. The " + EGG_COLOR + "[Hero's Egg] " + "§fhas hatched. A new " + HERO_COLOR + "[True Hero] " + "§fhas been birthed.");

        VoiceOfTheWorld.screenShake(player, 1.5f, 100);
        player.playNotifySound(SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.MASTER, 0.5f, 1.5f);

        ServerLevel level = player.getLevel();
        level.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 1, player.getZ(), 20, 0.5, 0.5, 0.5, 0.0);
    }

    public static void welcomeNewIndividual(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            delayedAnnouncement(serverPlayer,
                    "Notice.",
                    "New Individual detected within the world.",
                    "Registering soul signature...",
                    "Successful. Welcome to the world, " + player.getName().getString() + "."
            );
        }
    }
}
