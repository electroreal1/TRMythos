package com.github.mythos.mythos.voiceoftheworld;

import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "trmythos")
public class TrialTickHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            ServerPlayer player = (ServerPlayer) event.player;
            String activeID = TrialManager.getActiveTrialID(player);
            CompoundTag tag = player.getPersistentData();

            double ep = TensuraPlayerCapability.getBaseMagicule(player) + TensuraPlayerCapability.getBaseAura(player);
            if (ep >= 1000000000L) {
                process(player, "stability", 1);
            }

            if (player.getY() <= -10000) {
                process(player, "void_walker", 1);
            }

            if (activeID.isEmpty()) return;
            WorldTrial trial = WorldTrialRegistry.TRIALS.get(activeID);
            if (trial == null) return;

            String progKey = "Trial_Progress_" + activeID;
            boolean requirementsMet = true;

            if (trial.hasType(WorldTrial.TrialType.DIMENSION)) {
                if (!player.level.dimension().location().getPath().contains(trial.getMetadata())) {
                    requirementsMet = false;
                }
            }

            if (trial.hasType(WorldTrial.TrialType.Y_LEVEL)) {
                try {
                    double targetY = Double.parseDouble(trial.getMetadata());
                    if (player.getY() > targetY) requirementsMet = false;
                } catch (NumberFormatException e) {
                    requirementsMet = false;
                }
            }

            if (requirementsMet) {
                if (trial.hasType(WorldTrial.TrialType.STILLNESS)) {
                    if (player.getDeltaMovement().lengthSqr() < 0.001) {
                        incrementAndProcess(player, trial, tag, progKey);
                    } else if (tag.getInt(progKey) > 0) {
                        tag.putInt(progKey, 0);
                        VoiceOfTheWorld.delayedAnnouncement(player, VoiceOfTheWorld.Priority.ACQUISITION,
                                "Notice.", "§cMovement detected.", "Meditation interrupted. Progress reset.");
                        player.playNotifySound(SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.MASTER, 1.0f, 1.0f);
                    }
                }
                else if (trial.hasType(WorldTrial.TrialType.PASSIVE) ||
                        trial.hasType(WorldTrial.TrialType.DIMENSION) ||
                        trial.hasType(WorldTrial.TrialType.Y_LEVEL)) {
                    incrementAndProcess(player, trial, tag, progKey);
                }
            }
        }
    }

    private static void incrementAndProcess(ServerPlayer player, WorldTrial trial, CompoundTag tag, String progKey) {
        int prog = tag.getInt(progKey) + 1;
        tag.putInt(progKey, prog);
        trial.checkProgress(player, prog);
    }

    private static void process(ServerPlayer player, String id, int progress) {
        WorldTrial trial = WorldTrialRegistry.TRIALS.get(id);
        if (trial != null) trial.checkProgress(player, progress);
    }
}