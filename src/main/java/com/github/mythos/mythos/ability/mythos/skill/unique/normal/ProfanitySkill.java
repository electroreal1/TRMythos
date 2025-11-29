package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.network.TensuraNetwork;
import com.github.manasmods.tensura.network.play2client.RequestFxSpawningPacket;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import com.mojang.math.Vector3f;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

public class ProfanitySkill extends Skill {

    @Override
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/profanity.png");
    }


    public ProfanitySkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public int getMaxMastery() {
        return 1500;
    }

    public double getObtainingEpCost() {
        return 100000;
    }

    public double magiculeCost(LivingEntity entity, ManasSkillInstance instance) {
        double var10000;
        switch (instance.getMode()) {
            case 1:
                var10000 = 1000.0;
            case 2:
                var10000 = 1000.0;
                break;
            default:
                var10000 = 500.0;
        }

        return var10000;
    }

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity living, LivingHurtEvent e) {
        if (instance.isToggled()) {
            if (DamageSourceHelper.isDarkDamage(e.getSource())) {
                if (instance.isMastered(living)) {
                    e.setAmount(e.getAmount() * 4.0F);
                } else {
                    e.setAmount(e.getAmount() * 3.0F);
                }
            }
        }
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        TensuraEPCapability.getFrom(entity).ifPresent((cap) -> {
            if (!cap.isChaos() || !cap.isMajin()) {
                cap.setMajin(true);
            }
        });
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
        int clusters = 8;
        double yBase = 1.2;
        for (int i = 0; i < clusters; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double radius = 0.3 + rand.nextDouble() * 0.7;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yBase + (rand.nextDouble() - 0.5) * 0.5;
            int strokes = 2 + rand.nextInt(3);
            for (int s = 0; s < strokes; s++) {
                double offsetX = (rand.nextDouble() - 0.5) * 0.15;
                double offsetY = (rand.nextDouble() - 0.5) * 0.3;
                double offsetZ = (rand.nextDouble() - 0.5) * 0.15;
                float size = 0.6f + rand.nextFloat() * 0.3f;
                Vector3f color;
                double r = rand.nextDouble();
                if (r < 0.4) color = new Vector3f(0.5f, 0f, 0.5f);
                else if (r < 0.7) color = new Vector3f(0.6f, 0f, 0.8f);
                else color = new Vector3f(0.3f, 0.6f, 0.2f);
                server.sendParticles(new DustParticleOptions(color, size), px + offsetX, py + offsetY, pz + offsetZ, 1, 0, 0, 0, 0);
            }
        }
    }

    public int modes() {
        return 2;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    public Component getModeName(int mode) {
        MutableComponent var10000;
        switch (mode) {
            case 1:
                var10000 = Component.translatable("trmythos.skill.profanity.tainted_soul");
                break;
            case 2:
                var10000 = Component.translatable("trmythos.skill.profanity.baneful_essence");
                break;
            default:
                var10000 = Component.empty();
        }
        return var10000;
    }

    private boolean canTurnMajin(LivingEntity living) {
        if (TensuraEPCapability.isChaos(living)) {
            return true;
        } else {
            return !TensuraEPCapability.isMajin(living);
        }
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() != 1) return;
        if (SkillHelper.outOfMagicule(entity, instance)) return;

        Level level = entity.level;
        if (level.isClientSide) return;

        LivingEntity target = SkillHelper.getTargetingEntity(entity, 10.0, false);
        if (target == null) return;
        if (target instanceof Player p && p.getAbilities().invulnerable) return;

        TensuraEPCapability.getFrom(target).ifPresent(cap -> {
            boolean changed = false;

            if (canTurnMajin(target)) {
                // Convert to Majin if possible
                cap.setChaos(false);
                cap.setMajin(true);
                changed = true;
            }

            if (changed) {
                TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.SCULK_SOUL, 2.0);
                entity.level.playSound(
                        null,
                        entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.WARDEN_HEARTBEAT,
                        SoundSource.PLAYERS,
                        1.0F,
                        1.2F
                );
            }

            // Always apply random effects, even if already Majin
            applyRandomEffects(target, level.random);

            TensuraEPCapability.sync(target);
            entity.swing(InteractionHand.MAIN_HAND, true);
        });

        // Mastery points and cooldown
        instance.setCoolDown(30);
        this.addMasteryPoint(instance, entity);
    }


    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        Level level = entity.level;
        if (level.isClientSide) return true;
        if (instance.getMode() == 2) {

            // Every second
            if (heldTicks % 20 == 0) {
                if (SkillHelper.outOfMagicule(entity, instance)) return false;

                // Play aura sound & spawn FX
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 1.0F, 0.8F);

                TensuraNetwork.INSTANCE.send(
                        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                        new RequestFxSpawningPacket(
                                new ResourceLocation("tensura:strength_sap"),
                                entity.getId(), 0.0, 1.0, 0.0, true)
                );

                // Find targets in a 10-block radius
                List<LivingEntity> targets = level.getEntitiesOfClass(
                        LivingEntity.class,
                        entity.getBoundingBox().inflate(10.0),
                        e -> e.isAlive() && !e.is(entity) && !entity.isAlliedTo(e)
                );

                if (!targets.isEmpty()) {
                    double ep = TensuraEPCapability.getEP(entity);
                    if (ep <= 0) return true;

                    // Damage = 1 point per 10000 EP
                    float damagePerSecond = (float) (ep / 1000.0);


                    // Darkness DamageSource (custom). Adjust properties if needed.
                    DamageSource darknessSource = new DamageSource("tensura.dark_attack");
                    // e.g. darknessSource = darknessSource.setMagic(); // not available on all mappings
                    // or darknessSource = darknessSource.bypassArmor(); // if you want it to ignore armor

                    for (LivingEntity target : targets) {
                        if (target instanceof Player p && p.getAbilities().invulnerable) continue;

                        // Apply vanilla magic damage
                        target.hurt(DamageSource.MAGIC, damagePerSecond);

                        // Apply custom darkness damage
                        target.hurt(darknessSource, damagePerSecond);

                        TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.SMOKE, 1.0);
                    }
                }
            }

            // Add mastery every 3 seconds
            if (heldTicks % 60 == 0 && heldTicks > 0) {
                this.addMasteryPoint(instance, entity);
            }

            return true;
        }
        return false;
    }

