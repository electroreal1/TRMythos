package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.mythos.mythos.registry.mechanics.SkillEvolutionMechanics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "trmythos")
public class SkillEvolutionHandler {

    @SubscribeEvent
    public static void onSkillUnlocked(UnlockSkillEvent event) {
        SkillEvolutionMechanics.onCrimsonTyrantMastered(
                event.getSkillInstance(),
                (LivingEntity) event.getEntity(),
                event
        );
    }
}
