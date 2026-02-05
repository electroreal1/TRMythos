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

    private static final String PREFIX = "§8<< §eVoice of the World §8>> §f";

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

    public static void screenShake(ServerPlayer player, float intensity) {
        MythosNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                new ScreenShakePacket(intensity));
    }

    public static void checkHeroEggOrDemonLordSeed(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();

        // DEMON LORD SEED CHECK
        if (TensuraPlayerCapability.isDemonLordSeed(player) && !tag.getBoolean("Mythos_SeedNotified")) {
            triggerSeedAcquisition(player);
            tag.putBoolean("Mythos_SeedNotified", true);
        }

        // HERO'S EGG CHECK
        if (TensuraPlayerCapability.isHeroEgg(player) && !tag.getBoolean("Mythos_EggNotified")) {
            triggerEggAcquisition(player);
            tag.putBoolean("Mythos_EggNotified", true);
        }
    }

    private static void triggerSeedAcquisition(ServerPlayer player) {
        VoiceOfTheWorld.delayedAnnouncement(player,
                "Confirmed.",
                "Condition met. Individual: " + player.getName().getString() + " has acquired the [Demon Lord Seed].",
                "The path to the [Harvest Festival] is now accessible."
        );
        player.playNotifySound(SoundEvents.WITHER_SPAWN, SoundSource.MASTER, 0.4f, 0.5f);
    }

    private static void triggerEggAcquisition(ServerPlayer player) {
        VoiceOfTheWorld.delayedAnnouncement(player,
                "Confirmed.",
                "The [Hero's Egg] has been manifested within the soul.",
                "Fulfill your duties to hatch the potential of a [True Hero]."
        );
        player.playNotifySound(SoundEvents.BEACON_ACTIVATE, SoundSource.MASTER, 0.6f, 1.2f);
    }

    public static void checkAwakeningStatus(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();

        // True Demon Lord Check
        boolean isTDL = TensuraPlayerCapability.isTrueDemonLord(player);
        if (isTDL && !tag.getBoolean("Mythos_AcknowledgedTDL")) {
            triggerHarvestFestival(player);
            tag.putBoolean("Mythos_AcknowledgedTDL", true);
        }

        // True Hero Check
        boolean isHero = TensuraPlayerCapability.isTrueHero(player);
        if (isHero && !tag.getBoolean("Mythos_AcknowledgedHero")) {
            triggerHeroAwakening(player);
            tag.putBoolean("Mythos_AcknowledgedHero", true);
        }
    }

    private static void triggerHarvestFestival(ServerPlayer player) {
        VoiceOfTheWorld.broadcast(Objects.requireNonNull(player.getServer()),
                "Notice. Individual: " + player.getName().getString() + " has begun the [Harvest Festival].");

        VoiceOfTheWorld.delayedAnnouncement(player,
                "Requirement fulfilled. Soul transition initiated.",
                "Beginning gift distribution to subordinates...",
                "Successful. Evolution to [True Demon Lord] is complete."
        );

        VoiceOfTheWorld.screenShake(player, 100);
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400, 0, false, false));
        player.playNotifySound(SoundEvents.END_PORTAL_SPAWN, SoundSource.MASTER, 1.0f, 0.8f);
    }

    private static void triggerHeroAwakening(ServerPlayer player) {
        VoiceOfTheWorld.broadcast(Objects.requireNonNull(player.getServer()),
                "Announcement. The [Hero's Egg] has hatched. A new [True Hero] has been birthed.");

        VoiceOfTheWorld.screenShake(player, 100);
        player.playNotifySound(SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.MASTER, 0.5f, 1.5f);

        ServerLevel level = player.getLevel();
        level.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 1, player.getZ(), 20, 0.5, 0.5, 0.5, 0.0);
    }

    public static void welcomeNewIndividual(Player player) {
        delayedAnnouncement((ServerPlayer) player,
                "Notice.",
                "New Individual detected within the world.",
                "Registering soul signature...",
                "Successful. Welcome to the world, " + player.getName().getString() + "."
        );
    }
}
