package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class AuthorSkill extends Skill {
    public AuthorSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Author");
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("It is time to write a new story.");
    }

    @Override
    public int modes() {
        return 3;
    }

    @Override
    public double getObtainingEpCost() {
        return 99000;
    }

    @Override
    public int getMaxMastery() {
        return 3000;
    }

    @Override
    public Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.literal("Character Analysis");
            case 2 -> Component.literal("Envision");
            case 3 -> Component.literal("Charm Character");
            default -> Component.empty();
        };
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse) return (instance.getMode() == 1) ? 3 : (instance.getMode() - 1);
        else return (instance.getMode() == 3) ? 1 : (instance.getMode() + 1);
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level.isClientSide) return;

        CompoundTag tag = instance.getOrCreateTag();
        tag.putBoolean("isWriting", instance.getMode() == 2 && this.isInSlot(entity));
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() == 1) {
            if (entity instanceof Player player) {
                TensuraSkillCapability.getFrom(player).ifPresent((cap) -> {
                    int level;
                    if (player.isCrouching()) {
                        level = cap.getAnalysisMode();
                        switch (level) {
                            case 1:
                                cap.setAnalysisMode(2);
                                player.displayClientMessage(Component.translatable("tensura.skill.analytical.analyzing_mode.block").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true);
                                break;
                            case 2:
                                cap.setAnalysisMode(0);
                                player.displayClientMessage(Component.translatable("tensura.skill.analytical.analyzing_mode.both").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true);
                                break;
                            default:
                                cap.setAnalysisMode(1);
                                player.displayClientMessage(Component.translatable("tensura.skill.analytical.analyzing_mode.entity").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true);
                        }

                        player.playNotifySound(SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                        TensuraSkillCapability.sync(player);
                    } else {
                        level = instance.isMastered(entity) ? 5 : 3;
                        if (cap.getAnalysisLevel() != level) {
                            cap.setAnalysisLevel(level);
                            cap.setAnalysisDistance(instance.isMastered(entity) ? 10 : 5);
                            entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                        } else {
                            cap.setAnalysisLevel(0);
                            entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                        }

                        TensuraSkillCapability.sync(player);
                    }
                });
            }
        }

        if (instance.getMode() == 3) {
            CharmSkill.charm(instance, entity);
        }
    }
}
