package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DarkDesireHandler {

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        ServerPlayer user = event.getPlayer();

        ManasSkillInstance instance = SkillUtils.getSkillOrNull(user, Skills.KHONSU.get());

        if (instance != null && instance.getOrCreateTag().getBoolean("DarkDesireListening")) {
            event.setMessage(Component.empty());

            String targetName = event.getRawText().trim();
            CompoundTag tag = instance.getOrCreateTag();

            if (user.getServer() == null) return;

            ServerPlayer victim = null;
            for (ServerPlayer p : user.getServer().getPlayerList().getPlayers()) {
                if (p.getName().getString().equalsIgnoreCase(targetName)) {
                    victim = p;
                    break;
                }
            }

            if (victim != null) {
                double userMP = TensuraEPCapability.getEP(user);
                double victimMP = TensuraEPCapability.getEP(victim);

                if (victimMP < (userMP * 0.8)) {
                    victim.die(EntityDamageSource.GENERIC);
                    victim.level.playSound(null, victim.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 1.0F, 0.1F);

                    instance.setMastery(instance.getSkill().getMaxMastery() / 2);
                    user.displayClientMessage(Component.literal("§0Desire fulfilled. §8" + targetName + " has been erased."), true);

                    tag.putBoolean("DarkDesireListening", false);
                    instance.setCoolDown(24000);
                } else {
                    user.displayClientMessage(Component.literal("§cTheir soul is too heavy for your desire to claim."), true);
                }
            } else {
                user.displayClientMessage(Component.literal("§7The void finds no one by that name."), true);
            }

            instance.markDirty();
        }
    }
}
