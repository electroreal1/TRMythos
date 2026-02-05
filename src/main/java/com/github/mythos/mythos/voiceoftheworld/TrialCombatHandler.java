package com.github.mythos.mythos.voiceoftheworld;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "trmythos")
public class TrialCombatHandler {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            LivingEntity victim = event.getEntity();
            String activeID = TrialManager.getActiveTrialID(player);

            if (victim.getMaxHealth() >= (player.getMaxHealth() * 10)) {
                process(player, "giant_slayer", 1);
            }

            if ("pacifist".equals(activeID)) {
                if (victim instanceof net.minecraft.world.entity.npc.Villager || victim instanceof ServerPlayer) {
                    player.getPersistentData().putInt("Trial_Progress_pacifist", 0);

                    VoiceOfTheWorld.delayedAnnouncement(player, "Notice.",
                            "§cConfirmed. Pacifist vows broken.",
                            "Soul core destabilized due to hostile intent.",
                            "Trial progress reset to zero.");

                    player.playNotifySound(net.minecraft.sounds.SoundEvents.WITHER_SPAWN,
                            net.minecraft.sounds.SoundSource.MASTER, 1.0f, 0.5f);
                }
            }
        }
    }

    private static void process(ServerPlayer player, String id, int progress) {
        WorldTrial trial = WorldTrialRegistry.TRIALS.get(id);
        if (trial != null) trial.checkProgress(player, progress);
    }
}
