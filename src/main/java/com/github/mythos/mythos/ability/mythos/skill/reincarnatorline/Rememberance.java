package com.github.mythos.mythos.ability.mythos.skill.reincarnatorline;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import com.github.manasmods.tensura.capability.ep.ITensuraEPCapability;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.world.TensuraGameRules;
import com.github.mythos.mythos.registry.skill.Skills;
import com.github.mythos.mythos.util.MythosUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Rememberance extends Skill {
    public Rememberance(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public boolean meetEPRequirement(Player entity, double newEP) {
        CompoundTag data = entity.getPersistentData();

        int shardCount = data.getInt("MemoryShards");

        if (shardCount >= 5) return false;

        double currentEP = TensuraEPCapability.getCurrentEP(entity);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }

        return SkillUtils.isSkillMastered(entity, Skills.REINCARNATOR.get());
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Remembrance").withStyle(ChatFormatting.WHITE);
    }

    @Nullable
    @Override
    public MutableComponent getColoredName() {
        return Component.literal("Remembrance").withStyle(ChatFormatting.GOLD);
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("Death can never truly be the end so long as you can recognize yourself. Time has yet to wear away at your mind, and this skill is proof that it may never truly succeed in its efforts...");
    }

    @Override
    public int getMaxMastery() {
        return 20;
    }

    @Override
    public double getObtainingEpCost() {
        return 500000;
    }

    @Override
    public void onDeath(ManasSkillInstance instance, LivingDeathEvent event) {
        this.addMasteryPoint(instance, event.getEntity());
    }

    public Component getModeName(int mode) {
        MutableComponent var10000;
        switch (mode) {
            case 1 -> var10000 = Component.literal("Memory Retrieval");
            case 2 -> var10000 = Component.literal("Memory Alteration");
            default -> var10000 = Component.empty();
        }

        return var10000;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        // Memory Alteration
        if (instance.getMode() == 1) {
            if (!(entity instanceof Player player)) return;
            Entity targetEntity = MythosUtils.getLookedAtEntity(entity, 10);

            if (!(targetEntity instanceof LivingEntity target)) {
                player.displayClientMessage(Component.literal("No valid target found."), true);
                return;
            }

            double playerEP = TensuraEPCapability.getFrom(player).map(ITensuraEPCapability::getEP).orElse(0.0);
            double targetEP = TensuraEPCapability.getFrom(target).map(ITensuraEPCapability::getEP).orElse(0.0);

            if (targetEP >= playerEP) {
                player.displayClientMessage(Component.literal("Target's Existence Points are too high to dominate!").withStyle(ChatFormatting.RED), true);
                return;
            }

            SkillStorage playerStorage = SkillAPI.getSkillsFrom(player);
            SkillStorage targetStorage = SkillAPI.getSkillsFrom(target);

            boolean isPlunderEnabled = player.level.getGameRules().getBoolean(TensuraGameRules.SKILL_STEAL);
            int battlewillCost = 20000;

            List<ManasSkill> skillsToProcess = new ArrayList<>();

            for (ManasSkillInstance targetSkillInstance : targetStorage.getLearnedSkills()) {
                ManasSkill skill = targetSkillInstance.getSkill();

                if (skill instanceof Skill s) {
                    Skill.SkillType type = s.getType();
                    if (type == Skill.SkillType.INTRINSIC || type == Skill.SkillType.EXTRA || type == Skill.SkillType.COMMON) {
                        skillsToProcess.add(skill);
                    }
                }
            }

            for (ManasSkill skill : skillsToProcess) {
                if (!playerStorage.getSkill(skill).isPresent()) {
                    playerStorage.learnSkill(skill);

                    player.sendSystemMessage(Component.literal("Acquired: ").withStyle(ChatFormatting.GRAY)
                            .append(Objects.requireNonNull(skill.getName()).withStyle(ChatFormatting.GOLD)));
                }

                if (isPlunderEnabled) {
                    targetStorage.forgetSkill(skill);
                }
            }

            playerStorage.syncAll();
            targetStorage.syncAll();

            player.level.playSound(null, target.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.PLAYERS, 1.0f, 1.5f);
            if (player.level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SOUL, target.getX(), target.getY() + 1, target.getZ(), 15, 0.2, 0.2, 0.2, 0.1);
            }
        }

        if (instance.getMode() == 2) {
            CharmSkill.charm(instance, entity);
        }
    }
}
