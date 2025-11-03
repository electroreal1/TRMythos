package com.github.mythos.mythos.util;

//import com.github.lucifel.virtuoso.registry.skill.IntrinsicSkills;

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
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

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
            // Get persistent player data
            CompoundTag persistentData = serverPlayer.getPersistentData();

            // Create or get the "checkFirstLogin" tag compound
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
                int random = new Random().nextInt(100);

                boolean qualifies = false;
                if (playerRace == TensuraRaces.VAMPIRE.get() && random < 5) {
                    qualifies = true;
                } else if (playerRace == TensuraRaces.WIGHT.get() && random < 3) {
                    qualifies = true;
                }

                if (qualifies) {
                    EltnamSkill eltnam = (EltnamSkill) Skills.ELTNAM.get();
                    TensuraSkillInstance skillInstance = new TensuraSkillInstance(eltnam);

                    skillInstance.getOrCreateTag().putBoolean("NoMagiculeCost", true);

                    // Try to teach the skill to the player
                    if (SkillAPI.getSkillsFrom(serverPlayer).learnSkill(skillInstance)) {
                        serverPlayer.sendSystemMessage(Component.translatable("trmythos.skill.eltnam.obtained"));
                    }
                }
            }
        }
    }


}


