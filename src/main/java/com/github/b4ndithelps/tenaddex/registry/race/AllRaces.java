package com.github.b4ndithelps.tenaddex.registry.race;

import com.github.b4ndithelps.tenaddex.TensuraAddonExample;
import com.github.b4ndithelps.tenaddex.race.ExampleRace;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = TensuraAddonExample.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AllRaces {

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("example_race", new ExampleRace());
        });
    }

}
