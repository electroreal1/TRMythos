package com.github.mythos.mythos.registry.race;

import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.mythos.mythos.Mythos;
import com.github.mythos.mythos.race.CanineRaceLines.CerberusRaceLine.HoundOfHadesRace;
import com.github.mythos.mythos.race.CanineRaceLines.FenrirRaceLine.HeraldOfRagnarokRace;
import com.github.mythos.mythos.race.GodlingLines.BuddingDemigodRace;
import com.github.mythos.mythos.race.GodlingLines.EgyptianPantheon.DivineHostRace;
import com.github.mythos.mythos.race.GodlingLines.GodlingRace;
import com.github.mythos.mythos.race.GodlingLines.GreekPantheon.SemideusRace;
import com.github.mythos.mythos.race.GodlingLines.NorsePantheon.UulbornRace;
import com.github.mythos.mythos.race.JormungandrRaceLine.*;
import com.github.mythos.mythos.race.CanineRaceLines.CanineRace;
import com.github.mythos.mythos.race.CanineRaceLines.CerberusRaceLine.CerberusRace;
import com.github.mythos.mythos.race.CanineRaceLines.CerberusRaceLine.HellHoundRace;
import com.github.mythos.mythos.race.CanineRaceLines.FenrirRaceLine.DreadBeastRace;
import com.github.mythos.mythos.race.CanineRaceLines.FenrirRaceLine.FenrisWolfRace;
import com.github.mythos.mythos.race.JormungandrRaceLine.*;
import com.github.mythos.mythos.race.ValkyrieRaceLine.EnvoyOfValhallaRace;
import com.github.mythos.mythos.race.ValkyrieRaceLine.MaidenRace;
import com.github.mythos.mythos.race.ValkyrieRaceLine.ValkyrieRace;
import com.github.trmythos.trmythos.race.ValkyrieRaceLine.SoulCourierRace;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = Mythos.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MythosRaces {

    // Define the Race that you want to add here

    // Serpent Races
    public static final ResourceLocation LESSER_SERPENT_RACE = new ResourceLocation(Mythos.MOD_ID, "lesser_serpent");
    public static final ResourceLocation SERPENT_RACE = new ResourceLocation(Mythos.MOD_ID, "serpent");
    public static final ResourceLocation GREATER_SERPENT_RACE = new ResourceLocation(Mythos.MOD_ID, "greater_serpent");
    public static final ResourceLocation SON_OF_LOKI_RACE = new ResourceLocation(Mythos.MOD_ID, "son_of_loki");
    public static final ResourceLocation MIDGARDIAN_SPIRIT_RACE = new ResourceLocation(Mythos.MOD_ID, "midgardian_spirit");
    public static final ResourceLocation JORMUNGANDR_RACE = new ResourceLocation(Mythos.MOD_ID, "jormungandr");

    // Canine Races
    public static final ResourceLocation CANINE_RACE = new ResourceLocation(Mythos.MOD_ID, "canine");
    public static final ResourceLocation DREAD_BEAST_RACE = new ResourceLocation(Mythos.MOD_ID, "dread_beast");
    public static final ResourceLocation FENRIS_WOLF_RACE = new ResourceLocation(Mythos.MOD_ID, "fenris_wolf");
    public static final ResourceLocation HELL_HOUND_RACE = new ResourceLocation(Mythos.MOD_ID, "hell_hound");
    public static final ResourceLocation CERBERUS_RACE = new ResourceLocation(Mythos.MOD_ID, "cerberus");
    public static final ResourceLocation HERALD_OF_RAGNAROK_RACE = new ResourceLocation(Mythos.MOD_ID, "herald_of_ragnarok");
    public static final ResourceLocation HOUND_OF_HADES_RACE = new ResourceLocation(Mythos.MOD_ID, "hound_of_hades");

    // Valkyrie Races
    public static final ResourceLocation MAIDEN_RACE = new ResourceLocation(Mythos.MOD_ID, "maiden");
    public static final ResourceLocation SOUL_COURIER_RACE = new ResourceLocation(Mythos.MOD_ID, "soul_courier");
    public static final ResourceLocation VALKYRIE_RACE = new ResourceLocation(Mythos.MOD_ID, "valkyrie");
    public static final ResourceLocation ENVOY_OF_VALHALLA = new ResourceLocation(Mythos.MOD_ID, "envoy_of_valhalla");

    // Godling Races
    public static final ResourceLocation GODLING_RACE = new ResourceLocation(Mythos.MOD_ID, "godling");
    public static final ResourceLocation BUDDING_DEMIGOD = new ResourceLocation(Mythos.MOD_ID, "budding_demigod");
    public static final ResourceLocation SEMIDEUS = new ResourceLocation(Mythos.MOD_ID, "semideus");
    public static final ResourceLocation UUL_BORN = new ResourceLocation(Mythos.MOD_ID, "uul_born");
    public static final ResourceLocation DIVINE_HOST = new ResourceLocation(Mythos.MOD_ID, "divine_host");
    /**
     * Make sure that you register the race, otherwise it will not show up correctly in the selection menu
     * @param event
     */
    @SubscribeEvent
    public static void register(RegisterEvent event) {
        // Serpent Races
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

        // Canine Races
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("canine", new CanineRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("dread_beast", new DreadBeastRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("fenris_wolf", new FenrisWolfRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("hell_hound", new HellHoundRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("cerberus", new CerberusRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("herald_of_ragnarok", new HeraldOfRagnarokRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("hound_of_hades", new HoundOfHadesRace());
        });

        // Valkyrie Races
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("maiden", new MaidenRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("soul_courier", new SoulCourierRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("valkyrie", new ValkyrieRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("envoy_of_valhalla", new EnvoyOfValhallaRace());
        });

        // Godling Races
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("godling", new GodlingRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("budding_demigod", new BuddingDemigodRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("semideus", new SemideusRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("uul_born", new UulbornRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("divine_host", new DivineHostRace());
        });


    }

}