//    private static void applyRandomEffects(LivingEntity target, RandomSource random) {
//        Set<String> blacklist = new HashSet<>(MythosSkillsConfig.blacklistedEffects.get());
//
//        List<MobEffect> allEffects = ForgeRegistries.MOB_EFFECTS.getValues().stream()
//                .filter(Objects::nonNull)
//                .filter(effect -> !blacklist.contains(ForgeRegistries.MOB_EFFECTS.getKey(effect).toString()))
//                .collect(Collectors.toList());
//
//        for (MobEffect effect : ForgeRegistries.MOB_EFFECTS) {
//            if (effect == null) continue; // skip null effects
//            allEffects.add(effect);
//        }
//
//        if (allEffects.isEmpty()) return;
//
//        Collections.shuffle(allEffects, new java.util.Random(random.nextLong()));
//
//        // Random number of effects: 1, 2, or 3
//        int count = 1 + random.nextInt(3);
//
//        for (int i = 0; i < count && i < allEffects.size(); i++) {
//            MobEffect effect = allEffects.get(i);
//
//            // Random duration: 5–20 seconds (100–400 ticks)
//            int duration = 100 + random.nextInt(301);
//
//            // Random amplifier: 0–2 (level I–III)
//            int amplifier = random.nextInt(3);
//
//            target.addEffect(new MobEffectInstance(effect, duration, amplifier));
//        }
//    }
private static void applyRandomEffects(LivingEntity target, RandomSource random) {
    Set<String> blacklist = new HashSet<>(MythosSkillsConfig.blacklistedEffects.get());

    List<MobEffect> availableEffects = ForgeRegistries.MOB_EFFECTS.getValues().stream()
            .filter(Objects::nonNull)
            .filter(effect -> {
                var key = ForgeRegistries.MOB_EFFECTS.getKey(effect);
                return key != null && !blacklist.contains(key.toString());
            })
            .collect(Collectors.toList());

    if (availableEffects.isEmpty()) return;

    Collections.shuffle(availableEffects, new java.util.Random(random.nextLong()));

    int count = 1 + random.nextInt(3);

        for (int i = 0; i < count && i < availableEffects.size(); i++) {
          MobEffect effect = availableEffects.get(i);

         int duration = 100 + random.nextInt(301);

         int amplifier = random.nextInt(3);

         target.addEffect(new MobEffectInstance(effect, duration, amplifier));
        }
    }
}

