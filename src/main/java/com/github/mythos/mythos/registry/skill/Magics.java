package com.github.mythos.mythos.registry.skill;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.mythos.mythos.ability.mythos.magic.space.SchrodingersLabyrinthSpell;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class Magics {
    public static final DeferredRegister<ManasSkill> registry = DeferredRegister.create(SkillAPI.getSkillRegistryKey(), "trmythos");
    public static void init(IEventBus modEventBus) {
        registry.register(modEventBus);
    }

    public Magics() {
    }

    public static final RegistryObject<SchrodingersLabyrinthSpell> SCHRODINGERS =
            registry.register("schrodingers", SchrodingersLabyrinthSpell::new);
}
