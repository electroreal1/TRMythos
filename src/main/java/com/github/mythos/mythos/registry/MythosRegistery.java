package com.github.mythos.mythos.registry;

import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraftforge.eventbus.api.IEventBus;

public class MythosRegistery {
    public MythosRegistery () {
    }

    public static void register(IEventBus modEventBus) {
        Skills.init(modEventBus);
        MythosMobEffects.register(modEventBus);
        MythosEntity.register(modEventBus);
      //  MythosClient.clientSetup((FMLClientSetupEvent) modEventBus);
        MythosParticles.PARTICLE_TYPES.register(modEventBus);
    }}