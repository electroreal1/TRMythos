package com.github.mythos.mythos.registry.menu;

import com.github.mythos.mythos.menu.OrunMenu;
import com.github.mythos.mythos.menu.SoundSwapperMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MythosMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, "trmythos");

    public static final RegistryObject<MenuType<OrunMenu>> ORUN_MENU = MENUS.register("orun_menu",
            () -> IForgeMenuType.create(OrunMenu::new));
    public static final RegistryObject<MenuType<SoundSwapperMenu>> SOUND_MENU = MENUS.register("sound_menu",
            () -> IForgeMenuType.create(SoundSwapperMenu::new));


    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
