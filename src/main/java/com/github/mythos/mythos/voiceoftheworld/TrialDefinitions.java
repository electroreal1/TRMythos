package com.github.mythos.mythos.voiceoftheworld;

import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.mythos.mythos.registry.skill.Skills;

import java.util.EnumSet;

public class TrialDefinitions {
    public static void init() {
        WorldTrialRegistry.register(new WorldTrial("giant_slayer", "Trial of the Giant Slayer",
                EnumSet.of(WorldTrial.TrialType.KILL), 5, 15000, "", p -> {
            VoiceOfTheWorld.delayedAnnouncement(p, VoiceOfTheWorld.Priority.ACQUISITION, "Notice.",
                    "Confirmed. Concept of 'Size' has been surpassed.",
                    "Trial Completed.");
        }));

        WorldTrialRegistry.register(new WorldTrial("breather", "Trial of the Magicule Breather",
                EnumSet.of(WorldTrial.TrialType.DIMENSION), 144000, 20000, "labyrinth", p -> {
            VoiceOfTheWorld.delayedAnnouncement(p, VoiceOfTheWorld.Priority.ACQUISITION, "Notice.",
                    "Confirmed. Respiratory system has adapted to high-density environments.",
                    "Trial Completed.");
        }));

        WorldTrialRegistry.register(new WorldTrial("pacifist", "Trial of the Pacifist",
                EnumSet.of(WorldTrial.TrialType.PASSIVE), 240000, 30000, "", p -> {
            VoiceOfTheWorld.delayedAnnouncement(p, VoiceOfTheWorld.Priority.ACQUISITION, "Notice.",
                    "Confirmed. Inner peace has stabilized the spiritual body.",
                    "Trial Completed.");
        }));

        WorldTrialRegistry.register(new WorldTrial("stability", "Trial of the Soul Stability",
                EnumSet.of(WorldTrial.TrialType.EP_THRESHOLD), 1, 100000, "", p -> {
            VoiceOfTheWorld.delayedAnnouncement(p, VoiceOfTheWorld.Priority.ACQUISITION, "Notice.",
                    "Confirmed. Spiritual threshold exceeded.",
                    "Trial Completed.");
        }));

        WorldTrialRegistry.register(new WorldTrial("void_walker", "Trial of the Void Walker",
                EnumSet.of(WorldTrial.TrialType.Y_LEVEL), 1, 50000, "-10000", p -> {
            VoiceOfTheWorld.delayedAnnouncement(p, VoiceOfTheWorld.Priority.ACQUISITION, "Notice.",
                    "Confirmed. Survival in the Non-Existent Space verified.",
                    "Trial Completed.");
        }));

        WorldTrialRegistry.register(new WorldTrial("observer", "Trial of the World Observer",
                EnumSet.of(WorldTrial.TrialType.STILLNESS), 144000, 40000, "", p -> {
            VoiceOfTheWorld.delayedAnnouncement(p, VoiceOfTheWorld.Priority.ACQUISITION, "Notice.",
                    "Confirmed. Ego has remained static while mind accelerated.",
                    "Individual obtained the Skill [Gaze].");
            SkillUtils.learnSkill(p, Skills.GAZE.get());
        }));
    }
}