package com.github.mythos.mythos.handler;

import com.github.mythos.mythos.Mythos;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Mythos.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HaliShaderHandler {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (mc.player.hasEffect(MythosMobEffects.SUNRISE.get())) {
            loadShader(mc, new ResourceLocation("shaders/post/phosphor.json"));
        } else if (mc.player.hasEffect(MythosMobEffects.SUNSET.get())) {
            loadShader(mc, new ResourceLocation("shaders/post/desaturate.json"));
        } else {
            if (mc.gameRenderer.currentEffect() != null && isHaliShader(Objects.requireNonNull(mc.gameRenderer.currentEffect()).getName())) {
                mc.gameRenderer.shutdownEffect();
            }
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
}
