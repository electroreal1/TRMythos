package com.github.mythos.mythos.util;

//import com.github.lucifel.virtuoso.registry.skill.IntrinsicSkills;

import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.mythos.mythos.registry.skill.Skills;
import io.github.Memoires.trmysticism.registry.skill.UniqueSkills;
import net.minecraft.server.level.ServerPlayer;

public class MythosUtils extends SkillUtils {

    public MythosUtils() {
    }

    public static boolean hasGravityDominationAndSpatialDomination(ServerPlayer serverPlayer) {
        return SkillUtils.isSkillMastered(serverPlayer, ExtraSkills.SPATIAL_DOMINATION.get()) && isSkillMastered(serverPlayer, ExtraSkills.GRAVITY_DOMINATION.get());
    }

    public static boolean hasDreamer(ServerPlayer serverPlayer) {
        return SkillUtils.isSkillMastered(serverPlayer, UniqueSkills.DREAMER.get());
    }

    public static boolean hasProfanity(ServerPlayer serverPlayer) {
        return  SkillUtils.isSkillMastered(serverPlayer, Skills.PROFANITY.get());
    }
}
