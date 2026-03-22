package com.github.mythos.mythos.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class MythosDimensions {
    public static final ResourceKey<Level> SANCTUARY_KEY = ResourceKey.create(Registry.DIMENSION_REGISTRY,
            new ResourceLocation("trmythos", "sanctuary")
    );

    public static final ResourceKey<DimensionType> SANCTUARY_TYPE = ResourceKey.create(
            Registry.DIMENSION_TYPE_REGISTRY,
            new ResourceLocation("mythos", "sanctuary")
    );
}
