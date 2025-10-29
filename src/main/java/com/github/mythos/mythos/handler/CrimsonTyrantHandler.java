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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;
import java.util.UUID;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber
public class CrimsonTyrantHandler {

    private static final long MESSAGE_COOLDOWN = 15;
    private static final Random rand = new Random();

    private static boolean canSendMessage(Player player, String key) {
        long now = player.level.getGameTime();
        String tag = "CrimsonTyrantMsg_" + key;
        long last = player.getPersistentData().getLong(tag);
        if (now - last < MESSAGE_COOLDOWN) return false;
        player.getPersistentData().putLong(tag, now);
        return true;
    }

    private static void send(Player player, String key, Component text) {
        if (canSendMessage(player, key)) {
            player.displayClientMessage(text, false);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity killed = event.getEntity();
        Entity attackerEntity = event.getSource().getEntity();
        if (!(attackerEntity instanceof Player player)) return;
        if (player.level.isClientSide) return;

        SkillStorage attackerStorage = SkillAPI.getSkillsFrom(player);
        if (attackerStorage == null || attackerStorage.getSkill(Skills.CRIMSON_TYRANT.get()).isEmpty()) return;

        // --- Track kill streak ---
        int streak = player.getPersistentData().getInt("TyrantKillStreak") + 1;
        player.getPersistentData().putInt("TyrantKillStreak", streak);

        // --- HP warning ---
        if (player.getHealth() <= player.getMaxHealth() * 0.25f) {
            send(player, "low_hp", Component.literal(
                            "You must fight harder!, slay more!, SHED MORE BLOOD.")
                    .withStyle(ChatFormatting.DARK_RED));
        }

//        // --- Kill streak messages ---
//        switch (streak) {
//            case 2 -> send(player, "streak", Component.literal(
//                    "LET THE CARNAGE BEGIN!").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
//            case 3 -> send(player, "streak", Component.literal(
//                    "Blood flows freely... and I shall bathe in it!").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
//            case 5 -> send(player, "streak", Component.literal(
//                    "A SYMPHONY OF CARNAGE!").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
//            case 10 -> send(player, "streak", Component.literal(
//                    "AH, THE CARNAGE, THE BLOOD FLOWS FREELY, THIS ECSTASY!").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
//            default -> send(player, "streak", Component.literal(
//                    "Yes... more blood! MORE! MORE CARNAGE!").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
//        }

        // --- Specific enemies ---
        if (killed instanceof WitherBoss || killed instanceof EnderDragon) {
            send(player, "boss", Component.literal(
                    "A worthy foe... but still beneath me.").withStyle(ChatFormatting.DARK_RED));
        } else if (killed instanceof HinataSakaguchiEntity) {
            send(player, "hinata", Component.literal(
                    "Blasted saint, her blood shall feed the earth.").withStyle(ChatFormatting.DARK_RED));
        } else if (killed instanceof IfritEntity) {
            send(player, "ifrit", Component.literal(
                    "Blasted Fire Spirit, they don't shed blood... what a shame.").withStyle(ChatFormatting.DARK_RED));
        } else if (killed instanceof CharybdisEntity) {
            send(player, "charybdis", Component.literal(
                    "THE MONARCH OF THE SKIES CAN BLEED DRY, A WORTHY FOE INDEED.").withStyle(ChatFormatting.DARK_RED));
        } else if (killed instanceof OrcDisasterEntity) {
            send(player, "orc", Component.literal(
                    "This was supposed to be a disaster? SHOW THEM A REAL DISASTER — SHED MORE BLOOD!").withStyle(ChatFormatting.DARK_RED));
        }

        // --- Boss kill while low HP ---
        if ((killed instanceof WitherBoss || killed instanceof EnderDragon || killed instanceof HinataSakaguchiEntity ||
                killed instanceof CharybdisEntity || killed instanceof OrcDisasterEntity || killed instanceof IfritEntity)
                && player.getHealth() <= player.getMaxHealth() * 0.5f) {
            send(player, "low_hp_boss", Component.literal(
                            "Bleeding and victorious... the true essence of carnage!")
                    .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
        }

        // --- Skill flavor lines for the killed entity ---
        SkillStorage killedStorage = SkillAPI.getSkillsFrom(killed);
        if (killedStorage == null) return;

        UUID killedId = killed.getUUID();
        String skillTag = "CrimsonTyrant_SkillMsg_" + killedId;
        if (!player.getPersistentData().getBoolean(skillTag)) {
            java.util.function.BiConsumer<Skill, String[]> sendSkillLines = (skill, lines) -> {
                if (killedStorage.getSkill(skill).isPresent()) {
                    send(player, skill.getRegistryName().getPath() + "_" + killedId,
                            Component.literal(lines[rand.nextInt(lines.length)])
                                    .withStyle(ChatFormatting.DARK_RED));
                }
            };

            // --- All skill lines ---
            sendSkillLines.accept(Skills.CHILD_OF_THE_PLANE.get(), new String[]{
                    "Even the Plane’s child cannot escape bloodshed.",
                    "Blood and Plane converge… your end is crimson.",
                    "The world bleeds for me.",
                    "All the world's blessings could not save you from me!"
            });
            sendSkillLines.accept(Skills.PURITY_SKILL.get(), new String[]{
                    "So pure… now soaked in crimson.",
                    "Purity shattered by relentless carnage.",
                    "Even innocence bleeds before the Tyrant."
            });
            sendSkillLines.accept(UniqueSkills.LUST.get(), new String[]{
                    "Their desire could not save them… only the crimson prevails!",
                    "Lust blinds, but blood opens eyes.",
                    "Desire fades, but my carnage endures!"
            });
            sendSkillLines.accept(UniqueSkills.GREED.get(), new String[]{
                    "Greed made them weak… I reap what the foolish desire!",
                    "They reached for more… and received the edge of my blade.",
                    "Fortune favors the brave… but not the greedy."
            });
            sendSkillLines.accept(UniqueSkills.WRATH.get(), new String[]{
                    "Your rage was your undoing.",
                    "Wrath consumes, but I control.",
                    "Even fury can't save you from my blade."
            });
            sendSkillLines.accept(UniqueSkills.GREAT_SAGE.get(), new String[]{
                    "So you think your wisdom can save you? Think again.",
                    "Great Sage? More like Great Target.",
                    "Your calculations couldn't predict this fate."
            });
            sendSkillLines.accept(UniqueSkills.GLUTTONY.get(), new String[]{
                    "You devoured so much... but couldn't digest defeat.",
                    "Gluttony leads to excess, and now, excess blood.",
                    "Feasting on skills won't save you from mine."
            });
            sendSkillLines.accept(UniqueSkills.PRIDE.get(), new String[]{
                    "Your pride blinded you to your demise.",
                    "Pride comes before a fall... and you fell.",
                    "Even the proud bow before the crimson."
            });
            sendSkillLines.accept(UniqueSkills.PREDATOR.get(), new String[]{
                    "You hunted... but became the prey.",
                    "Predator? More like target practice.",
                    "Your skills couldn't save you from mine."
            });
            sendSkillLines.accept(UniqueSkills.SHADOW_STRIKER.get(), new String[]{
                    "You strike from the shadows... but I see all.",
                    "Shadow Striker? More like shadowed corpse.",
                    "Your stealth failed you."
            });
            sendSkillLines.accept(UniqueSkills.BERSERKER.get(), new String[]{
                    "Your rage was your undoing.",
                    "Berserker? More like berserk corpse.",
                    "Fury couldn't protect you."
            });
            sendSkillLines.accept(UniqueSkills.CHOSEN_ONE.get(), new String[]{
                    "Chosen? More like chosen for death.",
                    "Destiny couldn't save you from me.",
                    "Your fate was sealed the moment you crossed me."
            });
            sendSkillLines.accept(io.github.Memoires.trmysticism.registry.skill.UniqueSkills.DREAMER.get(), new String[]{
                    "Dreamer? More like drenched in blood now!",
                    "Your dreams are soaked in crimson.",
                    "Even your dreams could not escape the slaughter."
            });
            sendSkillLines.accept(Skills.OMNISCIENT_EYE.get(), new String[]{
                    "Omniscient Eye? You saw death, but not its carnage.",
                    "All-seeing? Yet you could not escape the bloodshed.",
                    "Your vision fails before crimson fury."
            });
            sendSkillLines.accept(Skills.OPPORTUNIST_SKILL.get(), new String[]{
                    "Opportunist? Every chance you had ended in blood.",
                    "You sought advantage… but only found carnage.",
                    "Fortune favored you, but my carnage runs unbidden by the constraints of probability!"
            });
            sendSkillLines.accept(Skills.PROFANITY.get(), new String[]{
                    "Is this kind of malice not what you're meant to embody? Are you not overjoyed by this sort of chaos?!",
                    "Try as you might, all the world's sin can not compare to the might of the crimson tide..",
                    "Profanity bleeds with you."
            });
            sendSkillLines.accept(Skills.BLOODSUCKER.get(), new String[]{
                    "Bloodsucker? You’ve become the feast!",
                    "You drained others… now your blood flows for me!",
                    "A fondness for bloodshed, yet lacking the same ambition..."
            });
            sendSkillLines.accept(Skills.FAKER.get(), new String[]{
                    "Faker? Illusions die, but blood remains.",
                    "Your petty forgeries had no hope of standing up to my crimson might.",
                    "Fakery cannot save you from crimson reality."
            });

            // Mark that skill messages for this kill have been sent
            player.getPersistentData().putBoolean(skillTag, true);
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

        if (hasCrimsonTyrant && hasChildOfThePlane &&
                !player.getPersistentData().getBoolean("CrimsonTyrant_ChildOfThePlane_FusionTriggered")) {
            send(player, "fusion", Component.literal(
                            "Blood and Plane converge… the world itself takes notice of me.")
                    .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
            player.getPersistentData().putBoolean("CrimsonTyrant_ChildOfThePlane_FusionTriggered", true);
        }
    }
}
