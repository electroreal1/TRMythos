package com.github.mythos.mythos.ability.skill.unique.vassal_line;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.event.PossessionEvent;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

public class UnitySkill extends Skill {

    public UnitySkill(SkillType type) {
        super(SkillType.UNIQUE);
    }


    public boolean canBeCopied(ManasSkillInstance instance, LivingEntity owner) {
        return false;
    }


    public int copyChance(LivingEntity owner, ManasSkillInstance instance, ManasSkill targetSkill) {
        return 0;
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity owner) {
        instance.getOrCreateTag().putBoolean("unity_passive_active", true);
    }

    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity owner) {
        instance.getOrCreateTag().putBoolean("unity_passive_active", false);
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity owner) {
        if (!instance.getOrCreateTag().getBoolean("unity_passive_active")) return;

        owner.level.getEntitiesOfClass(LivingEntity.class, owner.getBoundingBox().inflate(50),
                        target -> target != owner && !(target instanceof Player))
                .forEach(target -> charmPassive(owner, target));
    }

    private void charmPassive(LivingEntity source, LivingEntity target) {

        if (target instanceof Mob mob) {
            mob.setTarget(null);
            mob.setAggressive(false);
        }

        if (!target.hasEffect(TensuraMobEffects.MIND_CONTROL.get())) {
            int duration = 200; // short passive duration
            SkillHelper.checkThenAddEffectSource(target, source, TensuraMobEffects.MIND_CONTROL.get(),
                    duration, 1, true, true, false, true);

            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.HEART);
            Level level = source.level;
            level.playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @Override
    public int modes() {
        return 2;
    }

    @Override
    public MutableComponent getModeName(int mode) {
        switch (mode) {
            case 1: return Component.translatable("trmythos.skill.mode.unity.as_one");
            case 2: return Component.translatable("trmythos.skill.mode.unity.unity");
            default: return Component.empty();
        }
    }

    @Override
    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity owner) {
        switch (instance.getMode()) {
            case 1: // Possession mode
                if (!(owner instanceof Player player)) return;
                if (owner.level.isClientSide) return;

                TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
                    if (!cap.isSpiritualForm()) return;

                    LivingEntity target = SkillHelper.getTargetingEntity(owner, 5.0, false);
                    if (target == null || !target.isAlive()) return;

                    PossessionEvent event = new PossessionEvent(target, player);
                    if (MinecraftForge.EVENT_BUS.post(event)) return;

                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.teleportTo((ServerLevel) owner.level, target.getX(), target.getY(), target.getZ(), target.getYRot(), target.getXRot());
                    }

                    player.hurtMarked = true;
                    owner.level.playSound(null, target.getX(), target.getY(), target.getZ(),
                            net.minecraft.sounds.SoundEvents.EVOKER_CAST_SPELL,
                            net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);

                    // Clone/possession logic here...
                    // Charm is no longer triggered in mode 1 for mobs
                });
                break;

            case 2: // Mode 2 intentionally empty
                break;
        }
    }
}
