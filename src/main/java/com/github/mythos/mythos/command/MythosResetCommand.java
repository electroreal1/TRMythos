package com.github.mythos.mythos.command;

import com.github.mythos.mythos.handler.GodClassHandler;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class MythosResetCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("mythos").requires(source -> source.hasPermission(4)).then(Commands.literal("reset")
                .then(Commands.literal("all").executes(context -> {
            GodClassHandler.get(context.getSource().getLevel()).resetAllOwners();
            context.getSource().sendSuccess(Component.literal("§c[Mythos] All God statuses have been wiped."), true);
            return 1;
        })).then(Commands.literal("dendrahh").executes(context -> {
            GodClassHandler.get(context.getSource().getLevel()).setDendrahhObtained(false);
            context.getSource().sendSuccess(Component.literal("§e[Mythos] Dendrahh status reset."), true);
            return 1;
        })).then(Commands.literal("khonsu").executes(context -> {
            GodClassHandler.get(context.getSource().getLevel()).setKhonsuObtained(false);
            context.getSource().sendSuccess(Component.literal("§e[Mythos] Khonsu status reset."), true);
            return 1;
        })).then(Commands.literal("kthanid").executes(context -> {
            GodClassHandler.get(context.getSource().getLevel()).setKthanidObtained(false);
            context.getSource().sendSuccess(Component.literal("§e[Mythos] Kthanid status reset."), true);
            return 1;
        }))).then(Commands.literal("status").executes(context -> {
            GodClassHandler handler = GodClassHandler.get(context.getSource().getLevel());
            String status = String.format("§7Dendrahh: %b | Khonsu: %b | Kthanid: %b",
                    handler.isDendrahhObtained(), handler.isKhonsuObtained(), handler.isKthanidObtained());

            context.getSource().sendSuccess(Component.literal(status), false);
            return 1;
        })));
    }
}