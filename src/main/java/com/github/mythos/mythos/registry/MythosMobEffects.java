package com.github.mythos.mythos.registry;

import com.github.mythos.mythos.mob_effect.AvalonRegenerationEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.awt.*;

public class MythosMobEffects {
    public static void register(IEventBus modEventBus) {
        registry.register(modEventBus);
    }

    private static final DeferredRegister<MobEffect> registry = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "trmythos");

    public static final RegistryObject<MobEffect> AVALON_REGENERATION = registry.register("avalon_regeneration", () -> new AvalonRegenerationEffect(MobEffectCategory.BENEFICIAL, (new Color(255, 166, 4)).getRGB()));

    public static void init(IEventBus modEventBus) {
        registry.register(modEventBus);
    }
}