package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.mythos.mythos.registry.skill.Skills;
import io.github.Memoires.trmysticism.registry.skill.ExtraSkills;
import net.minecraft.world.entity.player.Player;

public class MirrorImageSkill extends Skill {
    public MirrorImageSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public double getObtainingEpCost() {
        return 75000;
    }

    public boolean meetEPRequirement(Player player, double newEP) {
        // Check EP using Tensura capability
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false; // not enough EP
        }
        return SkillUtils.isSkillMastered(player, (ManasSkill) Skills.MIRROR_IMAGE.get()) &&
                SkillUtils.isSkillMastered(player, (ManasSkill) ExtraSkills.LIGHT_MANIPULATION.get());
    }


}
