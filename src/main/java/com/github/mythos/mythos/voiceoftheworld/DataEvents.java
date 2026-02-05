package com.github.mythos.mythos.voiceoftheworld;

import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "trmythos", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DataEvents {
    @SubscribeEvent
    public static void onResourceReload(AddReloadListenerEvent event) {
        event.addListener(new TrialDataLoader());
    }
}
