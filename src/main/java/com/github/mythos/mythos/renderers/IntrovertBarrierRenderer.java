package com.github.mythos.mythos.renderers;

import com.github.mythos.mythos.entity.IntrovertBarrier;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class IntrovertBarrierRenderer extends EntityRenderer<IntrovertBarrier> {
    protected IntrovertBarrierRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(IntrovertBarrier introvertBarrier) {
        return null;
    }
}
