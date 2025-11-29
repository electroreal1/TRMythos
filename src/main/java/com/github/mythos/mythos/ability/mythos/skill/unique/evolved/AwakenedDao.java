package com.github.mythos.mythos.ability.mythos.skill.unique.evolved;

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
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AwakenedDao extends Skill {
    protected static final UUID ACCELERATION1 = UUID.fromString("189e6684-8b53-4abf-9156-07a1c6b1563b");
    protected static final UUID ACCELERATION2 = UUID.fromString("81112168-ecb2-4686-b91d-387f363843a8");

    public AwakenedDao(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public double getObtainingEpCost() {
        return 50000;
    }

    public boolean meetEPRequirement(Player player, double newEP) {
        // Check EP using Tensura capability
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false; // not enough EP
        }
        return SkillUtils.isSkillMastered(player, (ManasSkill) Skills.NASCENT_DAO.get());
    }

    @Override
    public int getMaxMastery() {
        return 4000;
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
            double regenMPPerTick = (maxMP / 100);
            double regenAPPerTick = (maxAP / 100);

            cap.setAura(Math.min(cap.getAura() + regenAPPerTick, maxAP));
            cap.setMagicule(Math.min(cap.getMagicule() + regenMPPerTick, maxMP));
        });

        TensuraPlayerCapability.sync(player);

        RandomSource rand = player.level.random;
        int particles = 20;
        double yOffset = 1.2;
        Level level = entity.level;
        if (!(level instanceof ServerLevel server)) return;


        for (int i = 0; i < particles; i++) {
            double radius = 1.0 + rand.nextDouble() * 0.5;
            double angle = rand.nextDouble() * 2 * Math.PI;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + (rand.nextDouble() - 0.5) * 0.5;

            float size = 0.7f + rand.nextFloat() * 0.2f;
            Vector3f color = rand.nextDouble() < 0.5 ? new Vector3f(0.6f, 1f, 0.3f) : new Vector3f(1f, 1f, 0.4f);

            double motionX = (player.getX() - px) * 0.05;
            double motionY = (player.getY() + yOffset - py) * 0.05;
            double motionZ = (player.getZ() - pz) * 0.05;

            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, motionX, motionY, motionZ, 0);
        }
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        Player player = (Player) entity;
        ItemStack held = player.getMainHandItem();
        Item item = held.getItem();

        int epGained = 0;

        if (item == TensuraMobDropItems.LOW_QUALITY_MAGIC_CRYSTAL.get()) {
            epGained = 20;
            this.addMasteryPoint(instance, entity);
            held.shrink(1);
        } else if (item == TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL.get()) {
            epGained = 60;
            this.addMasteryPoint(instance, entity);
            held.shrink(1);
        } else if (item == TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()) {
            epGained = 120;
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
