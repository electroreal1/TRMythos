package com.github.mythos.mythos.registry.dimensions;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MythosDimensions {
    public static void register(IEventBus modEventBus) {
        POI.register(modEventBus);
    }

    public static final DeferredRegister<PoiType> POI = DeferredRegister.create(ForgeRegistries.POI_TYPES, "mythos");
    public static final ResourceKey<Level> EXAMPLE =
            ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("mythos", "example"));
    public static final ResourceKey<DimensionType> EXAMPLE_TYPE =
            ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, EXAMPLE.location());

}
