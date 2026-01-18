package com.github.mythos.mythos.ability.mythos.skill.unique.megalomaniac_watcher;

import com.github.manasmods.tensura.ability.skill.Skill;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

public class WatcherSkill extends Skill {
    public WatcherSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("WIP");
    }
}
