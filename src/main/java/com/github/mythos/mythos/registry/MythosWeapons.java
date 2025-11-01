package com.github.mythos.mythos.registry;

import com.github.mythos.mythos.item.UndecemberItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MythosWeapons {
    private static final DeferredRegister<Item> registry;

    public static final RegistryObject<Item> UNDECEMBER;

    public MythosWeapons() {
    }

    public static void init(IEventBus modEventBus) {
        registry.register(modEventBus);
    }

    static {
        registry = DeferredRegister.create(ForgeRegistries.ITEMS, "trmythos");
        UNDECEMBER = registry.register("undecember", UndecemberItem::new);
    }
}
