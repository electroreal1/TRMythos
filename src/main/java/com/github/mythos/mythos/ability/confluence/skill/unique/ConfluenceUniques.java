package com.github.mythos.mythos.ability.confluence.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.skill.Skill;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ConfluenceUniques {

    private static final DeferredRegister<ManasSkill> registery = DeferredRegister.create(SkillAPI.getSkillRegistryKey(), "trmythos");

    // Spores.
    public static final RegistryObject<Sporeblood> SPOREBLOOD;

    // Hero blades.
    public static final RegistryObject<Excalibur> EXCALIBUR;
    public static final RegistryObject<Gram> GRAM;
    public static final RegistryObject<Fragarach> FRAGARACH;


    public ConfluenceUniques() {
    }

    public static void init(IEventBus modEventBus) {
        registery.register(modEventBus);
    }


    static {
        SPOREBLOOD = registery.register("sporeblood", () -> new Sporeblood(Skill.SkillType.UNIQUE));
        EXCALIBUR = registery.register("excalibur", () -> new Excalibur(Skill.SkillType.UNIQUE));
        GRAM = registery.register("gram", () -> new Gram(Skill.SkillType.UNIQUE));
        FRAGARACH = registery.register("fragarach", () -> new Fragarach(Skill.SkillType.UNIQUE));
    }
}
