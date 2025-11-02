package com.github.mythos.mythos.ability.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class OpportunistSkill extends Skill {
    public OpportunistSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public int getMaxMastery() {
        return 1500;
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        if (instance.isToggled()) {
            this.gainMastery(instance, entity);
        }
        return;
    }

    private void gainMastery(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        int time = tag.getInt("activatedTimes");
        if (time % 12 == 0) {
            this.addMasteryPoint(instance, entity);
        }

        tag.putInt("activatedTimes", time + 1);
    }

    @Override
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/opportunist.png");
    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent event) {
        if (!instance.isToggled()) {
                event.setAmount(event.getAmount() * 10.0F);
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
            event.setAmount(originalDamage * 10.0F);
        }
    }
}