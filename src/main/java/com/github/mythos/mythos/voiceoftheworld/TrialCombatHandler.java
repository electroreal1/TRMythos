package com.github.mythos.mythos.voiceoftheworld;

import com.github.mythos.mythos.config.MythosSkillsConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "trmythos")
public class TrialCombatHandler {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!MythosSkillsConfig.voice_of_the_world.get()) return;
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            LivingEntity victim = event.getEntity();
            String activeID = TrialManager.getActiveTrialID(player);

            if (activeID.isEmpty()) {
                if (isCalamityEntity(player, victim)) {
                    process(player, "giant_slayer", 1);
                }
                return;
            }

            WorldTrial activeTrial = WorldTrialRegistry.TRIALS.get(activeID);
            if (activeTrial == null) return;

            CompoundTag tag = player.getPersistentData();
            String progKey = "Trial_Progress_" + activeID;

            if (activeTrial.hasType(WorldTrial.TrialType.KILL)) {
                if (isCalamityEntity(player, victim)) {
                    int current = tag.getInt(progKey) + 1;
                    tag.putInt(progKey, current);
                    activeTrial.checkProgress(player, current);
                }
            }

            if (activeTrial.hasType(WorldTrial.TrialType.PASSIVE)) {
                if (victim instanceof Villager || victim instanceof ServerPlayer) {
                    tag.putInt(progKey, 0);

                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1200, 2));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 1));

                    VoiceOfTheWorld.delayedAnnouncement(player, VoiceOfTheWorld.Priority.ACQUISITION,
                            "Notice.",
                            "§cConfirmed. Vows broken.",
                            "Spiritual Backlash detected. Soul core destabilized.",
                            "Trial progress reset to zero.");

                    player.playNotifySound(SoundEvents.WITHER_SPAWN, SoundSource.MASTER, 0.8f, 0.5f);
                }
            }
        }
    }

    private static boolean isCalamityEntity(ServerPlayer player, LivingEntity victim) {
        return victim.getMaxHealth() >= (player.getMaxHealth() * 10);
    }

    private static void process(ServerPlayer player, String id, int progress) {
        WorldTrial trial = WorldTrialRegistry.TRIALS.get(id);
        if (trial != null) {
            trial.checkProgress(player, progress);
        }
    }
}