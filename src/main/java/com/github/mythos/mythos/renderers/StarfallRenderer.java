package com.github.mythos.mythos.renderers;

import com.github.mythos.mythos.entity.projectile.StarFallProjectile;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class StarfallRenderer extends EntityRenderer<StarFallProjectile> {


    public StarfallRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(StarFallProjectile starFallProjectile) {
        return null;
    }
}
