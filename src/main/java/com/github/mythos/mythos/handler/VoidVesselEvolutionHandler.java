package com.github.mythos.mythos.handler;

import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.race.RaceHelper;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.github.mythos.mythos.util.MythosUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "trmythos", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VoidVesselEvolutionHandler {

    public static final Set<UUID> evolvedThisTick = new HashSet<>();
    public static final Set<UUID> messageSentThisTick = new HashSet<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if ((event.player.tickCount & 5) != 0)
                return;

            Player player = event.player;
            UUID playerId = player.getUUID();
            if (MythosUtils.isEmpty(player) && !evolvedThisTick.contains(playerId)) {
                evolvePlayerIntoEmptyBody(player);
                evolvedThisTick.add(playerId);
            }
            evolvedThisTick.clear();
            messageSentThisTick.clear();

        }
    }

    private static void evolvePlayerIntoEmptyBody(Player player) {
        TensuraPlayerCapability.getFrom(player).ifPresent(data -> data.setTrackedEvolution(player, null));
        Race playerRace = TensuraPlayerCapability.getRace(player);
        IForgeRegistry<Race> raceRegistry = TensuraRaces.RACE_REGISTRY.get();

        if (raceRegistry != null && playerRace != null) {
            if (playerRace.getClass().getName().contains("saint")) {
                evolvePlayerEmpty(player, raceRegistry);
            }
        }
    }

    private static void evolvePlayerEmpty(Player player, IForgeRegistry<Race> registry) {
        UUID playerId = player.getUUID();
        Race targetRace = registry.getValue(MythosRaces.EMPTY_BODY);
        if (targetRace != null) {
            RaceHelper.evolveRace(player, targetRace, true, true);
            if (player.level.isClientSide()) return;

            if (!messageSentThisTick.contains(playerId)) {
                player.sendSystemMessage(Component.literal("You fill your body empty from within and become an empty shell.").withStyle(ChatFormatting.GRAY));
                player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1, 0.1f);
                messageSentThisTick.add(playerId);
            }
        }
    }
}
