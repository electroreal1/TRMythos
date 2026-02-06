package com.github.mythos.mythos.util;

import com.github.mythos.mythos.registry.MythosWorldVisuals;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "trmythos", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MythosSkyRenderer {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        if (MythosWorldVisuals.alpha > 0) {
            event.setRed(MythosWorldVisuals.r);
            event.setGreen(MythosWorldVisuals.g);
            event.setBlue(MythosWorldVisuals.b);
        }

    }


    @SubscribeEvent
    public static void onFogRender(ViewportEvent.RenderFog event) {
        if (MythosWorldVisuals.alpha > 0) {
            event.setCanceled(true);
        }
    }
}