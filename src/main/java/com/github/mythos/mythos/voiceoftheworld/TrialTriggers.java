package com.github.mythos.mythos.voiceoftheworld;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "mythos")
public class TrialTriggers {

    @SubscribeEvent
    public static void onEntityKill(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            LivingEntity victim = event.getEntity();

            if (victim.getMaxHealth() >= (player.getMaxHealth() * 10)) {
                WorldTrialRegistry.TRIALS.get("giant_slayer").checkProgress(player, 1);
            }

            if (victim instanceof ServerPlayer || victim.getType().toString().contains("villager")) {
                player.getPersistentData().putInt("Trial_Progress_pacifist", 0);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            ServerPlayer player = (ServerPlayer) event.player;

            if (player.getY() <= -100000) {
                if (WorldTrialRegistry.TRIALS.containsKey("void_walker")) {
                    WorldTrialRegistry.TRIALS.get("void_walker").checkProgress(player, 1);
                }
            }

        }
    }
}
