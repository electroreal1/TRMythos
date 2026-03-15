package com.github.mythos.mythos.menu;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WhiteoutOverlay {

    public static float intensity = 0.0f;

    @SubscribeEvent
    public static void onRenderGUI(RenderGuiOverlayEvent event) {
        if (intensity <= 0) return;

        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();

        PoseStack poseStack = event.getPoseStack();

        int alpha = (int) (intensity * 255);
        int color = (alpha << 24) | 0xFFFFFF;

        GuiComponent.fill(poseStack, 0, 0, width, height, color);
    }
}
