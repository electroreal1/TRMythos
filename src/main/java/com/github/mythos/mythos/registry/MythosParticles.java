package com.github.mythos.mythos.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MythosParticles {
    private static final DeferredRegister<ParticleType<?>> registry;
//    public static final RegistryObject<SimpleParticleType> DRAGONFIRE;
    public static final RegistryObject<SimpleParticleType> RED_RUNES;
    public MythosParticles() {
    }

    public static void init(IEventBus modEventBus) {
        registry.register(modEventBus);
    }


    static {
        registry = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "trmythos");
//        DRAGONFIRE = registry.register("dragonfire", () -> {
//            return new SimpleParticleType(false);
//        });
        RED_RUNES = registry.register("red_runes", () -> {
            return new SimpleParticleType(false);
        });
    }

}
