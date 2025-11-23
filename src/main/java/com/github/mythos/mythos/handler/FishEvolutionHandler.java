package com.github.mythos.mythos.handler;

import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.race.RaceHelper;
import com.github.manasmods.tensura.race.merfolk.DivineFishRace;
import com.github.manasmods.tensura.race.merfolk.EnlightenedMerfolkRace;
import com.github.manasmods.tensura.race.merfolk.MerfolkRace;
import com.github.manasmods.tensura.race.merfolk.MerfolkSaintRace;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.mythos.mythos.registry.race.MythosRaces;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "trmythos", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FishEvolutionHandler {
    public static final Set<UUID> evolvedThisTick = new HashSet<>();
    public static final Set<UUID> messageSentThisTick = new HashSet<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (event.player.tickCount % 5 != 0)
                return;
            Player player = event.player;
            UUID playerId = player.getUUID();
            if (TensuraEPCapability.isMajin((LivingEntity)player) && !evolvedThisTick.contains(playerId)) {
                evolvePlayerIntoSeaBeast(player);
                evolvedThisTick.add(playerId);
            }
            evolvedThisTick.clear();
            messageSentThisTick.clear();
        }
    }

    private static void evolvePlayerIntoSeaBeast(Player player) {
        TensuraPlayerCapability.getFrom(player).ifPresent(data -> data.setTrackedEvolution(player, (Race)null));
        Race playerRace = TensuraPlayerCapability.getRace((LivingEntity)player);
        IForgeRegistry<Race> raceRegistry = TensuraRaces.RACE_REGISTRY.get();
        if (raceRegistry != null && playerRace != null)
            if (playerRace.getClass() == MerfolkRace.class) {
                evolvePlayer(player, raceRegistry, MythosRaces.SEA_BEAST);
            } else if (playerRace.getClass() == EnlightenedMerfolkRace.class) {
                evolvePlayer(player, raceRegistry, MythosRaces.SEA_SERPENT);
            } else if (playerRace.getClass() == MerfolkSaintRace.class) {
                evolvePlayer(player, raceRegistry, MythosRaces.DARK_SEA_STALKER);
            } else if (playerRace.getClass() == DivineFishRace.class) {
                evolvePlayer(player, raceRegistry, MythosRaces.DARK_SEA_TYRANT);
            }
    }

    private static void evolvePlayer(Player player, IForgeRegistry<Race> registry, ResourceLocation targetRaceKey) {
        UUID playerId = player.getUUID();
        Race targetRace = (Race)registry.getValue(targetRaceKey);
        if (targetRace != null)
            RaceHelper.evolveRace(player, targetRace, true, true);
        if (player.level.isClientSide())
            return;
        if (!messageSentThisTick.contains(playerId)) {
            player.sendSystemMessage((Component)Component.literal("Something shifts within your body and soul. A new monster has been born...").withStyle(ChatFormatting.BLUE));
            player.level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            messageSentThisTick.add(playerId);
        }
    }
}