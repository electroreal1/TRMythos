package com.github.mythos.mythos.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "trmythos", value = Dist.CLIENT)
public class MythosSkyRenderer {
    private static final ResourceLocation SKY_IMAGE1 = new ResourceLocation("trmythos", "textures/sky/void_eye.png");

    public static boolean event1 = false;

    @SubscribeEvent
    public static void onRenderSky(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;

        if (!event1) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        PoseStack poseStack = event.getPoseStack();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SKY_IMAGE1);

        poseStack.pushPose();

        poseStack.mulPose(Vector3f.XP.rotationDegrees(0));

        Matrix4f matrix = poseStack.last().pose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();

        float size = 50f;
        float distance = -100f;

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix, -size, distance, size).uv(0.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix, size, distance, size).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix, size, distance, -size).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(matrix, -size, distance, -size).uv(0.0F, 1.0F).endVertex();
        tesselator.end();

        poseStack.popPose();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
}
