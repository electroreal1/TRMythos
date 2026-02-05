package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.mythos.mythos.voiceoftheworld.VoiceOfTheWorld;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = "trmythos")
public class UltimateSkillTracker {

    @SubscribeEvent
    public static void onSkillLearned(UnlockSkillEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!GodClassHandler.get(player.getLevel()).areAnnouncementsEnabled()) return;

        ManasSkillInstance instance = event.getSkillInstance();
        Skill skill = (Skill) instance.getSkill();

        if (skill.getType() == Skill.SkillType.ULTIMATE && !instance.isTemporarySkill()) {
            String skillName = Objects.requireNonNull(skill.getColoredName()).getString();

            VoiceOfTheWorld.broadcast(Objects.requireNonNull(player.getServer()),
                    "Confirmed. Individual: " + player.getName().getString() +
                            " has acquired Ultimate Skill: " + "§6[" + skillName + "]§f.");
        }
    }
}
