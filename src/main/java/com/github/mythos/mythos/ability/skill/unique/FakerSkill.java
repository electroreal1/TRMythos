package com.github.mythos.mythos.ability.skill.unique;

import com.github.manasmods.tensura.ability.skill.Skill;

public class FakerSkill extends Skill {
    public FakerSkill() {
        super(SkillType.UNIQUE);
    }

    public double getObtainingEpCost() {
        return 10.0;
    }
    public final double learnCost = 4;
    private final int numModes = 1;
    public int modes() {
        return numModes;
    }
    public double learningCost() {
        return learnCost;
    }
}
