package com.github.mythos.mythos.ability.mythos.skill.ultimate.lord;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class KairosSkill extends Skill {
    public KairosSkill(SkillType type) {
        super(type);
    }

    public boolean meetEPRequirement(@NotNull Player player, double newEP) {
        if (!EnableUltimateSkillObtainment()) return false;
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, Skills.OPPORTUNIST_SKILL.get());
    }

    @Override
    public double getObtainingEpCost() {
        return 1500000;
    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent event) {
        if (!instance.isToggled()) {
            event.setAmount(event.getAmount() * 100.0F);
            this.addMasteryPoint(instance, entity);
        }
    }

    public boolean canBeSlotted(ManasSkillInstance instance) {
        return instance.getMastery() < 0;
    }

    public void onTakenDamage(ManasSkillInstance instance, LivingDamageEvent event) {
        if (event.isCanceled()) return;

        DamageSource source = event.getSource();
        if (source.isBypassInvul()) return;

        if (!instance.isToggled()) {
            float originalDamage = event.getAmount();
            event.setAmount(originalDamage * 100.0F);
        }
    }
}
