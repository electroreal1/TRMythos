package com.github.mythos.mythos.renderers;

import com.github.mythos.mythos.registry.MythosRenderTypes;
import com.github.mythos.mythos.registry.MythosShaders;
import com.github.mythos.mythos.registry.MythosWorldVisuals;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class WorldDomeRenderer {
    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;
        if (MythosWorldVisuals.alpha <= 0 || MythosShaders.DOME_SHADER == null) return;

        Minecraft mc = Minecraft.getInstance();
        PoseStack posestack = event.getPoseStack();

        posestack.pushPose();
        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
        posestack.translate(-cam.x, -cam.y, -cam.z);
        posestack.translate(mc.player.getX(), mc.player.getY() + 1.5, mc.player.getZ());

        var shader = MythosShaders.DOME_SHADER;
        if (shader.getUniform("TintColor") != null) {
            shader.getUniform("TintColor").set(MythosWorldVisuals.r, MythosWorldVisuals.g, MythosWorldVisuals.b);
        }

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(MythosRenderTypes.mythosDome(() -> shader));

        renderSphere(consumer, posestack, 150.0f);

        bufferSource.endBatch();
        posestack.popPose();
    }

    private static void renderSphere(VertexConsumer consumer, PoseStack pose, float radius) {
        var mat = pose.last().pose();
        int segments = 24;

        for (int i = 0; i < segments; i++) {
            float lat0 = (float) Math.PI * (-0.5f + (float) (i) / segments);
            float z0 = (float) Math.sin(lat0) * radius;
            float zr0 = (float) Math.cos(lat0) * radius;

            float lat1 = (float) Math.PI * (-0.5f + (float) (i + 1) / segments);
            float z1 = (float) Math.sin(lat1) * radius;
            float zr1 = (float) Math.cos(lat1) * radius;

            for (int j = 0; j <= segments; j++) {
                float lng = (float) (2 * Math.PI * (float) (j) / segments);
                float x = (float) Math.cos(lng);
                float y = (float) Math.sin(lng);

                // Add vertices for a quad
                consumer.vertex(mat, x * zr0, z0, y * zr0).color(1f, 1f, 1f, 1f).uv((float)j/segments, (float)i/segments).endVertex();
                consumer.vertex(mat, x * zr1, z1, y * zr1).color(1f, 1f, 1f, 1f).uv((float)j/segments, (float)(i+1)/segments).endVertex();
            }
        }
    }

    private static Vec3 spherePoint(float u, float v, float radius) {
        double x = Math.cos(u) * Math.sin(v) * radius;
        double y = Math.cos(v) * radius;
        double z = Math.sin(u) * Math.sin(v) * radius;
        return new Vec3(x, y, z);
    }

    private static void put(VertexConsumer vc, com.mojang.math.Matrix4f mat, Vec3 p, float radius, float alpha, float u, float v, int light, float thunder01) {


        float bottomness = Mth.clamp((-((float) p.y) / radius), 0f, 1f);
        float band = smoothstep(0.35f, 0.85f, bottomness);


        float baseR = 0.05f;
        float baseG = 0.05f;
        float baseB = 0.06f;


        float pinkR = 0.90f;
        float pinkG = 0.20f;
        float pinkB = 0.55f;


        float thunderBoost = 0.35f * thunder01;
        float pr = Mth.clamp(pinkR + thunderBoost, 0f, 1f);
        float pg = Mth.clamp(pinkG + thunderBoost * 0.20f, 0f, 1f);
        float pb = Mth.clamp(pinkB + thunderBoost, 0f, 1f);

        float r = Mth.lerp(band, baseR, pr);
        float g = Mth.lerp(band, baseG, pg);
        float b = Mth.lerp(band, baseB, pb);

        vc.vertex(mat, (float) p.x, (float) p.y, (float) p.z).color(r, g, b, alpha)
                .uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0f, 1f, 0f).endVertex();
    }

    private static float smoothstep(float e0, float e1, float x) {
        x = Mth.clamp((x - e0) / (e1 - e0), 0f, 1f);
        return x * x * (3f - 2f * x);
    }
}
