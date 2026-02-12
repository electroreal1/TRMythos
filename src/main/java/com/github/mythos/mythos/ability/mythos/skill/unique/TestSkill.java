package com.github.mythos.mythos.ability.mythos.skill.unique;

import com.github.manasmods.tensura.ability.skill.Skill;

public class TestSkill extends Skill {
    public TestSkill(SkillType type) {
        super(SkillType.ULTIMATE);
    }

    //Spooky
    @Override
    public double getObtainingEpCost() {
        return 2000000000;
    }

}
