package com.github.trmythos.trmythos.registry.race;

import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.trmythos.trmythos.TRMythos;
import com.github.trmythos.trmythos.race.JormungandrRaceLine.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = TRMythos.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AllRaces {

    // Define the Race that you want to add here
    public static final ResourceLocation LESSER_SERPENT_RACE = new ResourceLocation(TRMythos.MODID, "lesser_serpent");
    public static final ResourceLocation SERPENT_RACE = new ResourceLocation(TRMythos.MODID, "serpent");
    public static final ResourceLocation GREATER_SERPENT_RACE = new ResourceLocation(TRMythos.MODID, "greater_serpent");
    public static final ResourceLocation SON_OF_LOKI_RACE = new ResourceLocation(TRMythos.MODID, "son_of_loki");
    public static final ResourceLocation MIDGARDIAN_SPIRIT_RACE = new ResourceLocation(TRMythos.MODID, "midgardian_spirit");
    public static final ResourceLocation JORMUNGANDR_RACE = new ResourceLocation(TRMythos.MODID, "jormungandr");


    /**
     * Make sure that you register the race, otherwise it will not show up correctly in the selection menu
     * @param event
     */
    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("serpent", new SerpentRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("lesser_serpent", new LesserSerpentRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("greater_serpent", new GreaterSerpentRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("son_of_loki", new SonOfLokiRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("midgardian_spirit", new MidgardianSpiritRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("jormungandr", new JormungandrRace());
        });
    }

}
