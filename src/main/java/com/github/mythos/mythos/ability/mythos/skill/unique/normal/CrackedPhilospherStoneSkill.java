package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.tensura.ability.skill.Skill;

public class CrackedPhilospherStoneSkill extends Skill {
    public CrackedPhilospherStoneSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public double getObtainingEpCost() {
        return 10000;
    }

    @Override
    public int getMaxMastery() {
        return 1000;
    }
}
