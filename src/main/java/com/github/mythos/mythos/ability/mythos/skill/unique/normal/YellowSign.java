package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class YellowSign extends Skill {
    public YellowSign(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public List<MobEffect> getImmuneEffects(ManasSkillInstance instance, LivingEntity entity) {
        List<MobEffect> list = new ArrayList<>();
        if (!instance.isToggled()) return list;
        list.add(MobEffects.CONFUSION);
        list.add(MobEffects.MOVEMENT_SLOWDOWN);
        list.add(TensuraMobEffects.FEAR.get());
        list.add(TensuraMobEffects.MIND_CONTROL.get());
        return list;
    }

    @Override
    public int modes() {
        return 1;
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Yellow Sign").withStyle(ChatFormatting.YELLOW);
    }

    @Override
    public @NotNull Component getSkillDescription() {
        return Component.literal("A fragment of forbidden truth seeps into your mind. The Yellow Sign appears before your eyes, warping perception and spreading subtle madness to those who draw near.");
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public int getMaxMastery() {
        return 3000;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity living) {
        CompoundTag tag = instance.getOrCreateTag();
        int five = tag.getInt("ProjectSignUses");
        if (five == 500) {
            instance.setMastery(3000);
        }
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            CompoundTag tag = instance.getOrCreateTag();

            projectSign(player, instance);

            int currentUses = tag.getInt("ProjectSignUses");
            tag.putInt("ProjectSignUses", currentUses + 1);

            if ((currentUses + 1) % 50 == 0) {
                player.displayClientMessage(Component.literal("§eThe Sign burns deeper... Progress: " + (currentUses + 1) + "/500"), true);
            }

            instance.setCoolDown(100);
        }
    }

    private void projectSign(ServerPlayer player, ManasSkillInstance instance) {
        Level level = player.level;
        double range = 15.0;

        AABB area = player.getBoundingBox().inflate(range);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area);

        for (LivingEntity target : targets) {
            if (target == player) continue;

            target.hurt(TensuraDamageSources.soulConsume(target), 30.0f);

            target.addEffect(new MobEffectInstance(MythosMobEffects.YELLOW_SIGN.get(), 200, 0));

            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
            target.addEffect(new MobEffectInstance(TensuraMobEffects.FEAR.get(), 100, 2));

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.WARPED_SPORE,
                        target.getX(), target.getY() + 1.5, target.getZ(),
                        25, 0.4, 0.4, 0.4, 0.05);
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.5f, 0.5f);
    }
}
