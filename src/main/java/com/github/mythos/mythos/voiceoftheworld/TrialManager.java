package com.github.mythos.mythos.voiceoftheworld;

import com.github.mythos.mythos.config.MythosSkillsConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.EnumSet;

public class TrialManager {
    private static final String ACTIVE_TRIAL_KEY = "Mythos_ActiveTrialID";
    private static boolean trialsPaused = false;

    public static boolean isPaused() {
        return trialsPaused;
    }

    public static void setPaused(boolean state) {
        trialsPaused = state;
    }

    public static boolean hasActiveTrial(ServerPlayer player) {
        return !getActiveTrialID(player).isEmpty();
    }

    public static String getActiveTrialID(ServerPlayer player) {
        return player.getPersistentData().getString(ACTIVE_TRIAL_KEY);
    }

    public static void initiateTrial(ServerPlayer player, String trialID) {
        if (!MythosSkillsConfig.voice_of_the_world.get()) return;
        if (player.level.isClientSide()) return;

        if (hasActiveTrial(player)) return;

        CompoundTag tag = player.getPersistentData();

        if (tag.getBoolean("Trial_Complete_" + trialID)) return;

        tag.putString(ACTIVE_TRIAL_KEY, trialID);

        WorldTrial trial = WorldTrialRegistry.TRIALS.get(trialID);
        if (trial != null) {
            VoiceOfTheWorld.delayedAnnouncement(player, VoiceOfTheWorld.Priority.ACQUISITION,
                    "Notice.",
                    "World Trial detected: [" + trial.getName() + "].",
                    "Conditions met. Initiation successful.",
                    "Recording progress within the soul core...");
        }

    }

    public static void init() {
        WorldTrialRegistry.register(new WorldTrial("giant_slayer", "Trial of the Giant Slayer",
                EnumSet.of(WorldTrial.TrialType.KILL), 5, 1500, "", p ->
                VoiceOfTheWorld.delayedAnnouncement(p, VoiceOfTheWorld.Priority.ACQUISITION, "Notice.",
                        "Confirmed. Concept of 'Size' has been surpassed.",
                        "Trial Completed.")));

        WorldTrialRegistry.register(new WorldTrial("breather", "Trial of the Magicule Breather",
                EnumSet.of(WorldTrial.TrialType.DIMENSION), 144000, 20000, "labyrinth", p ->
                VoiceOfTheWorld.delayedAnnouncement(p, VoiceOfTheWorld.Priority.ACQUISITION, "Notice.",
                        "Confirmed. Respiratory system has adapted to high-density environments.",
                        "Trial Completed.")));

        WorldTrialRegistry.register(new WorldTrial("pacifist", "Trial of the Pacifist",
                EnumSet.of(WorldTrial.TrialType.PASSIVE), 240000, 30000, "", p ->
                VoiceOfTheWorld.delayedAnnouncement(p, VoiceOfTheWorld.Priority.ACQUISITION, "Notice.",
                        "Confirmed. Inner peace has stabilized the spiritual body.",
                        "Trial Completed.")));

        WorldTrialRegistry.register(new WorldTrial("stability", "Trial of the Soul Stability",
                EnumSet.of(WorldTrial.TrialType.EP_THRESHOLD), 1, 10000, "", p ->
                VoiceOfTheWorld.delayedAnnouncement(p, VoiceOfTheWorld.Priority.ACQUISITION, "Notice.",
                        "Confirmed. Spiritual threshold exceeded.",
                        "Trial Completed.")));

        WorldTrialRegistry.register(new WorldTrial("void_walker", "Trial of the Void Walker",
                EnumSet.of(WorldTrial.TrialType.Y_LEVEL), 1, 5000, "-10000", p ->
                VoiceOfTheWorld.delayedAnnouncement(p, VoiceOfTheWorld.Priority.ACQUISITION, "Notice.",
                        "Confirmed. Survival in the Non-Existent Space verified.",
                        "Trial Completed.")));

        WorldTrialRegistry.register(new WorldTrial("observer", "Trial of the World Observer",
                EnumSet.of(WorldTrial.TrialType.STILLNESS), 144000, 4000, "", p ->
                VoiceOfTheWorld.delayedAnnouncement(p, VoiceOfTheWorld.Priority.ACQUISITION, "Notice.",
                        "Confirmed. Ego has remained static while mind accelerated.")));
    }

    public static void clearActiveTrial(ServerPlayer player) {
        player.getPersistentData().remove(ACTIVE_TRIAL_KEY);
    }
}