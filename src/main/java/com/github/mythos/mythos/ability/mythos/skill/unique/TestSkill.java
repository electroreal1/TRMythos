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

  //  @Override
 //   public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
    //    if (entity instanceof Player player) {
    //        player.sendSystemMessage(MythosUtils.isCustomAlignment(player) ? Component.literal("y") : Component.literal("5"));

     //       if (player.isShiftKeyDown()) {
     //           MythosUtils.setCustomAlignment(player, true);
     //       }
     //   }


  //  }
}
