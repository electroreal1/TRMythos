package com.github.mythos.mythos.voiceoftheworld;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class TrialManager {
    private static final Map<String, WorldTrial> REGISTERED_TRIALS = new HashMap<>();
    private static final String ACTIVE_TRIAL_KEY = "Mythos_ActiveTrialID";

    public static void registerTrial(WorldTrial trial) {
        REGISTERED_TRIALS.put(trial.getId(), trial);
    }

    public static boolean hasActiveTrial(ServerPlayer player) {
        return player.getPersistentData().contains(ACTIVE_TRIAL_KEY);
    }

    public static String getActiveTrialID(ServerPlayer player) {
        return player.getPersistentData().getString(ACTIVE_TRIAL_KEY);
    }

    public static boolean initiateTrial(ServerPlayer player, String trialID) {
        if (hasActiveTrial(player)) return false;

        CompoundTag tag = player.getPersistentData();
        if (tag.getBoolean("Trial_Complete_" + trialID)) return false;

        tag.putString(ACTIVE_TRIAL_KEY, trialID);
        return true;
    }

    public static void clearActiveTrial(ServerPlayer player) {
        player.getPersistentData().remove(ACTIVE_TRIAL_KEY);
    }
}
