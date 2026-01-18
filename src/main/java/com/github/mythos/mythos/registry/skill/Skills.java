package com.github.mythos.mythos.registry.skill;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.mythos.mythos.ability.mythos.skill.extra.AutomaticHakiCoatSkill;
import com.github.mythos.mythos.ability.mythos.skill.extra.SpatialStorageSkill;
import com.github.mythos.mythos.ability.mythos.skill.extra.ThunderRainSkill;
import com.github.mythos.mythos.ability.mythos.skill.ultimate.*;
import com.github.mythos.mythos.ability.mythos.skill.ultimate.god.DendrrahSkill;
import com.github.mythos.mythos.ability.mythos.skill.ultimate.god.Khonsu;
import com.github.mythos.mythos.ability.mythos.skill.ultimate.god.Kthanid;
import com.github.mythos.mythos.ability.mythos.skill.ultimate.lord.*;
import com.github.mythos.mythos.ability.mythos.skill.ultimate.prince.HaliSkill;
import com.github.mythos.mythos.ability.mythos.skill.ultimate.prince.ZeroSkill;
import com.github.mythos.mythos.ability.mythos.skill.unique.DominateSkill;
import com.github.mythos.mythos.ability.mythos.skill.unique.DullahanSkill;
import com.github.mythos.mythos.ability.mythos.skill.unique.TenaciousSkill;
import com.github.mythos.mythos.ability.mythos.skill.unique.evolved.*;
import com.github.mythos.mythos.ability.mythos.skill.unique.megalomaniac_watcher.MegalomaniacSkill;
import com.github.mythos.mythos.ability.mythos.skill.unique.megalomaniac_watcher.WatcherSkill;
import com.github.mythos.mythos.ability.mythos.skill.unique.normal.*;
import com.github.mythos.mythos.ability.mythos.skill.unique.vassal_line.BalanceSkill;
import com.github.mythos.mythos.ability.mythos.skill.unique.vassal_line.EvolutionSkill;
import com.github.mythos.mythos.ability.mythos.skill.unique.vassal_line.FoundationSkill;
import com.github.mythos.mythos.ability.mythos.skill.unique.vassal_line.UnitySkill;
import com.github.mythos.mythos.ability.mythos.skill.unique.zodiac_line.SagittariusSkill;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class Skills {
    private static final DeferredRegister<ManasSkill> registery = DeferredRegister.create(SkillAPI.getSkillRegistryKey(), "trmythos");

    // ultimates
    public static final RegistryObject<OrunmilaSkill> ORUNMILA;
    public static final RegistryObject<EternalSkill> ETERNAL;
    public static final RegistryObject<ImmortalSkill> IMMORTAL;
    public static final RegistryObject<ZepiaSkill> ZEPIA;
    public static final RegistryObject<TatariSkill> TATARI;
    public static final RegistryObject<IndraSkill> INDRA;
    public static final RegistryObject<VayuSkill> VAYU;
    public static final RegistryObject<ElementalQueenSkill> ELEMENTAL_QUEEN;
    public static final RegistryObject<OriginDao> ORIGIN_DAO;
    public static final RegistryObject<AresSkill> ARES;
    public static final RegistryObject<ApophisSkill> APOPHIS;
    public static final RegistryObject<DikeSkill> DIKE;
    public static final RegistryObject<RavanaSkill> RAVANA;
    public static final RegistryObject<LuciaSkill> LUCIA;
    public static final RegistryObject<Khaos> KHAOS;
    public static final RegistryObject<MammonSkill> MAMMON;
    public static final RegistryObject<AsclepiusSkill> ASCLEPIUS;

    // Prince Class
    public static final RegistryObject<ZeroSkill> ZERO;
    public static final RegistryObject<HaliSkill> HALI;

    // God Class
    public static final RegistryObject<DendrrahSkill> DENDRRAH;
    public static final RegistryObject<Khonsu> KHONSU;
    public static final RegistryObject<Kthanid> KTHANID;

    // uniques
    public static final RegistryObject<FakerSkill> FAKER;
    public static final RegistryObject<OmniscientEyeSkill> OMNISCIENT_EYE;
    public static final RegistryObject<PuritySkill> PURITY_SKILL;
    public static final RegistryObject<ProfanitySkill> PROFANITY;
    public static final RegistryObject<OpportunistSkill> OPPORTUNIST_SKILL;
    public static final RegistryObject<EltnamSkill> ELTNAM;
    public static final RegistryObject<BloodsuckerSkill> BLOODSUCKER;
    public static final RegistryObject<SaintSkill> SAINT;
    public static final RegistryObject<Demonologist> DEMONOLOGIST;
    public static final RegistryObject<DullahanSkill> DULLAHAN;
    public static final RegistryObject<TheWorldSkill> THE_WORLD;
    public static final RegistryObject<BibliomaniaSkill> BIBLIOMANIA;
    public static final RegistryObject<ChildOfThePlaneSkill> CHILD_OF_THE_PLANE;
    public static final RegistryObject<CrimsonTyrantSkill> CRIMSON_TYRANT;
    public static final RegistryObject<ZephyrosSkill> ZEPHYROS;
    public static final RegistryObject<HeavensWrathSkill> HEAVENS_WRATH;
    public static final RegistryObject<IntrovertSkill> INTROVERT;
    public static final RegistryObject<NascentDao> NASCENT_DAO;
    public static final RegistryObject<StargazerSkill> STARGAZER;
    public static final RegistryObject<TenaciousSkill> TENACIOUS;
    public static final RegistryObject<MirrorImageSkill> MIRROR_IMAGE;
    public static final RegistryObject<UnderworldPrince> UNDERWORLD_PRINCE;
    public static final RegistryObject<CommonSenseSkill> COMMON_SENSE;
    public static final RegistryObject<DominateSkill> DOMINATE;
    public static final RegistryObject<CrimsonOracleSkill> CRIMSON_ORACLE;
    public static final RegistryObject<SagittariusSkill> SAGITTARIUS;
    public static final RegistryObject<NpcLifeSkill> NPC_LIFE;
    public static final RegistryObject<AlchemistSkill> ALCHEMIST;
    public static final RegistryObject<CrackedPhilospherStoneSkill> CRACKED_PHILOSOPHER_STONE;
    public static final RegistryObject<PretenderKingSkill> PRETENDER_KING;
    public static final RegistryObject<HoarderSkill> HOARDER;
    public static final RegistryObject<NightsThiefSkill> NIGHTS_THIEF;
    public static final RegistryObject<FalseHeroSkill> FALSE_HERO;
    public static final RegistryObject<WavebreakerSkill> WAVEBREAKER;
    public static final RegistryObject<EarthshakerSkill> EARTHSHAKER;
    public static final RegistryObject<CrimsonArcanistSkill> CRIMSON_ARCANIST;
    public static final RegistryObject<Gaze> GAZE;
    public static final RegistryObject<ShadowOfTheTesseract> SHADOW_OF_THE_TESSERACT;
    public static final RegistryObject<YellowSign> YELLOW_SIGN;
    public static final RegistryObject<AuthorSkill> AUTHOR;
    public static final RegistryObject<CultistSkill> CULTIST;
    public static final RegistryObject<MegalomaniacSkill> MEGALOMANIAC;
    public static final RegistryObject<WatcherSkill> WATCHER;
    public static final RegistryObject<ControlFreakSkill> CONTROL_FREAK;
    public static final RegistryObject<LoserSkill> LOSER;

    // vassal
    public static final RegistryObject<FoundationSkill> FOUNDATION;
    public static final RegistryObject<UnitySkill> UNITY;
    public static final RegistryObject<EvolutionSkill> EVOLUTION;
    public static final RegistryObject<BalanceSkill> BALANCE;

    // evolved uniques
    public static final RegistryObject<CarnageSkill> CARNAGE;
    public static final RegistryObject<AwakenedDao> AWAKENED_DAO;
    public static final RegistryObject<TrueDao> TRUE_DAO;
    public static final RegistryObject<PerseveranceSkill> PERSEVERANCE;
//    public static final RegistryObject<ShadowSkill> SHADOW;

    // extra
    public static final RegistryObject<AutomaticHakiCoatSkill> AUTOMATIC_HAKI_COAT;
    public static final RegistryObject<SpatialStorageSkill> SPATIAL_STORAGE_SKILL;
    public static final RegistryObject<ThunderRainSkill> THUNDER_RAIN;

    public Skills() {
    }

    public static void init(IEventBus modEventBus) {
        registery.register(modEventBus);
    }


    static {

        // Ultimates
        ORUNMILA = registery.register("orunmila", () -> new OrunmilaSkill(Skill.SkillType.ULTIMATE));
        ZEPIA = registery.register("zepia", ZepiaSkill::new);
        TATARI = registery.register("tatari", TatariSkill::new);
        ETERNAL = registery.register("eternal", () -> new EternalSkill(Skill.SkillType.ULTIMATE));
        IMMORTAL = registery.register("immortal", () -> new ImmortalSkill(Skill.SkillType.ULTIMATE));
        INDRA = registery.register("indra", () -> new IndraSkill(Skill.SkillType.ULTIMATE));
        VAYU = registery.register("vayu", () -> new VayuSkill(Skill.SkillType.ULTIMATE));
        ELEMENTAL_QUEEN = registery.register("elemental_queen", () -> new ElementalQueenSkill(Skill.SkillType.ULTIMATE));
        ORIGIN_DAO = registery.register("origin_dao", () -> new OriginDao(Skill.SkillType.ULTIMATE));
        ARES = registery.register("ares", () -> new AresSkill(Skill.SkillType.ULTIMATE));
        APOPHIS = registery.register("apophis", () -> new ApophisSkill(Skill.SkillType.ULTIMATE));
        DIKE = registery.register("dike", () -> new DikeSkill(Skill.SkillType.ULTIMATE));
        RAVANA = registery.register("ravana", () -> new RavanaSkill(Skill.SkillType.ULTIMATE));
        LUCIA = registery.register("lucia", () -> new LuciaSkill(Skill.SkillType.ULTIMATE));
        KHAOS = registery.register("khaos", () -> new Khaos(Skill.SkillType.ULTIMATE));
        MAMMON = registery.register("mammon", () -> new MammonSkill(Skill.SkillType.ULTIMATE));
        ASCLEPIUS = registery.register("asclepius", () -> new AsclepiusSkill(Skill.SkillType.ULTIMATE));

        // Prince Class
        ZERO = registery.register("zero", () -> new ZeroSkill(Skill.SkillType.ULTIMATE));
        HALI = registery.register("hali", () -> new HaliSkill(Skill.SkillType.ULTIMATE));

        // god Class
        DENDRRAH = registery.register("dendrahh", () -> new DendrrahSkill(Skill.SkillType.ULTIMATE));
        KHONSU = registery.register("khonsu", () -> new Khonsu(Skill.SkillType.ULTIMATE));
        KTHANID = registery.register("kthanid", () -> new Kthanid(Skill.SkillType.ULTIMATE));

        // uniques
        FAKER = registery.register("faker", FakerSkill::new);
        ELTNAM = registery.register("eltnam", EltnamSkill::new);
        BIBLIOMANIA = registery.register("bibliomania", BibliomaniaSkill::new);
        SAINT = registery.register("saint", SaintSkill::new);
        DEMONOLOGIST = registery.register("demonologist", () -> new Demonologist(Skill.SkillType.UNIQUE));
        DULLAHAN = registery.register("dullahan", DullahanSkill::new);
        OMNISCIENT_EYE = registery.register("omniscient_eye", OmniscientEyeSkill::new);
        PURITY_SKILL = registery.register("purity", () -> new PuritySkill(Skill.SkillType.UNIQUE));
        OPPORTUNIST_SKILL = registery.register("opportunist", () -> new OpportunistSkill(Skill.SkillType.UNIQUE));
        PROFANITY = registery.register("profanity", () -> new ProfanitySkill(Skill.SkillType.UNIQUE));
        BLOODSUCKER = registery.register("bloodsucker", () -> new BloodsuckerSkill(Skill.SkillType.UNIQUE));
        CHILD_OF_THE_PLANE = registery.register("child_of_the_plane", () -> new ChildOfThePlaneSkill(Skill.SkillType.UNIQUE));
        CRIMSON_TYRANT = registery.register("crimson_tyrant", () -> new CrimsonTyrantSkill(Skill.SkillType.UNIQUE));
        ZEPHYROS = registery.register("zephyros", () -> new ZephyrosSkill(Skill.SkillType.UNIQUE));
        HEAVENS_WRATH = registery.register("heavens_wrath", () -> new HeavensWrathSkill(Skill.SkillType.UNIQUE));
        INTROVERT = registery.register("introvert", () -> new IntrovertSkill(Skill.SkillType.UNIQUE));
        NASCENT_DAO = registery.register("nascent_dao", () -> new NascentDao(Skill.SkillType.UNIQUE));
        STARGAZER = registery.register("stargazer", () -> new StargazerSkill(Skill.SkillType.UNIQUE));
        TENACIOUS = registery.register("tenacious", () -> new TenaciousSkill(Skill.SkillType.UNIQUE));
        MIRROR_IMAGE = registery.register("mirror_image", () -> new MirrorImageSkill(Skill.SkillType.UNIQUE));
        UNDERWORLD_PRINCE = registery.register("underworld_prince", () -> new UnderworldPrince(Skill.SkillType.UNIQUE));
        COMMON_SENSE = registery.register("common_sense", () -> new CommonSenseSkill(Skill.SkillType.UNIQUE));
        DOMINATE = registery.register("dominate", () -> new DominateSkill(Skill.SkillType.UNIQUE));
        CRIMSON_ORACLE = registery.register("crimson_oracle", () -> new CrimsonOracleSkill(Skill.SkillType.UNIQUE));
        SAGITTARIUS = registery.register("sagittarius", () -> new SagittariusSkill(Skill.SkillType.UNIQUE));
        NPC_LIFE = registery.register("npc_life", () -> new NpcLifeSkill(Skill.SkillType.UNIQUE));
        ALCHEMIST = registery.register("alchemist", () -> new AlchemistSkill(Skill.SkillType.UNIQUE));
        CRACKED_PHILOSOPHER_STONE = registery.register("cracked_philosopher_stone", () -> new CrackedPhilospherStoneSkill(Skill.SkillType.UNIQUE));
        PRETENDER_KING = registery.register("pretender_king", () -> new PretenderKingSkill(Skill.SkillType.UNIQUE));
        HOARDER = registery.register("hoarder", () -> new HoarderSkill(Skill.SkillType.UNIQUE));
        NIGHTS_THIEF = registery.register("nights_thief", () -> new NightsThiefSkill(Skill.SkillType.UNIQUE));
        FALSE_HERO = registery.register("false_hero", () -> new FalseHeroSkill(Skill.SkillType.UNIQUE));
        WAVEBREAKER = registery.register("wavebreaker", () -> new WavebreakerSkill(Skill.SkillType.UNIQUE));
        EARTHSHAKER = registery.register("earthshaker", () -> new EarthshakerSkill(Skill.SkillType.UNIQUE));
        CRIMSON_ARCANIST = registery.register("crimson_arcanist", () -> new CrimsonArcanistSkill(Skill.SkillType.UNIQUE));
        GAZE = registery.register("gaze", () -> new Gaze(Skill.SkillType.UNIQUE));
        YELLOW_SIGN = registery.register("yellow_sign", () -> new YellowSign(Skill.SkillType.UNIQUE));
        AUTHOR = registery.register("author", () -> new AuthorSkill(Skill.SkillType.UNIQUE));
        CULTIST = registery.register("cultist", () -> new CultistSkill(Skill.SkillType.UNIQUE));
        MEGALOMANIAC = registery.register("megalomaniac", () -> new MegalomaniacSkill(Skill.SkillType.UNIQUE));
        WATCHER = registery.register("watcher", () -> new WatcherSkill(Skill.SkillType.UNIQUE));
        CONTROL_FREAK = registery.register("control_freak", () -> new ControlFreakSkill(Skill.SkillType.UNIQUE));
        LOSER = registery.register("loser", () -> new LoserSkill(Skill.SkillType.UNIQUE));

        // vassal series
        UNITY = registery.register("unity", () -> new UnitySkill(Skill.SkillType.UNIQUE));
        FOUNDATION = registery.register("foundation", () -> new FoundationSkill(Skill.SkillType.UNIQUE));
        EVOLUTION = registery.register("evolution", () -> new EvolutionSkill(Skill.SkillType.UNIQUE));
        BALANCE = registery.register("balance", () -> new BalanceSkill(Skill.SkillType.UNIQUE));

        // evolved uniques
        CARNAGE = registery.register("carnage", () -> new CarnageSkill(Skill.SkillType.UNIQUE));
        THE_WORLD = registery.register("the_world", () -> new TheWorldSkill(Skill.SkillType.UNIQUE));
        AWAKENED_DAO = registery.register("awakened_dao", () -> new AwakenedDao(Skill.SkillType.UNIQUE));
        TRUE_DAO = registery.register("true_dao", () -> new TrueDao(Skill.SkillType.UNIQUE));
        PERSEVERANCE = registery.register("perseverance", () -> new PerseveranceSkill(Skill.SkillType.UNIQUE));
        SHADOW_OF_THE_TESSERACT = registery.register("shadow_of_the_tesseract", () -> new ShadowOfTheTesseract(Skill.SkillType.UNIQUE));
        //SHADOW = registery.register("shadow", () -> new ShadowSkill(Skill.SkillType.UNIQUE));

        // extra
        AUTOMATIC_HAKI_COAT = registery.register("automatic_haki_coat", () -> new AutomaticHakiCoatSkill(Skill.SkillType.EXTRA));
        SPATIAL_STORAGE_SKILL = registery.register("spatial_storage", () -> new SpatialStorageSkill(Skill.SkillType.EXTRA));
        THUNDER_RAIN = registery.register("thunder_rain", () -> new ThunderRainSkill(Skill.SkillType.EXTRA));
    }
}
