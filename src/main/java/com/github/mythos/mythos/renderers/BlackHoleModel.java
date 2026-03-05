package com.github.mythos.mythos.renderers;

import com.github.mythos.mythos.entity.BlackHoleEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BlackHoleModel extends AnimatedGeoModel<BlackHoleEntity> {

    @Override
    public ResourceLocation getModelResource(BlackHoleEntity object) {
        return new ResourceLocation("trmythos", "geo/black_hole.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlackHoleEntity object) {
        return new ResourceLocation("trmythos", "textures/entity/black_hole.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlackHoleEntity object) {
        return new ResourceLocation("trmythos", "animations/black_hole.animation.json");
    }
}