package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.tensura.event.SkillPlunderEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = "trmythos")
public class UncopyableSkills {
    public static final Set<ResourceLocation> BLOCKED_SKILLS = new HashSet<>();



    public static boolean isBlocked(ManasSkill skill) {
        if (skill == null || skill.getRegistryName() == null) return false;
        return BLOCKED_SKILLS.contains(skill.getRegistryName());
    }

    @SubscribeEvent
    public static void onSkillPlunder(SkillPlunderEvent event) {
        ManasSkill skill = event.getSkill();
        if (!isBlocked(skill)) return;

        event.setCanceled(true);

        if (event.getPlunderer() instanceof Player player) {
            player.displayClientMessage(Component.literal("This power cannot be plundered.").withStyle(ChatFormatting.DARK_RED), true);
        }
    }

    static {
        BLOCKED_SKILLS.add(new ResourceLocation("trmythos", "npc_life"));
        BLOCKED_SKILLS.add(new ResourceLocation("trmythos", "wavebreaker"));
    }
}
