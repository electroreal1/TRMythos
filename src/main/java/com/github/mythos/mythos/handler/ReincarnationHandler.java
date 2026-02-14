package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.race.Race;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = "trmythos")
public class ReincarnationHandler {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (SkillUtils.hasSkill(player, Skills.REINCARNATOR.get())) {
            CompoundTag persistTag = player.getPersistentData();
            persistTag.putBoolean("ShouldReincarnate", true);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            boolean reincarnating = event.getOriginal().getPersistentData().getBoolean("ShouldReincarnate");
            event.getEntity().getPersistentData().putBoolean("ShouldReincarnate", reincarnating);
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

            randomizeRace(player);
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

    private static void randomizeRace(ServerPlayer player) {
        List<? extends String> racePool = MythosSkillsConfig.REINCARNATION_RACES.get();
        if (racePool.isEmpty()) return;

        String randomRaceId = racePool.get(player.getRandom().nextInt(racePool.size()));
        ResourceLocation raceLoc = new ResourceLocation(randomRaceId);

        var registry = player.level.registryAccess()
                .registry(ResourceKey.createRegistryKey(new ResourceLocation("tensura", "races")))
                .orElse(null);

        if (registry != null) {
            Race selectedRace = (Race) registry.get(raceLoc);

            if (selectedRace != null) {
                TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
                    cap.setRace(player, selectedRace, true);

                    TensuraPlayerCapability.sync(player);
                });
                playReincarnationSound(player);
            }
        }
    }
}
