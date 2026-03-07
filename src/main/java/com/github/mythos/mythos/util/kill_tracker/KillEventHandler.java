package com.github.mythos.mythos.util.kill_tracker;

import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "trmythos")
public class KillEventHandler {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player killer && event.getEntity() instanceof Player victim) {
            boolean isAwakened = TensuraPlayerCapability.isTrueDemonLord(victim) || TensuraPlayerCapability.isTrueHero(victim);

            if (isAwakened) {
                killer.getCapability(KillTrackerProvider.CAPABILITY).ifPresent(IKillTracker::addAwakenedKills);
            }

        }
    }
}
