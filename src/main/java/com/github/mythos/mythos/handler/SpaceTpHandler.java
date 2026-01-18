package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber
public class SpaceTpHandler {

    private static final ResourceLocation SPATIAL_DOMINATION = new ResourceLocation("tensura", "spatial_domination");

    private static final ResourceLocation SPATIAL_MOTION = new ResourceLocation("tensura", "spatial_motion");

    @SubscribeEvent
    public static void onCommand(CommandEvent event) {

        if (!(event.getParseResults().getContext().getSource().getEntity() instanceof ServerPlayer player)) return;


        if (player.gameMode.getGameModeForPlayer() == GameType.CREATIVE || player.hasPermissions(2)) {
            return;
        }

        String input = event.getParseResults().getReader().getString();

        if (!input.startsWith("tp ") && !input.startsWith("teleport ")) return;

        var storage = SkillAPI.getSkillsFrom(player);
        if (storage == null) return;

        Optional<ManasSkillInstance> spatialDom = storage.getSkill(SkillAPI.getSkillRegistry().getValue(SPATIAL_DOMINATION));

        Optional<ManasSkillInstance> spatialMotion = storage.getSkill(SkillAPI.getSkillRegistry().getValue(SPATIAL_MOTION));

        if (spatialDom.isEmpty() || spatialMotion.isEmpty()) {
            deny(player, event);
            return;
        }

        if (!spatialDom.get().isMastered(player) || !spatialMotion.get().isMastered(player)) {
            deny(player, event);
            return;
        }

        if (containsEntityArgument(event)) {
            deny(player, event);
        }
    }

    private static boolean containsEntityArgument(CommandEvent event) {
        return event.getParseResults().getContext().getArguments().values().stream().anyMatch(arg -> arg.getResult() instanceof EntityArgument);
    }

    private static void deny(ServerPlayer player, CommandEvent event) {
        event.setCanceled(true);
        player.sendSystemMessage(Component.literal("Your spatial authority is insufficient.").withStyle(net.minecraft.ChatFormatting.DARK_PURPLE));
    }
}
