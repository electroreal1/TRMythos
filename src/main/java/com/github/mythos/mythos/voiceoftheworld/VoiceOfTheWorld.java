package com.github.mythos.mythos.voiceoftheworld;

import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.mythos.mythos.config.MythosSkillsConfig;
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
import java.util.PriorityQueue;
import java.util.UUID;

public class VoiceOfTheWorld {

    private static final String PREFIX = "§8<< §bVoice of the World §8>> §f";

    private static final String TDL_COLOR = "§4";    // Dark Red
    private static final String HERO_COLOR = "§6";   // Golden Yellow
    private static final String EGG_COLOR = "§e";    // Pale Yellow
    private static final String SEED_COLOR = "§c";   // Pale Red
    private static final String HARVEST_COLOR = "§5"; // Dark Purple
    private static final PriorityQueue<QueuedMessage> MESSAGE_QUEUE = new PriorityQueue<>();
    private static int ticksUntilNextMessage = 0;

    public enum Priority {
        WORLD(0),      // Highest
        ACQUISITION(1),
        PROGRESS(2);    // Lowest

        final int level;
        Priority(int level) { this.level = level; }
    }

    private record QueuedMessage(UUID playerUUID, String message, Priority priority, long timestamp)
            implements Comparable<QueuedMessage> {
        @Override
        public int compareTo(QueuedMessage other) {
            if (this.priority != other.priority) {
                return Integer.compare(this.priority.level, other.priority.level);
            }
            return Long.compare(this.timestamp, other.timestamp);
        }
    }

    public static void tickQueue(MinecraftServer server) {
        if (ticksUntilNextMessage > 0) {
            ticksUntilNextMessage--;
            return;
        }

        if (!MESSAGE_QUEUE.isEmpty()) {
            QueuedMessage next = MESSAGE_QUEUE.poll();
            ServerPlayer player = server.getPlayerList().getPlayer(next.playerUUID());

            if (player != null) {
                announceToPlayer(player, next.message());
                ticksUntilNextMessage = 60;
            }
        }
    }

    public static void delayedAnnouncement(ServerPlayer player, Priority priority, String... messages) {
        if (!MythosSkillsConfig.voice_of_the_world.get()) return;
        if (player == null) return;
        long time = System.currentTimeMillis();
        for (String msg : messages) {
            MESSAGE_QUEUE.add(new QueuedMessage(player.getUUID(), msg, priority, time++));
        }
    }

    public static void delayedAnnouncement(ServerPlayer player, String... messages) {
        if (!MythosSkillsConfig.voice_of_the_world.get()) return;
        delayedAnnouncement(player, Priority.PROGRESS, messages);
    }


    public static void broadcast(MinecraftServer server, String message) {
        if (!MythosSkillsConfig.voice_of_the_world.get()) return;
        Component text = Component.literal(PREFIX + message);
        server.getPlayerList().getPlayers().forEach(player -> {
            player.sendSystemMessage(text);
            player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER, 1.0f, 0.5f);
        });
    }

    public static void announceToPlayer(Player player, String message) {
        if (!MythosSkillsConfig.voice_of_the_world.get()) return;
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.literal(PREFIX + message));
            serverPlayer.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.MASTER, 1.0f, 0.5f);
        }
    }

    public static void screenShake(ServerPlayer player, float intensity, int durationTicks) {
        if (!MythosSkillsConfig.voice_of_the_world.get()) return;
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
        if (!MythosSkillsConfig.voice_of_the_world.get()) return;
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
        VoiceOfTheWorld.delayedAnnouncement(player, Priority.ACQUISITION,
                "Confirmed.",
                "Condition met. Individual: " + player.getName().getString() + " has acquired the [" + SEED_COLOR + "Demon Lord Seed" + "§f].",
                "The path to the [" + HARVEST_COLOR + "Harvest Festival" + "§f] is now accessible."
        );
        player.playNotifySound(SoundEvents.WITHER_SPAWN, SoundSource.MASTER, 0.4f, 0.5f);
    }

    private static void triggerEggAcquisition(ServerPlayer player) {
        VoiceOfTheWorld.delayedAnnouncement(player, Priority.ACQUISITION,
                "Confirmed.",
                "The [" + EGG_COLOR + "Hero's Egg " + "§f] has been manifested within the soul.",
                "Fulfill your duties to hatch the potential of a [" + HERO_COLOR + "True Hero" + "§f]."
        );
        player.playNotifySound(SoundEvents.BEACON_ACTIVATE, SoundSource.MASTER, 0.6f, 1.2f);
    }

    public static void checkAwakeningStatus(ServerPlayer player) {
        if (!MythosSkillsConfig.voice_of_the_world.get()) return;
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
                "Notice. Individual: " + player.getName().getString() + " has begun the [" + HARVEST_COLOR + "Harvest Festival" + "§f].");

        VoiceOfTheWorld.delayedAnnouncement(player, Priority.WORLD,
                "Requirement fulfilled. Soul transition initiated.",
                "Beginning gift distribution to subordinates...",
                "Successful. Evolution to [" + TDL_COLOR + "True Demon Lord" + "§f] is complete."
        );

        VoiceOfTheWorld.screenShake(player, 1.5f, 100); // Intensity reduced from 100 (which is extreme) to 1.5
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400, 0, false, false));
        player.playNotifySound(SoundEvents.END_PORTAL_SPAWN, SoundSource.MASTER, 1.0f, 0.8f);
    }

    private static void triggerHeroAwakening(ServerPlayer player) {
        VoiceOfTheWorld.broadcast(Objects.requireNonNull(player.getServer()),
                "Announcement. The [" + EGG_COLOR + "Hero's Egg" + "§f] has hatched. A new [" + HERO_COLOR + "True Hero" + "§f] has been born.");

        VoiceOfTheWorld.screenShake(player, 1.5f, 100);
        player.playNotifySound(SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.MASTER, 0.5f, 1.5f);

        ServerLevel level = player.getLevel();
        level.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 1, player.getZ(), 20, 0.5, 0.5, 0.5, 0.0);
    }

    public static void welcomeNewIndividual(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            delayedAnnouncement(serverPlayer, Priority.PROGRESS,
                    "Notice.",
                    "New Individual detected within the world.",
                    "Registering soul signature...",
                    "Successful. Welcome to the world, " + player.getName().getString() + "."
            );
        }
    }
}
