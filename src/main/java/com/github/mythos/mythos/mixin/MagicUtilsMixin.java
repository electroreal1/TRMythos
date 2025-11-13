package com.github.mythos.mythos.mixin;

import com.github.manasmods.tensura.ability.magic.MagicUltils;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.LivingEntity;
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
        return original || entity.hasEffect(MythosMobEffects.THUNDER_GOD.get());
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
        return 1.0F / original;
    }
}
