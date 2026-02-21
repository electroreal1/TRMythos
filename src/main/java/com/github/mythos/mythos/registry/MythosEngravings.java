package com.github.mythos.mythos.registry;

import com.github.manasmods.tensura.enchantment.EngravingEnchantment;
import com.github.mythos.mythos.engravings.VainOfTheWorld;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MythosEngravings {
    private static final DeferredRegister<Enchantment> registry;
    public static final RegistryObject<EngravingEnchantment> VAIN;


    public MythosEngravings() {
    }

    public static void init(IEventBus modEventBus) {
        registry.register(modEventBus);
    }

    static {
        registry = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "trmythos");
        VAIN = registry.register("vain_of_the_world", VainOfTheWorld::new);
    }
}
