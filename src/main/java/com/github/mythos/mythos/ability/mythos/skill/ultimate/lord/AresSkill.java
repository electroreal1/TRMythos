package com.github.mythos.mythos.ability.mythos.skill.ultimate.lord;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.voiceoftheworld.VoiceOfTheWorld;
import com.mojang.math.Vector3f;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class AresSkill extends Skill {
    public AresSkill(SkillType type) {
        super(SkillType.ULTIMATE);
    }

    @Override
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/ultimate/ares.png");
    }

    @Override
    public double getObtainingEpCost() {
        return 2000000;
    }

    public int getMaxMastery() {return 1000;}

    public boolean meetEPRequirement(@NotNull Player player, double newEP) {
        if (!EnableUltimateSkillObtainment()) return false;
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, UniqueSkills.DIVINE_BERSERKER.get());
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (entity instanceof Player player && !instance.isTemporarySkill()) {
            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            Skill greedSkill = UniqueSkills.DIVINE_BERSERKER.get();
            storage.getSkill(greedSkill).ifPresent(storage::forgetSkill);

            VoiceOfTheWorld.announceToPlayer(player,
                    "Confirmed. Skill [Divine Berserker] has successfully evolved into the Skill [Ares, Lord of War].");
        }
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public int modes() {return 1;}

    public Component getModeName(int mode) {
        MutableComponent name;
        switch (mode) {
            case 1:
                name = Component.literal("Divine Warrior Release");
                break;
            default:
                name = Component.empty();
        }
        return name;
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

        if (instance.isToggled()) {
            entity.addEffect(new MobEffectInstance((MobEffect) MythosMobEffects.LONGEVITY_REGENERATION.get(), 200, 3, false, false, false));
        }

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

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.hasEffect(MythosMobEffects.ARES_BERSERKER.get())) {
            entity.removeEffect(MythosMobEffects.ARES_BERSERKER.get());
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 1.0F, 1.0F);
            instance.setCoolDown(360);
        } else {
            if (SkillHelper.outOfMagicule(entity, instance))
                return;

            double souls = getSouls(entity);
            int amp = Math.min((int) Math.round(souls / 1000.0D), 200);

            instance.setCoolDown(360);
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ZOMBIE_INFECT, SoundSource.PLAYERS, 1.0F, 1.0F);
            if (isMastered(instance, entity)) {
                entity.addEffect(new MobEffectInstance((MobEffect)MythosMobEffects.ARES_BERSERKER.get(), 28800, amp, false, false, false));
            } else {
                entity.addEffect(new MobEffectInstance((MobEffect)MythosMobEffects.ARES_BERSERKER.get(), 14400, amp, false, false, false));
            }
            TensuraParticleHelper.addServerParticlesAroundSelf((Entity)entity, (ParticleOptions)ParticleTypes.EXPLOSION, 3.0D);
            TensuraParticleHelper.addServerParticlesAroundSelf((Entity)entity, (ParticleOptions)ParticleTypes.EXPLOSION, 2.0D);
            TensuraParticleHelper.spawnServerParticles(entity.level, (ParticleOptions) TensuraParticles.PURPLE_LIGHTNING_SPARK.get(), entity
                    .getX(), entity.getY(), entity.getZ(), 55, 0.08D, 0.08D, 0.08D, 0.5D, true);
        }
    }

    public int getSouls(LivingEntity entity) {
        if (entity instanceof Player) {
            Player player = (Player)entity;
            return ((Integer)TensuraPlayerCapability.getFrom(player)
                    .map(cap -> Integer.valueOf(cap.getSoulPoints() / 1000))
                    .orElse(Integer.valueOf(0))).intValue();
        }
        return 0;
    }
}
