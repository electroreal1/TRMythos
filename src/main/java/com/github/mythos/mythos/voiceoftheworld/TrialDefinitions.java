package com.github.mythos.mythos.voiceoftheworld;

import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.mythos.mythos.registry.skill.Skills;

public class TrialDefinitions {
    public static void init() {
        // Giant Slayer
        WorldTrialRegistry.register(new WorldTrial("giant_slayer", "Trial of the Giant Slayer", 1, 15000, p -> {
            VoiceOfTheWorld.delayedAnnouncement(p, "Notice.",
                    "Confirmed. Individual:  + p.getName().getString() +  has successfully hunted a Calamity-class entity.",
                    "Trial Completed.");
        }));

        // Magicule Breather
        WorldTrialRegistry.register(new WorldTrial("breather", "Trial of the Magicule Breather", 144000, 20000, p -> {
            VoiceOfTheWorld.delayedAnnouncement(p, "Notice.",
                    "Confirmed. Respiratory system has adapted to high-density Magicule environments.",
                    "Individual: " + p.getName().getString() + " is now recognized as a resident of the Deep dimensions.");
        }));

        // Pacifist
        WorldTrialRegistry.register(new WorldTrial("pacifist", "Trial of the Pacifist", 240000, 30000, p -> {
            VoiceOfTheWorld.delayedAnnouncement(p, "Notice.",
                    "Confirmed. Inner peace has stabilized the spiritual body for 10 cycles.",
                    "The path of non-violence has hardened the soul core.",
                    "Individual: " + p.getName().getString() + " has achieved an Aura of Tranquility.");
        }));

        // Soul Stability
        WorldTrialRegistry.register(new WorldTrial("stability", "Trial of the Soul Stability", 1, 100000, p -> {
            VoiceOfTheWorld.delayedAnnouncement(p, "Notice.",
                    "Confirmed. Spiritual threshold exceeded.",
                    "Individual: " + p.getName().getString() + " has maintained soul stability at the Genesis Tier.",
                    "Trial Completed.");
        }));

        // Void Walker
        WorldTrialRegistry.register(new WorldTrial("void_walker", "Trial of the Void Walker", 1, 50000, p -> {
            VoiceOfTheWorld.delayedAnnouncement(p, "Notice.",
                    "Confirmed. Survival in the Non-Existent Space verified.",
                    "Individual: " + p.getName().getString() + " has successfully traversed the Boundary of Nothingness.",
                    "Trial Completed.");
        }));

        // World Observer
        WorldTrialRegistry.register(new WorldTrial("observer", "Trial of the World Observer", 144000, 40000, p -> {
            VoiceOfTheWorld.delayedAnnouncement(p, "Notice.",
                    "Confirmed. The ego has remained static while the mind accelerated for 3,600 seconds.",
                    "Thought processing speed has reached the Administrative level.",
                    "Individual: " + p.getName().getString() + " has obtained the Skill [Gaze].");
            SkillUtils.learnSkill(p, Skills.GAZE.get());
        }));
    }
}
