package com.github.mythos.mythos.util;

//import com.github.lucifel.virtuoso.registry.skill.IntrinsicSkills;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.mythos.mythos.ability.mythos.skill.unique.EltnamSkill;
import com.github.mythos.mythos.registry.skill.Skills;
import io.github.Memoires.trmysticism.registry.skill.UniqueSkills;
import net.minecraft.server.level.ServerPlayer;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MythosUtils extends SkillUtils {

    public MythosUtils() {
    }

    public static boolean hasGravityDominationAndSpatialDomination(ServerPlayer serverPlayer) {
        return SkillUtils.isSkillMastered(serverPlayer, ExtraSkills.SPATIAL_DOMINATION.get()) && isSkillMastered(serverPlayer, ExtraSkills.GRAVITY_DOMINATION.get());
    }

    public static boolean hasDreamer(ServerPlayer serverPlayer) {
        return SkillUtils.isSkillMastered(serverPlayer, UniqueSkills.DREAMER.get());
    }

    public static boolean hasProfanity(ServerPlayer serverPlayer) {
        return  SkillUtils.isSkillMastered(serverPlayer, Skills.PROFANITY.get());
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
                        serverPlayer.sendSystemMessage(Component.translatable("trmythos.skill.eltnam.obtained"));
                    }
                }
            }
        }
    }


}


