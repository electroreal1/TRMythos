package com.github.mythos.mythos.command;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.mythos.mythos.handler.GodClassHandler;
import com.github.mythos.mythos.registry.skill.Skills;
import com.github.mythos.mythos.voiceoftheworld.TrialManager;
import com.github.mythos.mythos.voiceoftheworld.VoiceOfTheWorld;
import com.github.mythos.mythos.voiceoftheworld.WorldTrialRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

import static com.github.mythos.mythos.voiceoftheworld.WorldTrialRegistry.getActiveTrials;

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
                                    "Current Trials: " + getActiveTrials(player)
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
                                            context.getSource().sendSuccess(Component.literal("§a[Mythos] Cleared active trial lock for " + target.getScoreboardName()), true);
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
        );
    }
}