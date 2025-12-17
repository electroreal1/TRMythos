package com.github.mythos.mythos.mixin;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.magic.MagicUltils;
import com.github.mythos.mythos.ability.confluence.skill.unique.ConfluenceUniques;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({MagicUltils.class})
public class MagicUtilsMixin {

    public MagicUtilsMixin() {
    }

    @ModifyReturnValue(
            method = {"hasChantAnnulment"},
            at = {@At("RETURN")},
            remap = false
    )
    private static boolean trmythos$hasChantAnnulment(boolean original, LivingEntity entity) {
        return original || entity.hasEffect(MythosMobEffects.THUNDER_GOD.get());
    }

    @ModifyReturnValue(
            method = {"hasUniqueThoughtAcceleration"},
            at = {@At("RETURN")},
            remap = false
    )
    private static boolean trmythos$hasUniqueThoughtAcceleration(boolean original, LivingEntity entity) {
        return original || entity.hasEffect(MythosMobEffects.THUNDER_GOD.get()) || SkillUtils.hasSkill(entity, (ManasSkill)Skills.DEMONOLOGIST.get());
    }

    @ModifyReturnValue(
            method = {"castingSpeedMultipiler"},
            at = {@At("RETURN")},
            remap = false
    )
    private static float trmythos$castingSpeedMultiplier(float original, LivingEntity entity) {
        if (entity.hasEffect(MythosMobEffects.THUNDER_GOD.get())) {
            original += 5000;
        }
        if (entity instanceof Player player) {
            if (SkillUtils.isSkillToggled(player, (ManasSkill) Skills.DOMINATE.get())) {
                original *= 2;
            }
            if (SkillUtils.isSkillToggled(player, (ManasSkill) ConfluenceUniques.CELESTIAL_CULTIVATION_ORANGE.get())) {
                original *= 3;
            }
        }


        return 1.0F / original;
    }
}
