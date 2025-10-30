package com.github.mythos.mythos.ability.skill.unique.evolved;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CarnageSkill extends Skill {
    public CarnageSkill(SkillType type) {
        super(type);
    }

    public double getObtainingEpCost() {
        return 500000.0;
    }

    public static boolean VAMPIRE_CARNAGE = true;
//    @Override
//    public ResourceLocation getSkillIcon() {
//        return new ResourceLocation("trmythos", "textures/skill/unique/crimson_tyrant.png");
//    }

    private static final HashMap<UUID, Integer> killStacks = new HashMap<>();

    @SubscribeEvent
    public static void onEntityKilled(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            UUID id = player.getUUID();
            int stacks = killStacks.getOrDefault(id, 0);

            stacks = Math.min(stacks + 1, 100);
            killStacks.put(id, stacks);

            float multiplier = 3.0f + (stacks * 0.01f);
            player.getAttributes().getInstance(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).setBaseValue(4.0F * multiplier);
            player.getAttributes().getInstance(Attributes.ATTACK_SPEED).setBaseValue(0.1F * multiplier);
        }
        if (event.getSource() == null) return;
        if (!(event.getSource().getEntity() instanceof LivingEntity killer)) return;
        if (event.getSource().getEntity() instanceof LivingEntity) {
            if (!killer.getPersistentData().getBoolean("CrimsonAscendanceActive")) return;
            final int EXTRA_PER_KILL = 200;

            extendOrAddEffectDuration(killer, MythosMobEffects.BLOOD_COAT.get(), EXTRA_PER_KILL);
            extendOrAddEffectDuration(killer, TensuraMobEffects.HAKI_COAT.get(), EXTRA_PER_KILL);
            extendOrAddEffectDuration(killer, TensuraMobEffects.STRENGTHEN.get(), EXTRA_PER_KILL);
            extendOrAddEffectDuration(killer, TensuraMobEffects.INSPIRATION.get(), EXTRA_PER_KILL);

        }
    }

    private static void extendOrAddEffectDuration(LivingEntity entity, MobEffect effect, int extraDuration) {
        if (entity == null || effect == null || extraDuration <= 0) return;

        MobEffectInstance current = entity.getEffect(effect);
        if (current != null) {
            // keep same amplifier and flags, just increase duration
            int newDuration = current.getDuration() + extraDuration;
            entity.addEffect(new MobEffectInstance(
                    effect,
                    newDuration,
                    current.getAmplifier(),
                    current.isAmbient(),
                    current.isVisible(),
                    current.showIcon()
            ));
        } else {
            int defaultAmp = 0;
            entity.addEffect(new MobEffectInstance(effect, extraDuration, defaultAmp, false, false, false));
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            float healAmount = event.getAmount() * 0.15f;
            player.heal(healAmount);
        }
    }


    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event, Player player) {
        if (VAMPIRE_CARNAGE) {
            TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {

                Race vampireRace = TensuraRaces.VAMPIRE.get();
                Race currentRace = cap.getRace();

                if (currentRace != vampireRace) {
                    cap.setRace(player, vampireRace, true);
                }
            });
        }
    }

    public int modes() {
        return 2;
    }

    public Component getModeName(int mode) {
        MutableComponent name;
        switch (mode) {
            case 1 -> name = Component.translatable("trmythos.skill.carnage.blood");
            case 2 -> name = Component.translatable("trmythos.skill.carnage.absolute");
            default -> name = Component.empty();
        }
        return name;
    }

    public int nextMode(LivingEntity entity, com.github.manasmods.tensura.ability.TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    public double magiculeCost(LivingEntity entity, ManasSkillInstance instance) {
        double var10000;
        switch (instance.getMode()) {
            case 1:
                var10000 = 100000.0;
                break;
            case 2:
                var10000 = 1000.0;
                break;
            default:
                var10000 = 1000.0;
        }

        return var10000;
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity, Player caster) {
        switch (instance.getMode()) {
            case 1:
                double range = 5.0;

                List<LivingEntity> targets = caster.getLevel().getEntitiesOfClass(LivingEntity.class,
                        caster.getBoundingBox().inflate(15.0),
                        e -> e != caster && e.isAlive() && !e.isAlliedTo(caster));

                if (!targets.isEmpty()) {
                    float casterEP = (float) TensuraEPCapability.getCurrentEP(caster);

                    for (LivingEntity target : targets) {
                        // Skip invulnerable players
                        if (target instanceof Player player && player.getAbilities().invulnerable) continue;

                        float targetEP = target instanceof Player p ? (float) TensuraEPCapability.getCurrentEP(p) : 0f;

                        if (targetEP <= casterEP * 0.85f) {
                            assert target instanceof Player;
                            TensuraPlayerCapability.getFrom((Player) target).ifPresent(cap -> {
                                Race targetRace = cap.getRace();
                                Race vampireRace = TensuraRaces.VAMPIRE.get();

                                if (targetRace != null && targetRace != vampireRace) {
                                    cap.setRace(target instanceof Player ? (Player) target : null, vampireRace, true);
                                }
                            });
                        }
                    }
                }
                break;

            case 2:
                entity.getLevel().playSound(null, entity.blockPosition(), SoundEvents.WITHER_DEATH, SoundSource.PLAYERS, 2.0f, 0.9f);

                entity.addEffect(new MobEffectInstance(MythosMobEffects.BLOOD_COAT.get(), 1200, 1, false, false, false));
                entity.addEffect(new MobEffectInstance(TensuraMobEffects.HAKI_COAT.get(), 1200, 1, false, false, false));
                entity.addEffect(new MobEffectInstance(TensuraMobEffects.STRENGTHEN.get(), 1200, 20, false, false, false));
                entity.addEffect(new MobEffectInstance(TensuraMobEffects.INSPIRATION.get(), 1200, 15, false, false, false));

                TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.EXPLOSION_EMITTER);
                TensuraParticleHelper.spawnServerParticles(entity.level, (ParticleOptions) TensuraParticles.DARK_RED_LIGHTNING_SPARK.get(),
                        entity.getX(), entity.getY(), entity.getZ(), 55, 0.08, 0.08, 0.08, 0.5, true);

                entity.getPersistentData().putBoolean("AbsoluteBloodLordActive", true);

                instance.setCoolDown(1200);
        }
    }


}
