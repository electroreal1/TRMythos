package com.github.mythos.mythos.registry;

import com.github.mythos.mythos.client.screen.OrunScreen;
import com.github.mythos.mythos.particles.RedRunesParticles;
import com.github.mythos.mythos.registry.menu.MythosMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = "trmythos", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MythosClient {

    public static void clientSetup(final FMLClientSetupEvent event) {
       // FMLJavaModLoadingContext.get().getModEventBus().addListener(MythosClient::clientSetup);
        MenuScreens.register(MythosMenuTypes.ORUN_MENU.get(), OrunScreen::new);;
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.register(MythosParticles.RED_RUNES.get(), RedRunesParticles.Provider::new);
    }
}

