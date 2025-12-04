package com.github.mythos.mythos.ability.mythos.skill.unique.evolved;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.items.TensuraMobDropItems;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TrueDao extends Skill {
    protected static final UUID ACCELERATION1 = UUID.fromString("60a06181-8cf6-45e1-b1df-2e69f3544ace");
    protected static final UUID ACCELERATION2 = UUID.fromString("ba91c840-3ea9-497a-93b7-75216523344e");

    public TrueDao(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public double getObtainingEpCost() {
        return 1000000;
    }

    public boolean meetEPRequirement(Player player, double newEP) {
        // Check EP using Tensura capability
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false; // not enough EP
        }
        return SkillUtils.isSkillMastered(player, (ManasSkill) Skills.AWAKENED_DAO.get());
    }

    @Override
    public int getMaxMastery() {
        return 5000;
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }
    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION1, true);
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION2, true);
    }
    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION1, false);
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION2, false);
    }

    @Override
    public void onTick(ManasSkillInstance instance, @NotNull LivingEntity entity) {
        if (!instance.isToggled()) return;

        if (!(entity instanceof Player player)) return;

        TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
            double maxMP = player.getAttributeValue(TensuraAttributeRegistry.MAX_MAGICULE.get());
            double maxAP = player.getAttributeValue(TensuraAttributeRegistry.MAX_AURA.get());
            double regenMPPerTick = (maxMP / 20);
            double regenAPPerTick = (maxAP / 20);

            cap.setAura(Math.min(cap.getAura() + regenAPPerTick, maxAP));
            cap.setMagicule(Math.min(cap.getMagicule() + regenMPPerTick, maxMP));
        });

        TensuraPlayerCapability.sync(player);
    }
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event, ManasSkillInstance instance) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        float originalDamage = event.getAmount();
        event.setAmount(originalDamage * 1.4f);
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        Player player = (Player) entity;
        ItemStack held = player.getMainHandItem();
        Item item = held.getItem();

        int epGained = 0;

        if (item == TensuraMobDropItems.LOW_QUALITY_MAGIC_CRYSTAL.get()) {
            epGained = 40;
            this.addMasteryPoint(instance, entity);
            held.shrink(1);
        } else if (item == TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL.get()) {
            epGained = 120;
            this.addMasteryPoint(instance, entity);
            held.shrink(1);
        } else if (item == TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()) {
            epGained = 240;
            this.addMasteryPoint(instance, entity);
            held.shrink(1);
        } else {
            return;
        }

        instance.setCoolDown(5);
        SkillHelper.gainMaxAP(entity, epGained / 2);
        SkillHelper.gainMaxMP(entity, epGained / 2);
    }
}
