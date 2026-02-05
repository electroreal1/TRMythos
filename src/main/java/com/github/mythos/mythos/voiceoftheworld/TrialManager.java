package com.github.mythos.mythos.voiceoftheworld;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

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

    public static boolean initiateTrial(ServerPlayer player, String trialID) {
        if (player.level.isClientSide()) return false;

        if (hasActiveTrial(player)) return false;

        CompoundTag tag = player.getPersistentData();

        if (tag.getBoolean("Trial_Complete_" + trialID)) return false;

        tag.putString(ACTIVE_TRIAL_KEY, trialID);

        WorldTrial trial = WorldTrialRegistry.TRIALS.get(trialID);
        if (trial != null) {
            VoiceOfTheWorld.delayedAnnouncement(player, VoiceOfTheWorld.Priority.ACQUISITION,
                    "Notice.",
                    "World Trial detected: [" + trial.getName() + "].",
                    "Conditions met. Initiation successful.",
                    "Recording progress within the soul core...");
        }

        return true;
    }

    public static void clearActiveTrial(ServerPlayer player) {
        player.getPersistentData().remove(ACTIVE_TRIAL_KEY);
    }
}