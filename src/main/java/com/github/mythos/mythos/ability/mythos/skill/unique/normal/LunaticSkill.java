package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LunaticSkill extends Skill {
    public LunaticSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    public double getObtainingEpCost() {
        return 90000.0;
    }

    public int getMaxMastery() {
        return 1000;
    }

    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/lunatic.png");
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.isToggled()) {
            int amplifier = instance.isMastered(entity) ? 4 : 2;
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.INSANITY.get(), 20, amplifier, false, false, true));
        }
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("§5§lLunatic");
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("§dYou've torn through the veil of sanity, embracing chaos as truth. §4Madness§d fuels your growth—§5but at the cost of yourself§d.");
    }

    public Component getModeName(int mode) {
        MutableComponent var10000;
        switch (mode) {
            case 1 -> var10000 = Component.literal("Moment of Lucidity");
            case 2 -> var10000 = Component.literal("Delirium");
            case 3 -> var10000 = Component.literal("Psychosis");
            default -> var10000 = Component.empty();
        }

        return var10000;
    }

    @Override
    public int modes() {
        return 3;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        int current = instance.getMode();
        if (reverse) {
            return current <= 1 ? 3 : current - 1;
        } else {
            return current >= 3 ? 1 : current + 1;
        }
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!SkillHelper.outOfMagicule(entity, instance)) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                switch (instance.getMode()) {
                    case 1 -> {
                        this.momentOfLucidity(player);
                        this.addMasteryPoint(instance, entity);
                    }
                    case 2 -> {
                        this.Delirium(player);
                        this.addMasteryPoint(instance, entity);
                    }
                }

            }
        }
    }

    public void onHeld(ManasSkillInstance instance, Player player, int tickCount) {
        if (instance.getMode() == 3) {
            int playerInsanity = getInsanity(player);
            if (playerInsanity >= 2) {
                List<LivingEntity> affected = player.level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(10.0), (e) -> e != player && e.isAlive());

                int targetAmplifier = (playerInsanity / 2) - 1;

                for (LivingEntity target : affected) {
                    target.addEffect(new MobEffectInstance(TensuraMobEffects.INSANITY.get(), 100, Math.max(0, targetAmplifier), false, false, true));

                    if (getInsanity(target) >= 2 && tickCount % 20 == 0) {
                        TensuraEPCapability.getFrom(target).ifPresent((cap) -> {
                            cap.setSpiritualHealth(cap.getSpiritualHealth() - 1.0);
                        });
                    }
                }
            }
        }
    }

    private int getInsanity(LivingEntity entity) {
        MobEffectInstance effect = entity.getEffect(TensuraMobEffects.INSANITY.get());
        if (effect != null) {
            return effect.getAmplifier() + 1;
        }
        return 0;
    }

    private void setInsanity(LivingEntity entity, int level) {
        if (level <= 0) {
            entity.removeEffect(TensuraMobEffects.INSANITY.get());
            return;
        }

        int clampedLevel = Math.max(1, Math.min(level, 5));


        entity.addEffect(new MobEffectInstance(TensuraMobEffects.INSANITY.get(), 600,
                clampedLevel - 1, false, false, true));
    }

    private void momentOfLucidity(Player player) {
        int currentLevel = this.getInsanity(player);

        if (currentLevel > 0) {
            this.setInsanity(player, currentLevel - 1);

            player.displayClientMessage(Component.literal("You grasp onto a fleeting moment of clarity.")
                    .withStyle(ChatFormatting.LIGHT_PURPLE), true);
        }
    }

    private void Delirium(Player player) {
        LivingEntity target = this.getTarget(player, 15.0);
        if (target != null) {
            int playerInsanity = getInsanity(player);

            int targetAmplifier = Math.max(0, (playerInsanity / 2) - 1);

            target.addEffect(new MobEffectInstance(TensuraMobEffects.INSANITY.get(), 600, targetAmplifier));

            player.displayClientMessage(Component.literal("Your madness infects " + target.getDisplayName().getString()).withStyle(ChatFormatting.DARK_PURPLE), true);
        }
    }

    private LivingEntity getTarget(Player player, double range) {
        return player.level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(range), (entity) -> entity != player && entity.isAlive()).stream().findFirst().orElse(null);
    }
}
