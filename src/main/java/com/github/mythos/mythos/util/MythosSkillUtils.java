package com.github.mythos.mythos.util;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.config.TensuraConfig;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import static com.github.manasmods.tensura.ability.SkillUtils.isSkillToggled;

public class MythosSkillUtils extends com.github.manasmods.tensura.ability.SkillUtils {
    public static int getEarningLearnPoint(ManasSkillInstance instance, LivingEntity entity, boolean isMode) {
        int point = TensuraConfig.INSTANCE.skillsConfig.bonusLearningGain.get() + entity.getRandom().nextInt(1, isMode ? 3 : 5);
        if (isSkillToggled(entity, Skills.ORUNMILA.get())) {
            point += 20;
        }
        return point;
    }

    public static int getBonusMasteryPoint(ManasSkillInstance instance, LivingEntity entity, int originalPoint) {
        int point = TensuraConfig.INSTANCE.skillsConfig.bonusMasteryGain.get();
        if (isSkillToggled(entity, Skills.ORUNMILA.get())) {
            point += 20 * originalPoint;
        }
        return point;
    }

    public static boolean canNegateDodge(LivingEntity entity, DamageSource source) {
        Entity var3 = source.getEntity();
        if (var3 instanceof LivingEntity living) {

            if (isSkillToggled(entity, (ManasSkill)Skills.ORUNMILA.get())) {
                return false;
            }  else {
                return living.getRandom().nextBoolean();
            }
        } else {
            return false;
        }
    }

    public static boolean reducingResistances(LivingEntity living) {
        return isSkillToggled(living, Skills.ORUNMILA.get());
    }


}
