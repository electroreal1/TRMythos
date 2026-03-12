//package com.github.mythos.mythos.handler;
//
//import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
//import com.github.manasmods.manascore.api.skills.SkillAPI;
//import net.minecraft.commands.arguments.EntityArgument;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.level.GameType;
//import net.minecraftforge.event.CommandEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
//import java.util.Objects;
//import java.util.Optional;
//
//@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
//public class SpaceTpHandler {
//
//    private static final ResourceLocation SPATIAL_DOMINATION =
//            new ResourceLocation("tensura", "spatial_domination");
//
//    private static final ResourceLocation SPATIAL_MOTION =
//            new ResourceLocation("tensura", "spatial_motion");
//
//    @SubscribeEvent
//    public static void onCommand(CommandEvent event) {
//
//        if (!(event.getParseResults().getContext().getSource().getEntity() instanceof ServerPlayer player))
//            return;
//
//        if (player.gameMode.getGameModeForPlayer() == GameType.CREATIVE || player.hasPermissions(2))
//            return;
//
//        var nodes = event.getParseResults().getContext().getNodes();
//
//        if (nodes.isEmpty())
//            return;
//
//        String command = nodes.get(0).getNode().getName();
//
//        if (!command.equals("tp") && !command.equals("teleport"))
//            return;
//
//        var registry = SkillAPI.getSkillRegistry();
//
//        var domSkill = registry.getValue(SPATIAL_DOMINATION);
//        var motionSkill = registry.getValue(SPATIAL_MOTION);
//
//        if (domSkill == null || motionSkill == null)
//            return;
//
//        var storage = SkillAPI.getSkillsFrom(player);
//
//        Optional<ManasSkillInstance> spatialDom = storage.getSkill(domSkill);
//        Optional<ManasSkillInstance> spatialMotion = storage.getSkill(motionSkill);
//
//        if (spatialDom.isEmpty() || spatialMotion.isEmpty()) {
//            deny(player, event, "Your spatial authority is insufficient.");
//            return;
//        }
//
//        if (!spatialDom.get().isMastered(player)
//                || !spatialMotion.get().isMastered(player)) {
//            deny(player, event, "Your spatial authority is insufficient.");
//            return;
//        }
//
//        if (containsEntityArgument(event)) {
//            deny(player, event, "You cannot rewrite another will.");
//        }
//    }
//
//    private static boolean containsEntityArgument(CommandEvent event) {
//        return event.getParseResults()
//                .getContext()
//                .getArguments()
//                .values()
//                .stream()
//                .anyMatch(arg -> arg.getResult() instanceof EntityArgument);
//    }
//
//    private static void deny(ServerPlayer player, CommandEvent event, String message) {
//        event.setCanceled(true);
//        player.sendSystemMessage(
//                Component.literal(message)
//                        .withStyle(net.minecraft.ChatFormatting.DARK_PURPLE)
//        );
//    }
//}