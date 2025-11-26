package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.entity.magic.barrier.BlizzardEntity;
import com.github.manasmods.tensura.entity.magic.projectile.IceLanceProjectile;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SaintSkill extends Skill {
    protected static final UUID ACCELERATION = UUID.fromString("e15c70d7-56a3-4ee9-add5-9d42bbd3edea");
    //protected static final UUID CASTING = UUID.fromString(d1d356ef-eceb-41db-b85b-3174f8f149eb);

    public SaintSkill() {
        super(SkillType.UNIQUE);
    }

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
        return true;
    }

    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public double getObtainingEpCost() {
        return 12250.0;
    }

    public double learningCost() {
        return 12250.0; //same as obtaining cost i mean why not?
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            entity.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 240, 2, false, false, true));
        }
    }

    public void onLearnSkill(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, @NotNull UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            SkillUtils.learnSkill(entity, (ManasSkill) ResistanceSkills.WATER_ATTACK_NULLIFICATION.get());
            SkillUtils.learnSkill(entity, (ManasSkill) ResistanceSkills.THERMAL_FLUCTUATION_NULLIFICATION.get());
            SkillUtils.learnSkill(entity, (ManasSkill) ExtraSkills.UNIVERSAL_PERCEPTION.get());
        }
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
    }

    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);
    }

    @Nullable
    @Override
    public void onDamageEntity(ManasSkillInstance instance, LivingEntity attacker, LivingHurtEvent event) {
        if (DamageSourceHelper.isWaterDamage(event.getSource())) {

            float multiplier = 2.0f;

            event.setAmount(event.getAmount() * multiplier);
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
        if (instance.getMode() == 1){

        }

        if (instance.getMode() == 2) {

        }

        if (instance.getMode() == 3) {

            if (entity.isShiftKeyDown()) {
                var blizzards = entity.getLevel().getEntitiesOfClass(
                        BlizzardEntity.class,
                        entity.getBoundingBox().inflate(50),
                        bliz -> bliz.getOwner() == entity
                );

                if (!blizzards.isEmpty()) {
                    for (BlizzardEntity b : blizzards) {
                        b.discard();
                    }
                    return;
                }
            }

            if (hasBlizzard(entity)) {

                if (SkillHelper.outOfMagicule(entity, 2000.0D)) {
                    return;
                }

                entity.swing(InteractionHand.MAIN_HAND, true);
                instance.setCoolDown(10);
                addMasteryPoint(instance, entity);

                IceLanceProjectile iceLance = new IceLanceProjectile(entity.getLevel(), entity);

                iceLance.setSize(3.0F);
                iceLance.setSpeed(2.0F);
                iceLance.setDamage(250.0F);
                iceLance.setMpCost(2000.0D);
                iceLance.setSkill(instance);
                iceLance.setSpiritAttack(true);

                iceLance.setPosAndShoot(entity);
                entity.getLevel().addFreshEntity(iceLance);

                entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);

                return;
            }

            if (SkillHelper.outOfMagicule(entity, 10000.0D)) {
                return;
            }

            BlizzardEntity blizzard = new BlizzardEntity(entity.getLevel(), entity);

            blizzard.setLife(instance.isMastered(entity) ? 3200 : 2000);
            instance.setCoolDown(2);
            blizzard.setRadius(15.0F);
            blizzard.setDamage(30.0F);
            blizzard.setMpCost(10000.0D);
            blizzard.setSkill(instance);

            blizzard.setPos(
                    entity.getX(),
                    entity.getY() + (entity.getEyeHeight() / 2.0F),
                    entity.getZ()
            );

            entity.getLevel().addFreshEntity(blizzard);
            entity.playSound(SoundEvents.GENERIC_EXPLODE, 0.5f, 1.0f); // Sonido de invocación
            addMasteryPoint(instance, entity);
        }
    }

    private boolean hasBlizzard(LivingEntity owner) {
        return !owner.getLevel().getEntitiesOfClass(
                BlizzardEntity.class,
                owner.getBoundingBox().inflate(50),
                blizzard -> (blizzard.getOwner() == owner)
        ).isEmpty();
    }
}