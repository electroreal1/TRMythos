package com.github.mythos.mythos.registry.skill;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.mythos.mythos.ability.mythos.skill.extra.AutomaticHakiCoatSkill;
import com.github.mythos.mythos.ability.mythos.skill.extra.SpatialStorageSkill;
import com.github.mythos.mythos.ability.mythos.skill.extra.ThunderRainSkill;
import com.github.mythos.mythos.ability.mythos.skill.ultimate.*;
import com.github.mythos.mythos.ability.mythos.skill.unique.*;
import com.github.mythos.mythos.ability.mythos.skill.unique.evolved.*;
import com.github.mythos.mythos.ability.mythos.skill.unique.vassal_line.EvolutionSkill;
import com.github.mythos.mythos.ability.mythos.skill.unique.vassal_line.FoundationSkill;
import com.github.mythos.mythos.ability.mythos.skill.unique.vassal_line.UnitySkill;
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
    public static final RegistryObject<IndraSkill> INDRA;
    public static final RegistryObject<VayuSkill> VAYU;
    public static final RegistryObject<ElementalQueenSkill> ELEMENTAL_QUEEN;
    public static final RegistryObject<OriginDao> ORIGIN_DAO;
    // uniques
    public static final RegistryObject<FakerSkill> FAKER;
    public static final RegistryObject<OmniscientEyeSkill> OMNISCIENT_EYE;
    public static final RegistryObject<FoundationSkill> FOUNDATION;
    public static final RegistryObject<UnitySkill> UNITY;
    public static final RegistryObject<EvolutionSkill> EVOLUTION;
    public static final RegistryObject<PuritySkill> PURITY_SKILL;
    public static final RegistryObject<ProfanitySkill> PROFANITY;
    public static final RegistryObject<OpportunistSkill> OPPORTUNIST_SKILL;
    public static final RegistryObject<EltnamSkill> ELTNAM;
    public static final RegistryObject<BloodsuckerSkill> BLOODSUCKER;
//    public static final RegistryObject<DullahanSkill> DULLAHAN;
    public static final RegistryObject<TheWorldSkill> THE_WORLD;
    public static final RegistryObject<BibliomaniaSkill> BIBLIOMANIA;
    public static final RegistryObject<ChildOfThePlaneSkill> CHILD_OF_THE_PLANE;
    public static final RegistryObject<CrimsonTyrantSkill> CRIMSON_TYRANT;
    public static final RegistryObject<MonsterCreatorSkill> MONSTER_CREATOR;
    public static final RegistryObject<ZephyrosSkill> ZEPHYROS;
    public static final RegistryObject<HeavensWrathSkill> HEAVENS_WRATH;
    public static final RegistryObject<IntrovertSkill> INTROVERT;
    public static final RegistryObject<NascentDao> NASCENT_DAO;
    public static final RegistryObject<StargazerSkill> STARGAZER;
    public static final RegistryObject<TenaciousSkill> TENACIOUS;

    // evolved uniques
    public static final RegistryObject<CarnageSkill> CARNAGE;
    public static final RegistryObject<AwakenedDao> AWAKENED_DAO;
    public static final RegistryObject<TrueDao> TRUE_DAO;
    public static final RegistryObject<PerseveranceSkill> PERSEVERANCE;
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

        // ultimates
        ORUNMILA = registery.register("orunmila", () -> new OrunmilaSkill(Skill.SkillType.ULTIMATE));
        ZEPIA = registery.register("zepia", ZepiaSkill::new);
        ETERNAL = registery.register("eternal", () -> new EternalSkill(Skill.SkillType.ULTIMATE));
        IMMORTAL = registery.register("immortal", () -> new ImmortalSkill(Skill.SkillType.ULTIMATE));
        INDRA = registery.register("indra", () -> new IndraSkill(Skill.SkillType.ULTIMATE));
        VAYU = registery.register("vayu", () -> new VayuSkill(Skill.SkillType.ULTIMATE));
        ELEMENTAL_QUEEN = registery.register("elemental_queen", () -> new ElementalQueenSkill(Skill.SkillType.ULTIMATE));
        ORIGIN_DAO = registery.register("origin_dao", () -> new OriginDao(Skill.SkillType.ULTIMATE));

        // uniques
        FAKER = registery.register("faker", FakerSkill::new);
        ELTNAM = registery.register("eltnam", EltnamSkill::new);
        BIBLIOMANIA = registery.register("bibliomania", BibliomaniaSkill::new);
