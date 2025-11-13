package com.github.mythos.mythos.registry;

import com.github.mythos.mythos.entity.ThunderStorm;
import com.github.mythos.mythos.entity.projectile.DragonFireBreathProjectile;
import com.github.mythos.mythos.entity.projectile.VajraBreathProjectile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MythosEntityTypes {

    private static final DeferredRegister<EntityType<?>> registry;
    public static final RegistryObject<EntityType<DragonFireBreathProjectile>> DRAGONFIRE;
    public static final RegistryObject<EntityType<ThunderStorm>> THUNDER_STORM;
    public static final RegistryObject<EntityType<VajraBreathProjectile>> VAJRA_BREATH;

    //    public static final RegistryObject<EntityType<JusticeLightArrow>> JUSTICE_LIGHT_ARROW = ENTITY_TYPES.register("justice_light_arrow",
//            () -> EntityType.Builder.<JusticeLightArrow>of(JusticeLightArrow::new, MobCategory.MISC)
//                    .sized(0.5f, 0.5f)
//                    .clientTrackingRange(4)
//                    .updateInterval(20)
//                    .build("justice_light_arrow")
//    );
    public static void register(IEventBus modEventBus) {
    registry.register(modEventBus);
}
    public MythosEntityTypes() {
    }

    static {
        registry = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "trmythos");
        DRAGONFIRE = registry.register("dragonfire", () -> {
            return Builder.of(DragonFireBreathProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F).clientTrackingRange(64)
                    .build((new ResourceLocation("trmythos", "dragonfire")).toString());
        });
       VAJRA_BREATH = registry.register("vajra_breath",
                () -> EntityType.Builder.<VajraBreathProjectile>of(
                                (entityType, level) -> new VajraBreathProjectile(entityType, level),
                                MobCategory.MISC
                        )
                        .sized(1.0f, 1.0f)
                        .clientTrackingRange(64)
                        .build(new ResourceLocation("trmythos", "vajra_breath").toString())
        );
        THUNDER_STORM = registry.register("thunder_storm", () -> {
            return EntityType.Builder.of((EntityType<ThunderStorm> type, Level level) -> new ThunderStorm(type, level), MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .clientTrackingRange(64)
                    .updateInterval(Integer.MAX_VALUE)
                    .build(new ResourceLocation("tensura", "thunder_storm").toString());
        });
    }
}
