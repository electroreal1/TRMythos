package com.github.mythos.mythos.registry.menu;

import com.github.mythos.mythos.menu.OrunMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MythosMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, "mythos");

    public static final RegistryObject<MenuType<OrunMenu>> ORUN_MENU = MENU_TYPES.register("orun_menu",
            () -> IForgeMenuType.create((id, inv, data) -> new OrunMenu(id, inv))
    );
}
