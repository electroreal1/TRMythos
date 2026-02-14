package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.event.SkillPlunderEvent;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import com.github.mythos.mythos.registry.skill.Skills;
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
    private static final Set<ResourceLocation> PERMANENTLY_BLOCKED = new HashSet<>();

    public static boolean isPlunderBlocked(ManasSkill skill) {
        if (skill == null) return false;
        ResourceLocation loc = skill.getRegistryName();
        if (loc == null) return false;

        if (PERMANENTLY_BLOCKED.contains(loc)) return true;

        if (skill instanceof Skill tensuraSkill) {
            if (tensuraSkill.getType() == Skill.SkillType.ULTIMATE) {
                return !MythosSkillsConfig.ALLOW_ULTIMATE_COPYING.get();
            }
        }

        return false;
    }

    @SubscribeEvent
    public static void onSkillPlunder(SkillPlunderEvent event) {
        if (SkillUtils.hasSkill(event.getTarget(), Skills.UNITY.get())) {
            event.setCanceled(true);
            notifyPlunderer(event.getPlunderer());
            return;
        }

        if (isPlunderBlocked(event.getSkill())) {
            event.setCanceled(true);
            notifyPlunderer(event.getPlunderer());
        }
    }

    private static void notifyPlunderer(Object entity) {
        if (entity instanceof Player player) {
            player.displayClientMessage(Component.literal("This power is beyond your reach.")
                    .withStyle(ChatFormatting.DARK_RED), true);
        }
    }

    static {
        // High-level conceptual or NPC-only skills that should never be copied
        PERMANENTLY_BLOCKED.add(new ResourceLocation("trmythos", "npc_life"));
        PERMANENTLY_BLOCKED.add(new ResourceLocation("trmythos", "unity"));
        PERMANENTLY_BLOCKED.add(new ResourceLocation("trmythos", "evolution"));
        PERMANENTLY_BLOCKED.add(new ResourceLocation("trmythos", "balance"));
        PERMANENTLY_BLOCKED.add(new ResourceLocation("trmythos", "cycle"));
        PERMANENTLY_BLOCKED.add(new ResourceLocation("trmythos", "foundation"));
        PERMANENTLY_BLOCKED.add(new ResourceLocation("trmythos", "watcher"));
        PERMANENTLY_BLOCKED.add(new ResourceLocation("trmythos", "reincarnator"));
    }
}