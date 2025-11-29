package com.github.mythos.mythos.ability.mythos.skill.ultimate;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
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
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

public class AresSkill extends Skill {
    public AresSkill(SkillType type) {
        super(SkillType.ULTIMATE);
    }

    @Override
    public double getObtainingEpCost() {
        return 2000000;
    }

    public boolean meetEPRequirement(@NotNull Player player, double newEP) {
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, (ManasSkill) UniqueSkills.DIVINE_BERSERKER.get());
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity living, LivingHurtEvent e) {
        if (instance.isToggled()) {
            if (DamageSourceHelper.isPhysicalAttack(e.getSource())) {
                if (instance.isMastered(living)) {
                    e.setAmount(e.getAmount() * 6.0F);
                } else {
                    e.setAmount(e.getAmount() * 4.0F);
                }
            }
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
                        double dodgeChance = 0;
                        if (instance.isMastered(entity)) {
                            dodgeChance = 0.75;
                        } else {
                            dodgeChance = 0.5;
                        }

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
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }
    private static double rotation = 0;
    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        Level level = entity.level;
        if (!(level instanceof ServerLevel server)) return;

        RandomSource rand = player.level.random;
        int points = 16;
        double radius = 1.0;
        double yOffset = 1.2;

        for (int i = 0; i < points; i++) {
            double angle = i * 2 * Math.PI / points + rotation;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + Math.sin(rotation * 3 + i) * 0.2;
            float size = 0.7f + rand.nextFloat() * 0.3f;
            Vector3f color = new Vector3f(1f, 0.2f + rand.nextFloat() * 0.3f, 0f);
            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, 0, 0, 0, 0);
        }

        int bursts = 6;
        for (int i = 0; i < bursts; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double distance = 0.5 + rand.nextDouble();
            double px = player.getX() + Math.cos(angle) * distance;
            double pz = player.getZ() + Math.sin(angle) * distance;
            double py = player.getY() + yOffset + rand.nextDouble() * 0.5;
            float size = 0.8f + rand.nextFloat() * 0.3f;
            Vector3f color = new Vector3f(1f, 0.1f + rand.nextFloat() * 0.3f, 0f);
            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, 0, 0, 0, 0);
        }
    }
}
