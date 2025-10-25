package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.entity.CharybdisEntity;
import com.github.manasmods.tensura.entity.IfritEntity;
import com.github.manasmods.tensura.entity.OrcDisasterEntity;
import com.github.manasmods.tensura.entity.human.HinataSakaguchiEntity;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

public class CrimsonTyrantHandler {


    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (player.level.isClientSide) return;
        Random rand = (Random) player.getRandom();

        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        if (storage == null) return;

        Skill crimsonTyrant = Skills.CRIMSON_TYRANT.get();
        Skill ChildOfThePlane = Skills.CHILD_OF_THE_PLANE.get();

        if (!storage.getSkill(crimsonTyrant).isPresent()) return;

        LivingEntity victim = event.getEntity();

        if (player.getHealth() <= player.getMaxHealth() * 0.25) {
            player.displayClientMessage(
                    Component.literal("You must fight harder!, slay more!, SHED MORE BLOOD.")
                            .withStyle(ChatFormatting.DARK_RED), false
            );
        }

        // --- Player kill check ---
        if (event.getSource().getEntity() == player) {
            int streak = player.getPersistentData().getInt("TyrantKillStreak");
            streak++;
            player.getPersistentData().putInt("TyrantKillStreak", streak);

            if (streak == 2) {
                player.displayClientMessage(
                        Component.literal("LET THE CARNAGE BEGIN!")
                                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false
                );
            } else if (streak == 3) {
                player.displayClientMessage(
                        Component.literal("Blood flows freely... and I shall bathe in it!")
                                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false
                );
            } else if (streak == 5) {
                player.displayClientMessage(
                        Component.literal("A SYMPHONY OF CARNAGE!")
                                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false
                );
            } else if (streak == 10) {
                player.displayClientMessage(
                        Component.literal("AH THE CARNAGE, THE BLOOD FLOWS FREELY, THIS ECSTASY!")
                                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false
                );
            }

            player.displayClientMessage(
                    Component.literal("Yes... more blood! MORE! MORE CARNAGE!")
                            .withStyle(ChatFormatting.DARK_RED), false
            );
        }

        // --- Specific enemies ---
        if (victim instanceof WitherBoss || victim instanceof EnderDragon) {
            player.displayClientMessage(
                    Component.literal("A worthy foe... but still beneath me.").withStyle(ChatFormatting.DARK_RED), false
            );
        }
        if (victim instanceof HinataSakaguchiEntity) {
            player.displayClientMessage(
                    Component.literal("Blasted saint, her blood shall feed the earth.").withStyle(ChatFormatting.DARK_RED), false
            );
        }
        if (victim instanceof IfritEntity) {
            player.displayClientMessage(
                    Component.literal("Blasted Fire Spirit, they don't shed blood... what a shame.").withStyle(ChatFormatting.DARK_RED), false
            );
        }
        if (victim instanceof CharybdisEntity) {
            player.displayClientMessage(
                    Component.literal("THE MONARCH OF THE SKIES CAN BLEED DRY, A WORTHY FOE INDEED.").withStyle(ChatFormatting.DARK_RED), false
            );
        }
        if (victim instanceof OrcDisasterEntity) {
            player.displayClientMessage(
                    Component.literal("This was supposed to be a disaster? SHOW THEM A REAL DISASTER — SHED MORE BLOOD!")
                            .withStyle(ChatFormatting.DARK_RED), false
            );
        }

