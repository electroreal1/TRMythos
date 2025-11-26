package com.github.mythos.mythos.registry;

import com.github.manasmods.manascore.api.data.gen.annotation.GenerateItemModels;
import com.github.mythos.mythos.item.catharsis;
import com.github.mythos.mythos.item.fragarach;
import com.github.mythos.mythos.item.undecember;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MythosItems {

    private static final DeferredRegister<Item> registry;
    @GenerateItemModels.SingleTextureModel
    public static final RegistryObject<Item> DEMON_CORE;
    public static final RegistryObject<Item> UNDECEMBER;
    public static final RegistryObject<Item> CATHARSIS;
    public static final RegistryObject<Item> FRAGARACH;

    public MythosItems() {
    }

    public static void register(IEventBus modEventBus) {
        registry.register(modEventBus);
    }

    static {
        registry = DeferredRegister.create(ForgeRegistries.ITEMS, "trmythos");
        DEMON_CORE = registry.register("demon_core", () -> {
            return new Item((new Item.Properties()));
        });
        UNDECEMBER = registry.register("undecember", undecember::new);
        CATHARSIS = registry.register("catharsis", catharsis::new);
        FRAGARACH = registry.register("fragarach", fragarach::new);
    }


}
