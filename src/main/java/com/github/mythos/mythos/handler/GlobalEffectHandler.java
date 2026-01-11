package com.github.mythos.mythos.handler;

import com.github.mythos.mythos.Mythos;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = Mythos.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GlobalEffectHandler {
    private static final Random RANDOM = new Random();
    public static float shakeIntensity = 0f;
    public static boolean isGreatSilenceActive = false;
    private static int soundLoopTimer = 0;

    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (shakeIntensity > 0) {
            event.setPitch(event.getPitch() + (RANDOM.nextFloat() - 0.5f) * shakeIntensity);
            event.setYaw(event.getYaw() + (RANDOM.nextFloat() - 0.5f) * shakeIntensity);
            event.setRoll(event.getRoll() + (RANDOM.nextFloat() - 0.5f) * (shakeIntensity * 1.5f));

            shakeIntensity -= 0.05f;
            if (shakeIntensity < 0) shakeIntensity = 0;
        }
    }

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        MobEffectInstance effect = event.getEntity().getEffect(MythosMobEffects.ATROPHY.get());
        if (effect != null) {
            PoseStack stack = event.getPoseStack();
            int amp = effect.getAmplifier();

            stack.pushPose();
            if (amp == 0) {
                stack.scale(1.0f, 1.0f, 0.001f);
                stack.scale(0.001f, 1.0f, 0.001f);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        if (event.getEntity().hasEffect(MythosMobEffects.ATROPHY.get())) {
            event.getPoseStack().popPose();
        }
    }

    @SubscribeEvent
    public static void onClientChat(ClientChatReceivedEvent event) {
        if (isGreatSilenceActive) {
            String original = event.getMessage().getString();
            event.setMessage(Component.literal("§7§k" + original));
        }
    }

    @SubscribeEvent
    public static void onSoundPlay(PlaySoundEvent event) {
        if (isGreatSilenceActive && event.getSound() != null) {
            String path = event.getSound().getLocation().getPath();

            boolean isAllowed = path.contains("conduit.ambient") ||
                    path.contains("warden.heartbeat") ||
                    path.contains("beacon.ambient");

            if (!isAllowed) {
                event.setSound(null);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Pre event) {
        if (isGreatSilenceActive) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            long time = mc.level.getGameTime();
            float alpha = (float) (Math.sin(time * 0.4f) * 0.4f + 0.5f);

            if (RANDOM.nextFloat() < 0.05f) alpha = 0.1f;

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && isGreatSilenceActive) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                if (soundLoopTimer-- <= 0) {
                    mc.player.playSound(SoundEvents.CONDUIT_AMBIENT, 1.0f, 0.1f);
                    mc.player.playSound(SoundEvents.WARDEN_HEARTBEAT, 0.7f, 0.2f);
                    soundLoopTimer = 40;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        if (isGreatSilenceActive) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            float time = (mc.level.getGameTime() + Minecraft.getInstance().getFrameTime()) * 0.01f;
            event.setRed((float) Math.abs(Math.sin(time)) * 0.1f);
            event.setGreen(0.01f);
            event.setBlue((float) Math.abs(Math.cos(time)) * 0.2f);
        }
    }
}