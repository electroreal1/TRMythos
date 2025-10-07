package com.github.mythos.mythos.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MythosParticles {
    private static final DeferredRegister<ParticleType<?>> registry;
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "trmythos");
    public MythosParticles() {
    }

    static {
        registry = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "trmythos");
    }

}
