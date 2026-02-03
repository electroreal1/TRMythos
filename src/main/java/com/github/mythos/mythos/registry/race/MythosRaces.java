package com.github.mythos.mythos.registry.race;

import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.mythos.mythos.Mythos;
import com.github.mythos.mythos.race.CanineRaceLines.CanineRace;
import com.github.mythos.mythos.race.CanineRaceLines.CerberusRaceLine.CerberusRace;
import com.github.mythos.mythos.race.CanineRaceLines.CerberusRaceLine.HellHoundRace;
import com.github.mythos.mythos.race.CanineRaceLines.CerberusRaceLine.HoundOfHadesRace;
import com.github.mythos.mythos.race.CanineRaceLines.FenrirRaceLine.DreadBeastRace;
import com.github.mythos.mythos.race.CanineRaceLines.FenrirRaceLine.FenrisWolfRace;
import com.github.mythos.mythos.race.CanineRaceLines.FenrirRaceLine.HeraldOfRagnarokRace;
import com.github.mythos.mythos.race.DivinityLine.DarknessGod.GodOfDarknessRace;
import com.github.mythos.mythos.race.DivinityLine.DarknessGod.GreaterGodOfDarknessRace;
import com.github.mythos.mythos.race.DivinityLine.DarknessGod.LesserGodOfDarknessRace;
import com.github.mythos.mythos.race.DivinityLine.EarthGod.GodOfEarthRace;
import com.github.mythos.mythos.race.DivinityLine.EarthGod.GreaterGodOfEarthRace;
import com.github.mythos.mythos.race.DivinityLine.EarthGod.LesserGodOfEarthRace;
import com.github.mythos.mythos.race.DivinityLine.FlameGod.GodOfFlameRace;
import com.github.mythos.mythos.race.DivinityLine.FlameGod.GreaterGodOfFlameRace;
import com.github.mythos.mythos.race.DivinityLine.FlameGod.LesserGodOfFlameRace;
import com.github.mythos.mythos.race.DivinityLine.*;
import com.github.mythos.mythos.race.DivinityLine.LightGod.GodOfLightRace;
import com.github.mythos.mythos.race.DivinityLine.LightGod.GreaterGodOfLightRace;
import com.github.mythos.mythos.race.DivinityLine.LightGod.LesserGodOfLightRace;
import com.github.mythos.mythos.race.DivinityLine.SpaceGod.GodOfSpaceRace;
import com.github.mythos.mythos.race.DivinityLine.SpaceGod.GreaterGodOfSpaceRace;
import com.github.mythos.mythos.race.DivinityLine.SpaceGod.LesserGodOfSpaceRace;
import com.github.mythos.mythos.race.DivinityLine.WaterGod.GodOfWaterRace;
import com.github.mythos.mythos.race.DivinityLine.WaterGod.GreaterGodOfWaterRace;
import com.github.mythos.mythos.race.DivinityLine.WaterGod.LesserGodOfWaterRace;
import com.github.mythos.mythos.race.DivinityLine.WindGod.GodOfWindRace;
import com.github.mythos.mythos.race.DivinityLine.WindGod.GreaterGodOfWindRace;
import com.github.mythos.mythos.race.DivinityLine.WindGod.LesserGodOfWindRace;
import com.github.mythos.mythos.race.GodlingLines.BuddingDemigodRace;
import com.github.mythos.mythos.race.GodlingLines.EgyptianPantheon.DivineHostRace;
import com.github.mythos.mythos.race.GodlingLines.EgyptianPantheon.NetjeruRace;
import com.github.mythos.mythos.race.GodlingLines.GodlingRace;
import com.github.mythos.mythos.race.GodlingLines.GreekPantheon.EusebiaRace;
import com.github.mythos.mythos.race.GodlingLines.GreekPantheon.SemideusRace;
import com.github.mythos.mythos.race.GodlingLines.NorsePantheon.AesirRace;
import com.github.mythos.mythos.race.GodlingLines.NorsePantheon.UulbornRace;
import com.github.mythos.mythos.race.HydraLine.*;
import com.github.mythos.mythos.race.JormungandrRaceLine.*;
import com.github.mythos.mythos.race.MetalloidRaceLine.*;
import com.github.mythos.mythos.race.PaperLine.PaperRace;
import com.github.mythos.mythos.race.RevenantLine.*;
import com.github.mythos.mythos.race.ValkyrieRaceLine.EnvoyOfValhallaRace;
import com.github.mythos.mythos.race.ValkyrieRaceLine.MaidenRace;
import com.github.mythos.mythos.race.ValkyrieRaceLine.SoulCourierRace;
import com.github.mythos.mythos.race.ValkyrieRaceLine.ValkyrieRace;
import com.github.mythos.mythos.race.VampireEvoLine.*;
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
    public static final ResourceLocation AESIR = new ResourceLocation(Mythos.MOD_ID, "aesir");
    public static final ResourceLocation EUSEBIA = new ResourceLocation(Mythos.MOD_ID, "eusebia");
    public static final ResourceLocation NETJERU = new ResourceLocation(Mythos.MOD_ID, "netjeru");

    // Vampires

    public static final ResourceLocation VAMPIRE_BARON = new ResourceLocation(Mythos.MOD_ID, "vampire_baron");
    public static final ResourceLocation VAMPIRE_VISCOUNT = new ResourceLocation(Mythos.MOD_ID, "vampire_viscount");
    public static final ResourceLocation VAMPIRE_EARL = new ResourceLocation(Mythos.MOD_ID, "vampire_earl");
    public static final ResourceLocation VAMPIRE_MARQUIS = new ResourceLocation(Mythos.MOD_ID, "vampire_marquis");
    public static final ResourceLocation VAMPIRE_DUKE = new ResourceLocation(Mythos.MOD_ID, "vampire_duke");
    public static final ResourceLocation VAMPIRE_ARCHDUKE = new ResourceLocation(Mythos.MOD_ID, "vampire_archduke");
    public static final ResourceLocation VAMPIRE_PRINCE = new ResourceLocation(Mythos.MOD_ID, "vampire_prince");
    public static final ResourceLocation VAMPIRE_TRUE_ANCESTOR = new ResourceLocation(Mythos.MOD_ID, "vampire_true_ancestor");

    //Divinity Races
    public static final ResourceLocation NAMELESS_DIVINITY_RACE = new ResourceLocation(Mythos.MOD_ID, "nameless_divinity");
    public static final ResourceLocation NAMELESS_DIVINITY_V = new ResourceLocation(Mythos.MOD_ID, "nameless_divinity_v");
    public static final ResourceLocation LESSER_DIVINITY = new ResourceLocation(Mythos.MOD_ID, "lesser_divinity");
    public static final ResourceLocation MIDDLE_DIVINITY = new ResourceLocation(Mythos.MOD_ID, "middle_divinity");
    public static final ResourceLocation GREATER_DIVINITY = new ResourceLocation(Mythos.MOD_ID, "greater_divinity");
    public static final ResourceLocation LESSER_TITAN = new ResourceLocation(Mythos.MOD_ID, "lesser_titan");
    public static final ResourceLocation TITAN = new ResourceLocation(Mythos.MOD_ID, "titan");
    public static final ResourceLocation GREATER_TITAN = new ResourceLocation(Mythos.MOD_ID, "greater_titan");
    public static final ResourceLocation KING_OF_DIVINITY = new ResourceLocation(Mythos.MOD_ID, "king_of_divinity");
    //Flame God
    public static final ResourceLocation LESSER_GOD_OF_FLAME = new ResourceLocation(Mythos.MOD_ID, "lesser_god_of_flame");
    public static final ResourceLocation GOD_OF_FLAME = new ResourceLocation(Mythos.MOD_ID, "god_of_flame");
    public static final ResourceLocation GREATER_GOD_OF_FLAME = new ResourceLocation(Mythos.MOD_ID, "greater_god_of_flame");
    //Earth God
    public static final ResourceLocation LESSER_GOD_OF_EARTH = new ResourceLocation(Mythos.MOD_ID, "lesser_god_of_earth");
    public static final ResourceLocation GOD_OF_EARTH = new ResourceLocation(Mythos.MOD_ID, "god_of_earth");
    public static final ResourceLocation GREATER_GOD_OF_EARTH = new ResourceLocation(Mythos.MOD_ID, "greater_god_of_earth");
    //Water God
    public static final ResourceLocation LESSER_GOD_OF_WATER = new ResourceLocation(Mythos.MOD_ID, "lesser_god_of_water");
    public static final ResourceLocation GOD_OF_WATER = new ResourceLocation(Mythos.MOD_ID, "god_of_water");
    public static final ResourceLocation GREATER_GOD_OF_WATER = new ResourceLocation(Mythos.MOD_ID, "greater_god_of_water");
    //Wind God
    public static final ResourceLocation LESSER_GOD_OF_WIND = new ResourceLocation(Mythos.MOD_ID, "lesser_god_of_wind");
    public static final ResourceLocation GOD_OF_WIND = new ResourceLocation(Mythos.MOD_ID, "god_of_wind");
    public static final ResourceLocation GREATER_GOD_OF_WIND = new ResourceLocation(Mythos.MOD_ID, "greater_god_of_wind");
    //Space God
    public static final ResourceLocation LESSER_GOD_OF_SPACE = new ResourceLocation(Mythos.MOD_ID, "lesser_god_of_space");
    public static final ResourceLocation GOD_OF_SPACE = new ResourceLocation(Mythos.MOD_ID, "god_of_space");
    public static final ResourceLocation GREATER_GOD_OF_SPACE = new ResourceLocation(Mythos.MOD_ID, "greater_god_of_space");
    //Light God
    public static final ResourceLocation LESSER_GOD_OF_LIGHT = new ResourceLocation(Mythos.MOD_ID, "lesser_god_of_light");
    public static final ResourceLocation GOD_OF_LIGHT = new ResourceLocation(Mythos.MOD_ID, "god_of_light");
    public static final ResourceLocation GREATER_GOD_OF_LIGHT = new ResourceLocation(Mythos.MOD_ID, "greater_god_of_light");
    //Darkness God
    public static final ResourceLocation LESSER_GOD_OF_DARKNESS = new ResourceLocation(Mythos.MOD_ID, "lesser_god_of_darkness");
    public static final ResourceLocation GOD_OF_DARKNESS = new ResourceLocation(Mythos.MOD_ID, "god_of_darkness");
    public static final ResourceLocation GREATER_GOD_OF_DARKNESS = new ResourceLocation(Mythos.MOD_ID, "greater_god_of_darkness");

    // Hydra Races
    public static final ResourceLocation SEA_BEAST = new ResourceLocation(Mythos.MOD_ID, "sea_beast");
    public static final ResourceLocation SEA_SERPENT = new ResourceLocation(Mythos.MOD_ID, "sea_serpent");
    public static final ResourceLocation DARK_SEA_STALKER = new ResourceLocation(Mythos.MOD_ID, "dark_sea_stalker");
    public static final ResourceLocation DARK_SEA_TYRANT = new ResourceLocation(Mythos.MOD_ID, "dark_sea_tyrant");
    public static final ResourceLocation HYDRA = new ResourceLocation(Mythos.MOD_ID, "hydra");

    // Metalloid Races
    public static final ResourceLocation METALLOID = new ResourceLocation(Mythos.MOD_ID, "metalloid");
    public static final ResourceLocation METALLOID_PROTO = new ResourceLocation(Mythos.MOD_ID, "metalloid_proto");
    public static final ResourceLocation METALLOID_EXPERIMENTER = new ResourceLocation(Mythos.MOD_ID, "metalloid_experimenter");
    public static final ResourceLocation METALLOID_OVERSEER = new ResourceLocation(Mythos.MOD_ID, "metalloid_overseer");
    public static final ResourceLocation METALLOID_VANGUARD = new ResourceLocation(Mythos.MOD_ID, "metalloid_vanguard");
    public static final ResourceLocation METALLOID_SUPREME = new ResourceLocation(Mythos.MOD_ID, "metalloid_supreme");
    public static final ResourceLocation DEUS_EX_MACHINA = new ResourceLocation(Mythos.MOD_ID, "deus_ex_machina");

    // Revenant Races
    public static final ResourceLocation REVENANT = new ResourceLocation(Mythos.MOD_ID, "revenant");
    public static final ResourceLocation WRAITH = new ResourceLocation(Mythos.MOD_ID, "wraith");
    public static final ResourceLocation FORGOTTEN = new ResourceLocation(Mythos.MOD_ID, "forgotten");
    public static final ResourceLocation CHAOTIC_SPRITE = new ResourceLocation(Mythos.MOD_ID, "chaotic_sprite");
    public static final ResourceLocation VOID_APOSTLE = new ResourceLocation(Mythos.MOD_ID, "void_apostle");
    public static final ResourceLocation PRIMAL_CHAOS = new ResourceLocation(Mythos.MOD_ID, "primal_chaos");

    // Paper Races
    public static final ResourceLocation PAPER = new ResourceLocation(Mythos.MOD_ID, "paper");
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
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("aesir", new AesirRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("netjeru", new NetjeruRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("eusebia", new EusebiaRace());
        });

        // Vampire Races
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("vampire_baron", new VampireBaron());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("vampire_viscount", new VampireViscount());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("vampire_earl", new VampireEarl());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("vampire_marquis", new VampireMarquis());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("vampire_duke", new VampireDuke());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("vampire_archduke", new VampireArchduke());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("vampire_prince", new VampirePrince());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("vampire_true_ancestor", new VampireTrueAncestor());
        });

        // Divinity Races
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("nameless_divinity", new NamelessDivinityRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("nameless_divinity_v", new ValkNamelessDivinityRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("lesser_divinity", new LesserDivinityRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("middle_divinity", new MiddleDivinityRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("greater_divinity", new GreaterDivinityRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("lesser_titan", new LesserTitanRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("titan", new TitanRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("greater_titan", new GreaterTitanRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("king_of_divinity", new KingOfDivinityRace());
        });
        //Flame God
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("lesser_god_of_flame", new LesserGodOfFlameRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("god_of_flame", new GodOfFlameRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("greater_god_of_flame", new GreaterGodOfFlameRace());
        });
        //Earth God
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("lesser_god_of_earth", new LesserGodOfEarthRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("god_of_earth", new GodOfEarthRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("greater_god_of_earth", new GreaterGodOfEarthRace());
        });
        //Water God
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("lesser_god_of_water", new LesserGodOfWaterRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("god_of_water", new GodOfWaterRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("greater_god_of_water", new GreaterGodOfWaterRace());
        });
        //Wind God
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("lesser_god_of_wind", new LesserGodOfWindRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("god_of_wind", new GodOfWindRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("greater_god_of_wind", new GreaterGodOfWindRace());
        });
        //Space God
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("lesser_god_of_space", new LesserGodOfSpaceRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("god_of_space", new GodOfSpaceRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("greater_god_of_space", new GreaterGodOfSpaceRace());
        });
        //Light God
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("lesser_god_of_light", new LesserGodOfLightRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("god_of_light", new GodOfLightRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("greater_god_of_light", new GreaterGodOfLightRace());
        });
        //Darkness God
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("lesser_god_of_darkness", new LesserGodOfDarknessRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("god_of_darkness", new GodOfDarknessRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("greater_god_of_darkness", new GreaterGodOfDarknessRace());
        });

        // Hydra Races
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("sea_beast", new SeaBeastRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("sea_serpent", new SeaSerpentRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("dark_sea_stalker", new DarkSeaStalkerRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("dark_sea_tyrant", new DarkSeaTyrantRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("hydra", new HydraRace());
        });

        // Metalloid races
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("metalloid", new MetalloidRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("metalloid_proto", new MetalloidProtoRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("metalloid_experimenter", new MetalloidExperimenterRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("metalloid_overseer", new MetalloidOverseerRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("metalloid_supreme", new MetalloidSupremeRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("deus_ex_machina", new DeusExMachinaRace());
        });

        // Revenant Races
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("revenant", new RevenantRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("wraith", new WraithRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("forgotten", new ForgottenRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("chaotic_sprite", new ChaoticSpriteRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("void_apostle", new VoidApostleRace());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("primal_chaos", new PrimalChaosRace());
        });

        // Paper Races
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("paper", new PaperRace());
        });
    }

}
