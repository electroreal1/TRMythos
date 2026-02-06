package com.github.mythos.mythos.registry;

import com.github.mythos.mythos.client.screen.OrunScreen;
import com.github.mythos.mythos.handler.HaliShaderHandler;
import com.github.mythos.mythos.handler.YellowSignOverlayHandler;
import com.github.mythos.mythos.networking.play2server.ShaderPacket;
import com.github.mythos.mythos.registry.menu.MythosMenuTypes;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;

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

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceManager(),
                new ResourceLocation("trmythos", "mythos_dome"), DefaultVertexFormat.POSITION_COLOR_TEX), (inst) -> {
            MythosShaders.DOME_SHADER = inst;
        });
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.level != null && MythosWorldVisuals.alpha > 0) {

                if (mc.gameRenderer.currentEffect() == null) {
                    mc.gameRenderer.loadEffect(new ResourceLocation("trmythos", "shaders/post/master_sky.json"));
                }

                ShaderPacket.applyColorsToShader(MythosWorldVisuals.r, MythosWorldVisuals.g, MythosWorldVisuals.b);
            }
        }
    }
}