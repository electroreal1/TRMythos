package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.entity.CharybdisEntity;
import com.github.manasmods.tensura.entity.IfritEntity;
import com.github.manasmods.tensura.entity.OrcDisasterEntity;
import com.github.manasmods.tensura.entity.human.HinataSakaguchiEntity;
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
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class CrimsonTyrantHandler {


    public static void CrimsonTyrantPseudoEgo(LivingDeathEvent event, ManasSkillInstance instance, Player player, LivingHurtEvent eventHurt) {
        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        Skill CrimsonTyrantSkill = Skills.CRIMSON_TYRANT.get();
        Skill ChildOfThePlane = Skills.CHILD_OF_THE_PLANE.get();
        if (storage.getSkill(CrimsonTyrantSkill).isPresent()) {
            if (!(event.getSource().getEntity() instanceof Player)) return;
            if (player.level.isClientSide) return;

            int streak = player.getPersistentData().getInt("TyrantKillStreak");
            int combatTicks = player.getPersistentData().getInt("CombatTicks");
            combatTicks++;
            player.getPersistentData().putInt("CombatTicks", combatTicks);

            if (player.getHealth() <= player.getMaxHealth() * 0.25) {
                player.displayClientMessage(
                        Component.literal("You must fight harder!, slay more!, SHED MORE BLOOD.")
                                .withStyle(ChatFormatting.DARK_RED), false);
            }

            if (event.getSource().getEntity() == player) {
                player.displayClientMessage(
                        Component.literal("Yes... more blood! MORE! MORE CARNAGE!")
                                .withStyle(ChatFormatting.DARK_RED), false);
            }

            LivingEntity victim = event.getEntity();

            if (victim instanceof WitherBoss || victim instanceof EnderDragon) {
                player.displayClientMessage(
                        Component.literal("A worthy foe... but still beneath me.")
                                .withStyle(ChatFormatting.DARK_RED), false);
            }

            if (victim instanceof HinataSakaguchiEntity) {
                player.displayClientMessage(
                        Component.literal("Blasted saint, her blood shall feed the earth.")
                                .withStyle(ChatFormatting.DARK_RED), false);
            }

            if (victim instanceof IfritEntity) {
                player.displayClientMessage(
                        Component.literal("Blasted Fire Spirit, they don't shed blood... what a shame.")
                                .withStyle(ChatFormatting.DARK_RED), false);
            }

            if (victim instanceof CharybdisEntity) {
                player.displayClientMessage(
                        Component.literal("THE MONARCH OF THE SKIES CAN BLEED DRY, A WORTHY FOE INDEED.")
                                .withStyle(ChatFormatting.DARK_RED), false);
            }

            if (victim instanceof OrcDisasterEntity) {
                player.displayClientMessage(
                        Component.literal("This was supposed to be a disaster? SHOW THEM A REAL DISASTER — SHED MORE BLOOD!")
                                .withStyle(ChatFormatting.DARK_RED), false);
            }

            if (player.getPersistentData().getBoolean("InCombat") && player.getCombatTracker().getCombatDuration() > 400) {
                player.getPersistentData().putBoolean("InCombat", false);
                player.displayClientMessage(
                        Component.literal("The silence mocks me... where is the next offering? The next bloodshed? The next carnage?")
                                .withStyle(ChatFormatting.GRAY, ChatFormatting.DARK_RED), false);
            }

            if (event.getSource().getEntity() == player) {
                streak++;
                player.getPersistentData().putInt("TyrantKillStreak", streak);


                if (streak == 2) {
                        player.displayClientMessage(
                                Component.literal("LET THE CARNAGE BEGIN!")
                                        .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);
                    } else if (streak == 3) {
                    player.displayClientMessage(
                            Component.literal("Blood flows freely... and I shall bathe in it!")
                                    .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);
                } else if (streak == 5) {
                    player.displayClientMessage(
                            Component.literal("A SYMPHONY OF CARNAGE!")
                                    .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);
                } else if (streak == 10) {
                    player.displayClientMessage(
                            Component.literal("AH THE CARNAGE, THE BLOOD FLOWS FREELY, THIS ECSTASY!")
                                    .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);
                }
            }

            if (player.isSleeping()) {
                player.displayClientMessage(
                        Component.literal("You sleep while your blade thirsts? Disappointing.")
                                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC), false);
            }

            if (victim instanceof Player && event.getSource().getEntity() == player) {
                player.displayClientMessage(
                        Component.literal("Friend or foe... all bleed the same. ONLY THE RESULT MATTERS... CARNAGE!")
                                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);
            }

            if (eventHurt.getAmount() >= 300.0F) {
                player.displayClientMessage(
                        Component.literal("Do you feel it? The rush of slaughter! The ecstasy of power!").withStyle(ChatFormatting.RED), false
                );
            }

            if (player.getHealth() == player.getMaxHealth() && player.tickCount % 200 == 0) {
                player.displayClientMessage(
                        Component.literal("So much strength... yet no one left to test it. How dull.").withStyle(ChatFormatting.GRAY), false
                );
            }

            if (eventHurt.getAmount() >= player.getMaxHealth() * 0.3) {
                player.displayClientMessage(
                        Component.literal("Hurt? Good. Let the pain ignite your fury!").withStyle(ChatFormatting.DARK_RED), false
                );
            }

            if (player.getHealth() <= 1) {
                player.displayClientMessage(
                        Component.literal("HAHAHA! Is that all?! YOU’LL HAVE TO DO BETTER TO KILL ME!")
                                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false
                );
            }

            if (event.getEntity() instanceof Villager || event.getEntity() instanceof Animal) {
                player.displayClientMessage(
                        Component.literal("Even the meek bleed beautifully. Do not stop.")
                                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC), false
                );
            }

            if (player.level.isNight() && player.tickCount % 200 == 0) {
                player.displayClientMessage(
                        Component.literal("Darkness falls... how fitting. Let the hunt begin.")
                                .withStyle(ChatFormatting.DARK_RED), false
                );
            }


            if (combatTicks >= 400) { // 20 ticks * 20 seconds
                player.displayClientMessage(
                        Component.literal("Pathetic. Swing harder! Aim to maim, not to scare!")
                                .withStyle(ChatFormatting.DARK_RED), false
                );
                player.getPersistentData().putInt("CombatTicks", 0);
            }

            if (event.getEntity() instanceof Zombie || event.getEntity() instanceof Skeleton) {
                player.displayClientMessage(
                        Component.literal("Even death bends the knee before the Crimson Tyrant.")
                                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false
                );
            }

            if ((event.getEntity() instanceof WitherBoss || event.getEntity() instanceof EnderDragon || victim instanceof HinataSakaguchiEntity || victim instanceof CharybdisEntity || victim instanceof OrcDisasterEntity || victim instanceof IfritEntity)
                    && player.getHealth() <= player.getMaxHealth() * 0.5) {
                player.displayClientMessage(
                        Component.literal("Bleeding and victorious... the true essence of carnage!")
                                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false
                );
            }

            if (storage.getSkill(ChildOfThePlane).isPresent()) {
                if (!player.getPersistentData().getBoolean("CrimsonTyrant_ChildOfThePlane_FusionTriggered")) {
                    player.displayClientMessage(
                            Component.literal("Blood and Plane converge… the world itself takes notice of me.")
                                    .withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC, ChatFormatting.BOLD),
                            false
                    );
                    player.getPersistentData().putBoolean("CrimsonTyrant_ChildOfThePlane_FusionTriggered", true);
                }
            }

        }
    }


}

