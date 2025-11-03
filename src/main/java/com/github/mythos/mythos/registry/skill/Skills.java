package com.github.mythos.mythos.registry.skill;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.mythos.mythos.ability.mythos.skill.extra.AutomaticHakiCoatSkill;
import com.github.mythos.mythos.ability.mythos.skill.ultimate.EternalSkill;
import com.github.mythos.mythos.ability.mythos.skill.ultimate.ImmortalSkill;
import com.github.mythos.mythos.ability.mythos.skill.ultimate.OrunmilaSkill;
import com.github.mythos.mythos.ability.mythos.skill.ultimate.ZepiaSkill;
import com.github.mythos.mythos.ability.mythos.skill.unique.*;
import com.github.mythos.mythos.ability.mythos.skill.unique.evolved.CarnageSkill;
import com.github.mythos.mythos.ability.mythos.skill.unique.evolved.TheWorldSkill;
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
    public static final RegistryObject<ZepiaSkill> ZEPIA;
    public static final RegistryObject<BloodsuckerSkill> BLOODSUCKER;
    public static final RegistryObject<TheWorldSkill> THE_WORLD;
    public static final RegistryObject<ChildOfThePlaneSkill> CHILD_OF_THE_PLANE;
    public static final RegistryObject<CrimsonTyrantSkill> CRIMSON_TYRANT;

    // evolved uniques
    public static final RegistryObject<CarnageSkill> CARNAGE;
    // extra
    public static final RegistryObject<AutomaticHakiCoatSkill> AUTOMATIC_HAKI_COAT;

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

        // uniques
        FAKER = registery.register("faker", FakerSkill::new);
        ELTNAM = registery.register("eltnam", EltnamSkill::new);
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

        // evolved uniques
        CARNAGE = registery.register("carnage", () -> new CarnageSkill(Skill.SkillType.UNIQUE));
        THE_WORLD = registery.register("the_world", () -> new TheWorldSkill(Skill.SkillType.UNIQUE));

        // extra
        AUTOMATIC_HAKI_COAT = registery.register("automatic_haki_coat", () -> new AutomaticHakiCoatSkill(Skill.SkillType.EXTRA));
    }
}