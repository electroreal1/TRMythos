package com.github.mythos.mythos.handler;

import com.github.mythos.mythos.registry.MythosMobEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "trmythos")
public class KhaosHandler {

    private static int heartTick = 0;

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

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(MythosMobEffects.GREAT_SILENCE.get())) {
            if (mc.level.getGameTime() % 40 == 0) {
                mc.player.playSound(SoundEvents.CONDUIT_AMBIENT, 1.0f, 0.1f);
            }
            heartTick++;
            if (heartTick >= 30) {
                mc.player.playSound(SoundEvents.WARDEN_HEARTBEAT, 0.6f, 0.2f);
                heartTick = 0;
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderLiving(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity entity = event.getEntity();
        var effect = entity.getEffect(MythosMobEffects.ATROPHY.get());
        if (effect != null) {
            event.getPoseStack().pushPose();
            int amp = effect.getAmplifier();
            if (amp == 0) event.getPoseStack().scale(1.0f, 1.0f, 0.005f);
            else event.getPoseStack().scale(0.005f, 1.0f, 0.005f);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        if (event.getEntity().hasEffect(MythosMobEffects.ATROPHY.get())) {
            event.getPoseStack().popPose();
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onClientChat(ClientChatReceivedEvent event) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasEffect(MythosMobEffects.GREAT_SILENCE.get())) {
            String text = event.getMessage().getString();
            event.setMessage(Component.literal("§7§k" + text).withStyle(ChatFormatting.GRAY));
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onSoundPlay(PlaySoundEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(MythosMobEffects.GREAT_SILENCE.get())) {
            if (event.getSound() != null) {
                String path = event.getSound().getLocation().getPath();
                boolean allowed = path.contains("conduit") || path.contains("warden") || path.contains("beacon");
                if (!allowed) event.setSound(null);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderGui(RenderGuiOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(MythosMobEffects.GREAT_SILENCE.get())) {
            float timer = mc.level.getGameTime() + mc.getFrameTime();
            float alpha = (float) (Math.sin(timer * 0.5f) * 0.5f + 0.5f);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha * 0.5f);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(MythosMobEffects.GREAT_SILENCE.get())) {
            float time = (mc.level.getGameTime() + mc.getFrameTime()) * 0.01f;
            event.setRed((float)Math.abs(Math.sin(time)) * 0.2f);
            event.setGreen(0.05f);
            event.setBlue((float)Math.abs(Math.cos(time)) * 0.2f);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onFogDensity(ViewportEvent.RenderFog event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(MythosMobEffects.GREAT_SILENCE.get())) {
            event.setNearPlaneDistance(0f);
            event.setFarPlaneDistance(10f);
            event.setCanceled(true);
        }
    }
}
