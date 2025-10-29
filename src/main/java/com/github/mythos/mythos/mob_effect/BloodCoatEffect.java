package com.github.mythos.mythos.mob_effect;

import com.github.manasmods.tensura.effect.template.SkillMobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class BloodCoatEffect extends SkillMobEffect {
    public BloodCoatEffect(MobEffectCategory pCategory, int pColor) {
        super(MobEffectCategory.BENEFICIAL, 990000 );
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }


}
