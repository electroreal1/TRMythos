package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.registry.items.TensuraMobDropItems;
import com.mojang.math.Vector3f;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class NascentDao extends Skill {
    protected static final UUID ACCELERATION1 = UUID.fromString("ef094024-599c-484a-8bfc-d30a788f9f15");
    protected static final UUID ACCELERATION2 = UUID.fromString("438a5fdb-af6b-42fc-b9d2-5a5e1e0fbf5d");

    public NascentDao(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public double getObtainingEpCost() {
        return 50000;
    }

    @Override
    public int getMaxMastery() {
        return 3000;
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
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        Level level = entity.level;
        if (!(level instanceof ServerLevel server)) return;
        RandomSource rand = player.level.random;
        int particles = 25;
        double maxRadius = 3.0;
        double yOffset = 1.2;

        for (int i = 0; i < particles; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double radius = rand.nextDouble() * maxRadius;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + (rand.nextDouble() - 0.5);

            double dx = (player.getX() - px) * 0.05;
            double dz = (player.getZ() - pz) * 0.05;
            double dy = (player.getY() + yOffset - py) * 0.05;

            float size = 0.6f + rand.nextFloat() * 0.3f;
            Vector3f color = new Vector3f(0.6f + rand.nextFloat() * 0.4f, 1f, 0.3f);

            server.sendParticles(
                    new DustParticleOptions(color, size),
                    px, py, pz,
                    1, dx, dy, dz, 0.01
            );
        }
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        Player player = (Player) entity;
        ItemStack held = player.getMainHandItem();
        Item item = held.getItem();

        int epGained = 0;

        if (item == TensuraMobDropItems.LOW_QUALITY_MAGIC_CRYSTAL.get()) {
            epGained = 10;
            this.addMasteryPoint(instance, entity);
            held.shrink(1);
        } else if (item == TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL.get()) {
            epGained = 30;
            this.addMasteryPoint(instance, entity);
            held.shrink(1);
        } else if (item == TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()) {
            epGained = 60;
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
