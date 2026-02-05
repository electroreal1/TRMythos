package com.github.mythos.mythos.voiceoftheworld;

import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
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

            if (player.getY() <= -100000) {
                process(player, "void_walker", 1);
            }

            double ep = TensuraPlayerCapability.getBaseMagicule(player) + TensuraPlayerCapability.getBaseAura(player);
            if (ep >= 1000000000L) {
                process(player, "stability", 1);
            }

            if ("observer".equals(activeID)) {
                if (player.getDeltaMovement().lengthSqr() < 0.0001) {
                    incrementAndProcess(player, "observer", tag);
                }
            }

            if ("breather".equals(activeID)) {
                String dim = player.level.dimension().location().getPath();
                if (dim.contains("labyrinth") || dim.contains("hell")) {
                    incrementAndProcess(player, "breather", tag);
                }
            }

            if ("pacifist".equals(activeID)) {
                incrementAndProcess(player, "pacifist", tag);
            }
        }
    }

    private static void incrementAndProcess(ServerPlayer player, String id, CompoundTag tag) {
        int prog = tag.getInt("Trial_Progress_" + id) + 1;
        tag.putInt("Trial_Progress_" + id, prog);
        process(player, id, prog);
    }

    private static void process(ServerPlayer player, String id, int progress) {
        WorldTrial trial = WorldTrialRegistry.TRIALS.get(id);
        if (trial != null) trial.checkProgress(player, progress);
    }
}