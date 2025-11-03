package com.github.mythos.mythos.registry;

import com.github.mythos.mythos.item.fragarach;
import com.github.mythos.mythos.item.undecember;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class MythosWeapons {

    public static final RegistryObject<Item> UNDECEMBER;
    public static final RegistryObject<Item> FRAGARACH;


    public MythosWeapons() {
    }


    public static void init() {
    }

    static {
        UNDECEMBER = MythosItems.TENSURA_ITEMS.register("undecember", undecember::new);
        FRAGARACH = MythosItems.TENSURA_ITEMS.register("fragarach", fragarach::new);
//        UNDECEMBER_SWORD = MythosItems.TENSURA_ITEMS.register("undecember", () -> {
//            return new undecember_sword(TensuraToolTiers.PURE_MAGISTEEL, 30, 1.5F, 4.5, 100.0, 2.0, (new Item.Properties()).stacksTo(1));
//        });
    }


}
