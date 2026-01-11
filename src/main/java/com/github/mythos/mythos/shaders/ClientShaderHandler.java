package com.github.mythos.mythos.shaders;

import com.github.mythos.mythos.Mythos;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Mythos.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientShaderHandler {

    private static final ResourceLocation WEAK_SHADER = new ResourceLocation("minecraft", "shaders/post/phosphor.json");
    private static final ResourceLocation STRONG_SHADER = new ResourceLocation("minecraft", "shaders/post/blobs.json");

    private static int lastActiveAmplifier = -1;
    private static int soundLoopTimer = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        MobEffectInstance effect = mc.player.getEffect(MythosMobEffects.SPATIAL_DYSPHORIA.get());
        boolean hasEffect = effect != null;
        int currentAmplifier = hasEffect ? effect.getAmplifier() : -1;

        boolean currentlyHasShader = mc.gameRenderer.currentEffect() != null;
        ResourceLocation activeShaderId = currentlyHasShader ? ResourceLocation.tryParse(mc.gameRenderer.currentEffect().getName()) : null;
        ResourceLocation targetShader = currentAmplifier >= 2 ? STRONG_SHADER : WEAK_SHADER;

        if (hasEffect) {
            if (currentAmplifier != lastActiveAmplifier || activeShaderId == null || !activeShaderId.equals(targetShader)) {
                mc.gameRenderer.loadEffect(targetShader);
                lastActiveAmplifier = currentAmplifier;
            }
        } else if (currentlyHasShader && activeShaderId != null && (activeShaderId.equals(WEAK_SHADER) || activeShaderId.equals(STRONG_SHADER))) {
            mc.gameRenderer.shutdownEffect();
            lastActiveAmplifier = -1;
        }

        if (mc.player != null && mc.player.hasEffect(MythosMobEffects.GREAT_SILENCE.get())) {

            if (soundLoopTimer <= 0) {
                mc.player.playSound(SoundEvents.CONDUIT_AMBIENT, 1.0f, 0.1f);
                mc.player.playSound(SoundEvents.BEACON_AMBIENT, 0.4f, 0.2f);
                soundLoopTimer = 40;
            }
            soundLoopTimer--;
        } else {
            soundLoopTimer = 0;
        }
    }

    @SubscribeEvent
    public static void onFOVUpdate(ComputeFovModifierEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        MobEffectInstance effect = mc.player.getEffect(MythosMobEffects.SPATIAL_DYSPHORIA.get());

        if (effect != null) {
            int amp = effect.getAmplifier();

            float intensity = 1.0f + (amp * 0.5f);
            float speed = 0.1f + (amp * 0.05f);

            float timer = mc.player.tickCount + mc.getFrameTime();

            float wave = (float) Math.sin(timer * speed) * (0.2f * intensity);

            float jitterLimit = 0.05f * intensity;
            float jitter = (mc.player.getRandom().nextFloat() - 0.5f) * jitterLimit;

            event.setNewFovModifier(event.getFovModifier() + wave + jitter);
        }
    }


}