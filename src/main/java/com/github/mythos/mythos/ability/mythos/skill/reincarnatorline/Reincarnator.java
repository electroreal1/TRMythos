package com.github.mythos.mythos.ability.mythos.skill.reincarnatorline;

import com.github.manasmods.tensura.ability.skill.Skill;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

public class Reincarnator extends Skill {
    public Reincarnator(SkillType type) {
        super(type);
    }

    @Override
    public double getObtainingEpCost() {
        return 100000;
    }

    @Nullable
    @Override
    public MutableComponent getColoredName() {
        return Component.literal("You've met with death time and time again, living numerous lives, yet your memories and sense of self remain immutable. Truly a unique existence.");
    }
}