//        DULLAHAN = registery.register("dullahan", DullahanSkill::new);
        OMNISCIENT_EYE = registery.register("omniscient_eye", OmniscientEyeSkill::new);
        PURITY_SKILL = registery.register("purity", () -> new PuritySkill(Skill.SkillType.UNIQUE));
        UNITY = registery.register("unity", () -> new UnitySkill(Skill.SkillType.UNIQUE));
        FOUNDATION = registery.register("foundation", () -> new FoundationSkill(Skill.SkillType.UNIQUE));
        EVOLUTION = registery.register("evolution", () -> new EvolutionSkill(Skill.SkillType.UNIQUE));
        OPPORTUNIST_SKILL = registery.register("opportunist", () -> new OpportunistSkill(Skill.SkillType.UNIQUE));
        PROFANITY = registery.register("profanity", () -> new ProfanitySkill(Skill.SkillType.UNIQUE));
        BLOODSUCKER = registery.register("bloodsucker", () -> new BloodsuckerSkill(Skill.SkillType.UNIQUE));
        CHILD_OF_THE_PLANE = registery.register("child_of_the_plane", () -> new ChildOfThePlaneSkill(Skill.SkillType.UNIQUE));
        CRIMSON_TYRANT = registery.register("crimson_tyrant", () -> new CrimsonTyrantSkill(Skill.SkillType.UNIQUE));
        MONSTER_CREATOR = registery.register("monster_creator", () -> new MonsterCreatorSkill(Skill.SkillType.UNIQUE));
        ZEPHYROS = registery.register("zephyros", () -> new ZephyrosSkill(Skill.SkillType.UNIQUE));
        HEAVENS_WRATH = registery.register("heavens_wrath", () -> new HeavensWrathSkill(Skill.SkillType.UNIQUE));
        INTROVERT = registery.register("introvert", () -> new IntrovertSkill(Skill.SkillType.UNIQUE));
        NASCENT_DAO = registery.register("nascent_dao", () -> new NascentDao(Skill.SkillType.UNIQUE));
        STARGAZER = registery.register("stargazer", () -> new StargazerSkill(Skill.SkillType.UNIQUE));
        TENACIOUS = registery.register("tenacious", () -> new TenaciousSkill(Skill.SkillType.UNIQUE));
        PERSEVERANCE = registery.register("perseverance", () -> new PerseveranceSkill(Skill.SkillType.UNIQUE));

        // evolved uniques
        CARNAGE = registery.register("carnage", () -> new CarnageSkill(Skill.SkillType.UNIQUE));
        THE_WORLD = registery.register("the_world", () -> new TheWorldSkill(Skill.SkillType.UNIQUE));
        AWAKENED_DAO = registery.register("awakened_dao", () -> new AwakenedDao(Skill.SkillType.UNIQUE));
        TRUE_DAO = registery.register("true_dao", () -> new TrueDao(Skill.SkillType.UNIQUE));

        // extra
        AUTOMATIC_HAKI_COAT = registery.register("automatic_haki_coat", () -> new AutomaticHakiCoatSkill(Skill.SkillType.EXTRA));
        SPATIAL_STORAGE_SKILL = registery.register("spatial_storage", () -> new SpatialStorageSkill(Skill.SkillType.EXTRA));
        THUNDER_RAIN = registery.register("thunder_rain", () -> new ThunderRainSkill(Skill.SkillType.EXTRA));
    }
}
