package com.github.mythos.mythos.registry;

import com.github.mythos.mythos.registry.skill.UniqueSkills;
import net.minecraftforge.eventbus.api.IEventBus;

public class MythosRegistery {
    public MythosRegistery () {
    }

    public static void register(IEventBus modEventBus) {
        UniqueSkills.init(modEventBus);
        MythosMobEffects.register(modEventBus);
        MythosEntity.register(modEventBus);
    }}