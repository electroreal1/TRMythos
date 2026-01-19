package com.github.mythos.mythos.ability.mythos.skill.unique.vassal_line;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.resist.ResistSkill;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.Collection;
import java.util.List;

public class EvolutionSkill extends Skill {
    public EvolutionSkill(SkillType type) {
        super(type);
    }

    @Override
    public double getObtainingEpCost() {
        return 100000;
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        ManasSkillInstance resistance;
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player) {
            if (instance.isToggled()) {

                SkillStorage playerStorage = SkillAPI.getSkillsFrom(player);
                Collection<ManasSkillInstance> playerStorageLearnedSkills = playerStorage.getLearnedSkills();
                List<ManasSkillInstance> resistSkills = playerStorageLearnedSkills.stream().filter((skillInstance)
                        -> skillInstance.getSkill() instanceof ResistSkill).toList();

                for (ManasSkillInstance skill : resistSkills) {
                    resistance = skill;
                    if (Math.random() < 0.1) {
                        ManasSkill var12 = resistance.getSkill();
                        if (var12 instanceof ResistSkill resistSkill) {
                            if (resistance.canBeToggled(player) && resistSkill.isDamageResisted(event.getSource(), resistance)) {
                                if (resistance.getMastery() >= 0) {
                                    resistSkill.addMasteryPoint(resistance, player);
                                }

                                if (resistSkill.isMastered(resistance, player)) {
                                    resistSkill.evolveToNullification(resistance, player);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
