package com.github.mythos.mythos.voiceoftheworld;

import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.Set;
import java.util.function.Consumer;

public class WorldTrial {
    private final String id;
    private final String name;
    private final int requirement;
    private final int epReward;
    private final Set<TrialType> types;
    private final String metadata;
    private final Consumer<ServerPlayer> onComplete;

    public WorldTrial(String id, String name, Set<TrialType> types, int requirement, int epReward, String metadata, Consumer<ServerPlayer> onComplete) {
        this.id = id;
        this.name = name;
        this.types = types;
        this.requirement = requirement;
        this.epReward = epReward;
        this.metadata = metadata;
        this.onComplete = onComplete;
    }

    public enum TrialType {
        KILL,
        STILLNESS,
        DIMENSION,
        EP_THRESHOLD,
        Y_LEVEL,
        PASSIVE
    }

    public boolean hasType(TrialType type) {
        return types.contains(type);
    }

    public String getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public int getRequirement() {
        return this.requirement;
    }
    public String  getMetadata() {
        return this.metadata;
    }

    public void checkProgress(ServerPlayer player, int current) {
        if (!MythosSkillsConfig.voice_of_the_world.get()) return;
        CompoundTag tag = player.getPersistentData();
        String activeTrial = TrialManager.getActiveTrialID(player);

        if (!activeTrial.isEmpty() && !activeTrial.equals(this.id)) return;

        if (tag.getBoolean("Trial_Complete_" + this.id)) return;

        if (activeTrial.isEmpty()) {
            if (TrialManager.initiateTrial(player, this.id)) {
                VoiceOfTheWorld.delayedAnnouncement(player, "Notice.", "Hidden Condition met.", "Trial: [" + name + "] initiated.");
            } else return;
        }

        if (current < requirement) {
            if (requirement < 100 || current % (requirement / 100) == 0 || current == 1) {
                String progressStr = formatRequirement(current) + " / " + formatRequirement(requirement);
                VoiceOfTheWorld.announceToPlayer(player, "Progress: [" + progressStr + "].");
                player.playNotifySound(SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.MASTER, 0.5f, 0.5f);
            }
        } else {
            tag.putBoolean("Trial_Complete_" + this.id, true);

            double mag = TensuraPlayerCapability.getBaseMagicule(player);
            double aura = TensuraPlayerCapability.getBaseAura(player);
            TensuraPlayerCapability.setMagicule(player, mag + ((double) epReward / 2));
            TensuraPlayerCapability.setAura(player, aura + ((double) epReward / 2));

            VoiceOfTheWorld.screenShake(player, 1.0f, 40);
            TrialManager.clearActiveTrial(player);

            VoiceOfTheWorld.delayedAnnouncement(player, "Notice.", "Trial: [" + name + "] cleared.");
            onComplete.accept(player);
        }
    }


     // Formats ticks into human-readable time strings, or raw numbers for kills.

    String formatRequirement(int value) {
        if (id.equals("observer") || id.equals("breather") || id.equals("pacifist")) {
            int seconds = value / 20;
            if (seconds < 60) return seconds + "s";
            int minutes = seconds / 60;
            if (minutes < 60) return minutes + "m " + (seconds % 60) + "s";
            int hours = minutes / 60;
            return hours + "h " + (minutes % 60) + "m";
        }
        return String.valueOf(value);
    }
}