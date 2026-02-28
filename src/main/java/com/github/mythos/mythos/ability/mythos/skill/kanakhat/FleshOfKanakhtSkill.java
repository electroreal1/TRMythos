package com.github.mythos.mythos.ability.mythos.skill.kanakhat;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.mythos.mythos.handler.KanakhtHandler;
import com.github.mythos.mythos.util.MythosUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class FleshOfKanakhtSkill extends Skill {
    public FleshOfKanakhtSkill() {
        super(SkillType.UNIQUE);
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level.isClientSide || !(entity instanceof Player player)) return;
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("WIP");
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;

        LivingEntity target = MythosUtils.getLookedAtEntity(player, 10);
        if (target != null) {
            double playerEP = TensuraEPCapability.getEP(player);
            double targetEP = TensuraEPCapability.getEP(target);

            if (target.getHealth() < player.getHealth() || targetEP < playerEP) {
                KanakhtHandler.makeFleshPuppet(player, target);
                player.displayClientMessage(Component.literal("§4[Kanakht] §7Parasite successfully burrowed into §f" + target.getName().getString()), true);
            } else {
                player.displayClientMessage(Component.literal("§c[Kanakht] Host too strong to subvert."), true);
            }
        }
    }
}
