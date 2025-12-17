package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;
import java.util.UUID;

public class AlchemistSkill extends Skill {
    protected static final UUID MULTILAYER = UUID.fromString("2c03b682-5705-11ee-8c99-0242ac120002");

    public AlchemistSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public int modes() {
        return 4;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 4 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 4) ? 1 : (instance.getMode() + 1);
    }

    @Override
    public double magiculeCost(LivingEntity entity, ManasSkillInstance instance) {
        return switch (instance.getMode()) {
            case 1 -> 1020;
            case 2 -> 100;
            case 3 -> 2004;
            case 4 -> 20000.0;
            default -> 0.0;
        };
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        AttributeInstance attributeInstance = (AttributeInstance) Objects.requireNonNull(entity.getAttribute((Attribute) TensuraAttributeRegistry.BARRIER.get()));
        if (instance.getMode() == 2) {
            if (SkillHelper.outOfMagicule(entity, instance)) return;
            this.addMasteryPoint(instance, entity);
            instance.setCoolDown(10);
            double barrierPoints = 50;
            attributeInstance.addPermanentModifier(new AttributeModifier(MULTILAYER, "Multilayer Barrier", barrierPoints, AttributeModifier.Operation.ADDITION));
            entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }
}
