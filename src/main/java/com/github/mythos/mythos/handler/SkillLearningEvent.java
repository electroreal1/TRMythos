package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SkillLearningEvent {

    @SubscribeEvent
    public static void MemoryShards(UnlockSkillEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (SkillUtils.hasSkill(player, Skills.REINCARNATOR.get())) {
            if (player.getRandom().nextInt(10) == 0) {
                CompoundTag data = player.getPersistentData();

                int currentShards = data.getInt("MemoryShards");
                data.putInt("MemoryShards", currentShards + 1);

                player.displayClientMessage(Component.literal("You have obtained a Memory Shard!")
                        .withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD), true);

                player.level.playSound(null, player.blockPosition(),
                        SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 1.2f);


            }
        }
    }
}
