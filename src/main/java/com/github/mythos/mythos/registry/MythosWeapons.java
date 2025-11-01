package com.github.mythos.mythos.registry;

import com.github.mythos.mythos.item.undecember_sword;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class MythosWeapons {

    public static final RegistryObject<Item> UNDECEMBER;

    public MythosWeapons() {
    }


    public static void init() {
    }

    static {
        UNDECEMBER = MythosItems.TENSURA_ITEMS.register("undecember_sword", undecember_sword::new);
    }


}
