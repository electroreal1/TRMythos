package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.entity.magic.breath.BreathEntity;
import com.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import com.github.mythos.mythos.registry.skill.Skills;
import com.github.mythos.mythos.util.MythosUtils;
import com.github.mythos.mythos.util.damage.MythosDamageSources;
import com.mojang.math.Vector3f;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

public class ZephyrosSkill extends Skill {
    public ZephyrosSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    public void onDamageEntity(ManasSkillInstance instance, @NotNull LivingEntity living, @NotNull LivingHurtEvent e) {
        if (instance.isToggled()) {
            if (DamageSourceHelper.isWindDamage(e.getSource())) {
                if (instance.isMastered(living)) {
                    e.setAmount(e.getAmount() * 6.0F);
                } else {
                    e.setAmount(e.getAmount() * 4.0F);
                }
            }
        }
        if (TensuraSkillCapability.isSkillInSlot(living, (ManasSkill) Skills.ZEPHYROS.get())) {
            SkillUtils.reducingResistances(living);
        }
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public void onLearnSkill(ManasSkillInstance instance, Player player, UnlockSkillEvent event) {
        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        Skill windManip = ExtraSkills.WIND_MANIPULATION.get();
        if (storage.getSkill(windManip).isPresent()) {
            return;
        } else {
            storage.learnSkill(windManip);
        }
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }
    private static double rotation = 0;
    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (!MythosSkillsConfig.EnableSkillAuras()) return;
        if (!(entity instanceof Player player)) return;
        Level level = entity.level;
        if (!(level instanceof ServerLevel server)) return;
        RandomSource rand = player.level.random;
        int streams = 8;
        double yOffset = 1.2;

        for (int i = 0; i < streams; i++) {
            double angle = rotation + i * 2 * Math.PI / streams;
            double radius = 0.7 + Math.sin(rotation * 2 + i) * 0.1;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + Math.sin(rotation * 2 + i) * 0.2;

            float size = 0.6f + rand.nextFloat() * 0.3f;
            Vector3f color = rand.nextDouble() < 0.5 ? new Vector3f(0.7f, 0.9f, 1f) : new Vector3f(0.9f, 0.9f, 1f);
            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public int modes() {
        return 2;
    }
    public @NotNull Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.translatable("trmythos.skill.zephyros.wind");
            case 2 -> Component.translatable("trmythos.skill.zephyros.burst");
            default -> Component.empty();
        };
    }

    public int nextMode(@NotNull LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
            return instance.getMode() == 1 ? 2 : 1;
    }

    public boolean onHeld(@NotNull ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
             if (heldTicks % 20 == 0 && SkillHelper.outOfMagicule(entity, instance)) {
                return false;
            } else {
                if (heldTicks % 60 == 0 && heldTicks > 0) {
                    this.addMasteryPoint(instance, entity);
                }
                 if (instance.getMode() == 1) {
                     float damage = instance.isMastered(entity) ? 100.0F : 50.0F;
                    BreathEntity.spawnBreathEntity((EntityType) TensuraEntityTypes.WIND_BREATH.get(), entity, instance, damage, this.magiculeCost(entity, instance));
                    entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
                    entity.clearFire();
            }
        } return true;
    }

    @Override
    public void onPressed(@NotNull ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() == 2) {
        if (entity.level.isClientSide) return;

        if (SkillHelper.outOfMagicule(entity, instance)) return;

        float baseDamage = instance.isMastered(entity) ? 500 : 250;

        LivingEntity target = MythosUtils.getLookedAtEntity(entity, 10.0D);
        if (target == null) return;

        ServerLevel level = (ServerLevel) entity.level;

        target.hurt(MythosDamageSources.overpressureBurstWind(), baseDamage);

        Vec3 pos = target.position();
        level.sendParticles(ParticleTypes.CLOUD, pos.x, pos.y + 1, pos.z, 25, 0.4, 0.4, 0.4, 0.05);
        level.sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y + 1, pos.z, 1, 0, 0, 0, 0);
        level.playSound(null, target.blockPosition(), net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE,
                net.minecraft.sounds.SoundSource.PLAYERS, 0.6F, 1.4F);

        Vec3 dir = target.position().subtract(entity.position()).normalize().scale(0.5);
        target.push(dir.x, 0.3, dir.z);
        instance.setCoolDown(50);
        }
    }
}

