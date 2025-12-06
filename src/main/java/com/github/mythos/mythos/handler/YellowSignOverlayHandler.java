package com.github.mythos.mythos.handler;

import com.github.mythos.mythos.registry.MythosMobEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("trmythos")
public class YellowSignOverlayHandler {
    private static final ResourceLocation YELLOW_SIGN = new ResourceLocation("trmythos", "textures/gui/yellow_sign.png");
    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null) return;

        if (player.hasEffect(MythosMobEffects.YELLOW_SIGN.get())) {
            renderCustomOverlay(event);
        }
    }

    private static void renderCustomOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        RenderSystem.setShaderTexture(0, YELLOW_SIGN);
        RenderSystem.setShaderTexture(1, 1);

        int overlayWidth = 128;
        int overlayHeight = 128;
        int x = width / 2 - overlayWidth / 2;
        int y = height / 2 - overlayHeight / 2;

        GuiComponent.blit(event.getPoseStack(), x, y, 0, 0, overlayWidth, overlayHeight, overlayWidth, overlayHeight);
    }
}
