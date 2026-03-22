package com.github.mythos.mythos.renderers;

import com.github.mythos.mythos.entity.BoreasBarrier;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BoreasBarrierRenderer extends EntityRenderer<BoreasBarrier> {
    public BoreasBarrierRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(BoreasBarrier boreasBarrier) {
        return null;
    }
}
