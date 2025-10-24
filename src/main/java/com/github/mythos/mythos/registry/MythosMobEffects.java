package com.github.mythos.mythos.registry;

import com.github.mythos.mythos.mob_effect.AvalonRegenerationEffect;
//import com.github.mythos.mythos.mob_effect.RapidRegenerationEffect;
//import com.github.mythos.mythos.mob_effect.debuff.BloodDrainEffect;
//import com.github.mythos.mythos.mob_effect.debuff.VaporizationFreezeEffect;
import com.github.mythos.mythos.mob_effect.ChildOfThePlaneEffect;
import com.github.mythos.mythos.mob_effect.RapidRegenerationEffect;
import com.github.mythos.mythos.mob_effect.debuff.BloodDrainEffect;
import com.github.mythos.mythos.mob_effect.debuff.VaporizationFreezeEffect;
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

    public static final RegistryObject<MobEffect> AVALON_REGENERATION = registry.register("avalon_regeneration", () ->
            new AvalonRegenerationEffect(MobEffectCategory.BENEFICIAL, (new Color(255, 166, 4)).getRGB()));
    public static final RegistryObject<MobEffect> APOSTLE_REGENERATION = registry.register("apostle_regeneration", () ->
            new AvalonRegenerationEffect(MobEffectCategory.BENEFICIAL, (new Color(40, 2, 66)).getRGB()));
    public static final RegistryObject<MobEffect> RAPID_REGENERATION = registry.register("rapid_regeneration", () ->
            new RapidRegenerationEffect(MobEffectCategory.BENEFICIAL, (new Color(255, 166, 4)).getRGB()));
    public static final RegistryObject<MobEffect> VAPORIZATION_FREEZE = registry.register("vaporization_freeze", () ->
            new VaporizationFreezeEffect(MobEffectCategory.HARMFUL, (new Color(255, 144, 6)).getRGB()));
    public static final RegistryObject<MobEffect> BLOOD_DRAIN = registry.register("blood_drain", () ->
            new BloodDrainEffect(MobEffectCategory.HARMFUL, (new Color(255, 165, 3)).getRGB()));
    public static final RegistryObject<MobEffect> CHILD_OF_THE_PLANE = registry.register("child_of_the_plane_effect", () ->
            new ChildOfThePlaneEffect(MobEffectCategory.BENEFICIAL, (new Color(255, 165, 3)).getRGB()));
    public static void init(IEventBus modEventBus) {
        registry.register(modEventBus);
    }
}