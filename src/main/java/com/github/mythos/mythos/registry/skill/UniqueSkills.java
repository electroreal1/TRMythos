package com.github.mythos.mythos.registry.skill;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.mythos.mythos.ability.skill.unique.FakerSkill;
import com.github.mythos.mythos.ability.skill.unique.OmniscientEyeSkill;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class UniqueSkills {
    private static final DeferredRegister<ManasSkill> registery = DeferredRegister.create(SkillAPI.getSkillRegistryKey(), "trmythos");
    public static final RegistryObject<FakerSkill> FAKER;
    public static final RegistryObject<OmniscientEyeSkill> OMNISCIENT_EYE;

    public UniqueSkills() {
    }

    public static void init(IEventBus modEventBus) {
        registery.register(modEventBus);
    }


    static {
        FAKER = registery.register("faker", FakerSkill::new);
        OMNISCIENT_EYE = registery.register("omniscient_eye", OmniscientEyeSkill::new);
    }
}