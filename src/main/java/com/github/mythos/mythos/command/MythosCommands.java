package com.github.mythos.mythos.command;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.mythos.mythos.handler.GodClassHandler;
import com.github.mythos.mythos.registry.skill.Skills;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
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

                // GODCLASS
                .then(Commands.literal("godclass")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.literal("reset")
                                .then(Commands.literal("all").executes(context -> {
                                    GodClassHandler.get(context.getSource().getLevel()).resetAllOwners();
                                    context.getSource().sendSuccess(Component.literal("§c[Mythos] All God statuses have been wiped."), true);
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

                // BIBLIOMANIA
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
        );
    }
}