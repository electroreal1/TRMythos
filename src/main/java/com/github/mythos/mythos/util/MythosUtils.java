package com.github.mythos.mythos.util;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.mythos.mythos.registry.skill.Skills;
import com.github.mythos.mythos.voiceoftheworld.VoiceOfTheWorld;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;
import java.util.Random;
@Mod.EventBusSubscriber(modid = "trmythos")
public class MythosUtils  {

    public MythosUtils() {
    }
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            CompoundTag forgeData = serverPlayer.getPersistentData();
            CompoundTag persistentData;

            if (!forgeData.contains(Player.PERSISTED_NBT_TAG)) {
                persistentData = new CompoundTag();
                forgeData.put(Player.PERSISTED_NBT_TAG, persistentData);
            } else {
                persistentData = forgeData.getCompound(Player.PERSISTED_NBT_TAG);
            }

            if (!persistentData.getBoolean("Mythos_FirstLoginHandled")) {
                persistentData.putBoolean("Mythos_FirstLoginHandled", true);

                forgeData.put(Player.PERSISTED_NBT_TAG, persistentData);

                VoiceOfTheWorld.welcomeNewIndividual(serverPlayer);

                var race = TensuraPlayerCapability.getRace(serverPlayer);
                if (race != null && race.equals(TensuraRaces.WIGHT.get())) {
                    Random random = new Random();

                    if (random.nextInt(100) < 10) {
                        grantStarterSkill(serverPlayer, Skills.ELTNAM.get(),
                                Component.translatable("trmythos.skill.eltnam.obtained").withStyle(ChatFormatting.RED));
                    }

                    if (random.nextInt(100) < 10) {
                        grantStarterSkill(serverPlayer, Skills.CRIMSON_TYRANT.get(),
                                Component.literal("The Crimson Moon Far Away Extends Its Bloody Blessing, Let the Carnage Begin!").withStyle(ChatFormatting.DARK_RED));
                    }

                    if (random.nextInt(100) < 10) {
                        grantStarterSkill(serverPlayer, Skills.UNDERWORLD_PRINCE.get(),
                                Component.literal("The Underworld Beckons you to embrace souls of the damned!").withStyle(ChatFormatting.BLACK));
                    }
                }
            }
        }
    }
//
//    public static boolean isCustomAlignment(Player player) {
//        return player.getCapability(TensuraPlayerCapability.CAPABILITY).map(cap -> {
//            if (cap instanceof ICustomAlignmentAccessor accessor) {
//                return accessor.isCustomAlignment();
//            }
//            return false;
//        }).orElse(false);
//    }
//
//    public static void setCustomAlignment(Player player, boolean value) {
//        player.getCapability(TensuraPlayerCapability.CAPABILITY).ifPresent(cap -> {
//            if (cap instanceof ICustomAlignmentAccessor accessor) {
//                accessor.setCustomAlignment(value);
//                TensuraPlayerCapability.sync(player);
//            }
//        });
//    }

    private static void grantStarterSkill(ServerPlayer player, ManasSkill skill, Component message) {
        if (skill == null) return;
        TensuraSkillInstance skillInstance = new TensuraSkillInstance(skill);
        skillInstance.getOrCreateTag().putBoolean("NoMagiculeCost", true);

        if (SkillAPI.getSkillsFrom(player).learnSkill(skillInstance)) {
            player.sendSystemMessage(message);
            SkillAPI.getSkillsFrom(player).syncPlayer(player);
        }
    }

    public static LivingEntity getLookedAtEntity(LivingEntity user, double distance) {
        Vec3 eyePos = user.getEyePosition();
        Vec3 lookVec = user.getLookAngle();
        Vec3 endPos = eyePos.add(lookVec.scale(distance));

        AABB box = user.getBoundingBox().expandTowards(lookVec.scale(distance)).inflate(1.0D);
        LivingEntity nearest = null;
        double nearestDist = distance * distance;

        for (LivingEntity target : user.level.getEntitiesOfClass(LivingEntity.class, box, e -> e != user && e.isAlive())) {
            AABB targetBox = target.getBoundingBox().inflate(0.3D);
            Optional<Vec3> hit = targetBox.clip(eyePos, endPos);
            if (hit.isPresent()) {
                double dist = eyePos.distanceToSqr(hit.get());
                if (dist < nearestDist) {
                    nearest = target;
                    nearestDist = dist;
                }
            }
        }
        return nearest;

    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            VoiceOfTheWorld.tickQueue(event.getServer());
        }
    }

}


