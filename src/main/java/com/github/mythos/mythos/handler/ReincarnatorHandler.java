package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.RemoveSkillEvent;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.magic.Magic;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.resist.ResistSkill;
import com.github.manasmods.tensura.ability.skill.unique.CookSkill;
import com.github.manasmods.tensura.capability.effects.TensuraEffectsCapability;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.menu.RaceSelectionMenu;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.StatType;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.github.manasmods.tensura.item.custom.ResetScrollItem.*;

@Mod.EventBusSubscriber(modid = "trmythos")
public class ReincarnatorHandler {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        SkillStorage storage = SkillAPI.getSkillsFrom(player);

        storage.getLearnedSkills().removeIf(ManasSkillInstance::isTemporarySkill);

        if (SkillUtils.hasSkill(player, Skills.REINCARNATOR.get())) {
            CompoundTag persistTag = player.getPersistentData();
            persistTag.putBoolean("ShouldReincarnate", true);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {

            CompoundTag oldData = event.getOriginal().getPersistentData();
            CompoundTag newData = event.getEntity().getPersistentData();

            if (oldData.getBoolean("ShouldReincarnate")) {
                newData.putBoolean("ShouldReincarnate", true);
            }

            if (oldData.contains("MythosMemoryShards")) {
                newData.putInt("MythosMemoryShards", oldData.getInt("MythosMemoryShards"));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        CompoundTag persistTag = player.getPersistentData();

        if (persistTag.getBoolean("ShouldReincarnate")) {
            persistTag.remove("ShouldReincarnate");

            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            List<ManasSkill> toForget = storage.getLearnedSkills().stream()
                    .map(ManasSkillInstance::getSkill)
                    .filter(s -> s != Skills.REINCARNATOR.get())
                    .toList();

            toForget.forEach(storage::forgetSkill);

            resetRace(player);
        }
    }

    private static void playReincarnationSound(ServerPlayer player) {
        Level level = player.level;
        BlockPos pos = player.blockPosition();

        level.playSound(null, pos, SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0f, 0.5f);

        level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.2f, 1.3f);

        level.playSound(null, pos, SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 0.5f, 1.8f);
        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 0.7f);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 1, player.getZ(), 5, 0.2, 0.2, 0.2, 0);
            serverLevel.sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 1, player.getZ(), 100, 0.5, 1.0, 0.5, 0.1);
        }
    }

    public static void resetRace(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server != null) {
            ServerStatsCounter stats = server.getPlayerList().getPlayerStats(player);

            for (StatType<?> type : ForgeRegistries.STAT_TYPES) {
                resetStatType(player, stats, type);
            }

            stats.markAllDirty();
        }

        resetRaceFailsafe(player);
        TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
            if (cap.getRace() != null) {
                SkillStorage storage = SkillAPI.getSkillsFrom(player);
                Iterator<ManasSkillInstance> iterator = storage.getLearnedSkills().iterator();

                label37:
                while (true) {
                    TensuraSkillInstance instance;
                    do {
                        Object patt12668$temp;
                        do {
                            if (!iterator.hasNext()) {
                                storage.syncAll();
                                break label37;
                            }
                            patt12668$temp = iterator.next();
                        } while (!(patt12668$temp instanceof TensuraSkillInstance));

                        instance = (TensuraSkillInstance) patt12668$temp;

                        // --- REINCARNATOR PROTECTION ---
                        ResourceLocation skillId = instance.getSkill().getRegistryName();
                        if (skillId != null && skillId.getNamespace().equals("trmythos") && skillId.getPath().equals("reincarnator")) {
                            continue;
                        }
                        // -------------------------------

                    } while (!isIntrinsicSkills(player, cap, cap.getRace(), instance) && !(instance.getSkill() instanceof Magic) && !(instance.getSkill() instanceof ResistSkill));

                    if (!MinecraftForge.EVENT_BUS.post(new RemoveSkillEvent(instance, player))) {
                        iterator.remove();
                    }
                }
            }

            cap.clearIntrinsicSkills();
            TensuraPlayerCapability.resetEverything(player);
            if (SkillUtils.hasSkill(player, UniqueSkills.CHOSEN_ONE.get())) {
                cap.setBlessed(true);
                TensuraPlayerCapability.sync(player);
            }
        });

        grantTemporaryUniqueSkill(player);
        CookSkill.removeCookedHP(player, null);
        TensuraEPCapability.resetEverything(player);
        TensuraSkillCapability.resetEverything(player, false, true);
        TensuraEffectsCapability.resetEverything(player, true, true);
        player.setRespawnPosition(Level.OVERWORLD, null, 0.0F, false, false);
        List<ResourceLocation> races = TensuraPlayerCapability.loadRaces();
        NetworkHooks.openScreen(player, new SimpleMenuProvider(RaceSelectionMenu::new, Component.translatable("tensura.race.selection")), (buf) -> {
            buf.writeBoolean(true);
            buf.writeCollection(races, FriendlyByteBuf::writeResourceLocation);
        });
        RaceSelectionMenu.grantLearningResistance(player);
        resetFlight(player);
        playReincarnationSound(player);
    }

    private static Skill chooseRandomUnique(ServerPlayer player) {
        List<Skill> list = new ArrayList<>();
        for (ManasSkill skill : SkillAPI.getSkillRegistry()) {
            if (skill instanceof Skill s) {
                if (s.getType() == Skill.SkillType.UNIQUE && !SkillUtils.hasSkill(player, s) && s.getRegistryName() != null) {
                    list.add(s);
                }
            }
        }

        return list.isEmpty() ? null : list.get(player.getRandom().nextInt(list.size()));
    }

    private static void grantTemporaryUniqueSkill(ServerPlayer player) {
        Skill uniquePool = chooseRandomUnique(player);
        if (uniquePool == null) return;

        ManasSkillInstance tempUnique = new ManasSkillInstance(uniquePool);
        tempUnique.getOrCreateTag().putBoolean("NoMagiculeCost", true);
        boolean success = SkillUtils.learnSkill(player, tempUnique);
        if (!success) return;


        player.sendSystemMessage(Component.literal("The Voice of the World grants you a temporary soul: ")
                .append(Objects.requireNonNull(tempUnique.getSkill().getName()))
                .withStyle(ChatFormatting.LIGHT_PURPLE), false);

    }

    private static <T> void resetStatType(Player player, ServerStatsCounter stats, StatType<T> type) {
        for (T value : type.getRegistry()) {
            stats.setValue(player, type.get(value), 0);
        }
    }

    @SubscribeEvent
    public static void MemoryShards(UnlockSkillEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (SkillUtils.hasSkill(player, Skills.REINCARNATOR.get())) {
            if (player.getRandom().nextInt(10) == 0) {
                CompoundTag data = player.getPersistentData();

                int currentShards = data.getInt("MemoryShards");
                data.putInt("MemoryShards", currentShards + 1);

                player.displayClientMessage(Component.literal("You have obtained a Memory Shard!")
                        .withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD), true);

                player.level.playSound(null, player.blockPosition(),
                        SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 1.2f);


            }
        }
    }
}
