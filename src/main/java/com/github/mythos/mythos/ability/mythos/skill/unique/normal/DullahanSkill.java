package com.github.mythos.mythos.ability.mythos.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import net.minecraft.sounds.SoundEvents;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



public class DullahanSkill extends Skill {

    public DullahanSkill() {
        super(SkillType.UNIQUE);
    }

    @Nullable
    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
        return true;
    }

    public double getObtainingEpCost() {
        return 66000.0;
    }

    public double learningCost() {
        return 10000.0;
    }

    @Override
    public int getMaxMastery() {
        return 5000;
    }

    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    protected boolean canActivateInRaceLimit(ManasSkillInstance instance) {
        return instance.getMode() == 1;
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {

    }


    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        // Currently empty, could be used for cleanup later
    }

    @Override
    public void onLearnSkill(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, @NotNull UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            SkillUtils.learnSkill(entity, ExtraSkills.UNIVERSAL_PERCEPTION.get());
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
                name = Component.translatable("trmythos.skill.mode.dullahan.iris_out");
                break;
            case 2:
                name = Component.translatable("trmythos.skill.mode.dullahan.spine_whip");
                break;
            case 3:
                name = Component.translatable("trmythos.skill.mode.dullahan.soundless_coach");
                break;
            default:
                name = Component.empty();
        }
        return name;
    }
//    @SubscribeEvent
//    public static void onLivingHurt(LivingHurtEvent event) {
//        if (!(event.getSource().getEntity() instanceof ServerPlayer attacker)) return;
//
//        TensuraSkillInstance dullahan = SkillUtils.getSkillInstance(attacker, Skills.DULLAHAN.get());
//        if (dullahan == null || !dullahan.isToggled()) return;
//
//        LivingEntity target = event.getEntity();
//
//        double maxHealth = target.getMaxHealth();
//        double currentHealth = target.getHealth() - event.getAmount();
//        double threshold = maxHealth * 0.10;
//
//        if (currentHealth <= threshold && currentHealth > 0) {
//            target.hurt(event.getSource(), Float.MAX_VALUE);
//
//            if (target instanceof ServerPlayer victim) {
//                ItemStack head = new ItemStack(Items.PLAYER_HEAD);
//                head.getOrCreateTag().putString("SkullOwner", victim.getGameProfile().getName());
//                victim.spawnAtLocation(head, 1.0F);
//            }
//
//            attacker.sendSystemMessage(Component.literal("☠ Executed your foe under 10% HP!"));
//        }
//    }
}
