package com.github.mythos.mythos.handler;

import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "trmythos")
public class KhaosHandler {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player == null) return;

        Player player = event.player;

        if (player.getPersistentData().contains("BoundaryErasureUser")) {
            int timer = player.getPersistentData().getInt("BoundaryErasureUser");

            if (timer > 0) {
                player.getPersistentData().putInt("BoundaryErasureUser", timer - 1);

                BlockPos below = player.blockPosition().below();
                if (player.level.getBlockState(below).isAir() && !player.isShiftKeyDown()) {
                    Vec3 move = player.getDeltaMovement();
                    player.setDeltaMovement(move.x, 0, move.z);
                    player.setOnGround(true);
                    player.setDeltaMovement(player.getDeltaMovement().multiply(1.05, 1, 1.05));
                    player.fallDistance = 0;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAuraTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player == null || !event.player.isAlive()) return;
        if (event.player.level.isClientSide) return; // Server only

        Player player = event.player;
        Level level = player.level;

        if (player.getPersistentData().getBoolean("AuraOfUnmadeActive")) {
            if (player.tickCount % 10 == 0) {
                int radius = 15;
                BlockPos center = player.blockPosition();

                for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -2, -radius), center.offset(radius, 5, radius))) {
                    if (player.getRandom().nextFloat() > 0.1f) continue;

                    BlockState state = level.getBlockState(pos);
                    BlockState fakeState = null;

                    if (state.is(BlockTags.LEAVES)) fakeState = Blocks.STONE.defaultBlockState();
                    else if (state.is(Blocks.WATER)) fakeState = Blocks.GLASS.defaultBlockState();
                    else if (state.is(Blocks.FIRE)) fakeState = Blocks.AIR.defaultBlockState();

                    if (fakeState != null) {
                        sendFakeBlock(player, pos, fakeState);
                    }
                }
            }

            if (player.tickCount % 60 == 0) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.WARDEN_HEARTBEAT, SoundSource.AMBIENT, 1.0f, 0.1f);
            }
        }
    }

    public static void sendFakeBlock(Player originator, BlockPos pos, BlockState fakeState) {
        if (originator instanceof ServerPlayer serverPlayer) {
            ClientboundBlockUpdatePacket packet = new ClientboundBlockUpdatePacket(pos, fakeState);
            serverPlayer.getLevel().getChunkSource().broadcast(originator, packet);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(MythosMobEffects.ATROPHY.get())) {
            int amplifier = entity.getEffect(MythosMobEffects.ATROPHY.get()).getAmplifier();

            if (amplifier >= 1) {
                entity.setDeltaMovement(0, 0, 0);
                entity.setNoGravity(true);
                entity.setYRot(entity.yRotO);
                entity.setXRot(entity.xRotO);
            } else {
                Vec3 delta = entity.getDeltaMovement();
                entity.setDeltaMovement(delta.x, delta.y, delta.z * 0.01);
            }
        }
    }
}
