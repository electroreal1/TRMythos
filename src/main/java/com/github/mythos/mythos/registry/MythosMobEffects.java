package com.github.mythos.mythos.registry;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import java.awt.Color;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MythosMobEffects {
    private static final DeferredRegister<MobEffect> registry;
    public MythosMobEffects() {
    }

    public static void registery(IEventBus modEventBus) {
        registry.register(modEventBus);
    }

    static {
        registry = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "trmythos");
    }
}

