package com.github.mythos.mythos.handler;

import com.github.mythos.mythos.registry.MythosMobEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KhaosClientHandler {
    private static int heartTick = 0;

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
