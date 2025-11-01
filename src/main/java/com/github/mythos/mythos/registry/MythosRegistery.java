package com.github.mythos.mythos.registry;

import com.github.mythos.mythos.registry.menu.MythosMenuTypes;
import com.github.mythos.mythos.registry.skill.FusedSkills;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraftforge.eventbus.api.IEventBus;

public class MythosRegistery {
    public MythosRegistery () {
    }

    public static void register(IEventBus modEventBus) {
        Skills.init(modEventBus);

        FusedSkills.init(modEventBus);
        MythosMobEffects.register(modEventBus);
        MythosEntityTypes.register(modEventBus);
        MythosMenuTypes.register(modEventBus);
        MythosWeapons.init(modEventBus);
        //MythosClient.clientSetup((FMLClientSetupEvent) modEventBus);
        modEventBus.addListener(MythosClient::clientSetup);


    }

}