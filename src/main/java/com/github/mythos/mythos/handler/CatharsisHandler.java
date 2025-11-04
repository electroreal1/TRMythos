package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.mythos.mythos.ability.confluence.skill.unique.ConfluenceUniques;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CatharsisHandler {

    private static final String INCANTATION = "Exurge de deorsum, O Salvator mutationis!";
    private static boolean obtained = false;
    private static String ownerUUID = null;

    public static boolean isObtained() {
        return obtained;
    }

    public static boolean isOwner(String uuid) {
        return ownerUUID != null && ownerUUID.equals(uuid);
    }

    public static void markObtained(String uuid) {
        obtained = true;
        ownerUUID = uuid;
    }

    public static String getOwnerUUID() {
        return ownerUUID;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new CatharsisHandler());
    }

    @SubscribeEvent
    public void onPlayerChat(ServerChatEvent event) {
        if (event.isCanceled()) return;

        ServerPlayer player = event.getPlayer();
        String message = event.getMessage().getString(); // only the actual sent text

        if (!message.equalsIgnoreCase(INCANTATION)) return;

        if (!player.level.dimension().equals(Level.END)) {
            player.sendSystemMessage(Component.literal("You must be in the End to obtain Catharsis!").withStyle(ChatFormatting.RED), false);
            return;
        }

        if (obtained && !player.getUUID().toString().equals(ownerUUID)) {
            player.sendSystemMessage(Component.literal("Catharsis has already been claimed!").withStyle(ChatFormatting.RED), false);
            return;
        }

        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        ManasSkill skill = ConfluenceUniques.CATHARSIS.get();
        if (skill != null && !storage.getSkill(skill).isPresent()) {
            storage.learnSkill(skill);
            obtained = true;
            ownerUUID = player.getUUID().toString();
            player.sendSystemMessage(Component.literal("You have fused with Catharsis.").withStyle(ChatFormatting.GOLD), false);
        } else {
            player.sendSystemMessage(Component.literal("You already possess Catharsis.").withStyle(ChatFormatting.RED), false);
        }
    }

    @SubscribeEvent
    public void onBeingDamaged(LivingHurtEvent event) {
        Entity source = event.getSource().getEntity();
        Entity victim = event.getEntity();

        if (!(victim instanceof LivingEntity target)) return;
        if (!(source instanceof LivingEntity attacker)) return;

        SkillStorage targetStorage = SkillAPI.getSkillsFrom(target);
        Skill catharsisSkill = ConfluenceUniques.CATHARSIS.get();
        if (targetStorage == null || !targetStorage.getSkill(catharsisSkill).isPresent()) return;

        SkillStorage attackerStorage = SkillAPI.getSkillsFrom(attacker);
        Skill sporeblood = ConfluenceUniques.SPOREBLOOD.get();
        if (attackerStorage != null && attackerStorage.getSkill(sporeblood).isPresent()) {
            float reduced = event.getAmount() * 0.75f;
            event.setAmount(reduced);

            attacker.level.playSound(null, target.blockPosition(),
                    SoundEvents.SLIME_BLOCK_PLACE, SoundSource.PLAYERS,
                    0.6F, 0.8F + target.getRandom().nextFloat() * 0.4F);

            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.SPORE_BLOSSOM_AIR);
        }
    }
}


