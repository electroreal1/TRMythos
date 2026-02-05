package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.util.MythosUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Gaze extends Skill {
    public Gaze(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public double getObtainingEpCost() {
        return 90000;
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (entity instanceof Player player) {
            player.displayClientMessage(Component.literal("§b« Notification »\n")
                    .append(Component.literal("§b« Conditions met. Individual has perceived the hidden dimensions of the world. »\n"))
                    .append(Component.literal("§dSkill [Gaze] acquired.\n"))
                    .append(Component.literal("§7The world is now visible. Do not look away.")), false);
        }
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (SkillHelper.outOfMagicule(entity, instance)) {
            if (instance.isToggled()) instance.setToggled(false);
            return;
        }

        if (instance.isToggled()) {
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.PRESENCE_SENSE.get(), 20, 1, false, false, false));
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.HEAT_SENSE.get(), 20, 0, false, false, false));
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.AUDITORY_SENSE.get(), 20, 0, false, false, false));

        }

        if (this.isInSlot(entity)) {
            LivingEntity target = MythosUtils.getLookedAtEntity(entity, 30);

            if (target != null) {
                if (!target.isAlliedTo(entity)) {
                    SkillHelper.checkThenAddEffectSource(target, entity, MythosMobEffects.SPATIAL_DYSPHORIA.get(), 100, 1);
                    SkillHelper.checkThenAddEffectSource(target, entity, MobEffects.MOVEMENT_SLOWDOWN, 100, 2);
                }
            }
        }
    }

    @Override
    public List<MobEffect> getImmuneEffects(ManasSkillInstance instance, LivingEntity entity) {
        List<MobEffect> list = new ArrayList<>();
        if (this.isInSlot(entity)) {
            list.add(TensuraMobEffects.FEAR.get());
            list.add(TensuraMobEffects.MIND_CONTROL.get());
            list.add(TensuraMobEffects.INSANITY.get());
            list.add(MobEffects.CONFUSION);
        }
        return list;
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Gaze");
    }
}