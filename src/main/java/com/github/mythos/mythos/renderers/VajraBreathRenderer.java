package com.github.mythos.mythos.renderers;

import com.github.mythos.mythos.entity.projectile.VajraBreathProjectile;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class VajraBreathRenderer extends EntityRenderer<VajraBreathProjectile> {

    public VajraBreathRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(VajraBreathProjectile vajraBreathProjectile) {
        return null;
    }
}
