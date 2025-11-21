package com.github.mythos.mythos.ability.mythos.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import io.github.Memoires.trmysticism.entity.skill.LimitlessBarrierEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public class IntrovertSkill extends Skill {
    public IntrovertSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return instance.isToggled();
    }

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        Level level = entity.getLevel();
        LimitlessBarrierEntity barrier = new LimitlessBarrierEntity(level, entity);
        level.addFreshEntity(barrier);
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
