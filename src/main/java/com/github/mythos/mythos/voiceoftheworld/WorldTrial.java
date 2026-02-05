package com.github.mythos.mythos.voiceoftheworld;

import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.function.Consumer;

public class WorldTrial {
    private final String id;
    private final String name;
    private final int requirement;
    private final int epReward;
    private final transient Consumer<ServerPlayer> onComplete;

    public WorldTrial(String id, String name, int requirement, int epReward, Consumer<ServerPlayer> onComplete) {
        this.id = id;
        this.name = name;
        this.requirement = requirement;
        this.epReward = epReward;
        this.onComplete = onComplete;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void checkProgress(ServerPlayer player, int current) {
        CompoundTag tag = player.getPersistentData();

        String completionKey = "Trial_Complete_" + this.id;
        if (tag.getBoolean(completionKey)) return;

        String activeKey = "Trial_Active_" + this.id;
        if (!tag.contains(activeKey)) {
            tag.putBoolean(activeKey, true);
            VoiceOfTheWorld.delayedAnnouncement(player, "Notice.", "Trial: [" + name + "] initiated.");
        }

        if (current < requirement) {
            VoiceOfTheWorld.announceToPlayer(player,
                    "Calamity Entity suppressed. Progress: [" + current + "/" + requirement + "].");

            player.playNotifySound(SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.MASTER, 1.0f, 0.5f);
        } else  {
            tag.putBoolean(completionKey, true);

            double Magicules = TensuraPlayerCapability.getBaseMagicule(player);
            double Aura = TensuraPlayerCapability.getBaseAura(player);

            TensuraPlayerCapability.setMagicule(player, Magicules + ((double) this.epReward / 2));
            TensuraPlayerCapability.setAura(player, Aura + ((double) this.epReward / 2));

            VoiceOfTheWorld.screenShake(player, 1.0f, 20);

            VoiceOfTheWorld.delayedAnnouncement(player,
                    "Notice.",
                    "Trial: [" + name + "] cleared.",
                    "Confirmed. Existence Points increased by §a" + String.format("%,d", epReward) + " EP§f."
            );

            onComplete.accept(player);
        }
    }
}
