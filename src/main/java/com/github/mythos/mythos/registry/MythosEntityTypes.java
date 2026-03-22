package com.github.mythos.mythos.registry;

import com.github.mythos.mythos.entity.BlackHoleEntity;
import com.github.mythos.mythos.entity.BoreasBarrier;
import com.github.mythos.mythos.entity.IntrovertBarrier;
import com.github.mythos.mythos.entity.ThunderStorm;
import com.github.mythos.mythos.entity.boss.DendrrahEntity;
import com.github.mythos.mythos.entity.projectile.DragonFireBreathProjectile;
import com.github.mythos.mythos.entity.projectile.StarFallProjectile;
import com.github.mythos.mythos.entity.projectile.VajraBreathProjectile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MythosEntityTypes {

    private static final DeferredRegister<EntityType<?>> registry;
    public static final RegistryObject<EntityType<DragonFireBreathProjectile>> DRAGONFIRE;
    public static final RegistryObject<EntityType<ThunderStorm>> THUNDER_STORM;
    public static final RegistryObject<EntityType<VajraBreathProjectile>> VAJRA_BREATH;
    public static final RegistryObject<EntityType<IntrovertBarrier>> INTROVERT_BARRIER;
    public static final RegistryObject<EntityType<BoreasBarrier>> BOREAS_BARRIER;
    public static final RegistryObject<EntityType<StarFallProjectile>> STARFALL;
    public static final RegistryObject<EntityType<DendrrahEntity>> DENDRAHH;
    public static final RegistryObject<EntityType<BlackHoleEntity>> BLACK_HOLE;
    public static void register(IEventBus modEventBus) {
    registry.register(modEventBus);
}
    public MythosEntityTypes() {
    }

    static {
        registry = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "trmythos");

        DRAGONFIRE = registry.register("dragonfire",
                () -> Builder.of(DragonFireBreathProjectile::new, MobCategory.MISC)
                .sized(1.0F, 1.0F).clientTrackingRange(64)
                .build((new ResourceLocation("trmythos", "dragonfire")).toString()));

       VAJRA_BREATH = registry.register("vajra_breath",
                () -> EntityType.Builder.<VajraBreathProjectile>of(
                                VajraBreathProjectile::new, MobCategory.MISC)
                        .sized(1.0f, 1.0f)
                        .clientTrackingRange(64)
                        .build(new ResourceLocation("trmythos", "vajra_breath").toString()));

       STARFALL = registry.register("starfall",
                () -> EntityType.Builder.<StarFallProjectile>of(
                                StarFallProjectile::new, MobCategory.MISC)
                        .sized(1.0f, 1.0f)
                        .clientTrackingRange(64)
                        .build(new ResourceLocation("trmythos", "starfall").toString()));

        THUNDER_STORM = registry.register("thunder_storm", () ->
                EntityType.Builder.<ThunderStorm>of(
                                ThunderStorm::new, MobCategory.MISC)
                        .sized(0.1f, 0.1f)
                        .clientTrackingRange(64).updateInterval(Integer.MAX_VALUE)
                        .build(new ResourceLocation("trmythos", "thunder_storm").toString()));

        INTROVERT_BARRIER = registry.register("introvert_barrier",
                        () -> EntityType.Builder.<IntrovertBarrier>of(
                                        IntrovertBarrier::new, MobCategory.MISC)
                                .sized(1.0f, 1.0f)
                                .clientTrackingRange(64)
                                .build(new ResourceLocation("trmythos", "introvert_barrier").toString()));

        BOREAS_BARRIER = registry.register("boreas_barrier",
                        () -> EntityType.Builder.<BoreasBarrier>of(
                                        BoreasBarrier::new, MobCategory.MISC)
                                .sized(1.0f, 1.0f)
                                .clientTrackingRange(64)
                                .build(new ResourceLocation("trmythos", "boreas_barrier").toString()));

        DENDRAHH = registry.register("dendrahh",
                () -> EntityType.Builder.of(DendrrahEntity::new, MobCategory.MISC).sized(1, 2)
                        .clientTrackingRange(64)
                        .build(new ResourceLocation("trmythos", "dendrahh").toString()));

        BLACK_HOLE = registry.register("black_hole",
                () -> EntityType.Builder.of(BlackHoleEntity::new, MobCategory.MISC).sized(1, 2)
                        .clientTrackingRange(64)
                        .build(new ResourceLocation("trmythos", "black_hole").toString()));

    }


}
