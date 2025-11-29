package com.github.mythos.mythos.ability.mythos.skill.ultimate;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.items.TensuraMobDropItems;
import com.github.mythos.mythos.registry.skill.Skills;
import com.mojang.math.Vector3f;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class OriginDao extends Skill {
    protected static final UUID ACCELERATION1 = UUID.fromString("60a06181-8cf6-45e1-b1df-2e69f3544ace");
    protected static final UUID ACCELERATION2 = UUID.fromString("ba91c840-3ea9-497a-93b7-75216523344e");

    public OriginDao(SkillType type) {
        super(SkillType.ULTIMATE);
    }

    @Override
    public double getObtainingEpCost() {
        return 1000000;
    }

    @Override
    public int getMaxMastery() {
        return 6000;
    }

    public boolean meetEPRequirement(Player player, double newEP) {
        // Check EP using Tensura capability
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false; // not enough EP
        }
        return SkillUtils.isSkillMastered(player, (ManasSkill) Skills.TRUE_DAO.get());
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }
    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION1, true);
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION2, true);
    }
    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION1, false);
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION2, false);
    }

    @Override
    public void onTick(ManasSkillInstance instance, @NotNull LivingEntity entity) {
        if (!instance.isToggled()) return;

        if (!(entity instanceof Player player)) return;

        TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
            double maxMP = player.getAttributeValue(TensuraAttributeRegistry.MAX_MAGICULE.get());
            double maxAP = player.getAttributeValue(TensuraAttributeRegistry.MAX_AURA.get());
            double regenMPPerTick = (maxMP / 50);
            double regenAPPerTick = (maxAP / 50);

            cap.setAura(Math.min(cap.getAura() + regenAPPerTick, maxAP));
            cap.setMagicule(Math.min(cap.getMagicule() + regenMPPerTick, maxMP));
        });

        TensuraPlayerCapability.sync(player);

        RandomSource rand = player.level.random;
        int particles = 50;
        double yOffset = 1.2;
        Level level = entity.level;
        if (!(level instanceof ServerLevel server)) return;

        for (int i = 0; i < particles; i++) {
            double radius = 1.5 + rand.nextDouble() * 2.0;
            double angle = rand.nextDouble() * 2 * Math.PI;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + (rand.nextDouble() - 0.5);

            float size = 0.9f + rand.nextFloat() * 0.2f;
            Vector3f color;
            double r = rand.nextDouble();
            if (r < 0.5) color = new Vector3f(1f, 1f, 0.8f);
            else if (r < 0.8) color = new Vector3f(1f, 1f, 1f);
            else color = new Vector3f(0.6f, 1f, 1f);

            double motionX = (player.getX() - px) * 0.15;
            double motionY = (player.getY() + yOffset - py) * 0.15;
            double motionZ = (player.getZ() - pz) * 0.15;

            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, motionX, motionY, motionZ, 0);
        }
    }

    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        if (!event.isCanceled()) {
            if (this.isInSlot(event.getEntity())) {
                DamageSource damageSource = event.getSource();
                if (!damageSource.isBypassInvul() && !damageSource.isMagic()) {
                    Entity var5 = damageSource.getDirectEntity();
                    if (var5 instanceof LivingEntity) {
                        LivingEntity entity = (LivingEntity)var5;
                        double dodgeChance = 0.5;

                        if (!(entity.getRandom().nextDouble() >= dodgeChance)) {
                            entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 2.0F, 1.0F);
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        Player player = (Player) entity;
        ItemStack held = player.getMainHandItem();
        Item item = held.getItem();

        int epGained = 0;

        if (item == TensuraMobDropItems.LOW_QUALITY_MAGIC_CRYSTAL.get()) {
            epGained = 40;
            this.addMasteryPoint(instance, entity);
            held.shrink(1);
        } else if (item == TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL.get()) {
            epGained = 120;
            this.addMasteryPoint(instance, entity);
            held.shrink(1);
        } else if (item == TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()) {
            epGained = 240;
            this.addMasteryPoint(instance, entity);
            held.shrink(1);
        } else {
            return;
        }

        instance.setCoolDown(5);
        SkillHelper.gainMaxAP(entity, epGained / 2);
        SkillHelper.gainMaxMP(entity, epGained / 2);
    }
}
