package com.github.mythos.mythos.handler;

import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "trmythos")
public class SchrodingersHandler {


    @SubscribeEvent
    public static void onQuantumTick(LivingEvent.LivingTickEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level.isClientSide) return;

        if (victim.hasEffect(MythosMobEffects.SCHRODINGERS_LABYRINTH.get())) {
            CompoundTag tag = victim.getPersistentData();

            if (!tag.contains("LabyrinthAnchor")) {
                tag.putLong("LabyrinthAnchor", victim.blockPosition().asLong());
                return;
            }

            BlockPos anchor = BlockPos.of(tag.getLong("LabyrinthAnchor"));
            double distanceSq = victim.blockPosition().distSqr(anchor);

            if (distanceSq > 36) {
                performQuantumCollapse(victim, anchor);
            }
        }
    }

    private static void performQuantumCollapse(LivingEntity victim, BlockPos anchor) {
        if (victim.level instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.REVERSE_PORTAL, victim.getX(), victim.getY() + 1, victim.getZ(),
                    25, 0.2, 0.5, 0.2, 0.1);

            victim.teleportTo(anchor.getX() + 0.5, anchor.getY(), anchor.getZ() + 0.5);

            sl.playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0f, 1.8f);
        }
    }

}
