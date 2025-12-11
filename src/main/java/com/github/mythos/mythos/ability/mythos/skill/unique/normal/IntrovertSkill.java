package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.mythos.mythos.entity.IntrovertBarrier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public class IntrovertSkill extends Skill {
    public IntrovertSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/introvert.png");
    }

    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return instance.isToggled();
    }

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        if (!isInSlot(entity)) return;
        Level level = entity.getLevel();
        IntrovertBarrier barrier = new IntrovertBarrier(level, entity);
        level.addFreshEntity(barrier);
    }

    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        Level var4 = entity.level;
        if (var4 instanceof ServerLevel serverLevel) {
            serverLevel.getEntitiesOfClass(IntrovertBarrier.class, entity.getBoundingBox().inflate(50.0)).stream().filter((barrier) -> {
                return barrier.getOwner() != null && barrier.getOwner().equals(entity);
            }).forEach(Entity::discard);
        }
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        double radius = 10;
        double knockbackPower = 2.5;
        float damage = instance.isMastered(entity) ? 60 : 30;

        Level level = entity.getLevel();

        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class,
                entity.getBoundingBox().inflate(radius),
                e -> e != entity && e.isAlive()
        );

        for (LivingEntity target : targets) {

            double dx = target.getX() - entity.getX();
            double dz = target.getZ() - entity.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);

            if (dist == 0) dist = 0.1;

            double knockX = (dx / dist) * knockbackPower;
            double knockZ = (dz / dist) * knockbackPower;


            target.setDeltaMovement(
                    target.getDeltaMovement().add(knockX, 0.4, knockZ)
            );

            target.hurtMarked = true;

            target.hurt(DamageSource.playerAttack((Player) entity), damage);
        }
    }
}
