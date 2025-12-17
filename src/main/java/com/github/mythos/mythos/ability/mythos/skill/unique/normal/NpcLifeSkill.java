package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class NpcLifeSkill extends Skill {
    public static final UUID SIZE_UUID =
            UUID.fromString("7b3c0a6f-1f2d-4b51-b0aa-3a8f9a0c42de");

    public static final double SHRINK_AMOUNT = -0.1666667;
    public NpcLifeSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public int getMaxMastery() {
        return 1000;
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity living) {
        instance.addMasteryPoint(living);
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;

        AttributeInstance sizeAttr =
                player.getAttribute(TensuraAttributeRegistry.SIZE.get());
        if (sizeAttr == null) return;

        sizeAttr.removeModifier(SIZE_UUID);

        if (instance.isToggled()) {
            AttributeModifier modifier = new AttributeModifier(SIZE_UUID, "Blending In size reduction", SHRINK_AMOUNT,
                    AttributeModifier.Operation.ADDITION);
            sizeAttr.addPermanentModifier(modifier);
        }
    }

    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;

        AttributeInstance sizeAttr = player.getAttribute(TensuraAttributeRegistry.SIZE.get());
        if (sizeAttr == null) return;

        sizeAttr.removeModifier(SIZE_UUID);
    }

    @Override
    public void onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (instance.isToggled()) {
            ItemStack held = player.getMainHandItem();
            if (held.isEmpty()) return;

            ResourceLocation id = ForgeRegistries.ITEMS.getKey(held.getItem());

            if (id == null) return;

            if (!id.getPath().contains("sword")) return;

            event.setAmount(event.getAmount() * 2);
        }
    }

    @Override
    public void onDeath(ManasSkillInstance instance, LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (instance.onCoolDown()) return;

        event.setCanceled(true);

        float newHp = (float) (player.getMaxHealth() * (instance.isMastered(player) ? 0.5 : 0.3));
        float newSHP = (float) (TensuraEPCapability.getSpiritualHealth(player) * (instance.isMastered(player) ? 0.6 : 0.4));

        player.setHealth(newHp);
        TensuraEPCapability.setSpiritualHealth(player, newSHP);

        instance.setCoolDown(600);
    }
}