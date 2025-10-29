package com.github.mythos.mythos.registry.skill;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.mythos.mythos.ability.skill.unique.fused_skills.ParanoiaSkill;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class FusedSkills {

    private static final DeferredRegister<ManasSkill> registery = DeferredRegister.create(SkillAPI.getSkillRegistryKey(), "trmythos");

    // Uniques
    public static final RegistryObject<ParanoiaSkill> PARANOIA;

    public FusedSkills() {
    }

    public static void init(IEventBus modEventBus) {
        registery.register(modEventBus);
    }

    static {

        PARANOIA = registery.register("paranoia", () -> new ParanoiaSkill(Skill.SkillType.UNIQUE));


    }

}
