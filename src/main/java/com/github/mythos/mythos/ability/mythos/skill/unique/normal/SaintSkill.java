package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.manasmods.tensura.util.TensuraAdvancementsHelper;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import com.mojang.authlib.minecraft.TelemetrySession;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.checkerframework.checker.units.qual.Acceleration;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Predicate;

public class SaintSkill extends Skill {
    protected static final UUID ACCELERATION = UUID.fromString("e15c70d7-56a3-4ee9-add5-9d42bbd3edea");

    public SaintSkill() {
        super(SkillType.UNIQUE);
    }

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
        return true;
    }

    public double getObtainingEpCost() {
        return 12250.0;
    }

    public double learningCost() {
        return 12250.0; //same as obtaining cost i mean why not?
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
    }

    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);
    }

    public void onLearnSkill(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, @NotNull UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            SkillUtils.learnSkill(entity, (ManasSkill) ResistanceSkills.WATER_ATTACK_NULLIFICATION.get());
            SkillUtils.learnSkill(entity, (ManasSkill) ResistanceSkills.THERMAL_FLUCTUATION_NULLIFICATION.get());
            SkillUtils.learnSkill(entity, (ManasSkill) ExtraSkills.UNIVERSAL_PERCEPTION.get());
        }
    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity living, LivingHurtEvent e) {
        double multiplier;
        if (!DamageSourceHelper.isWaterDamage(e.getSource())) {
            multiplier = MythosSkillsConfig.waterMultiplier.get();

            e.setAmount((float) (e.getAmount() * multiplier));
        }
    }



    public int modes() {
        return 3;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 3 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 3) ? 1 : (instance.getMode() + 1);
    }

    public Component getModeName(int mode) {
        MutableComponent name;
        switch (mode) {
            case 1:
                name = Component.translatable("trmythos.skill.mode.saint.gift_giving");
                break;
            case 2:
                name = Component.translatable("trmythos.skill.mode.saint.gift_receiving");
                break;
            case 3:
                name = Component.translatable("trmythos.skill.mode.saint.blizzard_null_cast_time");
                break;
            default:
                name = Component.empty();
        }
        return name;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        switch (instance.getMode()) {
            case 1:

                break;

            case 2:

                break;

            case 3:

                break;

            default:
                break;

        }
    }
}