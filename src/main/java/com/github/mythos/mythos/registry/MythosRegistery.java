package com.github.mythos.mythos.registry;

import com.github.mythos.mythos.ability.confluence.skill.ConfluenceUniques;
import com.github.mythos.mythos.handler.CatharsisHandler;
import com.github.mythos.mythos.networking.MythosNetwork;
import com.github.mythos.mythos.registry.dimensions.MythosDimensions;
import com.github.mythos.mythos.registry.menu.MythosMenuTypes;
import com.github.mythos.mythos.registry.skill.FusedSkills;
import com.github.mythos.mythos.registry.skill.Skills;
import com.github.mythos.mythos.voiceoftheworld.TrialManager;
import net.minecraftforge.eventbus.api.IEventBus;

public class MythosRegistery {
    public MythosRegistery () {
    }

    public static void register(IEventBus modEventBus) {
        Skills.init(modEventBus);
        ConfluenceUniques.init(modEventBus);
        FusedSkills.init(modEventBus);
        MythosMobEffects.register(modEventBus);
        MythosEntityTypes.register(modEventBus);
        MythosDimensions.register(modEventBus);
        MythosMenuTypes.register(modEventBus);
        MythosItems.register(modEventBus);
        CatharsisHandler.register();
        MythosNetwork.register();
        //MythosClient.clientSetup((FMLClientSetupEvent) modEventBus);
        //modEventBus.addListener(MythosClient::clientSetup);

        // trials and voice of the world
        TrialManager.init();

    }

}