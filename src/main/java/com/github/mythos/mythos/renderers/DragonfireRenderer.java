package com.github.mythos.mythos.renderers;

import com.github.mythos.mythos.entity.projectile.DragonFireBreathProjectile;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DragonfireRenderer extends EntityRenderer<DragonFireBreathProjectile> {
    public DragonfireRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(DragonFireBreathProjectile dragonFireBreathProjectile) {
        return null;
    }

//    @Override
//    public ResourceLocation getTextureLocation(DragonFireBreathProjectile entity) {
//        return new ResourceLocation("trmythos", "textures/entity/dragonfire.png");
//    }
}
