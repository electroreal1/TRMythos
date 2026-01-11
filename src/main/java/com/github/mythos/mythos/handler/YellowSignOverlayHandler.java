package com.github.mythos.mythos.handler;

import com.github.mythos.mythos.Mythos;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Mythos.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class YellowSignOverlayHandler {
    private static final ResourceLocation YELLOW_SIGN_TEXTURE = new ResourceLocation("trmythos", "textures/gui/yellow_sign.png");

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.VIGNETTE.type()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(MythosMobEffects.YELLOW_SIGN.get())) {

            int width = event.getWindow().getGuiScaledWidth();
            int height = event.getWindow().getGuiScaledHeight();

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            float alpha = (float) (Math.sin(mc.level.getGameTime() * 0.1f) * 0.2f + 0.4f);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, YELLOW_SIGN_TEXTURE);

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();

            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(0.0D, height, -90.0D).uv(0.0F, 1.0F).endVertex();
            bufferbuilder.vertex(width, height, -90.0D).uv(1.0F, 1.0F).endVertex();
            bufferbuilder.vertex(width, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
            bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
            tesselator.end();

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null && mc.player.hasEffect(MythosMobEffects.YELLOW_SIGN.get())) {

            float r = 0.90f;
            float g = 0.75f;
            float b = 0.20f;

            assert mc.level != null;
            float pulse = (float) (Math.sin(mc.level.getGameTime() * 0.05f) * 0.05f);

            event.setRed(r + pulse);
            event.setGreen(g + pulse);
            event.setBlue(b);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null && mc.player.hasEffect(MythosMobEffects.YELLOW_SIGN.get())) {
            event.setNearPlaneDistance(2.0f);
            event.setFarPlaneDistance(24.0f);

            event.setCanceled(true);
        }
    }
}
