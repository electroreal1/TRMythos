package com.github.mythos.mythos.ability.mythos.battlewills;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.battlewill.Battewill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.registry.battlewill.MeleeArts;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

public class FlamingLionPunch extends Battewill {

    public FlamingLionPunch() {
    }

    public boolean meetEPRequirement(Player entity, double newEP) {
        return SkillUtils.isSkillMastered(entity, MeleeArts.ROARING_LION_PUNCH.get()) && SkillUtils.isSkillMastered(entity, ExtraSkills.FLAME_MANIPULATION.get());
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("Focus your aura into a fearsome blow with the regalness of a lion and the temperature of a dragon's breath.");
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Flaming Lion Punch");
    }

    public double learningCost() {
        return 20000.0;
    }

    public double auraCost(LivingEntity entity, ManasSkillInstance instance) {
        return 10.0;
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.getMainHandItem().isEmpty()) {
            double auraCost = instance.isMastered(entity) ? 6000.0 : 4000.0;
            if (entity instanceof Player player) {
                auraCost = Math.min(TensuraPlayerCapability.getAura(player) * 0.3, auraCost);
            }

            if (!SkillHelper.outOfAura(entity, auraCost)) {
                double reach = 3.0 + entity.getAttributeValue(ForgeMod.ATTACK_RANGE.get());
                LivingEntity target = SkillHelper.getTargetingEntity(entity, reach, false);
                if (target != null) {
                    Level level = entity.getLevel();
                    float damage = (float)(auraCost / this.auraCost(entity, instance));
                    DamageSource source = DamageSourceHelper.addSkillAndCost(DamageSource.mobAttack(entity), 0.0, instance);
                    DamageSource source1 = DamageSourceHelper.addSkillAndCost(TensuraDamageSources.heatWave(entity), 0.0, instance);
                    target.hurt(source, damage);
                    target.hurt(source1, 500);
                    SkillHelper.knockBack(entity, target, 0.02F * damage);
                    instance.addMasteryPoint(entity);
                    entity.swing(InteractionHand.MAIN_HAND, true);
                    level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 1.0F, 1.0F);
                }
            }
        }
    }
}
