package com.github.mythos.mythos.ability.confluence.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Gram extends Skill {
    public Gram(SkillType type) {
        super(type);
    }

    @Override
    public double getObtainingEpCost() {
        return 250000;
    }


    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event, ManasSkillInstance instance, LivingEntity living) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (TensuraSkillCapability.isSkillInSlot(living, (ManasSkill) ConfluenceUniques.GRAM.get())) {
            LivingEntity target = event.getEntity();
            double playerEP = TensuraPlayerCapability.getCurrentEP(player);

            double targetEP;
            if (target instanceof Player targetPlayer) {
                targetEP = TensuraPlayerCapability.getCurrentEP(targetPlayer);
            } else {
                targetEP = target.getPersistentData().getDouble("EP");
            }

            if (targetEP <= playerEP) return;

            double epDifferencePercent = ((targetEP - playerEP) / playerEP) * 100.0;
            double damageMultiplier = 1.0 + (epDifferencePercent / 100.0);

            String targetRace = String.valueOf(TensuraPlayerCapability.getRace(target));
            if (targetRace != null && MythosSkillsConfig.GRAM_EXTRA_DAMAGE_RACES.get().contains(targetRace)) {
                damageMultiplier *= 1.5;
            }


            float newDamage = (float) (event.getAmount() * damageMultiplier);
            event.setAmount(newDamage);
            instance.addMasteryPoint(player);
        }
    }

    @Override
    public int getMaxMastery() {
        return 3000;
    }


}
