package com.github.mythos.mythos.handler;

import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = "trmythos")
public class KanakhtHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (player.level.isClientSide || !com.github.manasmods.tensura.ability.SkillUtils.hasSkill(player, Skills.FLESH_OF_KANAKHT.get()))
            return;

        double ep = TensuraEPCapability.getEP(player);
        float healthPct = player.getHealth() / player.getMaxHealth();

        if (healthPct < 0.10f) {
          //  enterFleeMode(player);
            return;
        } else {

            List<LivingEntity> targets = player.level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(20),
                    e -> e != player && e.isAlive() && TensuraEPCapability.getEP(e) < (ep * 2));

            if (!targets.isEmpty()) {
                LivingEntity nearest = targets.get(0);
                forceAttack(player, nearest);
            }
        }
    }

//    private static void enterFleeMode(Player player) {
//        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 3, false, false));
//        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 20, 2, false, false));
//
//        List<LivingEntity> enemies = player.level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(10), e -> e != player);
//        Vec3 fleeDir = player.position().subtract(enemies.get(0).position()).normalize();
//        player.setDeltaMovement(fleeDir.x * 0.5, 0.2, fleeDir.z * 0.5);
//    }

    private static void forceAttack(Player player, LivingEntity target) {
        if (player == null || target == null) return;

        player.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());

        Vec3 moveDir = target.position().subtract(player.position()).normalize();
        double dist = player.distanceTo(target);

        if (dist > 3.0) {
            player.setDeltaMovement(moveDir.x * 0.45, player.getDeltaMovement().y, moveDir.z * 0.45);
            player.setSprinting(true);
        } else {
            if (player.attackAnim == 0) {
                player.attack(target);
                player.swing(InteractionHand.MAIN_HAND);
            }
        }
    }

    public static void makeFleshPuppet(Player master, LivingEntity victim) {
        victim.getPersistentData().putUUID("KanakhtMaster", master.getUUID());
        victim.addEffect(new MobEffectInstance(MythosMobEffects.FLESH.get(), 1200, 0));

    }
}