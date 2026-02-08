package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;
import java.util.UUID;

public class AlchemistSkill extends Skill {
    protected static final UUID MULTILAYER = UUID.fromString("c74ccef7-40de-4e72-aff6-0f99a07adc37");
    protected static final UUID SHP = UUID.fromString("9fd27908-f361-49db-9636-e2301139258f");
    protected static final UUID HP = UUID.fromString("710c8826-48e0-4df5-88c6-678fa0272c27");
    public AlchemistSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public int modes() {
        return 3;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 3 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 3) ? 1 : (instance.getMode() + 1);
    }

    @Override
    public double magiculeCost(LivingEntity entity, ManasSkillInstance instance) {
        return switch (instance.getMode()) {
            case 1 -> 1020;
            case 2 -> 100;
            case 3 -> 2004;
            default -> 0.0;
        };
    }

    @Override
    public Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.literal("Health Alchemy");
            case 2 -> Component.literal("Barrier Alchemy");
            case 3 -> Component.literal("Soul Condensation");
            default -> Component.empty();
        };
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        AttributeInstance attributeInstance2 = Objects.requireNonNull(entity.getAttribute(Attributes.MAX_HEALTH));
        AttributeInstance attributeInstance1 = Objects.requireNonNull(entity.getAttribute(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get()));
        AttributeInstance attributeInstance = Objects.requireNonNull(entity.getAttribute(TensuraAttributeRegistry.BARRIER.get()));
        if (instance.getMode() == 2) {
            if (SkillHelper.outOfMagicule(entity, instance)) return;
            this.addMasteryPoint(instance, entity);
            instance.setCoolDown(10);
            double barrierPoints = 50;
            attributeInstance.addPermanentModifier(new AttributeModifier(MULTILAYER, "Multilayer Barrier", barrierPoints, AttributeModifier.Operation.ADDITION));
            entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        if (instance.getMode() == 1) {
            if (entity.isShiftKeyDown()) {
                if (SkillHelper.outOfMagicule(entity, instance)) return;
                this.addMasteryPoint(instance, entity);
                instance.setCoolDown(10);
                double exchangeRate = -2;
                double exchangeRateAdd = 1;
                attributeInstance1.addPermanentModifier(new AttributeModifier(SHP, "Spiritual Health Max", exchangeRate, AttributeModifier.Operation.ADDITION));
                attributeInstance2.addPermanentModifier(new AttributeModifier(HP, "Health Max", exchangeRateAdd, AttributeModifier.Operation.ADDITION));
            } else {
                if (SkillHelper.outOfMagicule(entity, instance)) return;
                this.addMasteryPoint(instance, entity);
                instance.setCoolDown(10);
                double exchangeRate = -2;
                double exchangeRateAdd = 1;
                attributeInstance1.addPermanentModifier(new AttributeModifier(SHP, "Spiritual Health Max", exchangeRateAdd, AttributeModifier.Operation.ADDITION));
                attributeInstance2.addPermanentModifier(new AttributeModifier(HP, "Health Max", exchangeRate, AttributeModifier.Operation.ADDITION));
            }
        }

        if (instance.getMode() == 3 && entity instanceof Player player) {
            TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                int soulPoints = cap.getSoulPoints();
                if (soulPoints >= 1500000) {
                    cap.setSoulPoints(soulPoints - 1500000);
                    SkillStorage storage = SkillAPI.getSkillsFrom(player);
                    double currentEP = TensuraEPCapability.getCurrentEP(player);
                    Skill cracked = Skills.CRACKED_PHILOSOPHER_STONE.get();
                    if (storage.getSkill(cracked).isPresent()) return;
                    if (cracked.getObtainingEpCost() > currentEP) {
                        player.sendSystemMessage(Component.literal("Not Enough EP to Obtain Cracked Philospher's Stone.").withStyle(ChatFormatting.RED));
                    } else if (cracked.getObtainingEpCost() < currentEP) {
                        storage.learnSkill(cracked);
                        instance.setCoolDown(60);
                        this.addMasteryPoint(instance, entity);
                        player.sendSystemMessage(Component.literal("You have Acquired Cracked Philospher's Stone.").withStyle(ChatFormatting.BLUE));
                        }
                    }
                });

            }
        }

}
