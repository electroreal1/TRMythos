package com.github.mythos.mythos.registry;

import com.github.mythos.mythos.client.screen.OrunScreen;
import com.github.mythos.mythos.handler.HaliShaderHandler;
import com.github.mythos.mythos.handler.YellowSignOverlayHandler;
import com.github.mythos.mythos.registry.menu.MythosMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientOnlyRegistrar {
    public static void registerClientOnlyEvents() {
        // Registers the FMLClientSetupEvent
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientOnlyRegistrar::onClientSetup);

        // Manual registration for handlers that DON'T have @Mod.EventBusSubscriber
        MinecraftForge.EVENT_BUS.register(YellowSignOverlayHandler.class);
        MinecraftForge.EVENT_BUS.register(HaliShaderHandler.class);
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(MythosMenuTypes.ORUN_MENU.get(), OrunScreen::new);
        });
    }
}