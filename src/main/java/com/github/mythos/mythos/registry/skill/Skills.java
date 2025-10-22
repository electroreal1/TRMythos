package com.github.mythos.mythos.registry.skill;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.mythos.mythos.ability.skill.ultimate.OrunmilaSkill;
import com.github.mythos.mythos.ability.skill.unique.*;
import com.github.mythos.mythos.ability.skill.unique.vassal_line.EvolutionSkill;
import com.github.mythos.mythos.ability.skill.unique.vassal_line.FoundationSkill;
import com.github.mythos.mythos.ability.skill.unique.vassal_line.UnitySkill;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class Skills {
    private static final DeferredRegister<ManasSkill> registery = DeferredRegister.create(SkillAPI.getSkillRegistryKey(), "trmythos");
    public static final RegistryObject<FakerSkill> FAKER;
    public static final RegistryObject<OmniscientEyeSkill> OMNISCIENT_EYE;
    public static final RegistryObject<FoundationSkill> FOUNDATION;
    public static final RegistryObject<UnitySkill> UNITY;
    public static final RegistryObject<EvolutionSkill> EVOLUTION;
    public static final RegistryObject<PuritySkill> PURITY_SKILL;
    public static final RegistryObject<ProfanitySkill> PROFANITY;
    public static final RegistryObject<OpportunistSkill> OPPORTUNIST_SKILL;
    public static final RegistryObject<OrunmilaSkill> ORUNMILA;
    public static final RegistryObject<EltnamSkill> ELTNAM;
    public static final RegistryObject<CursedBloodlineSkill> CURSED_BLOODLINE;
   // public static final RegistryObject<BloodsuckerSkill> BLOODSUCKER;

    public Skills() {
    }

    public static void init(IEventBus modEventBus) {

        registery.register(modEventBus);
    }


    static {
        FAKER = registery.register("faker", FakerSkill::new);
        ELTNAM = registery.register("eltnam", EltnamSkill::new);
        CURSED_BLOODLINE = registery.register("cursed_bloodline", CursedBloodlineSkill::new);
        PROFANITY = registery.register("profanity", () -> new ProfanitySkill(Skill.SkillType.UNIQUE));
        OMNISCIENT_EYE = registery.register("omniscient_eye", OmniscientEyeSkill::new);
        ORUNMILA = registery.register("orunmila", () -> new OrunmilaSkill(Skill.SkillType.ULTIMATE));
        PURITY_SKILL = registery.register("purity", () -> new PuritySkill(Skill.SkillType.UNIQUE));
        UNITY = registery.register("unity", () -> new UnitySkill(Skill.SkillType.UNIQUE));
        FOUNDATION = registery.register("foundation", () -> new FoundationSkill(Skill.SkillType.UNIQUE));
        EVOLUTION = registery.register("evolution", () -> new EvolutionSkill(Skill.SkillType.UNIQUE));
        OPPORTUNIST_SKILL = registery.register("opportunist", () -> new OpportunistSkill(Skill.SkillType.UNIQUE));
      //  BLOODSUCKER = registery.register("bloodsucker", () -> new BloodsuckerSkill(Skill.SkillType.UNIQUE));
    }
}