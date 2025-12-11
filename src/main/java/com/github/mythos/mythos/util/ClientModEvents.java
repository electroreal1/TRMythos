package com.github.mythos.mythos.util;

import com.github.mythos.mythos.client.screen.OrunScreen;
import com.github.mythos.mythos.registry.MythosEntityTypes;
import com.github.mythos.mythos.registry.menu.MythosMenuTypes;
import com.github.mythos.mythos.renderers.VajraBreathRenderer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = "trmythos",
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = {Dist.CLIENT}
)

public class ClientModEvents {
    public ClientModEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(MythosMenuTypes.ORUN_MENU.get(), OrunScreen::new);
           // EntityRenderers.register(MythosEntityTypes.THUNDER_STORM.get(), ThunderStormRenderer::new);
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
//        event.registerEntityRenderer(MythosEntityTypes.DRAGONFIRE.get(), DragonfireRenderer::new);
      //  event.registerEntityRenderer(MythosEntityTypes.THUNDER_STORM.get(), ThunderStormRenderer::new);
        event.registerEntityRenderer(MythosEntityTypes.VAJRA_BREATH.get(), VajraBreathRenderer::new);
    }
}
