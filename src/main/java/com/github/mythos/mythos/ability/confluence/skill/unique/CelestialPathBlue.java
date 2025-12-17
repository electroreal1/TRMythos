package com.github.mythos.mythos.ability.confluence.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CelestialPathBlue extends Skill {
    public CelestialPathBlue(SkillType type) {
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
    public int modes() {
        return 1;
    }

    @Override
    public Component getModeName(int mode) {
        return Component.translatable("trmythos.skill.celestial_path_blue.fate");
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.LUCK, 60, 10));
    }

    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        if (!event.isCanceled()) {
                DamageSource damageSource = event.getSource();
                if (!damageSource.isBypassInvul() && !damageSource.isMagic()) {
                    Entity var5 = damageSource.getDirectEntity();
                    if (var5 instanceof LivingEntity) {
                        LivingEntity entity = (LivingEntity)var5;
                        double dodgeChance = 0.5;

                        if (!(entity.getRandom().nextDouble() >= dodgeChance)) {
                            entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 2.0F, 1.0F);
                            event.setCanceled(true);
                            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 3));
                        }
                    }
                }
        }
    }

    @SubscribeEvent
    public static void onMobDrops(LivingDropsEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity killer)) return;

        float multiplier = 1.5F;
        RandomSource random = killer.getRandom();

        for (ItemEntity drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            int extra = Mth.floor(stack.getCount() * (multiplier - 1));

            if (extra > 0 && random.nextFloat() < 0.6F) {
                stack.grow(extra);
            }
        }
    }

    @SubscribeEvent
    public static void onEffectApply(MobEffectEvent.Applicable event, ManasSkillInstance instance) {
        LivingEntity entity = event.getEntity();

        if (!instance.isToggled()) return;

        if (!event.getEffectInstance().getEffect().isBeneficial()) {
            event.setCanceled(true);
            instance.setToggled(false);
            entity.level.playSound(null, entity.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.2F);
        }
    }


    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.isToggled()) {
            instance.setToggled(false);
        }
        if (!instance.isToggled()) {
            instance.setToggled(true);
        }
    }


}