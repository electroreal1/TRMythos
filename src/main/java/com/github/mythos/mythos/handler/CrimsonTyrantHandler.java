package com.github.mythos.mythos.handler;

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
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CrimsonTyrantHandler {


    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (player.level.isClientSide) return;

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

