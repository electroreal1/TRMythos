package com.github.mythos.mythos.handler;

import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.config.MythosContagionConfig;
import com.github.mythos.mythos.networking.MythosNetwork;
import com.github.mythos.mythos.networking.play2server.OpenContagionGuiPacket;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "trmythos")
public class ContagionHandler {
    private static final UUID ENVY_HEALTH_UUID = UUID.fromString("f47e3b9d-d860-4229-8886-7fd1a93b03c6");

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            if (SkillUtils.hasSkill(player, Skills.CONTAGION.get())) {
                awardBiomatter(player, 1);
            }
        }
    }

    public static void awardBiomatter(Player player, int amount) {
        CompoundTag contagionTag = getContagionTag(player);

        if (SkillUtils.hasSkill(player, UniqueSkills.HEALER.get()) || SkillUtils.hasSkill(player, Skills.EVOLUTION.get())) {
            amount = 5;
        }

        contagionTag.putInt("Biomatter", contagionTag.getInt("Biomatter") + amount);
    }

    public static CompoundTag getContagionTag(Player player) {
        CompoundTag forgeData = player.getPersistentData();
        if (!forgeData.contains("ContagionData")) {
            forgeData.put("ContagionData", new CompoundTag());
        }
        return forgeData.getCompound("ContagionData");
    }

    public static int getBiomatter(Player player) {
        return getContagionTag(player).getInt("Biomatter");
    }

    public static void addBiomatter(Player player, int amount) {
        CompoundTag tag = getContagionTag(player);
        tag.putInt("Biomatter", tag.getInt("Biomatter") + amount);
    }

    public static int getMutationLevel(Player player, String trait) {
        CompoundTag tag = getContagionTag(player);
        if (!tag.contains("MutationPaths")) {
            tag.put("MutationPaths", new CompoundTag());
        }
        return tag.getCompound("MutationPaths").getInt(trait);
    }

    public static void incrementPath(Player player, String path) {
        CompoundTag contagion = getContagionTag(player);
        if (!contagion.contains("MutationPaths")) {
            contagion.put("MutationPaths", new CompoundTag());
        }
        CompoundTag paths = contagion.getCompound("MutationPaths");
        paths.putInt(path, paths.getInt(path) + 1);
    }

    public static boolean isListeningForMutation(Player player) {
        return getContagionTag(player).getBoolean("IsMutating");
    }

    public static void setListening(Player player, boolean state) {
        getContagionTag(player).putBoolean("IsMutating", state);
    }

    @SubscribeEvent
    public static void onInfect(MobEffectEvent.Added event) {
        if (event.getEffectInstance().getEffect() == MythosMobEffects.PATHOGEN.get()) {
            LivingEntity victim = event.getEntity();
            CompoundTag tag = victim.getPersistentData();

            if (tag.hasUUID("ContagionSource")) {
                UUID infectorUUID = tag.getUUID("ContagionSource");
                Player infector = victim.level.getPlayerByUUID(infectorUUID);
                if (infector != null) {
                    awardBiomatter(infector, 1);
                }
            }
        }
    }

    public static void tryViralTakeover(LivingEntity victim, UUID currentHost, UUID attackerUUID) {
        if (victim.level.isClientSide) return;

        Player hostPlayer = victim.level.getPlayerByUUID(currentHost);
        Player attackerPlayer = victim.level.getPlayerByUUID(attackerUUID);

        if (attackerPlayer == null) return;

        int hostAntigen = hostPlayer != null ? getMutationLevel(hostPlayer, "Antigen") : 0;
        int attackerAggression = getMutationLevel(attackerPlayer, "Aggression");

        if (attackerAggression > hostAntigen) {
            victim.getPersistentData().putUUID("ContagionSource", attackerUUID);
            victim.level.playSound(null, victim.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.HOSTILE, 1.0f, 0.5f);
        } else {
            attackerPlayer.hurt(TensuraDamageSources.infection(hostPlayer), 1.0f);
        }
    }

    @SubscribeEvent
    public static void onMutationChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();

        if (player == null) return;

        if (!isListeningForMutation(player)) return;

        String rawMsg = event.getRawText();
        if (rawMsg == null || rawMsg.trim().isEmpty() || rawMsg.startsWith("/")) return;

        event.setCanceled(true);

        String cleanMsg = rawMsg.trim().toLowerCase();
        handleMutationLogic(player, cleanMsg);
    }

    public static void handleMutationLogic(ServerPlayer player, String pathArg) {
        String matchedPath = null;

        for (String pathName : MythosContagionConfig.ENABLED_PATHS.keySet()) {
            if (pathName.equalsIgnoreCase(pathArg)) {
                matchedPath = pathName;
                break;
            }
        }

        if (matchedPath != null) {
            int currentLvl = getMutationLevel(player, matchedPath);

            var maxLvlSetting = MythosContagionConfig.MAX_LEVELS.get(matchedPath);
            if (maxLvlSetting == null) return;

            int maxLvl = maxLvlSetting.get();
            int biomatter = getBiomatter(player);
            int cost = 1000 + (currentLvl * 500);

            if (currentLvl < maxLvl) {
                if (biomatter >= cost) {
                    // 2. Apply the mutation
                    incrementPath(player, matchedPath);
                    addBiomatter(player, -cost);

                    player.displayClientMessage(Component.literal("§2[Contagion] §a" + matchedPath + " §7evolved to Level " + (currentLvl + 1)), false);
                    player.level.playSound(null, player.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 1.0f, 1.0f);

                    openMutationGui(player);
                } else {
                    player.displayClientMessage(Component.literal("§cInsufficient Biomatter! Need: " + cost), true);
                    openMutationGui(player);
                }
            } else {
                player.displayClientMessage(Component.literal("§6" + matchedPath + " is already maxed."), true);
            }
        }
    }

    public static void openMutationGui(ServerPlayer player) {
        int bio = getBiomatter(player);
        Map<String, Integer> levels = new HashMap<>();

        System.out.println("Contagion Debug: Finding paths for " + player.getName().getString());

        for (String path : MythosContagionConfig.ENABLED_PATHS.keySet()) {
            if (MythosContagionConfig.ENABLED_PATHS.get(path).get()) {
                levels.put(path, getMutationLevel(player, path));
            }
        }

        System.out.println("Contagion Debug: Found " + levels.size() + " enabled paths.");

        if (levels.isEmpty()) {
            player.sendSystemMessage(Component.literal("§cError: No mutation paths enabled in config!"));
        }

        MythosNetwork.sendToPlayer(new OpenContagionGuiPacket(bio, levels), player);
    }

    public static void applyEnvySteal(Player source, LivingEntity victim, int level) {
        if (victim.level.isClientSide) return;
        var sHealthAttr = source.getAttribute(Attributes.MAX_HEALTH);
        if (sHealthAttr != null) {
            double amount = level * 2.0;
            sHealthAttr.removeModifier(ENVY_HEALTH_UUID);
            sHealthAttr.addTransientModifier(new AttributeModifier(ENVY_HEALTH_UUID, "Envy Health Theft", amount, AttributeModifier.Operation.ADDITION));
            source.heal((float) amount);
        }
    }
}