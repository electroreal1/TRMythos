package com.github.mythos.mythos.util;

//import com.github.lucifel.virtuoso.registry.skill.IntrinsicSkills;
import com.github.lucifel.virtuoso.ability.skill.ultimate.HormeSkill;
import com.github.lucifel.virtuoso.registry.skill.IntrinsicSkills;
import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.tensura.ability.SkillUtils;
import net.minecraft.server.level.ServerPlayer;

public class MythosUtils extends SkillUtils {

    public MythosUtils() {
    }

    public static boolean hasAvalon(ServerPlayer serverPlayer) {
        return SkillUtils.isSkillMastered(serverPlayer, IntrinsicSkills.AVALON.get());
    }

}
