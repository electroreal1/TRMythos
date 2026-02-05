package com.github.mythos.mythos.command;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import com.github.mythos.mythos.handler.GodClassHandler;
import com.github.mythos.mythos.registry.skill.Skills;
import com.github.mythos.mythos.voiceoftheworld.TrialManager;
import com.github.mythos.mythos.voiceoftheworld.VoiceOfTheWorld;
import com.github.mythos.mythos.voiceoftheworld.WorldTrialRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;


public class MythosCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("mythos")

                // Godclass
                .then(Commands.literal("godclass")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.literal("reset")
                                .then(Commands.literal("all").executes(context -> {
                                    GodClassHandler.get(context.getSource().getLevel()).resetAllOwners();
                                    context.getSource().sendSuccess(Component.literal("§c[Mythos] All God statuses have been wiped."), true);
                                    VoiceOfTheWorld.broadcast(context.getSource().getServer(),
                                            "Notice. All Divine Authorities have been withdrawn. The seats of the Gods are now vacant.");
                                    return 1;
                                }))
                                .then(Commands.literal("dendrahh").executes(context -> {
                                    GodClassHandler.get(context.getSource().getLevel()).setDendrahhObtained(false);
                                    context.getSource().sendSuccess(Component.literal("§e[Mythos] Dendrahh status reset."), true);
                                    return 1;
                                }))
                                .then(Commands.literal("khonsu").executes(context -> {
                                    GodClassHandler.get(context.getSource().getLevel()).setKhonsuObtained(false);
                                    context.getSource().sendSuccess(Component.literal("§e[Mythos] Khonsu status reset."), true);
                                    return 1;
                                }))
                                .then(Commands.literal("kthanid").executes(context -> {
                                    GodClassHandler.get(context.getSource().getLevel()).setKthanidObtained(false);
                                    context.getSource().sendSuccess(Component.literal("§e[Mythos] Kthanid status reset."), true);
                                    return 1;
                                }))
                        )
                        .then(Commands.literal("status").executes(context -> {
                            GodClassHandler handler = GodClassHandler.get(context.getSource().getLevel());
                            String status = String.format("§7Dendrahh: %b | Khonsu: %b | Kthanid: %b",
                                    handler.isDendrahhObtained(), handler.isKhonsuObtained(), handler.isKthanidObtained());
                            context.getSource().sendSuccess(Component.literal(status), false);
                            return 1;
                        }))
                )

                // Bibliomania
                .then(Commands.literal("bibliomania")
                        .then(Commands.literal("get").executes(context -> {
                            if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
                                context.getSource().sendFailure(Component.literal("This command must be run by a player."));
                                return 0;
                            }

                            Optional<ManasSkillInstance> skillOpt = SkillAPI.getSkillsFrom(player).getSkill(Skills.BIBLIOMANIA.get());

                            if (skillOpt.isPresent()) {
                                CompoundTag tag = skillOpt.get().getOrCreateTag();
                                float rp = tag.getFloat("recordPoints");
                                context.getSource().sendSuccess(Component.literal("§d[Bibliomania] §fYour current Record Points: §b" + rp), false);
                            } else {
                                context.getSource().sendFailure(Component.literal("§cError: You do not possess the Bibliomania skill."));
                            }
                            return 1;
                        }))

                        .then(Commands.literal("set")
                                .requires(source -> source.hasPermission(4))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.argument("amount", FloatArgumentType.floatArg(0, 1000))
                                                .executes(context -> {
                                                    ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
                                                    float amount = FloatArgumentType.getFloat(context, "amount");
                                                    Optional<ManasSkillInstance> skillOpt = SkillAPI.getSkillsFrom(targetPlayer).getSkill(Skills.BIBLIOMANIA.get());

                                                    if (skillOpt.isPresent()) {
                                                        ManasSkillInstance inst = skillOpt.get();
                                                        CompoundTag tag = inst.getOrCreateTag();
                                                        tag.putFloat("recordPoints", amount);
                                                        inst.markDirty();
                                                        context.getSource().sendSuccess(Component.literal("§d[Bibliomania] §fSet Record Points for " + targetPlayer.getScoreboardName() + " to §b" + amount), true);
                                                    } else {
                                                        context.getSource().sendFailure(Component.literal("§cError: Target does not have the Bibliomania skill."));
                                                    }
                                                    return 1;
                                                })
                                        )
                                )
                        )
                )


                // World Trials
                .then(Commands.literal("trials")
                        .then(Commands.literal("get").executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();

                            VoiceOfTheWorld.delayedAnnouncement(player,
                                    "Inquiry received.",
                                    "Scanning internal soul progress...",
                                    "Current Trials: " + WorldTrialRegistry.getActiveTrialStatus(player)
                            );
                            return 1;
                        }))

                        .requires(source -> source.hasPermission(4))
                        // Clear
                        .then(Commands.literal("clear")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(context -> {
                                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                            TrialManager.clearActiveTrial(target);
                                            context.getSource().sendSuccess(Component.literal("§a[Mythos] Cleared active trials for " + target.getScoreboardName()), true);
                                            return 1;
                                        })
                                )
                        )
                        // Set
                        .then(Commands.literal("set")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.argument("trialId", StringArgumentType.word())
                                                .executes(context -> {
                                                    ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                                    String trialId = StringArgumentType.getString(context, "trialId");

                                                    if (!WorldTrialRegistry.TRIALS.containsKey(trialId)) {
                                                        context.getSource().sendFailure(Component.literal("§cError: Trial ID '" + trialId + "' not found in registry."));
                                                        return 0;
                                                    }

                                                    TrialManager.clearActiveTrial(target);
                                                    TrialManager.initiateTrial(target, trialId);

                                                    context.getSource().sendSuccess(Component.literal("§a[Mythos] Forced trial '" + trialId + "' onto " + target.getScoreboardName()), true);
                                                    return 1;
                                                })
                                        )
                                )
                        )
                )
                // Config
                .then(Commands.literal("config")
                        .requires(source -> source.hasPermission(4))
                        // Vampire Ancestor
                        .then(Commands.literal("vampire_ancestor")
                                .then(Commands.argument("value", BoolArgumentType.bool()).executes(context -> {
                                    boolean val = BoolArgumentType.getBool(context, "value");
                                    MythosSkillsConfig.VampireAncestor.set(val);
                                    context.getSource().sendSuccess(Component.literal("§6[Mythos Config] §fVampireAncestor set to: " + val), true);
                                    return 1;
                                })))
                        // Dead Apostle Ancestor
                        .then(Commands.literal("dead_apostle_ancestor")
                                .then(Commands.argument("value", BoolArgumentType.bool()).executes(context -> {
                                    boolean val = BoolArgumentType.getBool(context, "value");
                                    MythosSkillsConfig.DeadApostleAncestor.set(val);
                                    context.getSource().sendSuccess(Component.literal("§6[Mythos Config] §fDeadApostleAncestor set to: " + val), true);
                                    return 1;
                                })))
                        // Vampire Carnage
                        .then(Commands.literal("vampire_carnage")
                                .then(Commands.argument("value", BoolArgumentType.bool()).executes(context -> {
                                    boolean val = BoolArgumentType.getBool(context, "value");
                                    MythosSkillsConfig.VampireCarnage.set(val);
                                    context.getSource().sendSuccess(Component.literal("§6[Mythos Config] §fVampireCarnage set to: " + val), true);
                                    return 1;
                                })))
                        // End of Evil Reset
                        .then(Commands.literal("end_of_evil_reset")
                                .then(Commands.argument("value", BoolArgumentType.bool()).executes(context -> {
                                    boolean val = BoolArgumentType.getBool(context, "value");
                                    MythosSkillsConfig.endOfEvilReset.set(val);
                                    context.getSource().sendSuccess(Component.literal("§6[Mythos Config] §fendOfEvilReset set to: " + val), true);
                                    return 1;
                                })))
                        // Apophis Embodiment
                        .then(Commands.literal("apophis_embodiment")
                                .then(Commands.argument("value", BoolArgumentType.bool()).executes(context -> {
                                    boolean val = BoolArgumentType.getBool(context, "value");
                                    MythosSkillsConfig.ApophisEmbodiment.set(val);
                                    context.getSource().sendSuccess(Component.literal("§6[Mythos Config] §fApophisEmbodiment set to: " + val), true);
                                    return 1;
                                })))
                        // Enable Ultimate Skill Obtainment
                        .then(Commands.literal("enable_ultimate_skills")
                                .then(Commands.argument("value", BoolArgumentType.bool()).executes(context -> {
                                    boolean val = BoolArgumentType.getBool(context, "value");
                                    MythosSkillsConfig.EnableUltimateSkillObtainment.set(val);
                                    context.getSource().sendSuccess(Component.literal("§6[Mythos Config] §fEnableUltimateSkillObtainment set to: " + val), true);
                                    return 1;
                                })))

                        .then(Commands.literal("announce_ultimates")
                                .requires(source -> source.hasPermission(4))
                                .then(Commands.literal("on").executes(context -> {
                                    GodClassHandler.get(context.getSource().getLevel()).setAnnouncementsEnabled(true);
                                    context.getSource().sendSuccess(Component.literal("§a[Mythos] Announce Ultimate Skills is now enabled."), true);
                                    return 1;
                                }))
                                .then(Commands.literal("off").executes(context -> {
                                    GodClassHandler.get(context.getSource().getLevel()).setAnnouncementsEnabled(false);
                                    context.getSource().sendSuccess(Component.literal("§c[Mythos] Announce Ultimate Skills is now disabled."), true);
                                    return 1;
                                }))
                        )
                )
        );
    }
}