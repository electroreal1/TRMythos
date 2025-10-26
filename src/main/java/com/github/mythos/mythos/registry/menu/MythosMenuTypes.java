package com.github.mythos.mythos.registry.menu;

import com.github.mythos.mythos.menu.GenesisCoreMenu;
import com.github.mythos.mythos.menu.OrunMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MythosMenuTypes {

    private static final DeferredRegister<MenuType<?>> registry;

    public static final RegistryObject<MenuType<GenesisCoreMenu>> GENESIS_CORE_MENU;
    public static final RegistryObject<MenuType<OrunMenu>> ORUN_MENU;


    public MythosMenuTypes() {
    }

    public static void init(IEventBus modEventBus) {
        registry.register(modEventBus);
    }

    static {
        registry = DeferredRegister.create(ForgeRegistries.MENU_TYPES, "trmythos");
        GENESIS_CORE_MENU = registry.register("genesis_core_menu", () -> {
            return IForgeMenuType.create(GenesisCoreMenu::new);
        });
        ORUN_MENU = registry.register("orun_menu", () -> {
            return IForgeMenuType.create(OrunMenu::new);
        });


    }

}
