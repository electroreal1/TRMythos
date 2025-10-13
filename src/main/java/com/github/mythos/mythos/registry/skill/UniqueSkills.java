package com.github.mythos.mythos.registry.skill;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.DemonLordHakiSkill;
import com.github.mythos.mythos.ability.skill.unique.FakerSkill;
import com.github.mythos.mythos.ability.skill.unique.OmniscientEyeSkill;
import com.github.mythos.mythos.ability.skill.unique.OpportunistSkill;
import com.github.mythos.mythos.ability.skill.unique.PuritySkill;
import com.github.mythos.mythos.ability.skill.unique.vassal_line.EvolutionSkill;
import com.github.mythos.mythos.ability.skill.unique.vassal_line.FoundationSkill;
import com.github.mythos.mythos.ability.skill.unique.vassal_line.UnitySkill;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class UniqueSkills {
    private static final DeferredRegister<ManasSkill> registery = DeferredRegister.create(SkillAPI.getSkillRegistryKey(), "trmythos");
    public static final RegistryObject<FakerSkill> FAKER;
    public static final RegistryObject<OmniscientEyeSkill> OMNISCIENT_EYE;
    public static final RegistryObject<FoundationSkill> FOUNDATION;
    public static final RegistryObject<UnitySkill> UNITY;
    public static final RegistryObject<EvolutionSkill> EVOLUTION;
    public static final RegistryObject<PuritySkill> PURITY_SKILL;
    public static final RegistryObject<OpportunistSkill> OPPORTUNIST_SKILL;

    public UniqueSkills() {
    }

    public static void init(IEventBus modEventBus) {
        registery.register(modEventBus);
    }


    static {
        FAKER = registery.register("faker", FakerSkill::new);
        OMNISCIENT_EYE = registery.register("omniscient_eye", OmniscientEyeSkill::new);
        PURITY_SKILL = registery.register("purity", () -> new PuritySkill(Skill.SkillType.UNIQUE));
        UNITY = registery.register("unity", () -> new UnitySkill(Skill.SkillType.UNIQUE));
        FOUNDATION = registery.register("foundation", () -> new FoundationSkill(Skill.SkillType.UNIQUE));
        EVOLUTION = registery.register("evolution", () -> new EvolutionSkill(Skill.SkillType.UNIQUE));
        OPPORTUNIST_SKILL = registery.register("opportunist", () -> new OpportunistSkill(Skill.SkillType.UNIQUE));
    }
}