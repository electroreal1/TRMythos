package com.github.mythos.mythos.util;

//import com.github.lucifel.virtuoso.registry.skill.IntrinsicSkills;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.mythos.mythos.ability.mythos.skill.unique.normal.CrimsonTyrantSkill;
import com.github.mythos.mythos.ability.mythos.skill.unique.normal.EltnamSkill;
import com.github.mythos.mythos.ability.mythos.skill.unique.normal.UnderworldPrince;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Optional;
import java.util.Random;

public class MythosUtils extends SkillUtils {

    public MythosUtils() {
    }

    public static boolean isRace(Entity entity, Race race) {
        return false;
    }
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            CompoundTag persistentData = serverPlayer.getPersistentData();
            CompoundTag loginData;
            if (!persistentData.contains("checkFirstLogin")) {
                loginData = new CompoundTag();
                persistentData.put("checkFirstLogin", loginData);
            } else {
                loginData = persistentData.getCompound("checkFirstLogin");
            }
            // If player is logging in for the first time
            if (!loginData.getBoolean("firstLogin")) {
                loginData.putBoolean("firstLogin", true);

                var playerRace = TensuraPlayerCapability.getRace(serverPlayer);
                if (TensuraPlayerCapability.getRace((LivingEntity)player) == TensuraRaces.WIGHT.get() && (
                        new Random()).nextInt(100) < 10) {
                    EltnamSkill eltnam = (EltnamSkill) Skills.ELTNAM.get();
                    TensuraSkillInstance noCost = new TensuraSkillInstance((ManasSkill)eltnam);
                    noCost.getOrCreateTag().putBoolean("NoMagiculeCost", true);
                    if (SkillAPI.getSkillsFrom((Entity)player).learnSkill((ManasSkillInstance)noCost)) {
                        serverPlayer.sendSystemMessage(Component.translatable("trmythos.skill.eltnam.obtained").withStyle(ChatFormatting.RED));
                    }
                }
                if (TensuraPlayerCapability.getRace((LivingEntity)player) == TensuraRaces.WIGHT.get() && (
                        new Random()).nextInt(100) < 10) {
                    CrimsonTyrantSkill crimson = (CrimsonTyrantSkill) Skills.CRIMSON_TYRANT.get();
                    TensuraSkillInstance noCost = new TensuraSkillInstance((ManasSkill)crimson);
                    noCost.getOrCreateTag().putBoolean("NoMagiculeCost", true);
                    if (SkillAPI.getSkillsFrom((Entity)player).learnSkill((ManasSkillInstance)noCost)) {
                        serverPlayer.sendSystemMessage(Component.literal("The Crimson Moon Far Away Extends It's Bloody Blessing, Let the Carnage Begin!").withStyle(ChatFormatting.DARK_RED));
                    }
                }
                if (TensuraPlayerCapability.getRace((LivingEntity)player) == TensuraRaces.WIGHT.get() && (
                        new Random()).nextInt(100) < 10) {
                    UnderworldPrince underworld = (UnderworldPrince) Skills.UNDERWORLD_PRINCE.get();
                    TensuraSkillInstance noCost = new TensuraSkillInstance((ManasSkill)underworld);
                    noCost.getOrCreateTag().putBoolean("NoMagiculeCost", true);
                    if (SkillAPI.getSkillsFrom((Entity)player).learnSkill((ManasSkillInstance)noCost)) {
                        serverPlayer.sendSystemMessage(Component.literal("The Underworld Beckons you to embrace souls of the damned!").withStyle(ChatFormatting.BLACK));
                    }
                }
            }
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

}


