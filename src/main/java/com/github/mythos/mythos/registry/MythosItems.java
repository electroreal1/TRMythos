package com.github.mythos.mythos.registry;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MythosItems {

    public static final DeferredRegister<Item> TENSURA_ITEMS;

    public MythosItems() {
    }

    public static void register(IEventBus modEventBus) {
        TENSURA_ITEMS.register(modEventBus);
        MythosWeapons.init();
    }

    static {
        TENSURA_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "trmythos");
    }

}
