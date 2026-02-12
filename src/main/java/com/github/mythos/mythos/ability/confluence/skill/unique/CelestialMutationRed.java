package com.github.mythos.mythos.ability.confluence.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.battlewill.Battewill;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.resist.ResistSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSource;
import com.github.mythos.mythos.ability.confluence.skill.ConfluenceUniques;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.Collection;
import java.util.List;

public class CelestialMutationRed extends Skill {
    public CelestialMutationRed(SkillType type) {
        super(type);
    }

    @Override
    public double getObtainingEpCost() {
        return 500000;
    }

    @Override
    public double learningCost() {
        return 10000;
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
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

    @Override
    public void onTakenDamage(ManasSkillInstance instance, LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (!instance.isToggled()) return;

        float reduction = 0.5F;

        event.setAmount(event.getAmount() * (1.0F - reduction));

        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (!player.level.isClientSide) {

                attacker.hurt(DamageSource.thorns(player), event.getAmount() / 10);
            }
        }
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity living) {
        if (living instanceof Player player) {
            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            Skill blue = ConfluenceUniques.CELESTIAL_PATH_BLUE.get();
            if (!SkillUtils.fullyHasSkill(player, blue)) {
                double chance = 0.01;
                double currentEP = TensuraEPCapability.getCurrentEP(player);

                if (!(player.getRandom().nextDouble() == chance)) {
                    if (blue.getObtainingEpCost() > currentEP) {
                        player.sendSystemMessage(Component.literal("Not Enough EP To Acquire Celestial Path - Blue Mask").withStyle(ChatFormatting.RED));
                    } else if (blue.getObtainingEpCost() < currentEP) {
                        storage.learnSkill(blue);
                        player.sendSystemMessage(Component.literal("You have Acquired Celestial Path - Blue Mask").withStyle(ChatFormatting.BLUE));
                    }
                }
            }
        }
    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity attacker, LivingHurtEvent e) {
        if (this.isInSlot(attacker)) {
            DamageSource source = e.getSource();
            if (source.getDirectEntity() == attacker) {
                if (DamageSourceHelper.isPhysicalAttack(source)) {
                    float damage = instance.isMastered(attacker) ? 150.0F : 75.0F;
                    e.setAmount((e.getAmount() + damage) * 2);
                    this.addMasteryPoint(instance, attacker);
                }
                TensuraDamageSource damageSource = (TensuraDamageSource) source;
                if (damageSource.getSkill() != null && damageSource.getSkill().getSkill() instanceof Battewill) {
                    e.setAmount(e.getAmount() * 2.0F);
                }
            }
        }
    }


}