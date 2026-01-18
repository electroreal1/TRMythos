package com.github.mythos.mythos.ability.mythos.skill.unique.megalomaniac_watcher;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.jetbrains.annotations.Nullable;

public class MegalomaniacSkill extends Skill {
    public MegalomaniacSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("WIP");
    }

    @Override
    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        if (isInSlot(event.getEntity()) && !event.getSource().isBypassInvul()) {
            double chance = 0.75;
            if (event.getEntity().getRandom().nextDouble() < chance) {
                event.setCanceled(true);
                event.getEntity().level.playSound(null, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(),
                        SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.5F, 2.0F);
            }
        }
    }


}
