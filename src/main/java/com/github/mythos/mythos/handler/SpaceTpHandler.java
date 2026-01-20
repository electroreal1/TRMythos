package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.Optional;

@Mod.EventBusSubscriber
public class SpaceTpHandler {

    private static final ResourceLocation SPATIAL_DOMINATION =
            new ResourceLocation("tensura", "spatial_domination");

    private static final ResourceLocation SPATIAL_MOTION =
            new ResourceLocation("tensura", "spatial_motion");

    private static final ResourceLocation IN_COMBAT_EFFECT =
            new ResourceLocation("tensura", "in_combat");

    @SubscribeEvent
    public static void onCommand(CommandEvent event) {

        if (!(event.getParseResults().getContext().getSource().getEntity() instanceof ServerPlayer player))
            return;

        if (player.gameMode.getGameModeForPlayer() == GameType.CREATIVE || player.hasPermissions(2))
            return;

        String input = event.getParseResults().getReader().getString();

        if (!input.startsWith("tp ") && !input.startsWith("teleport "))
            return;

        MobEffect inCombat = ForgeRegistries.MOB_EFFECTS.getValue(IN_COMBAT_EFFECT);
        if (inCombat != null && player.hasEffect(inCombat)) {
            deny(player, event, "Space refuses to bend during combat.");
            return;
        }

        var storage = SkillAPI.getSkillsFrom(player);

        Optional<ManasSkillInstance> spatialDom =
                storage.getSkill(Objects.requireNonNull(SkillAPI.getSkillRegistry().getValue(SPATIAL_DOMINATION)));

        Optional<ManasSkillInstance> spatialMotion =
                storage.getSkill(Objects.requireNonNull(SkillAPI.getSkillRegistry().getValue(SPATIAL_MOTION)));

        if (spatialDom.isEmpty() || spatialMotion.isEmpty()) {
            deny(player, event, "Your spatial authority is insufficient.");
            return;
        }

        if (!spatialDom.get().isMastered(player)
                || !spatialMotion.get().isMastered(player)) {
            deny(player, event, "Your spatial authority is insufficient.");
            return;
        }

        if (containsEntityArgument(event)) {
            deny(player, event, "You cannot rewrite another will.");
        }
    }

    private static boolean containsEntityArgument(CommandEvent event) {
        return event.getParseResults()
                .getContext()
                .getArguments()
                .values()
                .stream()
                .anyMatch(arg -> arg.getResult() instanceof EntityArgument);
    }

    private static void deny(ServerPlayer player, CommandEvent event, String message) {
        event.setCanceled(true);
        player.sendSystemMessage(
                Component.literal(message)
                        .withStyle(net.minecraft.ChatFormatting.DARK_PURPLE)
        );
    }
}
