package com.github.mythos.mythos.util;

//import com.github.lucifel.virtuoso.registry.skill.IntrinsicSkills;

import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import net.minecraft.server.level.ServerPlayer;

public class MythosUtils extends SkillUtils {

    public MythosUtils() {
    }

    public static boolean hasGravityDominationAndSpatialDomination(ServerPlayer serverPlayer) {
        return SkillUtils.isSkillMastered(serverPlayer, ExtraSkills.SPATIAL_DOMINATION.get()) && isSkillMastered(serverPlayer, ExtraSkills.GRAVITY_DOMINATION.get());
    }

}
