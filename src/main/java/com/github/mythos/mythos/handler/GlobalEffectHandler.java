package com.github.mythos.mythos.handler;

import com.github.mythos.mythos.Mythos;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
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
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;
import java.util.Random;

@Mod.EventBusSubscriber(modid = Mythos.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GlobalEffectHandler {
    public static boolean isGreatSilenceActive = false;
    private static final Random RANDOM = new Random();
    public static float shakeIntensity = 0f;
    private static int soundLoopTimer = 0;

    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (shakeIntensity > 0) {
            event.setPitch(event.getPitch() + (RANDOM.nextFloat() - 0.5f) * shakeIntensity);
            event.setYaw(event.getYaw() + (RANDOM.nextFloat() - 0.5f) * shakeIntensity);
            event.setRoll(event.getRoll() + (RANDOM.nextFloat() - 0.5f) * (shakeIntensity * 1.5f));
        }
    }

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance effect = entity.getEffect(MythosMobEffects.ATROPHY.get());

        if (effect != null) {
            PoseStack stack = event.getPoseStack();
            int amp = effect.getAmplifier();

            stack.pushPose();
            if (amp == 0) {
                stack.scale(1.0f, 1.0f, 0.005f);
            } else {
                stack.scale(0.005f, 1.0f, 0.005f);
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
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(MythosMobEffects.GREAT_SILENCE.get())) {
            String original = event.getMessage().getString();
            event.setMessage(Component.literal("§7§k" + original).withStyle(ChatFormatting.GRAY));
        }

    }

    @SubscribeEvent
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
    public static void onRenderGui(RenderGuiOverlayEvent.Pre event) {
        if (isGreatSilenceActive) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            long time = mc.level.getGameTime();
            float alpha = (float) (Math.sin(time * 0.4f) * 0.4f + 0.5f);

            if (RANDOM.nextFloat() < 0.05f) alpha = 0.1f;

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(MythosMobEffects.GREAT_SILENCE.get())) {
            float timer = mc.level.getGameTime() + mc.getFrameTime();
            float alpha = (float) (Math.sin(timer * 0.5f) * 0.5f + 0.5f);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha * 0.5f);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (mc.player.hasEffect(MythosMobEffects.GREAT_SILENCE.get())) {
            if (soundLoopTimer-- <= 0) {
                mc.player.playSound(SoundEvents.CONDUIT_AMBIENT, 1.0f, 0.1f);
                mc.player.playSound(SoundEvents.WARDEN_HEARTBEAT, 0.7f, 0.2f);
                soundLoopTimer = 40;
            }
        }

        MobEffectInstance dysphoria = mc.player.getEffect(MythosMobEffects.SPATIAL_DYSPHORIA.get());
        if (dysphoria != null) {
            shakeIntensity = 0.1f * (dysphoria.getAmplifier() + 1);
        } else {
            shakeIntensity = Math.max(0, shakeIntensity - 0.01f);
        }


        if (mc.player.hasEffect(MythosMobEffects.SUNRISE.get())) {
            loadShader(mc, new ResourceLocation("shaders/post/phosphor.json"));
        } else if (mc.player.hasEffect(MythosMobEffects.SUNSET.get())) {
            loadShader(mc, new ResourceLocation("shaders/post/desaturate.json"));
        } else {
            if (mc.gameRenderer.currentEffect() != null && isHaliShader(Objects.requireNonNull(mc.gameRenderer.currentEffect()).getName())) {
                mc.gameRenderer.shutdownEffect();
            }
        }

        if (mc.player != null && GlobalEffectHandler.isGreatSilenceActive) {
            if (mc.level.getGameTime() % 40 == 0) {
                mc.player.playSound(SoundEvents.CONDUIT_AMBIENT, 1.0f, 0.1f);
            }

            if (mc.level.getGameTime() % 30 == 0) {
                mc.player.playSound(SoundEvents.WARDEN_HEARTBEAT, 0.6f, 0.2f);
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

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(MythosMobEffects.GREAT_SILENCE.get())) {
            float time = (mc.level.getGameTime() + mc.getFrameTime()) * 0.01f;
            event.setRed((float)Math.abs(Math.sin(time)) * 0.2f);
            event.setGreen(0.05f);
            event.setBlue((float)Math.abs(Math.cos(time)) * 0.2f);
        }
    }

    private static void loadShader(Minecraft mc, ResourceLocation loc) {
        if (mc.gameRenderer.currentEffect() == null || !mc.gameRenderer.currentEffect().getName().equals(loc.toString())) {
            mc.gameRenderer.loadEffect(loc);
        }
    }

    private static boolean isHaliShader(String name) {
        return name.contains("phosphor") || name.contains("desaturate");
    }


    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderLiving(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity entity = event.getEntity();
        var effect = entity.getEffect(MythosMobEffects.ATROPHY.get());
        if (effect != null) {
            event.getPoseStack().pushPose();
            int amp = effect.getAmplifier();

            if (amp == 0) {
                event.getPoseStack().scale(0.005f, 10.0f, 0.005f);
                event.getPoseStack().translate(0.005f, 10.0f, 0.005f);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderHotbar(RenderGuiOverlayEvent.Pre event) {
        if (GlobalEffectHandler.isGreatSilenceActive) {
            long time = Minecraft.getInstance().level.getGameTime();
            float alpha = (time % 5 == 0) ? 0.2f : 1.0f;

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderHotbarPost(RenderGuiOverlayEvent.Post event) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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