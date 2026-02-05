package com.github.mythos.mythos.voiceoftheworld;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import java.util.LinkedHashMap;
import java.util.Map;

public class WorldTrialRegistry {
    public static final Map<String, WorldTrial> TRIALS = new LinkedHashMap<>();

    public static void register(WorldTrial trial) {
        TRIALS.put(trial.getId(), trial);
    }

    public static String getActiveTrialStatus(Player player) {
        String activeID = TrialManager.getActiveTrialID((net.minecraft.server.level.ServerPlayer) player);

        if (activeID.isEmpty()) {
            return "None.";
        }

        WorldTrial trial = TRIALS.get(activeID);
        if (trial == null) return "Unknown Trial Active (" + activeID + ")";

        CompoundTag tag = player.getPersistentData();
        int progress = tag.getInt("Trial_Progress_" + activeID);

        String currentStr = trial.formatRequirement(progress);
        String maxStr = trial.formatRequirement(trial.getRequirement());

        return "§eCurrent Trial: §f" + trial.getName() + " §7[" + currentStr + " / " + maxStr + "]";
    }
}