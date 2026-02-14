package com.github.mythos.mythos.ability.mythos.skill.reincarnatorline;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.Nullable;

public class Reincarnator extends Skill {
    public Reincarnator(SkillType type) {
        super(type);
    }

    @Override
    public int getMaxMastery() {
        return 300;
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Reincarnator").withStyle(ChatFormatting.WHITE);
    }

    @Nullable
    @Override
    public MutableComponent getColoredName() {
        return Component.literal("Reincarnator").withStyle(ChatFormatting.GOLD);
    }

    @Override
    public double getObtainingEpCost() {
        return 100000;
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("You've met with death time and time again, living numerous lives, yet your memories and sense of self remain immutable. Truly a unique existence.");
    }

    @Override
    public int modes() {
        return 1;
    }

    @Override
    public void onDeath(ManasSkillInstance instance, LivingDeathEvent event) {
        instance.addMasteryPoint(event.getEntity());
    }

    @Nullable
    @Override
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("tensura", "textures/gui/skills/default_icon.png");
    }

    @Override
    public Component getModeName(int mode) {
        return Component.literal("Default");
    }
}
