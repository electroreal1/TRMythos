package com.github.mythos.mythos.registry.menu;

import com.github.mythos.mythos.menu.OrunMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.github.mythos.mythos.Mythos.MOD_ID;

public class MythosMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, MOD_ID);

    public static final RegistryObject<MenuType<OrunMenu>> ORUN_MENU =
            MENUS.register("orun_menu", () -> new MenuType<>(OrunMenu::new));



    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
