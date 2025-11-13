package com.github.mythos.mythos.ability.mythos.skill.ultimate;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

public class VayuSkill extends Skill {
    public VayuSkill(SkillType type) {
        super(SkillType.ULTIMATE);
    }

    public double getObtainingEpCost() {
        return 3000000.0;
    }

    public boolean meetEPRequirement(@NotNull Player player, double newEP) {
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, (ManasSkill) Skills.ZEPHYROS.get());
    }

    public void onEntityHit(ManasSkillInstance instance, @NotNull LivingEntity living, @NotNull LivingHurtEvent e, Player player) {
        if (instance.isToggled()) {
            if (DamageSourceHelper.isWindDamage(e.getSource())) {
                if (instance.isMastered(living)) {
                    e.setAmount(e.getAmount() * 15.0F);
                } else {
                    e.setAmount(e.getAmount() * 10.0F);
                }
            }
        }
    }

    public void onEntityHurt(LivingHurtEvent event, ManasSkillInstance instance) {
        if (instance.isToggled()) {
            LivingEntity target = event.getEntity();
            DamageSource source = event.getSource();
            float amount = event.getAmount();

            if (DamageSourceHelper.isWindDamage(source)) {
                event.setCanceled(true);

                applyHealth(target, amount);
            }
        }
    }

    private static void applyHealth(LivingEntity entity, float amount) {
        float currentAbsorption = entity.getAbsorptionAmount();
        entity.setAbsorptionAmount(currentAbsorption + amount);
    }



}
