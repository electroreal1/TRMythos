package com.github.mythos.mythos.registry;

import com.github.mythos.mythos.client.screen.OrunScreen;
import com.github.mythos.mythos.handler.GlobalEffectHandler;
import com.github.mythos.mythos.handler.HaliShaderHandler;
import com.github.mythos.mythos.handler.KhaosClientHandler;
import com.github.mythos.mythos.handler.YellowSignOverlayHandler;
import com.github.mythos.mythos.registry.menu.MythosMenuTypes;
import com.github.mythos.mythos.shaders.ClientShaderHandler;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientOnlyRegistrar {
    public static void registerClientOnlyEvents() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientOnlyRegistrar::onClientSetup);

        MinecraftForge.EVENT_BUS.register(ClientShaderHandler.class);
        MinecraftForge.EVENT_BUS.register(YellowSignOverlayHandler.class);
        MinecraftForge.EVENT_BUS.register(HaliShaderHandler.class);
        MinecraftForge.EVENT_BUS.register(GlobalEffectHandler.class);
        MinecraftForge.EVENT_BUS.register(KhaosClientHandler.class);
        // Note: KhaosHandler was already registered in the main constructor.
        // If it has client code, it needs to be split!
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(MythosMenuTypes.ORUN_MENU.get(), OrunScreen::new);
        });
    }
}
