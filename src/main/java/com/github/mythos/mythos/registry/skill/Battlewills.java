package com.github.mythos.mythos.registry.skill;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.mythos.mythos.ability.mythos.battlewills.FlamingLionPunch;
import com.github.mythos.mythos.ability.mythos.battlewills.HeavenlyLionPunch;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class Battlewills {
    public static final DeferredRegister<ManasSkill> registry = DeferredRegister.create(SkillAPI.getSkillRegistryKey(), "trmythos");
    public static void init(IEventBus modEventBus) {
        registry.register(modEventBus);
    }

    public Battlewills() {
    }

    public static final RegistryObject<HeavenlyLionPunch> HEAVENLY_LION_PUNCH = registry.register("heavenly_lion_punch", HeavenlyLionPunch::new);
    public static final RegistryObject<FlamingLionPunch> FLAMING_LION_PUNCH = registry.register("flaming_lion_punch", FlamingLionPunch::new);
}