        // --- Passive mobs ---
        if (victim instanceof Villager || victim instanceof Animal) {
            player.displayClientMessage(
                    Component.literal("Even the meek bleed beautifully. Do not stop.").withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC), false
            );
        }

        // --- Undead ---
        if (victim instanceof Zombie || victim instanceof Skeleton) {
            player.displayClientMessage(
                    Component.literal("Even death bends the knee before the Crimson Tyrant.").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false
            );
        }

        // --- Boss kill while low HP ---
        if ((victim instanceof WitherBoss || victim instanceof EnderDragon || victim instanceof HinataSakaguchiEntity || victim instanceof CharybdisEntity || victim instanceof OrcDisasterEntity || victim instanceof IfritEntity)
                && player.getHealth() <= player.getMaxHealth() * 0.5) {
            player.displayClientMessage(
                    Component.literal("Bleeding and victorious... the true essence of carnage!")
                            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false
            );
        }

        if (storage.getSkill(Skills.CHILD_OF_THE_PLANE.get()).isPresent()) {
            String[] lines = {
                    "Even the Plane’s child cannot escape bloodshed.",
                    "Blood and Plane converge… your end is crimson.",
                    "The world bleeds for me.",
                    "All the world's blessings could not save you from me!"
            };
            player.displayClientMessage(
                    Component.literal(lines[rand.nextInt(lines.length)]).withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD),
                    false
            );
        }

        if (storage.getSkill(Skills.PURITY_SKILL.get()).isPresent()) {
            String[] lines = {
                    "So pure… now soaked in crimson.",
                    "Purity shattered by relentless carnage.",
                    "Even innocence bleeds before the Tyrant."
            };
            player.displayClientMessage(
                    Component.literal(lines[rand.nextInt(lines.length)]).withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                    false
            );
        }

        if (storage.getSkill(UniqueSkills.LUST.get()).isPresent()) {
            String[] lustLines = {
                    "Their desire could not save them… only the crimson prevails!",
                    "Lust blinds, but blood opens eyes.",
                    "Desire fades, but my carnage endures!"
            };
            player.displayClientMessage(
                    Component.literal(lustLines[rand.nextInt(lustLines.length)])
                            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC),
                    false
            );
        }

        if (storage.getSkill(UniqueSkills.GREED.get()).isPresent()) {
            String[] greedLines = {
                    "Greed made them weak… I reap what the foolish desire!",
                    "They reached for more… and received the edge of my blade.",
                    "Fortune favors the brave… but not the greedy."
            };
            player.displayClientMessage(
                    Component.literal(greedLines[rand.nextInt(greedLines.length)])
                            .withStyle(ChatFormatting.DARK_RED),
                    false
            );
        }

        if (storage.getSkill(UniqueSkills.WRATH.get()).isPresent()) {
            String[] wrathLines = {
                    "Your rage was your undoing.",
                    "Wrath consumes, but I control.",
                    "Even fury can't save you from my blade."
            };
            player.displayClientMessage(
                    Component.literal(wrathLines[rand.nextInt(wrathLines.length)])
                            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC),
                    false
            );
        }

        if (storage.getSkill(Skills.PURITY_SKILL.get()).isPresent() && storage.getSkill(Skills.CHILD_OF_THE_PLANE.get()).isPresent()) {
            String[] fusionLines = {
                    "Even the pure and the chosen bleed before me!",
                    "Two forces converge… yet both fall to crimson might!"
            };
            player.displayClientMessage(
                    Component.literal(fusionLines[rand.nextInt(fusionLines.length)])
                            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                    false
            );
        }

        if (storage.getSkill(UniqueSkills.GREAT_SAGE.get()).isPresent()) {
            String[] greatSageLines = {
                    "So you think your wisdom can save you? Think again.",
                    "Great Sage? More like Great Target.",
                    "Your calculations couldn't predict this fate."
            };
            player.displayClientMessage(
                    Component.literal(greatSageLines[rand.nextInt(greatSageLines.length)])
                            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD),
                    false
            );
        }

        if (storage.getSkill(UniqueSkills.GLUTTONY.get()).isPresent()) {
            String[] gluttonyLines = {
                    "You devoured so much... but couldn't digest defeat.",
                    "Gluttony leads to excess, and now, excess blood.",
                    "Feasting on skills won't save you from mine."
            };
            player.displayClientMessage(
                    Component.literal(gluttonyLines[rand.nextInt(gluttonyLines.length)])
                            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC),
                    false
            );
        }

        if (storage.getSkill(UniqueSkills.PRIDE.get()).isPresent()) {
            String[] prideLines = {
                    "Your pride blinded you to your demise.",
                    "Pride comes before a fall... and you fell.",
                    "Even the proud bow before the crimson."
            };
            player.displayClientMessage(
                    Component.literal(prideLines[rand.nextInt(prideLines.length)])
                            .withStyle(ChatFormatting.DARK_RED),
                    false
            );
        }

        if (storage.getSkill(UniqueSkills.PREDATOR.get()).isPresent()) {
            String[] predatorLines = {
                    "You hunted... but became the prey.",
                    "Predator? More like target practice.",
                    "Your skills couldn't save you from mine."
            };
            player.displayClientMessage(
                    Component.literal(predatorLines[rand.nextInt(predatorLines.length)])
                            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD),
                    false
            );
        }

        if (storage.getSkill(UniqueSkills.SHADOW_STRIKER.get()).isPresent()) {
            String[] shadowStrikerLines = {
                    "You strike from the shadows... but I see all.",
                    "Shadow Striker? More like shadowed corpse.",
                    "Your stealth failed you."
            };
            player.displayClientMessage(
                    Component.literal(shadowStrikerLines[rand.nextInt(shadowStrikerLines.length)])
                            .withStyle(ChatFormatting.DARK_RED),
                    false
            );
        }

        if (storage.getSkill(UniqueSkills.BERSERKER.get()).isPresent()) {
            String[] berserkerLines = {
                    "Your rage was your undoing.",
                    "Berserker? More like berserk corpse.",
                    "Fury couldn't protect you."
            };
            player.displayClientMessage(
                    Component.literal(berserkerLines[rand.nextInt(berserkerLines.length)])
                            .withStyle(ChatFormatting.DARK_RED),
                    false
            );
        }

        if (storage.getSkill(UniqueSkills.CHOSEN_ONE.get()).isPresent()) {
            String[] chosenOneLines = {
                    "Chosen? More like chosen for death.",
                    "Destiny couldn't save you from me.",
                    "Your fate was sealed the moment you crossed me."
            };
            player.displayClientMessage(
                    Component.literal(chosenOneLines[rand.nextInt(chosenOneLines.length)])
                            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD),
                    false
            );
        }

        if (storage.getSkill(UniqueSkills.SEER.get()).isPresent()) {
            player.displayClientMessage(
                    Component.literal("Had you seen a future so pitiful for yourself? And you still chose to stand in my path? How foolish.")
                            .withStyle(ChatFormatting.DARK_RED), false);
        }

        if (storage.getSkill(UniqueSkills.MARTIAL_MASTER.get()).isPresent()) {
            String[] martialMasterLines = {
                    "Your martial prowess couldn't save you.",
                    "Mastery ends in defeat before me.",
                    "Your skills were no match for my power."
            };
            player.displayClientMessage(
                    Component.literal(martialMasterLines[rand.nextInt(martialMasterLines.length)])
                            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC),
                    false
            );
        }

        if (storage.getSkill(io.github.Memoires.trmysticism.registry.skill.UniqueSkills.DREAMER.get()).isPresent()) {
            String[] lines = {
                    "Dreamer? More like drenched in blood now!",
                    "Your dreams are soaked in crimson.",
                    "Even your dreams could not escape the slaughter."
            };
            player.displayClientMessage(
                    Component.literal(lines[rand.nextInt(lines.length)]).withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC),
                    false
            );
        }

        if (storage.getSkill(Skills.OMNISCIENT_EYE.get()).isPresent()) {
            String[] lines = {
                    "Omniscient Eye? You saw death, but not its carnage.",
                    "All-seeing? Yet you could not escape the bloodshed.",
                    "Your vision fails before crimson fury."
            };
            player.displayClientMessage(
                    Component.literal(lines[rand.nextInt(lines.length)]).withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD),
                    false
            );
        }

        if (storage.getSkill(Skills.OPPORTUNIST_SKILL.get()).isPresent()) {
            String[] lines = {
                    "Opportunist? Every chance you had ended in blood.",
                    "You sought advantage… but only found carnage.",
                    "Fortune favored you, but my carnage runs unbidden by the constraints of probability!"
            };
            player.displayClientMessage(
                    Component.literal(lines[rand.nextInt(lines.length)]).withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC),
                    false
            );
        }

        if (storage.getSkill(Skills.PROFANITY.get()).isPresent()) {
            String[] lines = {
                    "Is this kind of malice not what you're meant to embody? Are you not overjoyed by this sort of chaos?!",
                    "Try as you might, all the world's sin can not compare to the might of the crimson tide..",
                    "Profanity bleeds with you."
            };
            player.displayClientMessage(
                    Component.literal(lines[rand.nextInt(lines.length)]).withStyle(ChatFormatting.DARK_RED),
                    false
            );
        }

        if (storage.getSkill(Skills.BLOODSUCKER.get()).isPresent()) {
            String[] lines = {
                    "Bloodsucker? You’ve become the feast!",
                    "You drained others… now your blood flows for me!",
                    "A fondness for bloodshed, yet lacking the same ambition..."
            };
            player.displayClientMessage(
                    Component.literal(lines[rand.nextInt(lines.length)]).withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD),
                    false
            );
        }


        if (storage.getSkill(Skills.FAKER.get()).isPresent()) {
            String[] lines = {
                    "Faker? Illusions die, but blood remains.",
                    "Your petty forgeries had no hope of standing up to my crimson might.",
                    "Fakery cannot save you from crimson reality."
            };
            player.displayClientMessage(
                    Component.literal(lines[rand.nextInt(lines.length)]).withStyle(ChatFormatting.DARK_RED),
                    false
            );
        }


    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (player.level.isClientSide) return;

        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        if (storage == null) return;

        if (!storage.getSkill(Skills.CRIMSON_TYRANT.get()).isPresent()) return;

        // Massive damage triggers
        if (event.getAmount() >= 300.0F) {
            player.displayClientMessage(
                    Component.literal("Do you feel it? The rush of slaughter! The ecstasy of power!")
                            .withStyle(ChatFormatting.RED), false
            );
        }
        if (event.getAmount() >= player.getMaxHealth() * 0.3) {
            player.displayClientMessage(
                    Component.literal("Hurt? Good. Let the pain ignite your fury!").withStyle(ChatFormatting.DARK_RED), false
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (player.level.isClientSide) return;

        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        if (storage == null) return;

        boolean hasCrimsonTyrant = storage.getSkill(Skills.CRIMSON_TYRANT.get()).isPresent();
        boolean hasChildOfThePlane = storage.getSkill(Skills.CHILD_OF_THE_PLANE.get()).isPresent();

        if (hasCrimsonTyrant && hasChildOfThePlane && !player.getPersistentData().getBoolean("CrimsonTyrant_ChildOfThePlane_FusionTriggered")) {
            player.displayClientMessage(
                    Component.literal("Blood and Plane converge… the world itself takes notice of me.")
                            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC, ChatFormatting.BOLD),
                    false
            );
            player.getPersistentData().putBoolean("CrimsonTyrant_ChildOfThePlane_FusionTriggered", true);
        }
    }




}

