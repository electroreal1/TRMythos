package com.github.mythos.mythos.ability.mythos.magic.space;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.magic.MagicElemental;
import com.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class SchrodingersLabyrinthSpell extends SpiritualMagic {

    public SchrodingersLabyrinthSpell() {
        super(null, SpiritLevel.LORD);
    }

    @Override
    public MagicElemental getElemental() {
        return MagicElemental.SPACE;
    }

    public int defaultCast() {
        return 0;
    }

    @Override
    public int masteryCast() {
        return 0;
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Schrodinger's Labyrinth");
    }

    public double magiculeCost(LivingEntity entity, ManasSkillInstance instance) {
        return 15000.0;
    }

    public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (this.getHeldTicks(instance) >= this.castingTime(instance, entity)) {
            if (!SkillHelper.outOfMagicule(entity, instance)) {
                int radius = instance.isMastered(entity) ? 30 : 20;
                LivingEntity target = SkillHelper.getTargetingEntity(entity, radius, false, true);
                if (target != null) {
                    entity.swing(InteractionHand.MAIN_HAND, true);
                    entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_PREPARE_MIRROR, SoundSource.PLAYERS, 1.0F, 1.0F);

                    target.addEffect(new MobEffectInstance(MythosMobEffects.SCHRODINGERS_LABYRINTH.get(), 200, 1, false, false, false));
                }
            }
        }
    }
}
