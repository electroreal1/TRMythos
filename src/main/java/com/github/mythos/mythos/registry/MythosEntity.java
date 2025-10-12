package com.github.mythos.mythos.registry;

import com.github.mythos.mythos.entity.projectile.JusticeLightArrow;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MythosEntity {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "trmythos");
//    public static final RegistryObject<EntityType<JusticeLightArrow>> JUSTICE_LIGHT_ARROW = ENTITY_TYPES.register("justice_light_arrow",
//            () -> EntityType.Builder.<JusticeLightArrow>of(JusticeLightArrow::new, MobCategory.MISC)
//                    .sized(0.5f, 0.5f)
//                    .clientTrackingRange(4)
//                    .updateInterval(20)
//                    .build("justice_light_arrow")
//    );
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

    static {
    }
}
